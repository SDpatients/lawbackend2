package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.CaseTaskUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.CaseTaskResponse;
import com.lawbackend2.lawbackend2.dto.response.CaseTaskStatistics;
import com.lawbackend2.lawbackend2.entity.CaseTask;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.enums.CaseTaskStatus;
import com.lawbackend2.lawbackend2.exception.CaseTaskNotFoundException;
import com.lawbackend2.lawbackend2.exception.CaseTaskStatusInvalidException;
import com.lawbackend2.lawbackend2.repository.CaseTaskRepository;
import com.lawbackend2.lawbackend2.repository.FileRecordRepository;
import com.lawbackend2.lawbackend2.service.CaseTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CaseTaskServiceImpl implements CaseTaskService {

    private final CaseTaskRepository caseTaskRepository;
    private final FileRecordRepository fileRecordRepository;

    public CaseTaskServiceImpl(CaseTaskRepository caseTaskRepository, FileRecordRepository fileRecordRepository) {
        this.caseTaskRepository = caseTaskRepository;
        this.fileRecordRepository = fileRecordRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CaseTask> createTasksForCase(Long caseId) {
        log.info("为案件创建25个核心任务, caseId: {}", caseId);

        List<CaseTask> tasks = new ArrayList<>();

        String[][] taskDefinitions = {
            {"TASK_001", "提交破产申请材料", "申请人"},
            {"TASK_002", "法院立案形式审查", "法院立案庭"},
            {"TASK_003", "破产原因实质审查", "法院破产审判庭"},
            {"TASK_004", "同步选任管理人", "法院"},
            {"TASK_005", "裁定受理并公告", "法院"},
            {"TASK_006", "全面接管债务人", "管理人"},
            {"TASK_007", "调查财产及经营状况", "管理人"},
            {"TASK_008", "决定合同继续履行或解除", "管理人"},
            {"TASK_009", "追收债务人财产", "管理人"},
            {"TASK_010", "通知已知债权人并公告", "管理人"},
            {"TASK_011", "接收、登记债权申报", "管理人"},
            {"TASK_012", "审查申报债权并编制债权表", "管理人"},
            {"TASK_013", "筹备第一次债权人会议", "管理人"},
            {"TASK_014", "召开会议核查债权与议决事项", "债权人会议"},
            {"TASK_015", "表决通过财产变价/分配方案", "债权人会议、法院"},
            {"TASK_016", "宣告重整与和解", "法院"},
            {"TASK_017", "审查宣告破产条件", "法院"},
            {"TASK_018", "裁定宣告债务人破产", "法院"},
            {"TASK_019", "拟定并执行财产变价方案", "管理人"},
            {"TASK_020", "执行破产财产分配", "管理人"},
            {"TASK_021", "破产费用与共益债务", "管理人"},
            {"TASK_022", "提请终结破产程序", "管理人"},
            {"TASK_023", "法院裁定并公告", "法院"},
            {"TASK_024", "办理企业注销登记", "管理人"},
            {"TASK_025", "管理人终止执行职务并归档", "管理人"}
        };

        for (int i = 0; i < taskDefinitions.length; i++) {
            CaseTask task = new CaseTask();
            task.setCaseId(caseId);
            task.setTaskCode(taskDefinitions[i][0]);
            task.setTaskName(taskDefinitions[i][1]);
            task.setTaskDescription(taskDefinitions[i][2]);
            task.setStatus(CaseTaskStatus.IN_PROGRESS.name());
            task.setSortOrder(i + 1);
            tasks.add(task);
        }

        List<CaseTask> savedTasks = caseTaskRepository.saveAll(tasks);
        log.info("成功为案件创建{}个任务, caseId: {}", savedTasks.size(), caseId);
        return savedTasks;
    }

    @Override
    public Page<CaseTaskResponse> getTasksByCaseId(Long caseId, String status, String taskCode, Pageable pageable) {
        log.debug("查询案件任务列表, caseId: {}, status: {}, taskCode: {}", caseId, status, taskCode);

        Page<CaseTask> page;
        if (status != null) {
            List<CaseTask> tasks = caseTaskRepository.findByCaseIdAndStatus(caseId, status);
            tasks = tasks.stream()
                    .filter(t -> taskCode == null || t.getTaskCode().equals(taskCode))
                    .collect(Collectors.toList());
            page = new org.springframework.data.domain.PageImpl<>(tasks, pageable, tasks.size());
        } else {
            page = caseTaskRepository.findByCaseId(caseId, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "sortOrder")));
            if (taskCode != null) {
                List<CaseTask> filteredTasks = page.getContent().stream()
                        .filter(t -> t.getTaskCode().equals(taskCode))
                        .collect(Collectors.toList());
                page = new org.springframework.data.domain.PageImpl<>(filteredTasks, pageable, filteredTasks.size());
            }
        }

        return page.map(this::convertToResponse);
    }

    @Override
    public CaseTask getTaskById(Long taskId) {
        return caseTaskRepository.findById(taskId)
                .orElseThrow(() -> new CaseTaskNotFoundException(taskId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseTaskResponse updateTask(Long taskId, CaseTaskUpdateRequest request) {
        log.info("更新任务信息, taskId: {}, request: {}", taskId, request);

        CaseTask task = getTaskById(taskId);

        if (request.getTaskDescription() != null) {
            task.setTaskDescription(request.getTaskDescription());
        }

        if (request.getStatus() != null) {
            validateTaskStatus(request.getStatus());
            task.setStatus(request.getStatus());
        }

        CaseTask updatedTask = caseTaskRepository.save(task);
        log.info("任务更新成功, taskId: {}", taskId);
        return convertToResponse(updatedTask);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateTaskStatus(List<Long> taskIds, String status) {
        log.info("批量更新任务状态, taskIds: {}, status: {}", taskIds, status);

        validateTaskStatus(status);

        int successCount = 0;
        for (Long taskId : taskIds) {
            try {
                CaseTask task = getTaskById(taskId);
                task.setStatus(status);
                caseTaskRepository.save(task);
                successCount++;
            } catch (Exception e) {
                log.error("更新任务状态失败, taskId: {}", taskId, e);
            }
        }

        log.info("批量更新任务状态完成, 成功: {}, 失败: {}", successCount, taskIds.size() - successCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(Long taskId) {
        log.info("删除任务, taskId: {}", taskId);

        CaseTask task = getTaskById(taskId);
        task.setIsDeleted(true);
        caseTaskRepository.save(task);

        log.info("任务删除成功, taskId: {}", taskId);
    }

    @Override
    public CaseTaskStatistics getStatistics(Long caseId) {
        log.debug("查询案件任务统计, caseId: {}", caseId);

        List<CaseTask> tasks = caseTaskRepository.findByCaseIdOrderBySortOrder(caseId);

        CaseTaskStatistics statistics = new CaseTaskStatistics();
        statistics.setTotalTasks(tasks.size());

        int completedTasks = 0;
        int inProgressTasks = 0;
        int reviewingTasks = 0;
        int skippedTasks = 0;
        int rejectedTasks = 0;
        int totalFiles = 0;

        for (CaseTask task : tasks) {
            switch (task.getStatus()) {
                case "COMPLETED":
                    completedTasks++;
                    break;
                case "IN_PROGRESS":
                    inProgressTasks++;
                    break;
                case "REVIEWING":
                    reviewingTasks++;
                    break;
                case "SKIPPED":
                    skippedTasks++;
                    break;
                case "REJECTED":
                    rejectedTasks++;
                    break;
            }

            Page<FileRecord> filePage = fileRecordRepository.findByConditions("CASE_TASK", task.getId().toString(), "ACTIVE", Pageable.unpaged());
            totalFiles += filePage.getContent().size();
        }

        statistics.setCompletedTasks(completedTasks);
        statistics.setInProgressTasks(inProgressTasks);
        statistics.setReviewingTasks(reviewingTasks);
        statistics.setSkippedTasks(skippedTasks);
        statistics.setRejectedTasks(rejectedTasks);
        statistics.setTotalFiles(totalFiles);

        if (tasks.size() > 0) {
            double completionRate = (completedTasks * 100.0) / tasks.size();
            statistics.setCompletionRate(Math.round(completionRate * 100.0) / 100.0);
        } else {
            statistics.setCompletionRate(0.0);
        }

        return statistics;
    }

    private void validateTaskStatus(String status) {
        try {
            CaseTaskStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new CaseTaskStatusInvalidException(status);
        }
    }

    private CaseTaskResponse convertToResponse(CaseTask task) {
        CaseTaskResponse response = new CaseTaskResponse();
        BeanUtils.copyProperties(task, response);

        Page<FileRecord> filePage = fileRecordRepository.findByConditions("CASE_TASK", task.getId().toString(), "ACTIVE", Pageable.unpaged());
        response.setFileCount((int) filePage.getTotalElements());

        return response;
    }
}

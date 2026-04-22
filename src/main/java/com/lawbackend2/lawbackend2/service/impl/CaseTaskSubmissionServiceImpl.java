package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.FileRecordInfo;
import com.lawbackend2.lawbackend2.dto.request.CaseTaskSubmissionBatchUpdateRequest;
import com.lawbackend2.lawbackend2.dto.request.CaseTaskSubmissionCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.CaseTaskSubmissionReviewRequest;
import com.lawbackend2.lawbackend2.dto.request.CaseTaskSubmissionUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.CaseSubmissionSummaryResponse;
import com.lawbackend2.lawbackend2.dto.response.CaseTaskSubmissionResponse;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.CaseTask;
import com.lawbackend2.lawbackend2.entity.CaseTaskSubmission;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.enums.SubmissionStatus;
import com.lawbackend2.lawbackend2.enums.SubmissionType;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.exception.CaseTaskNotFoundException;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.CaseTaskRepository;
import com.lawbackend2.lawbackend2.repository.CaseTaskSubmissionRepository;
import com.lawbackend2.lawbackend2.repository.FileRecordRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.CaseTaskSubmissionService;
import com.lawbackend2.lawbackend2.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CaseTaskSubmissionServiceImpl implements CaseTaskSubmissionService {

    private final CaseTaskSubmissionRepository submissionRepository;
    private final CaseTaskRepository caseTaskRepository;
    private final FileRecordRepository fileRecordRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final BankruptCaseRepository bankruptCaseRepository;

    public CaseTaskSubmissionServiceImpl(CaseTaskSubmissionRepository submissionRepository,
                                      CaseTaskRepository caseTaskRepository,
                                      FileRecordRepository fileRecordRepository,
                                      UserRepository userRepository,
                                      NotificationService notificationService,
                                      BankruptCaseRepository bankruptCaseRepository) {
        this.submissionRepository = submissionRepository;
        this.caseTaskRepository = caseTaskRepository;
        this.fileRecordRepository = fileRecordRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.bankruptCaseRepository = bankruptCaseRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseTaskSubmissionResponse createSubmission(CaseTaskSubmissionCreateRequest request, Long userId) {
        log.info("创建任务提交, caseTaskId: {}, userId: {}", request.getCaseTaskId(), userId);

        CaseTask task = caseTaskRepository.findById(request.getCaseTaskId())
                .orElseThrow(() -> new CaseTaskNotFoundException(request.getCaseTaskId()));

        Integer maxSubmissionNumber = submissionRepository.getMaxSubmissionNumberByCaseTaskId(request.getCaseTaskId());
        int nextSubmissionNumber = (maxSubmissionNumber == null ? 0 : maxSubmissionNumber) + 1;

        CaseTaskSubmission submission = new CaseTaskSubmission();
        submission.setCaseTaskId(request.getCaseTaskId());
        submission.setSubmissionTitle(request.getSubmissionTitle());
        submission.setSubmissionContent(request.getSubmissionContent());
        submission.setSubmissionType(request.getSubmissionType() != null ? request.getSubmissionType() : SubmissionType.NORMAL.name());
        submission.setSubmissionNumber(nextSubmissionNumber);
        submission.setStatus(SubmissionStatus.APPROVED.name());
        submission.setCreateUserId(userId);
        submission.setUpdateUserId(userId);
        
        if (request.getCreateTime() != null) {
            submission.setCreateTime(request.getCreateTime());
        } else {
            submission.setCreateTime(LocalDateTime.now());
        }

        CaseTaskSubmission savedSubmission = submissionRepository.save(submission);
        log.info("任务提交创建成功, submissionId: {}", savedSubmission.getId());

        User user = userRepository.findById(userId).orElse(null);
        String realName = user != null ? user.getRealName() : "未知用户";
        String content = String.format("%s 提交了案件流程任务：%s", realName, request.getSubmissionTitle());
        notificationService.sendNotificationToAdminAndSuperAdmin(
                "案件流程任务提交通知",
                content,
                "CASE_TASK_SUBMISSION",
                savedSubmission.getId(),
                "CaseTaskSubmission",
                userId,
                realName
        );

        return convertToResponse(savedSubmission);
    }

    @Override
    public Page<CaseTaskSubmissionResponse> getSubmissionsByTaskId(Long caseTaskId, Pageable pageable) {
        log.debug("查询任务提交列表, caseTaskId: {}", caseTaskId);

        Page<CaseTaskSubmission> page = submissionRepository.findByCaseTaskId(caseTaskId, 
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "submissionNumber")));

        return page.map(this::convertToResponse);
    }

    @Override
    public CaseTaskSubmissionResponse getSubmissionById(Long submissionId) {
        log.debug("查询单个提交详情, submissionId: {}", submissionId);

        CaseTaskSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new BusinessException("提交记录不存在"));

        return convertToResponse(submission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseTaskSubmissionResponse reviewSubmission(Long submissionId, CaseTaskSubmissionReviewRequest request, Long reviewerId) {
        log.info("审核任务提交, submissionId: {}, reviewerId: {}, status: {}", submissionId, reviewerId, request.getStatus());

        CaseTaskSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new BusinessException("提交记录不存在"));

        if (!SubmissionStatus.PENDING.name().equals(submission.getStatus())) {
            throw new BusinessException("只能审核待审核状态的提交");
        }

        submission.setStatus(request.getStatus());
        submission.setReviewOpinion(request.getReviewOpinion());
        submission.setReviewerId(reviewerId);
        submission.setReviewTime(LocalDateTime.now());
        submission.setUpdateUserId(reviewerId);

        CaseTaskSubmission savedSubmission = submissionRepository.save(submission);
        log.info("任务提交审核完成, submissionId: {}", submissionId);
        return convertToResponse(savedSubmission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSubmission(Long submissionId, Long userId) {
        log.info("删除任务提交, submissionId: {}, userId: {}", submissionId, userId);

        CaseTaskSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new BusinessException("提交记录不存在"));

        submissionRepository.delete(submission);
        log.info("任务提交删除成功, submissionId: {}", submissionId);
    }

    @Override
    public List<CaseTaskSubmissionResponse> getLatestSubmissions(Long caseTaskId, Integer limit) {
        log.debug("查询最新提交记录, caseTaskId: {}, limit: {}", caseTaskId, limit);

        List<CaseTaskSubmission> submissions = submissionRepository.findLatestByCaseTaskId(caseTaskId);

        if (limit != null && submissions.size() > limit) {
            submissions = submissions.subList(0, limit);
        }

        return submissions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseTaskSubmissionResponse updateSubmission(CaseTaskSubmissionUpdateRequest request, Long userId) {
        log.info("更新任务提交, submissionId: {}, userId: {}", request.getId(), userId);

        CaseTaskSubmission submission = submissionRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException("提交记录不存在"));

        if (submission.getIsDeleted()) {
            throw new BusinessException("提交记录已删除，无法修改");
        }

        if (SubmissionStatus.REJECTED.name().equals(submission.getStatus())) {
            throw new BusinessException("已驳回的提交记录无法修改");
        }

        if (request.getSubmissionTitle() != null && !request.getSubmissionTitle().isEmpty()) {
            submission.setSubmissionTitle(request.getSubmissionTitle());
        }

        if (request.getSubmissionContent() != null) {
            submission.setSubmissionContent(request.getSubmissionContent());
        }

        if (request.getSubmissionType() != null && !request.getSubmissionType().isEmpty()) {
            submission.setSubmissionType(request.getSubmissionType());
        }

        submission.setUpdateUserId(userId);

        CaseTaskSubmission savedSubmission = submissionRepository.save(submission);
        log.info("任务提交更新成功, submissionId: {}", savedSubmission.getId());

        return convertToResponse(savedSubmission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseTaskSubmissionResponse batchUpdateSubmission(CaseTaskSubmissionBatchUpdateRequest request, Long userId) {
        log.info("批量更新任务提交, submissionId: {}, userId: {}", request.getId(), userId);

        CaseTaskSubmission submission = submissionRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException("提交记录不存在"));

        if (submission.getIsDeleted()) {
            throw new BusinessException("提交记录已删除，无法修改");
        }

        if (SubmissionStatus.REJECTED.name().equals(submission.getStatus())) {
            throw new BusinessException("已驳回的提交记录无法修改");
        }

        if (request.getSubmissionTitle() != null && !request.getSubmissionTitle().isEmpty()) {
            submission.setSubmissionTitle(request.getSubmissionTitle());
        }

        if (request.getSubmissionContent() != null) {
            submission.setSubmissionContent(request.getSubmissionContent());
        }

        if (request.getSubmissionType() != null && !request.getSubmissionType().isEmpty()) {
            submission.setSubmissionType(request.getSubmissionType());
        }

        if (request.getFileOperations() != null && !request.getFileOperations().isEmpty()) {
            for (CaseTaskSubmissionBatchUpdateRequest.FileOperation fileOp : request.getFileOperations()) {
                if (fileOp.getFileId() == null) {
                    continue;
                }

                FileRecord file = fileRecordRepository.findById(fileOp.getFileId())
                        .orElseThrow(() -> new BusinessException("文件不存在: " + fileOp.getFileId()));

                if (!"CASE_TASK_SUBMISSION".equals(file.getBizType()) || !submission.getId().toString().equals(file.getBizId())) {
                    throw new BusinessException("文件不属于该提交: " + fileOp.getFileId());
                }

                if ("DELETE".equalsIgnoreCase(fileOp.getOperation())) {
                    file.setIsDeleted(true);
                    file.setDeleteTime(LocalDateTime.now());
                    file.setDeleteUserId(userId);
                    fileRecordRepository.save(file);
                    log.info("删除文件, fileId: {}", fileOp.getFileId());
                } else if ("UPDATE".equalsIgnoreCase(fileOp.getOperation())) {
                    file.setDescription(fileOp.getDescription());
                    fileRecordRepository.save(file);
                    log.info("更新文件描述, fileId: {}", fileOp.getFileId());
                }
            }
        }

        submission.setUpdateUserId(userId);

        CaseTaskSubmission savedSubmission = submissionRepository.save(submission);
        log.info("任务提交批量更新成功, submissionId: {}", savedSubmission.getId());

        return convertToResponse(savedSubmission);
    }

    private CaseTaskSubmissionResponse convertToResponse(CaseTaskSubmission submission) {
        CaseTaskSubmissionResponse response = new CaseTaskSubmissionResponse();
        BeanUtils.copyProperties(submission, response);

        response.setCreatorName(getUserName(submission.getCreateUserId()));

        Page<FileRecord> filePage = fileRecordRepository.findByConditions(
                "CASE_TASK_SUBMISSION", 
                submission.getId().toString(), 
                "ACTIVE", 
                Pageable.unpaged()
        );
        response.setFileCount((int) filePage.getTotalElements());

        return response;
    }

    private String getUserName(Long userId) {
        if (userId == null) {
            return "";
        }
        return userRepository.findById(userId)
                .map(User::getRealName)
                .orElse("");
    }

    @Override
    public CaseSubmissionSummaryResponse getCaseSubmissionSummary(Long caseId) {
        log.info("查询案件提交汇总, caseId: {}", caseId);

        BankruptCase bankruptCase = bankruptCaseRepository.findById(caseId)
                .orElseThrow(() -> new BusinessException("案件不存在"));

        List<CaseTask> tasks = caseTaskRepository.findByCaseIdOrderBySortOrder(caseId);

        Map<Integer, List<CaseTask>> tasksByStage = groupTasksByStage(tasks);

        List<CaseSubmissionSummaryResponse.StageSubmissionSummary> stages = new ArrayList<>();
        for (int stageNum = 1; stageNum <= 7; stageNum++) {
            List<CaseTask> stageTasks = tasksByStage.getOrDefault(stageNum, new ArrayList<>());
            if (stageTasks.isEmpty()) {
                continue;
            }

            CaseSubmissionSummaryResponse.StageSubmissionSummary stageSummary = new CaseSubmissionSummaryResponse.StageSubmissionSummary();
            stageSummary.setStageNum(stageNum);
            stageSummary.setStageName(getStageName(stageNum));

            List<CaseSubmissionSummaryResponse.TaskSubmissionSummary> taskSummaries = stageTasks.stream()
                    .map(this::convertToTaskSummary)
                    .collect(Collectors.toList());

            stageSummary.setTasks(taskSummaries);
            stages.add(stageSummary);
        }

        CaseSubmissionSummaryResponse response = new CaseSubmissionSummaryResponse();
        response.setCaseId(caseId);
        response.setCaseNumber(bankruptCase.getCaseNumber());
        response.setStages(stages);

        return response;
    }

    private Map<Integer, List<CaseTask>> groupTasksByStage(List<CaseTask> tasks) {
        Map<Integer, List<CaseTask>> stageMap = new HashMap<>();
        for (int i = 1; i <= 7; i++) {
            stageMap.put(i, new ArrayList<>());
        }

        for (CaseTask task : tasks) {
            int stageNum = getStageNumFromTaskCode(task.getTaskCode());
            if (stageNum > 0) {
                stageMap.get(stageNum).add(task);
            }
        }

        return stageMap;
    }

    private int getStageNumFromTaskCode(String taskCode) {
        if (taskCode == null) {
            return 0;
        }
        try {
            int taskNum = Integer.parseInt(taskCode.substring(5));
            if (taskNum >= 1 && taskNum <= 2) {
                return 1;
            } else if (taskNum >= 3 && taskNum <= 7) {
                return 2;
            } else if (taskNum >= 8 && taskNum <= 11) {
                return 3;
            } else if (taskNum >= 12 && taskNum <= 13) {
                return 4;
            } else if (taskNum >= 14 && taskNum <= 16) {
                return 5;
            } else if (taskNum >= 17 && taskNum <= 19) {
                return 6;
            } else if (taskNum >= 20 && taskNum <= 23) {
                return 7;
            }
        } catch (Exception e) {
            log.warn("解析任务编号失败: {}", taskCode);
        }
        return 0;
    }

    private String getStageName(int stageNum) {
        switch (stageNum) {
            case 1:
                return "申请与受理";
            case 2:
                return "管理人接管";
            case 3:
                return "债权申报与审查";
            case 4:
                return "债权人会议";
            case 5:
                return "重整和解及破产宣告";
            case 6:
                return "财产变价与分配";
            case 7:
                return "程序终结";
            default:
                return "未知阶段";
        }
    }

    private CaseSubmissionSummaryResponse.TaskSubmissionSummary convertToTaskSummary(CaseTask task) {
        CaseSubmissionSummaryResponse.TaskSubmissionSummary taskSummary = new CaseSubmissionSummaryResponse.TaskSubmissionSummary();
        taskSummary.setTaskId(task.getId());
        taskSummary.setTaskCode(task.getTaskCode());
        taskSummary.setTaskName(task.getTaskName());
        taskSummary.setTaskDescription(task.getTaskDescription());
        taskSummary.setStatus(task.getStatus());

        List<CaseTaskSubmission> submissions = submissionRepository.findByCaseTaskId(task.getId());
        List<CaseSubmissionSummaryResponse.SubmissionInfo> submissionInfos = submissions.stream()
                .map(this::convertToSubmissionInfo)
                .collect(Collectors.toList());

        taskSummary.setSubmissions(submissionInfos);

        return taskSummary;
    }

    private CaseSubmissionSummaryResponse.SubmissionInfo convertToSubmissionInfo(CaseTaskSubmission submission) {
        CaseSubmissionSummaryResponse.SubmissionInfo info = new CaseSubmissionSummaryResponse.SubmissionInfo();
        info.setSubmissionId(submission.getId());
        info.setSubmissionTitle(submission.getSubmissionTitle());
        info.setSubmissionContent(submission.getSubmissionContent());
        info.setSubmissionType(submission.getSubmissionType());
        info.setSubmissionNumber(submission.getSubmissionNumber());
        info.setStatus(submission.getStatus());
        info.setCreatorName(getUserName(submission.getCreateUserId()));
        info.setReviewerId(submission.getReviewerId());
        info.setReviewOpinion(submission.getReviewOpinion());
        info.setReviewTime(formatDateTime(submission.getReviewTime()));
        info.setCreateTime(formatDateTime(submission.getCreateTime()));
        info.setUpdateTime(formatDateTime(submission.getUpdateTime()));

        Page<FileRecord> filePage = fileRecordRepository.findByConditions(
                "CASE_TASK_SUBMISSION",
                submission.getId().toString(),
                "ACTIVE",
                Pageable.unpaged()
        );
        info.setFileCount((int) filePage.getTotalElements());

        return info;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public Map<Long, List<CaseTaskSubmissionResponse>> getLatestSubmissionsBatch(List<Long> caseTaskIds) {
        log.info("批量查询最新提交记录, caseTaskIds: {}", caseTaskIds);

        List<CaseTaskSubmission> submissions = submissionRepository.findLatestByCaseTaskIds(caseTaskIds);

        Map<Long, List<CaseTaskSubmissionResponse>> result = new HashMap<>();

        for (Long caseTaskId : caseTaskIds) {
            result.put(caseTaskId, new ArrayList<>());
        }

        for (CaseTaskSubmission submission : submissions) {
            Long caseTaskId = submission.getCaseTaskId();
            List<CaseTaskSubmissionResponse> taskSubmissions = result.get(caseTaskId);
            if (taskSubmissions != null) {
                taskSubmissions.add(convertToResponse(submission));
            }
        }

        return result;
    }
}

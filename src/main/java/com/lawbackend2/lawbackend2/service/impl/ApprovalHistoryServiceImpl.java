package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.response.ApprovalHistoryResponse;
import com.lawbackend2.lawbackend2.entity.ApprovalHistory;
import com.lawbackend2.lawbackend2.entity.CaseTask;
import com.lawbackend2.lawbackend2.entity.CaseTaskSubmission;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.ApprovalHistoryRepository;
import com.lawbackend2.lawbackend2.repository.CaseTaskRepository;
import com.lawbackend2.lawbackend2.repository.CaseTaskSubmissionRepository;
import com.lawbackend2.lawbackend2.repository.FileRecordRepository;
import com.lawbackend2.lawbackend2.service.ApprovalHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ApprovalHistoryServiceImpl implements ApprovalHistoryService {
    private final ApprovalHistoryRepository approvalHistoryRepository;
    private final CaseTaskRepository caseTaskRepository;
    private final CaseTaskSubmissionRepository caseTaskSubmissionRepository;
    private final FileRecordRepository fileRecordRepository;

    public ApprovalHistoryServiceImpl(ApprovalHistoryRepository approvalHistoryRepository,
                                      CaseTaskRepository caseTaskRepository,
                                      CaseTaskSubmissionRepository caseTaskSubmissionRepository,
                                      FileRecordRepository fileRecordRepository) {
        this.approvalHistoryRepository = approvalHistoryRepository;
        this.caseTaskRepository = caseTaskRepository;
        this.caseTaskSubmissionRepository = caseTaskSubmissionRepository;
        this.fileRecordRepository = fileRecordRepository;
    }

    @Override
    public PageResult<ApprovalHistoryResponse> getApprovalHistoryList(Integer pageNum, Integer pageSize, Long approvalId, Long caseId, Long approverId) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "approvalDate"));
        
        Page<ApprovalHistory> page;
        
        // 根据参数进行过滤查询
        if (!ObjectUtils.isEmpty(approvalId)) {
            page = approvalHistoryRepository.findByApprovalId(approvalId, pageable);
        } else if (!ObjectUtils.isEmpty(caseId)) {
            page = approvalHistoryRepository.findByCaseId(caseId, pageable);
        } else if (!ObjectUtils.isEmpty(approverId)) {
            page = approvalHistoryRepository.findByApproverId(approverId, pageable);
        } else {
            page = approvalHistoryRepository.findAll(pageable);
        }
        
        PageResult<ApprovalHistoryResponse> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent().stream()
                .map(ApprovalHistoryResponse::fromEntity)
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public ApprovalHistoryResponse getApprovalHistoryDetail(Long historyId) {
        ApprovalHistory history = approvalHistoryRepository.findById(historyId)
                .orElseThrow(() -> new BusinessException("审批历史不存在"));
        return ApprovalHistoryResponse.fromEntity(history);
    }

    @Override
    @Transactional
    public ApprovalHistory createApprovalHistory(ApprovalHistory approvalHistory) {
        // 处理approvalType为TASK_{任务编号}的情况
        String approvalType = approvalHistory.getApprovalType();
        if (approvalType != null && approvalType.startsWith("TASK_")) {
            // 提取任务编号
            String taskCode = approvalType.substring(5);
            Long caseId = approvalHistory.getCaseId();
            
            // 根据案件ID和任务编号查询CaseTask
            Optional<CaseTask> caseTaskOpt = caseTaskRepository.findByCaseIdAndTaskCode(caseId, taskCode);
            if (caseTaskOpt.isPresent()) {
                CaseTask caseTask = caseTaskOpt.get();
                Long taskId = caseTask.getId();
                
                // 根据任务ID查询最新的任务提交记录
                Optional<CaseTaskSubmission> submissionOpt = caseTaskSubmissionRepository.findFirstByCaseTaskIdOrderBySubmissionNumberDesc(taskId);
                if (submissionOpt.isPresent()) {
                    CaseTaskSubmission submission = submissionOpt.get();
                    Long submissionId = submission.getId();
                    
                    // 查询该任务提交记录的文件列表
                    Page<FileRecord> fileRecordPage = fileRecordRepository.findByConditions("CASE_TASK_SUBMISSION", String.valueOf(submissionId), null, Pageable.unpaged());
                    List<FileRecord> fileRecords = fileRecordPage.getContent();
                    
                    // 将文件ID列表作为附件索引，用逗号分隔
                    if (!fileRecords.isEmpty()) {
                        String attachment = fileRecords.stream()
                                .map(file -> String.valueOf(file.getId()))
                                .collect(Collectors.joining(","));
                        approvalHistory.setApprovalAttachment(attachment);
                    }
                }
            }
        }
        return approvalHistoryRepository.save(approvalHistory);
    }

    @Override
    @Transactional
    public ApprovalHistory updateApprovalHistory(Long historyId, ApprovalHistory approvalHistory) {
        Optional<ApprovalHistory> existingHistory = approvalHistoryRepository.findById(historyId);
        if (existingHistory.isEmpty()) {
            throw new BusinessException("审批历史不存在");
        }
        
        ApprovalHistory history = existingHistory.get();
        history.setApprovalId(approvalHistory.getApprovalId());
        history.setCaseId(approvalHistory.getCaseId());
        history.setApproverId(approvalHistory.getApproverId());
        history.setApprovalType(approvalHistory.getApprovalType());
        history.setApprovalTitle(approvalHistory.getApprovalTitle());
        history.setApprovalAttachment(approvalHistory.getApprovalAttachment());
        history.setApprovalStatus(approvalHistory.getApprovalStatus());
        history.setApprovalOpinion(approvalHistory.getApprovalOpinion());
        history.setApprovalDate(approvalHistory.getApprovalDate());
        
        return approvalHistoryRepository.save(history);
    }

    @Override
    @Transactional
    public void deleteApprovalHistory(Long historyId) {
        if (!approvalHistoryRepository.existsById(historyId)) {
            throw new BusinessException("审批历史不存在");
        }
        approvalHistoryRepository.deleteById(historyId);
    }
}
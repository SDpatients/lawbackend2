package com.lawbackend2.lawbackend2.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.ApprovalCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.ApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.ApprovalStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.ApprovalUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.ApprovalResponse;
import com.lawbackend2.lawbackend2.entity.Approval;
import com.lawbackend2.lawbackend2.entity.ApprovalHistory;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.CaseTask;
import com.lawbackend2.lawbackend2.entity.CaseTaskSubmission;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.ApprovalHistoryRepository;
import com.lawbackend2.lawbackend2.repository.ApprovalRepository;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.CaseTaskRepository;
import com.lawbackend2.lawbackend2.repository.CaseTaskSubmissionRepository;
import com.lawbackend2.lawbackend2.repository.FileRecordRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.ApprovalService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApprovalServiceImpl implements ApprovalService {
    private final ApprovalRepository approvalRepository;
    private final ApprovalHistoryRepository approvalHistoryRepository;
    private final BankruptCaseRepository bankruptCaseRepository;
    private final UserRepository userRepository;
    private final CaseTaskRepository caseTaskRepository;
    private final CaseTaskSubmissionRepository caseTaskSubmissionRepository;
    private final FileRecordRepository fileRecordRepository;
    private final ObjectMapper objectMapper;

    public ApprovalServiceImpl(ApprovalRepository approvalRepository, ApprovalHistoryRepository approvalHistoryRepository, BankruptCaseRepository bankruptCaseRepository, UserRepository userRepository, CaseTaskRepository caseTaskRepository, CaseTaskSubmissionRepository caseTaskSubmissionRepository, FileRecordRepository fileRecordRepository, ObjectMapper objectMapper) {
        this.approvalRepository = approvalRepository;
        this.approvalHistoryRepository = approvalHistoryRepository;
        this.bankruptCaseRepository = bankruptCaseRepository;
        this.userRepository = userRepository;
        this.caseTaskRepository = caseTaskRepository;
        this.caseTaskSubmissionRepository = caseTaskSubmissionRepository;
        this.fileRecordRepository = fileRecordRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Long createApproval(ApprovalCreateRequest request, Long userId) {
        Optional<Approval> existingApprovalOpt = approvalRepository.findByCaseIdAndApprovalType(request.getCaseId(), request.getApprovalType());
        
        if (existingApprovalOpt.isPresent()) {
            Approval existingApproval = existingApprovalOpt.get();
            if (!"REJECTED".equals(existingApproval.getApprovalStatus()) && !"FAIL".equals(existingApproval.getApprovalResult())) {
                throw new BusinessException("该案件已存在相同类型的审批申请");
            }
            // 如果状态为已拒绝或者结果为失败，则更新为待审核并修改相关参数
            BeanUtils.copyProperties(request, existingApproval, "id", "createTime", "createUserId", "approvalCount", "lawyerId");
            if (request.getLawyerId() != null) {
                existingApproval.setLawyerId(request.getLawyerId());
            }
            existingApproval.setApprovalStatus("PENDING");
            existingApproval.setApprovalResult(null);
            existingApproval.setApproverId(null);
            existingApproval.setApprovalDate(null);
            existingApproval.setUpdateUserId(userId);
            existingApproval.setUpdateTime(LocalDateTime.now());
            
            Approval saved = approvalRepository.save(existingApproval);
            return saved.getId();
        }
        
        Approval approval = new Approval();
        BeanUtils.copyProperties(request, approval, "lawyerId");
        approval.setLawyerId(userId);
        approval.setApprovalStatus("PENDING");
        approval.setApprovalCount(0);
        approval.setStatus("ACTIVE");
        approval.setCreateUserId(userId);
        
        // 如果approvalType以TASK_开头，则查询相关任务、提交记录和文件信息
        if (request.getApprovalType() != null && request.getApprovalType().startsWith("TASK_")) {
            String taskCode = request.getApprovalType();
            enrichApprovalWithTaskInfo(approval, request.getCaseId(), taskCode);
        }
        
        Approval saved = approvalRepository.save(approval);
        return saved.getId();
    }
    
    private void enrichApprovalWithTaskInfo(Approval approval, Long caseId, String taskCode) {
        try {
            // 1. 查询任务信息
            Optional<CaseTask> taskOpt = caseTaskRepository.findByCaseIdAndTaskCode(caseId, taskCode);
            if (!taskOpt.isPresent()) {
                return;
            }
            CaseTask task = taskOpt.get();
            
            // 2. 查询该任务的所有提交记录
            List<CaseTaskSubmission> submissions = caseTaskSubmissionRepository.findByCaseTaskId(task.getId());
            
            // 3. 查询所有提交记录关联的文件
            List<String> submissionIds = submissions.stream()
                    .map(sub -> sub.getId().toString())
                    .collect(Collectors.toList());
            
            List<FileRecord> files = fileRecordRepository.findByBizTypeAndBizIds("CASE_TASK_SUBMISSION", submissionIds, null);
            
            // 4. 构建approval_content的JSON数据
            Map<String, Object> contentMap = new HashMap<>();
            contentMap.put("task", buildTaskInfo(task));
            contentMap.put("submissions", submissions.stream()
                    .map(this::buildSubmissionInfo)
                    .collect(Collectors.toList()));
            
            String approvalContent = objectMapper.writeValueAsString(contentMap);
            approval.setApprovalContent(approvalContent);
            
            // 5. 构建approval_attachment的JSON数据
            Map<String, Object> attachmentMap = new HashMap<>();
            Map<String, List<Map<String, Object>>> filesBySubmission = new HashMap<>();
            
            for (FileRecord file : files) {
                String submissionId = file.getBizId();
                filesBySubmission.computeIfAbsent(submissionId, k -> new ArrayList<>()).add(buildFileInfo(file));
            }
            
            attachmentMap.put("files", filesBySubmission);
            String approvalAttachment = objectMapper.writeValueAsString(attachmentMap);
            approval.setApprovalAttachment(approvalAttachment);
            
        } catch (JsonProcessingException e) {
            throw new BusinessException("构建审批信息失败", e);
        }
    }
    
    private Map<String, Object> buildTaskInfo(CaseTask task) {
        Map<String, Object> taskInfo = new HashMap<>();
        taskInfo.put("id", task.getId());
        taskInfo.put("taskCode", task.getTaskCode());
        taskInfo.put("taskName", task.getTaskName());
        taskInfo.put("taskDescription", task.getTaskDescription());
        taskInfo.put("status", task.getStatus());
        taskInfo.put("sortOrder", task.getSortOrder());
        return taskInfo;
    }
    
    private Map<String, Object> buildSubmissionInfo(CaseTaskSubmission submission) {
        Map<String, Object> submissionInfo = new HashMap<>();
        submissionInfo.put("id", submission.getId());
        submissionInfo.put("submissionTitle", submission.getSubmissionTitle());
        submissionInfo.put("submissionContent", submission.getSubmissionContent());
        submissionInfo.put("submissionType", submission.getSubmissionType());
        submissionInfo.put("submissionNumber", submission.getSubmissionNumber());
        submissionInfo.put("status", submission.getStatus());
        submissionInfo.put("reviewerId", submission.getReviewerId());
        submissionInfo.put("reviewOpinion", submission.getReviewOpinion());
        submissionInfo.put("reviewTime", submission.getReviewTime());
        submissionInfo.put("createTime", submission.getCreateTime());
        return submissionInfo;
    }
    
    private Map<String, Object> buildFileInfo(FileRecord file) {
        Map<String, Object> fileInfo = new HashMap<>();
        fileInfo.put("id", file.getId());
        fileInfo.put("originalFileName", file.getOriginalFileName());
        fileInfo.put("storedFileName", file.getStoredFileName());
        fileInfo.put("filePath", file.getFilePath());
        fileInfo.put("fileSize", file.getFileSize());
        fileInfo.put("fileExtension", file.getFileExtension());
        fileInfo.put("mimeType", file.getMimeType());
        fileInfo.put("description", file.getDescription());
        fileInfo.put("sortOrder", file.getSortOrder());
        fileInfo.put("uploadTime", file.getUploadTime());
        return fileInfo;
    }

    @Override
    public PageResult<ApprovalResponse> getApprovalList(Integer pageNum, Integer pageSize, Long caseId, Long lawyerId, String approvalType, String approvalStatus, String status, String approvalTitle) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        
        // 使用Spring Data JPA的查询方法根据条件过滤
        List<Approval> approvals = approvalRepository.findAll();
        
        // 手动过滤，实际项目中应使用Specification或自定义查询方法
        approvals = approvals.stream()
                .filter(approval -> caseId == null || approval.getCaseId().equals(caseId))
                .filter(approval -> lawyerId == null || approval.getLawyerId().equals(lawyerId))
                .filter(approval -> {
                    if (approvalType == null || approvalType.isEmpty()) {
                        return true;
                    }
                    // 特殊处理：如果approvalType为TASK_，则返回approval_type为TASK_前缀的所有数据
                    if ("TASK_".equals(approvalType)) {
                        return approval.getApprovalType().startsWith("TASK_");
                    }
                    return approval.getApprovalType().equals(approvalType);
                })
                .filter(approval -> approvalStatus == null || approvalStatus.isEmpty() || approval.getApprovalStatus().equals(approvalStatus))
                .filter(approval -> status == null || status.isEmpty() || approval.getStatus().equals(status))
                .filter(approval -> {
                    if (approvalTitle == null || approvalTitle.isEmpty()) {
                        return true;
                    }
                    return approval.getApprovalTitle() != null && approval.getApprovalTitle().contains(approvalTitle);
                })
                // 按照createTime降序排序
                .sorted((a1, a2) -> a2.getCreateTime().compareTo(a1.getCreateTime()))
                .collect(java.util.stream.Collectors.toList());
        
        // 分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, approvals.size());
        List<Approval> pagedApprovals = approvals.subList(start, end);
        
        // 转换为包含案件编号和创建人姓名的响应DTO
        List<ApprovalResponse> responseList = pagedApprovals.stream().map(approval -> {
            String caseNumber = "";
            if (approval.getCaseId() != null) {
                Optional<BankruptCase> bankruptCaseOpt = bankruptCaseRepository.findById(approval.getCaseId());
                if (bankruptCaseOpt.isPresent()) {
                    caseNumber = bankruptCaseOpt.get().getCaseNumber();
                }
            }
            
            String realName = "";
            if (approval.getCreateUserId() != null) {
                Optional<User> userOpt = userRepository.findById(approval.getCreateUserId());
                if (userOpt.isPresent()) {
                    realName = userOpt.get().getRealName();
                }
            }
            
            return ApprovalResponse.fromEntity(approval, caseNumber, realName);
        }).collect(java.util.stream.Collectors.toList());
        
        PageResult<ApprovalResponse> result = new PageResult<>();
        result.setTotal((long) approvals.size());
        result.setList(responseList);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public ApprovalResponse getApprovalDetail(Long approvalId) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new BusinessException("审批不存在"));
        
        // 查询案件编号
        String caseNumber = "";
        if (approval.getCaseId() != null) {
            Optional<BankruptCase> bankruptCaseOpt = bankruptCaseRepository.findById(approval.getCaseId());
            if (bankruptCaseOpt.isPresent()) {
                caseNumber = bankruptCaseOpt.get().getCaseNumber();
            }
        }
        
        // 查询创建人姓名
        String realName = "";
        if (approval.getCreateUserId() != null) {
            Optional<User> userOpt = userRepository.findById(approval.getCreateUserId());
            if (userOpt.isPresent()) {
                realName = userOpt.get().getRealName();
            }
        }
        
        return ApprovalResponse.fromEntity(approval, caseNumber, realName);
    }

    @Override
    public void updateApproval(Long approvalId, ApprovalUpdateRequest request) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new BusinessException("审批不存在"));
        BeanUtils.copyProperties(request, approval, "id", "approvalStatus", "approvalResult", "approverId", "approvalDate", "approvalCount", "status");
        approvalRepository.save(approval);
    }

    @Override
    public void approveApproval(Long approvalId, ApprovalRequest request) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new BusinessException("审批不存在"));

        if (!"PENDING".equals(approval.getApprovalStatus())) {
            throw new BusinessException("该审批已处理，无法重复审批");
        }

        LocalDateTime now = LocalDateTime.now();
        
        // 更新审批主表
        approval.setApprovalStatus(request.getApprovalResult().equals("PASS") ? "APPROVED" : "REJECTED");
        approval.setApprovalResult(request.getApprovalResult());
        approval.setApproverId(request.getApproverId());
        approval.setApprovalDate(now);
        approval.setApprovalCount(approval.getApprovalCount() + 1);
        
        approvalRepository.save(approval);
        
        // 创建审批历史记录
        ApprovalHistory history = new ApprovalHistory();
        history.setApprovalId(approval.getId());
        history.setCaseId(approval.getCaseId());
        history.setApproverId(request.getApproverId());
        history.setApprovalType(approval.getApprovalType());
        history.setApprovalTitle(approval.getApprovalTitle());
        history.setApprovalAttachment(approval.getApprovalAttachment());
        history.setApprovalStatus(approval.getApprovalStatus());
        history.setApprovalOpinion(request.getApprovalOpinion());
        history.setApprovalDate(now);
        history.setStatus("ACTIVE");
        
        approvalHistoryRepository.save(history);
    }

    @Override
    public void updateApprovalStatus(Long approvalId, ApprovalStatusRequest request) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new BusinessException("审批不存在"));
        approval.setApprovalStatus(request.getApprovalStatus());
        approvalRepository.save(approval);
    }

    @Override
    public void deleteApproval(Long approvalId) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new BusinessException("审批不存在"));
        approvalRepository.delete(approval);
    }
}
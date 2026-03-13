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
        // 校验：如果案件状态已为 COMPLETED，则不能再次提交审批
        BankruptCase bankruptCase = bankruptCaseRepository.findById(request.getCaseId())
                .orElseThrow(() -> new BusinessException("案件不存在"));
        if ("COMPLETED".equals(bankruptCase.getCaseStatus())) {
            throw new BusinessException("该案件已结案，无法再次提交审批");
        }
        
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
            
            // 如果 approvalType 以 TASK_开头，则重新查询相关任务、提交记录和文件信息
            if (request.getApprovalType() != null && request.getApprovalType().startsWith("TASK_")) {
                String taskCode = request.getApprovalType();
                enrichApprovalWithTaskInfo(existingApproval, request.getCaseId(), taskCode, request.getApprovalAttachment());
            } else if ("CASE_SUBMIT".equals(request.getApprovalType())) {
                // 如果 approvalType 为 CASE_SUBMIT，则查询该案件的所有 CASE_SUBMIT 类型的文件
                enrichApprovalWithCaseSubmitFiles(existingApproval, request.getCaseId(), request.getApprovalAttachment());
            }
            
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
        
        // 如果 approvalType 以 TASK_开头，则查询相关任务、提交记录和文件信息
        if (request.getApprovalType() != null && request.getApprovalType().startsWith("TASK_")) {
            String taskCode = request.getApprovalType();
            enrichApprovalWithTaskInfo(approval, request.getCaseId(), taskCode, request.getApprovalAttachment());
        } else if ("CASE_SUBMIT".equals(request.getApprovalType())) {
            // 如果 approvalType 为 CASE_SUBMIT，则查询该案件的所有 CASE_SUBMIT 类型的文件
            enrichApprovalWithCaseSubmitFiles(approval, request.getCaseId(), request.getApprovalAttachment());
            // 将案件状态改为 AWAITING
            bankruptCase.setCaseStatus("AWAITING");
            bankruptCaseRepository.save(bankruptCase);
        }
        
        Approval saved = approvalRepository.save(approval);
        return saved.getId();
    }
    
    private void enrichApprovalWithTaskInfo(Approval approval, Long caseId, String taskCode, String frontendAttachment) {
        try {
            // 1. 查询任务信息
            Optional<CaseTask> taskOpt = caseTaskRepository.findByCaseIdAndTaskCode(caseId, taskCode);
            if (!taskOpt.isPresent()) {
                return;
            }
            CaseTask task = taskOpt.get();
            
            // 2. 查询该任务的所有提交记录（只查询未删除的）
            List<CaseTaskSubmission> submissions = caseTaskSubmissionRepository.findLatestByCaseTaskId(task.getId());
            
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
            
            // 添加前端传入的附件信息
            if (frontendAttachment != null && !frontendAttachment.isEmpty()) {
                attachmentMap.put("frontendAttachment", frontendAttachment);
            }
            
            // 添加从数据库查询到的文件信息
            Map<String, List<Map<String, Object>>> filesBySubmission = new HashMap<>();
            
            for (FileRecord file : files) {
                String submissionId = file.getBizId();
                filesBySubmission.computeIfAbsent(submissionId, k -> new ArrayList<>()).add(buildFileInfo(file));
            }
            
            if (!filesBySubmission.isEmpty()) {
                attachmentMap.put("files", filesBySubmission);
            }
            
            // 只在有数据时设置attachment，避免空JSON字符串
            if (!attachmentMap.isEmpty()) {
                String approvalAttachment = objectMapper.writeValueAsString(attachmentMap);
                // 打印原始长度和内容
                System.out.println("Original approval attachment length: " + approvalAttachment.length());
                System.out.println("Files by submission size: " + filesBySubmission.size());
                System.out.println("Files count: " + files.size());
                // 检查长度，如果超过3990个字符，进行截断
                if (approvalAttachment.length() > 3990) {
                    System.out.println("Attachment too long, truncating...");
                    // 保留前端附件信息和文件数量，只截断详细文件信息
                    Map<String, Object> truncatedMap = new HashMap<>();
                    if (attachmentMap.containsKey("frontendAttachment")) {
                        truncatedMap.put("frontendAttachment", attachmentMap.get("frontendAttachment"));
                    }
                    truncatedMap.put("filesCount", filesBySubmission.size());
                    truncatedMap.put("files", "[文件信息过长，已截断]");
                    truncatedMap.put("message", "文件信息过长，已截断");
                    approvalAttachment = objectMapper.writeValueAsString(truncatedMap);
                    System.out.println("Truncated approval attachment length: " + approvalAttachment.length());
                }
                // 打印最终长度和内容
                System.out.println("Final approval attachment length: " + approvalAttachment.length());
                System.out.println("Final approval attachment content: " + approvalAttachment);
                approval.setApprovalAttachment(approvalAttachment);
            }
            
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
        // 只保留最核心的字段，进一步减少JSON长度
        fileInfo.put("id", file.getId());
        fileInfo.put("originalFileName", file.getOriginalFileName());
        return fileInfo;
    }
    
    private void enrichApprovalWithCaseSubmitFiles(Approval approval, Long caseId, String frontendAttachment) {
        try {
            // 查询该案件的所有CASE_SUBMIT类型的文件（只查询未删除的）
            List<String> caseIds = new ArrayList<>();
            caseIds.add(caseId.toString());
            List<FileRecord> caseSubmitFiles = fileRecordRepository.findByBizTypeAndBizIds("CASE_SUBMIT", caseIds, null);
            
            // 查询该案件的所有case类型的文件（只查询未删除的）
            List<FileRecord> caseTypeFiles = fileRecordRepository.findByBizTypeAndBizIds("case", caseIds, null);
            
            // 合并所有文件
            List<FileRecord> files = new ArrayList<>();
            files.addAll(caseSubmitFiles);
            files.addAll(caseTypeFiles);
            
            // 构建approval_attachment的JSON数据
            Map<String, Object> attachmentMap = new HashMap<>();
            
            // 添加前端传入的附件信息
            if (frontendAttachment != null && !frontendAttachment.isEmpty()) {
                attachmentMap.put("frontendAttachment", frontendAttachment);
            }
            
            // 添加从数据库查询到的文件信息
            List<Map<String, Object>> caseFiles = new ArrayList<>();
            for (FileRecord file : files) {
                caseFiles.add(buildFileInfo(file));
            }
            
            if (!caseFiles.isEmpty()) {
                attachmentMap.put("files", caseFiles);
                attachmentMap.put("filesCount", caseFiles.size());
            }
            
            // 只在有数据时设置attachment，避免空JSON字符串
            if (!attachmentMap.isEmpty()) {
                String approvalAttachment = objectMapper.writeValueAsString(attachmentMap);
                // 检查长度，如果超过3990个字符，进行截断
                if (approvalAttachment.length() > 3990) {
                    System.out.println("Attachment too long, truncating...");
                    // 保留前端附件信息和文件数量，只截断详细文件信息
                    Map<String, Object> truncatedMap = new HashMap<>();
                    if (attachmentMap.containsKey("frontendAttachment")) {
                        truncatedMap.put("frontendAttachment", attachmentMap.get("frontendAttachment"));
                    }
                    truncatedMap.put("filesCount", caseFiles.size());
                    truncatedMap.put("files", "[文件信息过长，已截断]");
                    truncatedMap.put("message", "文件信息过长，已截断");
                    approvalAttachment = objectMapper.writeValueAsString(truncatedMap);
                    System.out.println("Truncated approval attachment length: " + approvalAttachment.length());
                }
                // 打印调试信息
                System.out.println("Case submit approval attachment length: " + approvalAttachment.length());
                System.out.println("Case submit files count: " + files.size());
                System.out.println("Case submit approval attachment content: " + approvalAttachment);
                approval.setApprovalAttachment(approvalAttachment);
            }
            
        } catch (JsonProcessingException e) {
            throw new BusinessException("构建审批信息失败", e);
        }
    }

    @Override
    public PageResult<ApprovalResponse> getApprovalList(Integer pageNum, Integer pageSize, Long caseId, Long lawyerId, String approvalType, String approvalStatus, String status, String approvalTitle) {
        return getApprovalList(pageNum, pageSize, caseId, lawyerId, approvalType, approvalStatus, status, approvalTitle, null);
    }
    
    public PageResult<ApprovalResponse> getApprovalList(Integer pageNum, Integer pageSize, Long caseId, Long lawyerId, String approvalType, String approvalStatus, String status, String approvalTitle, Boolean onlyPending) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        
        // 使用 Spring Data JPA 的查询方法根据条件过滤
        List<Approval> approvals = approvalRepository.findAll();
        
        // 手动过滤，实际项目中应使用 Specification 或自定义查询方法
        approvals = approvals.stream()
                .filter(approval -> caseId == null || approval.getCaseId().equals(caseId))
                .filter(approval -> lawyerId == null || approval.getLawyerId().equals(lawyerId))
                .filter(approval -> {
                    if (approvalType == null || approvalType.isEmpty()) {
                        return true;
                    }
                    // 特殊处理：如果 approvalType 为 TASK_，则返回 approval_type 为 TASK_前缀的所有数据
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
                .filter(approval -> {
                    if (onlyPending == null || !onlyPending) {
                        return true;
                    }
                    // 只筛选 approvalResult 为 null 的数据
                    return approval.getApprovalResult() == null;
                })
                // 按照 createTime 降序排序
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
    public void approveApproval(Long approvalId, ApprovalRequest request, Long approverId) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new BusinessException("审批不存在"));

        if (!"PENDING".equals(approval.getApprovalStatus())) {
            throw new BusinessException("该审批已处理，无法重复审批");
        }

        LocalDateTime now = LocalDateTime.now();
        
        // 检查并截断 approvalAttachment，避免长度超过限制
        if (approval.getApprovalAttachment() != null && approval.getApprovalAttachment().length() > 3990) {
            approval.setApprovalAttachment(approval.getApprovalAttachment().substring(0, 3990) + "...");
        }
        
        // 更新审批主表
        approval.setApprovalStatus(request.getApprovalResult().equals("PASS") ? "APPROVED" : "REJECTED");
        approval.setApprovalResult(request.getApprovalResult());
        approval.setApproverId(approverId);
        approval.setApprovalDate(now);
        approval.setApprovalCount(approval.getApprovalCount() + 1);
        
        // 根据审批结果更新案件状态
        if (approval.getCaseId() != null) {
            BankruptCase bankruptCase = bankruptCaseRepository.findById(approval.getCaseId())
                    .orElseThrow(() -> new BusinessException("案件不存在"));
            
            if ("PASS".equals(request.getApprovalResult())) {
                // 审批通过：案件状态改为 COMPLETED，并设置结案日期为当天
                bankruptCase.setCaseStatus("COMPLETED");
                bankruptCase.setClosingDate(java.time.LocalDate.now());
            } else if ("FAIL".equals(request.getApprovalResult())) {
                // 审批不通过：案件状态改为 ONGOING
                bankruptCase.setCaseStatus("ONGOING");
            }
            
            bankruptCaseRepository.save(bankruptCase);
        }
        
        approvalRepository.save(approval);
        
        // 创建审批历史记录
        ApprovalHistory history = new ApprovalHistory();
        history.setApprovalId(approval.getId());
        history.setCaseId(approval.getCaseId());
        history.setApproverId(approverId);
        history.setApprovalType(approval.getApprovalType());
        history.setApprovalTitle(approval.getApprovalTitle());
        // 检查并截断 approvalAttachment，避免长度超过限制
        if (approval.getApprovalAttachment() != null && approval.getApprovalAttachment().length() > 3990) {
            history.setApprovalAttachment(approval.getApprovalAttachment().substring(0, 3990) + "...");
        } else {
            history.setApprovalAttachment(approval.getApprovalAttachment());
        }
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

    @Override
    public Map<String, Object> getApprovalAttachments(Long approvalId, Boolean includeImages, Boolean includeFiles) {
        // 1. 查找审批信息
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new BusinessException("审批不存在"));
        
        // 2. 设置默认值
        if (includeImages == null) {
            includeImages = false;
        }
        if (includeFiles == null) {
            includeFiles = true;
        }
        
        // 3. 初始化返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("approvalId", approvalId);
        
        Map<String, List<Map<String, Object>>> attachments = new HashMap<>();
        int totalFiles = 0;
        int imageFiles = 0;
        
        // 4. 优先处理approval_attachment字段中已存储的文件信息
        if (approval.getApprovalAttachment() != null && !approval.getApprovalAttachment().isEmpty()) {
            try {
                Map<String, Object> attachmentData = objectMapper.readValue(approval.getApprovalAttachment(), Map.class);
                if (attachmentData.containsKey("files")) {
                    Object filesObj = attachmentData.get("files");
                    if (filesObj instanceof List) {
                        // CASE_SUBMIT类型的文件存储格式
                        List<?> filesList = (List<?>) filesObj;
                        List<Map<String, Object>> fileList = new ArrayList<>();
                        
                        for (Object fileObj : filesList) {
                            if (fileObj instanceof Map) {
                                Map<?, ?> fileMap = (Map<?, ?>) fileObj;
                                Map<String, Object> fileInfo = new HashMap<>();
                                fileInfo.put("id", fileMap.get("id"));
                                fileInfo.put("originalFileName", fileMap.get("originalFileName"));
                                
                                // 尝试根据文件名判断是否为图片
                                String fileName = fileMap.get("originalFileName") != null ? fileMap.get("originalFileName").toString() : "";
                                String lowerFileName = fileName.toLowerCase();
                                if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg") || lowerFileName.endsWith(".png") || lowerFileName.endsWith(".gif") || lowerFileName.endsWith(".bmp")) {
                                    imageFiles++;
                                }
                                
                                fileList.add(fileInfo);
                                totalFiles++;
                            }
                        }
                        
                        if (!fileList.isEmpty()) {
                            attachments.put("case_files", fileList);
                        }
                    } else if (filesObj instanceof Map) {
                        // TASK类型的文件存储格式
                        Map<?, ?> filesMap = (Map<?, ?>) filesObj;
                        for (Map.Entry<?, ?> entry : filesMap.entrySet()) {
                            String submissionId = entry.getKey().toString();
                            Object value = entry.getValue();
                            if (value instanceof List) {
                                List<?> filesList = (List<?>) value;
                                List<Map<String, Object>> fileList = new ArrayList<>();
                                
                                for (Object fileObj : filesList) {
                                    if (fileObj instanceof Map) {
                                        Map<?, ?> fileMap = (Map<?, ?>) fileObj;
                                        Map<String, Object> fileInfo = new HashMap<>();
                                        fileInfo.put("id", fileMap.get("id"));
                                        fileInfo.put("originalFileName", fileMap.get("originalFileName"));
                                        
                                        // 尝试根据文件名判断是否为图片
                                        String fileName = fileMap.get("originalFileName") != null ? fileMap.get("originalFileName").toString() : "";
                                        String lowerFileName = fileName.toLowerCase();
                                        if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg") || lowerFileName.endsWith(".png") || lowerFileName.endsWith(".gif") || lowerFileName.endsWith(".bmp")) {
                                            imageFiles++;
                                        }
                                        
                                        fileList.add(fileInfo);
                                        totalFiles++;
                                    }
                                }
                                
                                if (!fileList.isEmpty()) {
                                    attachments.put(submissionId, fileList);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // 解析失败时，继续执行原来的逻辑
                System.out.println("解析approval_attachment失败: " + e.getMessage());
            }
        }
        
        // 5. 如果没有从approval_attachment中获取到文件信息，再处理任务类型的审批
        if (attachments.isEmpty() && approval.getCaseId() != null && approval.getApprovalType() != null && approval.getApprovalType().startsWith("TASK_")) {
            String taskCode = approval.getApprovalType();
            Optional<CaseTask> taskOpt = caseTaskRepository.findByCaseIdAndTaskCode(approval.getCaseId(), taskCode);
            
            if (taskOpt.isPresent()) {
                CaseTask task = taskOpt.get();
                
                // 6. 查询该任务的所有提交记录
                List<CaseTaskSubmission> submissions = caseTaskSubmissionRepository.findByCaseTaskId(task.getId());
                
                // 7. 查询所有提交记录关联的文件
                List<String> submissionIds = submissions.stream()
                        .map(sub -> sub.getId().toString())
                        .collect(Collectors.toList());
                
                if (!submissionIds.isEmpty()) {
                    List<FileRecord> files = fileRecordRepository.findByBizTypeAndBizIds("CASE_TASK_SUBMISSION", submissionIds, null);
                    
                    // 8. 按提交记录ID组织文件
                    for (FileRecord file : files) {
                        String submissionId = file.getBizId();
                        
                        if (!attachments.containsKey(submissionId)) {
                            attachments.put(submissionId, new ArrayList<>());
                        }
                        
                        Map<String, Object> fileInfo = buildFileInfoWithDetails(file, includeImages);
                        attachments.get(submissionId).add(fileInfo);
                        
                        totalFiles++;
                        if (file.getMimeType() != null && file.getMimeType().startsWith("image/")) {
                            imageFiles++;
                        }
                    }
                }
            }
        }
        
        // 9. 构建返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("approvalId", approvalId);
        data.put("attachments", attachments);
        data.put("totalFiles", totalFiles);
        data.put("imageFiles", imageFiles);
        
        return data;
    }
    
    private Map<String, Object> buildFileInfoWithDetails(FileRecord file, boolean includeImages) {
        Map<String, Object> fileInfo = new HashMap<>();
        fileInfo.put("id", file.getId());
        fileInfo.put("originalFileName", file.getOriginalFileName());
        fileInfo.put("fileSize", file.getFileSize());
        fileInfo.put("fileExtension", file.getFileExtension());
        fileInfo.put("mimeType", file.getMimeType());
        
        if (includeImages && file.getMimeType() != null && file.getMimeType().startsWith("image/")) {
            try {
                java.nio.file.Path filePath = java.nio.file.Paths.get(file.getFilePath());
                if (java.nio.file.Files.exists(filePath)) {
                    byte[] fileContent = java.nio.file.Files.readAllBytes(filePath);
                    String base64Content = java.util.Base64.getEncoder().encodeToString(fileContent);
                    fileInfo.put("imageData", "data:" + file.getMimeType() + ";base64," + base64Content);
                }
            } catch (Exception e) {
            }
        }
        
        return fileInfo;
    }

    @Override
    public List<ApprovalResponse> getApprovalProgressByCaseId(Long caseId, String approvalType) {
        List<Approval> approvals = approvalRepository.findByCaseId(caseId);
        
        approvals = approvals.stream()
                .filter(approval -> approval.getApprovalResult() == null)
                .filter(approval -> {
                    if (approvalType == null || approvalType.isEmpty()) {
                        if ("CASE_SUBMIT".equals(approval.getApprovalType())) {
                            return true;
                        }
                        if (approval.getApprovalType() != null && approval.getApprovalType().startsWith("TASK_")) {
                            return true;
                        }
                        return false;
                    }
                    if ("CASE_SUBMIT".equals(approvalType)) {
                        return "CASE_SUBMIT".equals(approval.getApprovalType());
                    }
                    if (approvalType.startsWith("TASK_")) {
                        return approval.getApprovalType() != null && approval.getApprovalType().startsWith("TASK_");
                    }
                    return approval.getApprovalType() != null && approval.getApprovalType().equals(approvalType);
                })
                .sorted((a1, a2) -> a2.getCreateTime().compareTo(a1.getCreateTime()))
                .collect(Collectors.toList());
        
        return approvals.stream().map(approval -> {
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
        }).collect(Collectors.toList());
    }

    @Override
    public long getPendingCaseSubmitCount() {
        return approvalRepository.countByApprovalResultIsNullAndApprovalType("CASE_SUBMIT");
    }

    @Override
    public long getPendingTaskCount() {
        List<Approval> pendingApprovals = approvalRepository.findByApprovalResultIsNull();
        return pendingApprovals.stream()
                .filter(approval -> approval.getApprovalType() != null && approval.getApprovalType().startsWith("TASK_"))
                .count();
    }

    @Override
    public long getPendingTotalCount() {
        List<Approval> pendingApprovals = approvalRepository.findByApprovalResultIsNull();
        return pendingApprovals.stream()
                .filter(approval -> approval.getApprovalType() != null && 
                        ("CASE_SUBMIT".equals(approval.getApprovalType()) || approval.getApprovalType().startsWith("TASK_")))
                .count();
    }
}
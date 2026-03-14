package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.WorkLogCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkLogCreateWithFilesRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkLogStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkLogUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.WorkLogDetailResponse;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.entity.WorkLog;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.repository.WorkLogRepository;
import com.lawbackend2.lawbackend2.service.FileService;
import com.lawbackend2.lawbackend2.service.NotificationService;
import com.lawbackend2.lawbackend2.service.WorkLogService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkLogServiceImpl implements WorkLogService {

    private final WorkLogRepository workLogRepository;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public WorkLogServiceImpl(WorkLogRepository workLogRepository, 
                            FileService fileService,
                            UserRepository userRepository,
                            NotificationService notificationService) {
        this.workLogRepository = workLogRepository;
        this.fileService = fileService;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Long createWorkLog(WorkLogCreateRequest request) {
        WorkLog workLog = new WorkLog();
        BeanUtils.copyProperties(request, workLog);
        workLog.setStatus("ACTIVE");
        
        Long currentUserId = SecurityUtil.getCurrentUserId();
        workLog.setCreateUserId(currentUserId);
        
        WorkLog saved = workLogRepository.save(workLog);

        User user = userRepository.findById(currentUserId).orElse(null);
        String realName = user != null ? user.getRealName() : "未知用户";
        String content = String.format("%s 创建了工作日志：%s", realName, request.getWorkType());
        notificationService.sendNotificationToAdminAndSuperAdmin(
                "工作日志通知",
                content,
                "WORK_LOG",
                saved.getId(),
                "WorkLog",
                currentUserId,
                realName
        );

        return saved.getId();
    }

    @Override
    public Map<String, Object> createWorkLogWithFiles(WorkLogCreateWithFilesRequest request, List<MultipartFile> files) {
        WorkLog workLog = new WorkLog();
        BeanUtils.copyProperties(request, workLog);
        workLog.setStatus("ACTIVE");
        
        Long currentUserId = SecurityUtil.getCurrentUserId();
        workLog.setCreateUserId(currentUserId);
        
        WorkLog saved = workLogRepository.save(workLog);
        Long logId = saved.getId();

        List<FileRecord> uploadedFiles = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                FileRecord fileRecord = fileService.uploadFile(file, "WORK_LOG", logId.toString());
                uploadedFiles.add(fileRecord);
            }
        }

        if (!uploadedFiles.isEmpty()) {
            String attachmentIds = uploadedFiles.stream()
                    .map(FileRecord::getId)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            workLog.setAttachmentIds(attachmentIds);
            workLogRepository.save(workLog);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("logId", logId);
        result.put("files", uploadedFiles);

        User user = userRepository.findById(currentUserId).orElse(null);
        String realName = user != null ? user.getRealName() : "未知用户";
        String content = String.format("%s 创建了工作日志：%s", realName, request.getWorkType());
        notificationService.sendNotificationToAdminAndSuperAdmin(
                "工作日志通知",
                content,
                "WORK_LOG",
                logId,
                "WorkLog",
                currentUserId,
                realName
        );

        return result;
    }

    @Override
    public PageResult<WorkLog> getWorkLogList(Integer pageNum, Integer pageSize, Long caseId, String workType, LocalDate startDate, LocalDate endDate, Long createUserId, String status) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "workDate"));
        Page<WorkLog> page = workLogRepository.findByConditions(caseId, workType, startDate, endDate, createUserId, status, pageable);
        PageResult<WorkLog> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public WorkLog getWorkLogDetail(Long logId) {
        return workLogRepository.findById(logId)
                .orElseThrow(() -> new BusinessException("工作日志不存在"));
    }

    @Override
    public void updateWorkLog(Long logId, WorkLogUpdateRequest request) {
        WorkLog workLog = getWorkLogDetail(logId);
        BeanUtils.copyProperties(request, workLog, "id", "status");
        workLogRepository.save(workLog);
    }

    @Override
    public void updateWorkLogStatus(Long logId, WorkLogStatusRequest request) {
        WorkLog workLog = getWorkLogDetail(logId);
        workLog.setStatus(request.getStatus());
        workLogRepository.save(workLog);
    }

    @Override
    public void deleteWorkLog(Long logId) {
        WorkLog workLog = getWorkLogDetail(logId);
        workLogRepository.delete(workLog);
    }

    @Override
    public com.lawbackend2.lawbackend2.dto.response.WorkLogDetailResponse getWorkLogDetailWithFiles(Long logId) {
        WorkLog workLog = getWorkLogDetail(logId);
        WorkLogDetailResponse response = new WorkLogDetailResponse();
        BeanUtils.copyProperties(workLog, response);
        
        if (workLog.getAttachmentIds() != null && !workLog.getAttachmentIds().isEmpty()) {
            List<Long> fileIds = Arrays.stream(workLog.getAttachmentIds().split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<FileRecord> fileRecords = new ArrayList<>();
            for (Long fileId : fileIds) {
                fileRecords.add(fileService.getFileInfo(fileId));
            }
            response.setAttachments(fileRecords);
        }
        
        return response;
    }

    @Override
    public java.util.Map<String, Object> updateWorkLogWithFiles(Long logId, com.lawbackend2.lawbackend2.dto.request.WorkLogUpdateRequest request, java.util.List<org.springframework.web.multipart.MultipartFile> files) {
        WorkLog workLog = getWorkLogDetail(logId);
        BeanUtils.copyProperties(request, workLog, "id", "status");
        
        List<FileRecord> uploadedFiles = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                FileRecord fileRecord = fileService.uploadFile(file, "WORK_LOG", logId.toString());
                uploadedFiles.add(fileRecord);
            }
        }
        
        if (!uploadedFiles.isEmpty()) {
            String existingAttachmentIds = workLog.getAttachmentIds() != null ? workLog.getAttachmentIds() : "";
            String newAttachmentIds = uploadedFiles.stream()
                    .map(FileRecord::getId)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            
            String attachmentIds = existingAttachmentIds.isEmpty() ? newAttachmentIds : existingAttachmentIds + "," + newAttachmentIds;
            workLog.setAttachmentIds(attachmentIds);
        }
        
        WorkLog updated = workLogRepository.save(workLog);
        
        Map<String, Object> result = new HashMap<>();
        result.put("logId", updated.getId());
        result.put("files", uploadedFiles);
        
        return result;
    }

    @Override
    public void deleteWorkLogWithFiles(Long logId) {
        WorkLog workLog = getWorkLogDetail(logId);
        
        if (workLog.getAttachmentIds() != null && !workLog.getAttachmentIds().isEmpty()) {
            List<Long> fileIds = Arrays.stream(workLog.getAttachmentIds().split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            for (Long fileId : fileIds) {
                fileService.deleteFile(fileId);
            }
        }
        
        workLogRepository.delete(workLog);
    }
}

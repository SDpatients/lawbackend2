package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.FileRecordInfo;
import com.lawbackend2.lawbackend2.entity.CaseTask;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.exception.CaseTaskNotFoundException;
import com.lawbackend2.lawbackend2.repository.CaseTaskRepository;
import com.lawbackend2.lawbackend2.repository.FileRecordRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.CaseTaskFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CaseTaskFileServiceImpl implements CaseTaskFileService {

    private final CaseTaskRepository caseTaskRepository;
    private final FileRecordRepository fileRecordRepository;
    private final UserRepository userRepository;

    @Value("${file.upload.path:D:\\law-upload}")
    private String uploadPath;

    public CaseTaskFileServiceImpl(CaseTaskRepository caseTaskRepository,
                                   FileRecordRepository fileRecordRepository,
                                   UserRepository userRepository) {
        this.caseTaskRepository = caseTaskRepository;
        this.fileRecordRepository = fileRecordRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileRecord uploadFile(Long taskId, MultipartFile file, String description, Long userId) {
        log.info("上传任务文件, taskId: {}, fileName: {}, userId: {}", taskId, file.getOriginalFilename(), userId);

        CaseTask task = caseTaskRepository.findById(taskId)
                .orElseThrow(() -> new CaseTaskNotFoundException(taskId));

        if (file.isEmpty()) {
            throw new com.lawbackend2.lawbackend2.exception.BusinessException("文件不能为空");
        }

        long fileSize = file.getSize();
        if (fileSize > 50 * 1024 * 1024) {
            throw new com.lawbackend2.lawbackend2.exception.BusinessException("文件大小不能超过50MB");
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);

        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String modulePath = "CASE_TASK";
        String datePath = Paths.get(uploadPath, modulePath, dateStr).toString();

        try {
            Files.createDirectories(Paths.get(datePath));

            String uuid = UUID.randomUUID().toString();
            String storedFileName = uuid + "_" + originalFileName;
            Path filePath = Paths.get(datePath, storedFileName);

            file.transferTo(filePath.toFile());

            LocalDateTime now = LocalDateTime.now();
            FileRecord fileRecord = new FileRecord();
            fileRecord.setOriginalFileName(originalFileName);
            fileRecord.setStoredFileName(storedFileName);
            fileRecord.setFilePath(filePath.toString());
            fileRecord.setFileSize(fileSize);
            fileRecord.setFileExtension(fileExtension);
            fileRecord.setMimeType(file.getContentType());
            fileRecord.setBizType("CASE_TASK");
            fileRecord.setBizId(taskId.toString());
            fileRecord.setUploadTime(now);
            fileRecord.setUploadUserId(userId);
            fileRecord.setCreateUserId(userId);
            fileRecord.setUpdateUserId(userId);
            fileRecord.setFileStatus(1);
            fileRecord.setStatus("ACTIVE");
            fileRecord.setCreateTime(now);
            fileRecord.setUpdateTime(now);

            FileRecord savedFile = fileRecordRepository.save(fileRecord);
            log.info("文件上传成功, fileId: {}", savedFile.getId());
            return savedFile;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new com.lawbackend2.lawbackend2.exception.BusinessException("文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public FileRecord uploadFileForSubmission(Long submissionId, MultipartFile file, String description, Long userId) {
        return uploadFileForSubmission(submissionId, file, description, userId, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileRecord uploadFileForSubmission(Long submissionId, MultipartFile file, String description, Long userId, Integer sortOrder) {
        log.info("上传任务提交文件, submissionId: {}, fileName: {}, userId: {}, sortOrder: {}", submissionId, file.getOriginalFilename(), userId, sortOrder);

        if (file.isEmpty()) {
            throw new com.lawbackend2.lawbackend2.exception.BusinessException("文件不能为空");
        }

        long fileSize = file.getSize();
        if (fileSize > 50 * 1024 * 1024) {
            throw new com.lawbackend2.lawbackend2.exception.BusinessException("文件大小不能超过50MB");
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);

        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String modulePath = "CASE_TASK_SUBMISSION";
        String datePath = Paths.get(uploadPath, modulePath, dateStr).toString();

        try {
            Files.createDirectories(Paths.get(datePath));

            String uuid = UUID.randomUUID().toString();
            String storedFileName = uuid + "_" + originalFileName;
            Path filePath = Paths.get(datePath, storedFileName);

            file.transferTo(filePath.toFile());

            LocalDateTime now = LocalDateTime.now();
            FileRecord fileRecord = new FileRecord();
            fileRecord.setOriginalFileName(originalFileName);
            fileRecord.setStoredFileName(storedFileName);
            fileRecord.setFilePath(filePath.toString());
            fileRecord.setFileSize(fileSize);
            fileRecord.setFileExtension(fileExtension);
            fileRecord.setMimeType(file.getContentType());
            fileRecord.setBizType("CASE_TASK_SUBMISSION");
            fileRecord.setBizId(submissionId.toString());
            fileRecord.setUploadTime(now);
            fileRecord.setUploadUserId(userId);
            fileRecord.setCreateUserId(userId);
            fileRecord.setUpdateUserId(userId);
            fileRecord.setFileStatus(1);
            fileRecord.setStatus("ACTIVE");
            fileRecord.setDescription(description);
            fileRecord.setCreateTime(now);
            fileRecord.setUpdateTime(now);

            if (sortOrder != null) {
                fileRecord.setSortOrder(sortOrder);
            } else {
                Page<FileRecord> existingFiles = fileRecordRepository.findByConditions(
                        "CASE_TASK_SUBMISSION",
                        submissionId.toString(),
                        "ACTIVE",
                        org.springframework.data.domain.Pageable.unpaged()
                );
                Integer maxSortOrder = existingFiles.getContent().stream()
                        .map(FileRecord::getSortOrder)
                        .filter(order -> order != null)
                        .max(Integer::compare)
                        .orElse(0);
                fileRecord.setSortOrder(maxSortOrder + 1);
            }

            FileRecord savedFile = fileRecordRepository.save(fileRecord);
            log.info("任务提交文件上传成功, fileId: {}", savedFile.getId());
            return savedFile;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new com.lawbackend2.lawbackend2.exception.BusinessException("文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public List<FileRecordInfo> getFilesByTaskId(Long taskId) {
        log.debug("查询任务文件列表, taskId: {}", taskId);

        CaseTask task = caseTaskRepository.findById(taskId)
                .orElseThrow(() -> new CaseTaskNotFoundException(taskId));

        Page<FileRecord> page = fileRecordRepository.findByConditions("CASE_TASK", taskId.toString(), "ACTIVE", Pageable.unpaged());
        List<FileRecord> files = page.getContent();

        List<Long> userIds = files.stream()
                .map(FileRecord::getUploadUserId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> userIdToNameMap;
        if (!userIds.isEmpty()) {
            List<User> users = userRepository.findAllById(userIds);
            userIdToNameMap = users.stream()
                    .collect(Collectors.toMap(User::getId, User::getRealName));
        } else {
            userIdToNameMap = Map.of();
        }

        final Map<Long, String> finalUserIdToNameMap = userIdToNameMap;
        return files.stream().map(file -> {
            FileRecordInfo info = new FileRecordInfo();
            info.setId(file.getId());
            info.setOriginalFileName(file.getOriginalFileName());
            info.setFilePath(file.getFilePath());
            info.setFileSize(file.getFileSize());
            info.setUploadTime(file.getUploadTime());
            info.setUploadUserName(finalUserIdToNameMap.getOrDefault(file.getUploadUserId(), ""));
            return info;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long taskId, Long fileId, Long userId) {
        log.info("删除任务文件, taskId: {}, fileId: {}, userId: {}", taskId, fileId, userId);

        CaseTask task = caseTaskRepository.findById(taskId)
                .orElseThrow(() -> new CaseTaskNotFoundException(taskId));

        FileRecord file = fileRecordRepository.findById(fileId)
                .orElseThrow(() -> new com.lawbackend2.lawbackend2.exception.BusinessException("文件不存在"));

        if (!"CASE_TASK".equals(file.getBizType()) || !taskId.toString().equals(file.getBizId())) {
            throw new com.lawbackend2.lawbackend2.exception.BusinessException("文件不属于该任务");
        }

        file.setIsDeleted(true);
        file.setDeleteTime(LocalDateTime.now());
        file.setDeleteUserId(userId);
        fileRecordRepository.save(file);

        log.info("文件删除成功, fileId: {}", fileId);
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }
}

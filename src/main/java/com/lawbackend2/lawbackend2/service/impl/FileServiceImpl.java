package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.FileRecordRepository;
import com.lawbackend2.lawbackend2.service.FileService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class FileServiceImpl implements FileService {

    private final FileRecordRepository fileRecordRepository;

    @Value("${file.upload.path:C:\\law-upload}")
    private String uploadPath;

    public FileServiceImpl(FileRecordRepository fileRecordRepository) {
        this.fileRecordRepository = fileRecordRepository;
    }

    @Override
    public FileRecord uploadFile(MultipartFile file, String bizType, String bizId) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        long fileSize = file.getSize();
        if (fileSize > 50 * 1024 * 1024) {
            throw new BusinessException("文件大小不能超过50MB");
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);

        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String modulePath = bizType != null ? bizType : "common";
        String datePath = Paths.get(uploadPath, modulePath, dateStr).toString();

        try {
            Files.createDirectories(Paths.get(datePath));

            String storedFileName = generateUniqueFileName(datePath, originalFileName);
            Path filePath = Paths.get(datePath, storedFileName);

            file.transferTo(filePath.toFile());

            Long currentUserId = SecurityUtil.getCurrentUserId();
            LocalDateTime now = LocalDateTime.now();

            FileRecord fileRecord = new FileRecord();
            fileRecord.setOriginalFileName(originalFileName);
            fileRecord.setStoredFileName(storedFileName);
            fileRecord.setFilePath(filePath.toString());
            fileRecord.setFileSize(fileSize);
            fileRecord.setFileExtension(fileExtension);
            fileRecord.setMimeType(file.getContentType());
            fileRecord.setBizType(bizType);
            fileRecord.setBizId(bizId);
            fileRecord.setUploadTime(now);
            fileRecord.setUploadUserId(currentUserId);
            fileRecord.setFileStatus(1);
            fileRecord.setStatus("ACTIVE");
            fileRecord.setCreateUserId(currentUserId);

            return fileRecordRepository.save(fileRecord);
        } catch (IOException e) {
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }

    private String generateUniqueFileName(String directoryPath, String originalFileName) {
        String fileNameWithoutExt = originalFileName;
        String fileExtension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < originalFileName.length() - 1) {
            fileNameWithoutExt = originalFileName.substring(0, dotIndex);
            fileExtension = originalFileName.substring(dotIndex);
        }

        String newFileName = originalFileName;
        int counter = 1;
        File file = new File(directoryPath, newFileName);

        while (file.exists()) {
            counter++;
            newFileName = fileNameWithoutExt + "(" + counter + ")" + fileExtension;
            file = new File(directoryPath, newFileName);
        }

        return newFileName;
    }

    @Override
    public FileRecord getFileInfo(Long fileId) {
        return fileRecordRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException("文件不存在"));
    }

    @Override
    public PageResult<FileRecord> getFileList(Integer pageNum, Integer pageSize, String bizType, String bizId, String status) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "uploadTime"));
        Page<FileRecord> page = fileRecordRepository.findByConditions(bizType, bizId, status, pageable);

        PageResult<FileRecord> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public void deleteFile(Long fileId) {
        FileRecord fileRecord = getFileInfo(fileId);

        try {
            Path filePath = Paths.get(fileRecord.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            fileRecordRepository.deleteById(fileId);
        } catch (IOException e) {
            throw new BusinessException("文件删除失败：" + e.getMessage());
        }
    }

    @Override
    public void deleteFiles(List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            throw new BusinessException("文件ID列表不能为空");
        }

        for (Long fileId : fileIds) {
            try {
                deleteFile(fileId);
            } catch (Exception e) {
                throw new BusinessException("批量删除文件失败，文件ID: " + fileId + "，错误: " + e.getMessage());
            }
        }
    }

    @Override
    public FileRecord renameFile(Long fileId, String newFileName) {
        if (newFileName == null || newFileName.trim().isEmpty()) {
            throw new BusinessException("新文件名不能为空");
        }

        FileRecord fileRecord = getFileInfo(fileId);

        String oldFilePath = fileRecord.getFilePath();
        Path parentPath = Paths.get(oldFilePath).getParent();
        String directoryPath = parentPath.toString();
        String newFileExtension = getFileExtension(newFileName);

        String newStoredFileName = generateUniqueFileName(directoryPath, newFileName);
        Path newFilePath = parentPath.resolve(newStoredFileName);

        try {
            Files.move(Paths.get(oldFilePath), newFilePath);

            fileRecord.setOriginalFileName(newFileName);
            fileRecord.setStoredFileName(newStoredFileName);
            fileRecord.setFilePath(newFilePath.toString());
            fileRecord.setFileExtension(newFileExtension);

            return fileRecordRepository.save(fileRecord);
        } catch (IOException e) {
            throw new BusinessException("文件重命名失败：" + e.getMessage());
        }
    }

    @Override
    public FileRecord updateFileStatus(Long fileId, String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new BusinessException("状态不能为空");
        }

        FileRecord fileRecord = getFileInfo(fileId);
        fileRecord.setStatus(status);
        fileRecord.setUpdateTime(LocalDateTime.now());

        return fileRecordRepository.save(fileRecord);
    }

    @Override
    public void updateFilesStatus(List<Long> fileIds, String status) {
        if (fileIds == null || fileIds.isEmpty()) {
            throw new BusinessException("文件ID列表不能为空");
        }

        if (status == null || status.trim().isEmpty()) {
            throw new BusinessException("状态不能为空");
        }

        for (Long fileId : fileIds) {
            try {
                FileRecord fileRecord = getFileInfo(fileId);
                fileRecord.setStatus(status);
                fileRecord.setUpdateTime(LocalDateTime.now());
                fileRecordRepository.save(fileRecord);
            } catch (Exception e) {
                throw new BusinessException("批量更新文件状态失败，文件ID: " + fileId + "，错误: " + e.getMessage());
            }
        }
    }

    @Override
    public Map<String, Object> getFileStatistics(String bizType, String bizId) {
        Map<String, Object> statistics = new HashMap<>();

        List<FileRecord> allFiles;
        if (bizType != null || bizId != null) {
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
            Page<FileRecord> page = fileRecordRepository.findByConditions(bizType, bizId, null, pageable);
            allFiles = page.getContent();
        } else {
            allFiles = fileRecordRepository.findAll();
        }

        long totalFiles = allFiles.size();
        long totalSize = allFiles.stream().mapToLong(f -> f.getFileSize() != null ? f.getFileSize() : 0).sum();

        Map<String, Long> statusCount = new HashMap<>();
        Map<String, Long> extensionCount = new HashMap<>();

        for (FileRecord file : allFiles) {
            String status = file.getStatus() != null ? file.getStatus() : "UNKNOWN";
            statusCount.put(status, statusCount.getOrDefault(status, 0L) + 1);

            String extension = file.getFileExtension() != null ? file.getFileExtension() : "unknown";
            extensionCount.put(extension, extensionCount.getOrDefault(extension, 0L) + 1);
        }

        statistics.put("totalFiles", totalFiles);
        statistics.put("totalSize", totalSize);
        statistics.put("totalSizeMB", totalSize / (1024.0 * 1024.0));
        statistics.put("statusCount", statusCount);
        statistics.put("extensionCount", extensionCount);

        return statistics;
    }

    @Override
    public FileRecord previewFile(Long fileId) {
        FileRecord fileRecord = getFileInfo(fileId);

        String mimeType = fileRecord.getMimeType();
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        if (!mimeType.startsWith("image/") && !mimeType.startsWith("text/") && 
            !mimeType.equals("application/pdf")) {
            throw new BusinessException("该文件类型不支持预览");
        }

        return fileRecord;
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    @Override
    public List<FileRecord> uploadCaseTaskFiles(List<MultipartFile> files, Long caseId, Integer stageNum, String taskCode) {
        if (files == null || files.isEmpty()) {
            throw new BusinessException("文件列表不能为空");
        }

        if (caseId == null || stageNum == null || taskCode == null || taskCode.trim().isEmpty()) {
            throw new BusinessException("案件ID、阶段号和任务编码不能为空");
        }

        String bizId = caseId + "_" + stageNum + "_" + taskCode;
        List<FileRecord> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            FileRecord fileRecord = uploadFile(file, "CASE_TASK", bizId);
            uploadedFiles.add(fileRecord);
        }

        return uploadedFiles;
    }

    @Override
    public List<FileRecord> getCaseTaskFiles(Long caseId, Integer stageNum, String taskCode) {
        if (caseId == null || stageNum == null || taskCode == null || taskCode.trim().isEmpty()) {
            throw new BusinessException("案件ID、阶段号和任务编码不能为空");
        }

        String bizId = caseId + "_" + stageNum + "_" + taskCode;
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "uploadTime"));
        Page<FileRecord> page = fileRecordRepository.findByConditions("CASE_TASK", bizId, null, pageable);

        return page.getContent();
    }

    @Override
    public void deleteCaseTaskFiles(List<Long> fileIds, Long caseId, Integer stageNum, String taskCode) {
        if (fileIds == null || fileIds.isEmpty()) {
            throw new BusinessException("文件ID列表不能为空");
        }

        if (caseId == null || stageNum == null || taskCode == null || taskCode.trim().isEmpty()) {
            throw new BusinessException("案件ID、阶段号和任务编码不能为空");
        }

        String bizId = caseId + "_" + stageNum + "_" + taskCode;

        for (Long fileId : fileIds) {
            FileRecord fileRecord = getFileInfo(fileId);
            if (!"CASE_TASK".equals(fileRecord.getBizType()) || !bizId.equals(fileRecord.getBizId())) {
                throw new BusinessException("文件不属于指定的案件任务");
            }
            deleteFile(fileId);
        }
    }

    @Override
    public List<FileRecord> getAllFilesByBizTypeAndBizId(String bizType, String bizId) {
        if (bizType == null || bizType.trim().isEmpty()) {
            throw new BusinessException("业务类型不能为空");
        }
        if (bizId == null || bizId.trim().isEmpty()) {
            throw new BusinessException("业务ID不能为空");
        }

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "uploadTime"));
        Page<FileRecord> page = fileRecordRepository.findByConditions(bizType, bizId, null, pageable);
        return page.getContent();
    }
}
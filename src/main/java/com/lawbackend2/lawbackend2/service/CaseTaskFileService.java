package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.FileRecordInfo;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CaseTaskFileService {

    FileRecord uploadFile(Long taskId, MultipartFile file, String description, Long userId);

    FileRecord uploadFileForSubmission(Long submissionId, MultipartFile file, String description, Long userId);

    FileRecord uploadFileForSubmission(Long submissionId, MultipartFile file, String description, Long userId, Integer sortOrder);

    List<FileRecordInfo> getFilesByTaskId(Long taskId);

    void deleteFile(Long taskId, Long fileId, Long userId);
}

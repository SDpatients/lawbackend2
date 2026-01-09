package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileService {

    FileRecord uploadFile(MultipartFile file, String bizType, Long bizId);

    FileRecord getFileInfo(Long fileId);

    PageResult<FileRecord> getFileList(Integer pageNum, Integer pageSize, String bizType, Long bizId, String status);

    void deleteFile(Long fileId);

    void deleteFiles(List<Long> fileIds);

    FileRecord renameFile(Long fileId, String newFileName);

    FileRecord updateFileStatus(Long fileId, String status);

    void updateFilesStatus(List<Long> fileIds, String status);

    Map<String, Object> getFileStatistics(String bizType, Long bizId);

    FileRecord previewFile(Long fileId);
}

package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileService {

    FileRecord uploadFile(MultipartFile file, String bizType, String bizId);

    FileRecord getFileInfo(Long fileId);

    PageResult<FileRecord> getFileList(Integer pageNum, Integer pageSize, String bizType, String bizId, String status);

    void deleteFile(Long fileId);

    void deleteFiles(List<Long> fileIds);

    FileRecord renameFile(Long fileId, String newFileName);

    FileRecord updateFileStatus(Long fileId, String status);

    void updateFilesStatus(List<Long> fileIds, String status);

    Map<String, Object> getFileStatistics(String bizType, String bizId);

    FileRecord previewFile(Long fileId);

    List<FileRecord> uploadCaseTaskFiles(List<MultipartFile> files, Long caseId, Integer stageNum, String taskCode);

    List<FileRecord> getCaseTaskFiles(Long caseId, Integer stageNum, String taskCode);

    void deleteCaseTaskFiles(List<Long> fileIds, Long caseId, Integer stageNum, String taskCode);

    List<FileRecord> getAllFilesByBizTypeAndBizId(String bizType, String bizId);
}

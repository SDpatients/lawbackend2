package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "文件管理")
@RestController
@RequestMapping("/file")
@Validated
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @Operation(summary = "文件上传")
    @PostMapping("/upload")
    public Result<FileRecord> uploadFile(
            @Parameter(description = "文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "业务类型") @RequestParam("bizType") String bizType,
            @Parameter(description = "业务ID") @RequestParam("bizId") String bizId) {

        FileRecord fileRecord = fileService.uploadFile(file, bizType, bizId);
        return Result.success(fileRecord);
    }

    @Operation(summary = "文件下载")
    @GetMapping("/download/{fileId}")
    public void downloadFile(
            @Parameter(description = "文件ID") @PathVariable Long fileId,
            HttpServletResponse response) throws IOException {

        FileRecord fileRecord = fileService.getFileInfo(fileId);

        java.io.File file = new java.io.File(fileRecord.getFilePath());
        if (!file.exists()) {
            throw new RuntimeException("文件不存在");
        }

        Resource resource = new org.springframework.core.io.FileSystemResource(file);

        String contentType = fileRecord.getMimeType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        response.setContentType(contentType);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileRecord.getOriginalFileName() + "\"");
        response.setContentLengthLong(file.length());

        Files.copy(file.toPath(), response.getOutputStream());
    }

    @Operation(summary = "获取文件信息")
    @GetMapping("/{fileId}")
    public Result<FileRecord> getFileInfo(@Parameter(description = "文件ID") @PathVariable Long fileId) {
        FileRecord fileRecord = fileService.getFileInfo(fileId);
        return Result.success(fileRecord);
    }

    @Operation(summary = "文件列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<FileRecord>> getFileList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "业务类型") @RequestParam(required = false) String bizType,
            @Parameter(description = "业务ID") @RequestParam(required = false) String bizId,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {

        PageResult<FileRecord> result = fileService.getFileList(pageNum, pageSize, bizType, bizId, status);
        return Result.success(result);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{fileId}")
    public Result<Void> deleteFile(@Parameter(description = "文件ID") @PathVariable Long fileId) {
        fileService.deleteFile(fileId);
        return Result.success();
    }

    @Operation(summary = "批量删除文件")
    @DeleteMapping("/batch")
    public Result<Void> deleteFiles(@Parameter(description = "文件ID列表") @RequestBody List<Long> fileIds) {
        fileService.deleteFiles(fileIds);
        return Result.success();
    }

    @Operation(summary = "文件重命名")
    @PutMapping("/{fileId}/rename")
    public Result<FileRecord> renameFile(
            @Parameter(description = "文件ID") @PathVariable Long fileId,
            @Parameter(description = "新文件名") @RequestParam("newFileName") String newFileName) {
        FileRecord fileRecord = fileService.renameFile(fileId, newFileName);
        return Result.success(fileRecord);
    }

    @Operation(summary = "更新文件状态")
    @PutMapping("/{fileId}/status")
    public Result<FileRecord> updateFileStatus(
            @Parameter(description = "文件ID") @PathVariable Long fileId,
            @Parameter(description = "状态") @RequestParam("status") String status) {
        FileRecord fileRecord = fileService.updateFileStatus(fileId, status);
        return Result.success(fileRecord);
    }

    @Operation(summary = "批量更新文件状态")
    @PutMapping("/batch/status")
    public Result<Void> updateFilesStatus(
            @Parameter(description = "文件ID列表") @RequestBody List<Long> fileIds,
            @Parameter(description = "状态") @RequestParam("status") String status) {
        fileService.updateFilesStatus(fileIds, status);
        return Result.success();
    }

    @Operation(summary = "获取文件统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getFileStatistics(
            @Parameter(description = "业务类型") @RequestParam(required = false) String bizType,
            @Parameter(description = "业务ID") @RequestParam(required = false) String bizId) {
        Map<String, Object> statistics = fileService.getFileStatistics(bizType, bizId);
        return Result.success(statistics);
    }

    @Operation(summary = "文件预览")
    @GetMapping("/preview/{fileId}")
    public ResponseEntity<Resource> previewFile(
            @Parameter(description = "文件ID") @PathVariable Long fileId) throws IOException {
        FileRecord fileRecord = fileService.previewFile(fileId);

        java.io.File file = new java.io.File(fileRecord.getFilePath());
        if (!file.exists()) {
            throw new RuntimeException("文件不存在");
        }

        Resource resource = new org.springframework.core.io.FileSystemResource(file);

        String contentType = fileRecord.getMimeType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileRecord.getOriginalFileName() + "\"")
                .body(resource);
    }

    @Operation(summary = "案件任务文件批量上传")
    @PostMapping("/case-task/upload")
    public Result<List<FileRecord>> uploadCaseTaskFiles(
            @Parameter(description = "文件列表") @RequestParam("files") List<MultipartFile> files,
            @Parameter(description = "案件ID") @RequestParam("caseId") Long caseId,
            @Parameter(description = "阶段号") @RequestParam("stageNum") Integer stageNum,
            @Parameter(description = "任务编码") @RequestParam("taskCode") String taskCode) {

        List<FileRecord> fileRecords = fileService.uploadCaseTaskFiles(files, caseId, stageNum, taskCode);
        return Result.success(fileRecords);
    }

    @Operation(summary = "获取案件任务文件列表")
    @GetMapping("/case-task/files")
    public Result<List<FileRecord>> getCaseTaskFiles(
            @Parameter(description = "案件ID") @RequestParam("caseId") Long caseId,
            @Parameter(description = "阶段号") @RequestParam("stageNum") Integer stageNum,
            @Parameter(description = "任务编码") @RequestParam("taskCode") String taskCode) {

        List<FileRecord> fileRecords = fileService.getCaseTaskFiles(caseId, stageNum, taskCode);
        return Result.success(fileRecords);
    }

    @Operation(summary = "删除案件任务文件")
    @DeleteMapping("/case-task/files")
    public Result<Void> deleteCaseTaskFiles(
            @Parameter(description = "文件ID列表") @RequestBody List<Long> fileIds,
            @Parameter(description = "案件ID") @RequestParam("caseId") Long caseId,
            @Parameter(description = "阶段号") @RequestParam("stageNum") Integer stageNum,
            @Parameter(description = "任务编码") @RequestParam("taskCode") String taskCode) {

        fileService.deleteCaseTaskFiles(fileIds, caseId, stageNum, taskCode);
        return Result.success();
    }

    @Operation(summary = "根据业务类型和业务ID查询所有文件列表")
    @GetMapping("/all")
    public Result<List<FileRecord>> getAllFilesByBizTypeAndBizId(
            @Parameter(description = "业务类型") @RequestParam("bizType") String bizType,
            @Parameter(description = "业务ID") @RequestParam("bizId") String bizId) {

        List<FileRecord> fileRecords = fileService.getAllFilesByBizTypeAndBizId(bizType, bizId);
        return Result.success(fileRecords);
    }

    @Operation(summary = "根据文件路径下载文件（兼容旧数据）")
    @GetMapping("/download-by-path")
    public void downloadFileByPath(
            @Parameter(description = "文件路径") @RequestParam("filePath") String filePath,
            @Parameter(description = "文件名") @RequestParam(value = "fileName", required = false) String fileName,
            HttpServletResponse response) throws IOException {

        // 处理文件路径
        String targetFilePath = processFilePath(filePath, fileName);

        java.io.File file = new java.io.File(targetFilePath);
        if (!file.exists()) {
            throw new RuntimeException("文件不存在");
        }

        Resource resource = new org.springframework.core.io.FileSystemResource(file);

        String downloadFileName = (fileName != null && !fileName.trim().isEmpty()) ? fileName : file.getName();
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        response.setContentType(contentType);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + downloadFileName + "\"");
        response.setContentLengthLong(file.length());

        Files.copy(file.toPath(), response.getOutputStream());
    }

    @Operation(summary = "根据文件路径预览文件（兼容旧数据）")
    @GetMapping("/preview-by-path")
    public ResponseEntity<Resource> previewFileByPath(
            @Parameter(description = "文件路径") @RequestParam("filePath") String filePath,
            @Parameter(description = "文件名") @RequestParam(value = "fileName", required = false) String fileName) throws IOException {

        // 处理文件路径
        String targetFilePath = processFilePath(filePath, fileName);

        java.io.File file = new java.io.File(targetFilePath);
        if (!file.exists()) {
            throw new RuntimeException("文件不存在");
        }

        Resource resource = new org.springframework.core.io.FileSystemResource(file);

        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        String previewFileName = (fileName != null && !fileName.trim().isEmpty()) ? fileName : file.getName();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + previewFileName + "\"")
                .body(resource);
    }
    
    /**
     * 处理文件路径
     * @param filePath 文件路径参数
     * @param fileName 文件名参数
     * @return 处理后的文件路径
     * @throws IOException IO异常
     */
    private String processFilePath(String filePath, String fileName) throws IOException {
        // 打印调试信息
        log.debug("原始filePath: {}", filePath);
        log.debug("原始fileName: {}", fileName);
        
        // 先对整个filePath进行URL解码
        String decodedFilePath = java.net.URLDecoder.decode(filePath, "UTF-8");
        log.debug("解码后的filePath: {}", decodedFilePath);
        
        // 分割文件路径
        String[] filePaths = decodedFilePath.split(";" );
        log.debug("分割后的文件路径数量: {}", filePaths.length);
        
        // 直接使用最后一个文件路径
        if (filePaths.length > 0) {
            String lastPath = filePaths[filePaths.length - 1];
            log.debug("使用最后一个路径: {}", lastPath);
            return lastPath;
        }
        
        // 如果没有路径，返回原始路径
        log.debug("使用原始路径: {}", decodedFilePath);
        return decodedFilePath;
    }
}

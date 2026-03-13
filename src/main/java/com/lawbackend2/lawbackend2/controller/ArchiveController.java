package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.DataPermission;
import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.ArchiveCategoryResponse;
import com.lawbackend2.lawbackend2.dto.ArchiveRecordResponse;
import com.lawbackend2.lawbackend2.dto.ArchiveUpdateRequest;
import com.lawbackend2.lawbackend2.dto.ArchiveUploadRequest;
import com.lawbackend2.lawbackend2.service.ArchiveService;
import com.lawbackend2.lawbackend2.util.PermissionChecker;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
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

@Slf4j
@Tag(name = "归档管理", description = "案件卷宗归档相关接口")
@RestController
@RequestMapping("/archive")
@Validated
public class ArchiveController {

    private final ArchiveService archiveService;
    private final PermissionChecker permissionChecker;

    @Autowired
    public ArchiveController(ArchiveService archiveService, PermissionChecker permissionChecker) {
        this.archiveService = archiveService;
        this.permissionChecker = permissionChecker;
    }

    @Operation(summary = "获取归档分类树", description = "获取归档分类的完整树形结构")
    @Parameter(name = "status", description = "状态", required = false)
    @GetMapping("/categories")
    public Result<List<ArchiveCategoryResponse>> getCategoryTree(
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        List<ArchiveCategoryResponse> categories = archiveService.getCategoryTree(status);
        return Result.success(categories);
    }

    @Operation(summary = "上传归档文件", description = "上传归档文件到指定案件的指定分类")
    @PostMapping("/{caseId}/upload")
    @DataPermission(moduleType = "archive", permissionType = "upload")
    public Result<ArchiveRecordResponse> uploadArchiveFile(
            @Parameter(description = "案件ID") @PathVariable Long caseId,
            @Parameter(description = "文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "归档分类代码") @RequestParam("categoryCode") String categoryCode,
            @Parameter(description = "文件标题") @RequestParam(required = false) String fileTitle,
            @Parameter(description = "文件描述") @RequestParam(required = false) String fileDescription,
            @Parameter(description = "是否机密") @RequestParam(required = false) Boolean isConfidential,
            @Parameter(description = "访问级别") @RequestParam(required = false) String accessLevel) {

        permissionChecker.checkArchiveEditPermission(caseId);
        Long userId = SecurityUtil.getCurrentUserId();

        ArchiveUploadRequest request = new ArchiveUploadRequest();
        request.setCategoryCode(categoryCode);
        request.setFileTitle(fileTitle);
        request.setFileDescription(fileDescription);
        request.setIsConfidential(isConfidential);
        request.setAccessLevel(accessLevel);

        ArchiveRecordResponse response = archiveService.uploadArchiveFile(caseId, file, request, userId);
        return Result.success("文件上传成功", response);
    }

    @Operation(summary = "获取归档文件列表", description = "获取指定案件和分类的归档文件列表")
    @GetMapping("/{caseId}/files")
    @DataPermission(moduleType = "archive", permissionType = "view")
    public Result<PageResult<ArchiveRecordResponse>> getArchiveFiles(
            @Parameter(description = "案件ID") @PathVariable Long caseId,
            @Parameter(description = "归档分类代码") @RequestParam(required = false) String categoryCode,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "记录状态") @RequestParam(required = false) String status,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {

        permissionChecker.checkArchiveAccessPermission(caseId);
        PageResult<ArchiveRecordResponse> result = archiveService.getArchiveFiles(
                caseId, categoryCode, pageNum, pageSize, status, keyword);
        return Result.success(result);
    }

    @Operation(summary = "获取归档记录详情", description = "根据记录ID获取归档记录详情")
    @GetMapping("/record/{recordId}")
    @DataPermission(moduleType = "archive", permissionType = "view")
    public Result<ArchiveRecordResponse> getArchiveRecord(
            @Parameter(description = "记录ID") @PathVariable Long recordId) {
        ArchiveRecordResponse response = archiveService.getArchiveRecord(recordId);
        return Result.success(response);
    }

    @Operation(summary = "更新归档记录", description = "更新归档记录信息")
    @PutMapping("/record/{recordId}")
    @DataPermission(moduleType = "archive", permissionType = "edit")
    public Result<ArchiveRecordResponse> updateArchiveRecord(
            @Parameter(description = "记录ID") @PathVariable Long recordId,
            @RequestBody ArchiveUpdateRequest request) {

        Long userId = SecurityUtil.getCurrentUserId();
        ArchiveRecordResponse response = archiveService.updateArchiveRecord(recordId, request, userId);
        return Result.success("更新成功", response);
    }

    @Operation(summary = "删除归档文件", description = "删除归档文件(逻辑删除)")
    @DeleteMapping("/record/{recordId}")
    @DataPermission(moduleType = "archive", permissionType = "delete")
    public Result<String> deleteArchiveRecord(
            @Parameter(description = "记录ID") @PathVariable Long recordId) {

        Long userId = SecurityUtil.getCurrentUserId();
        archiveService.deleteArchiveRecord(recordId, userId);
        return Result.success("删除成功");
    }

    @Operation(summary = "批量删除归档文件", description = "批量删除归档文件(逻辑删除)")
    @DeleteMapping("/records/batch")
    @DataPermission(moduleType = "archive", permissionType = "delete")
    public Result<String> deleteArchiveRecords(
            @RequestBody List<Long> recordIds) {

        Long userId = SecurityUtil.getCurrentUserId();
        archiveService.deleteArchiveRecords(recordIds, userId);
        return Result.success("批量删除成功");
    }

    @Operation(summary = "获取归档文件统计", description = "获取指定案件的归档文件统计信息")
    @GetMapping("/{caseId}/statistics")
    @DataPermission(moduleType = "archive", permissionType = "view")
    public Result<Long> getArchiveCount(
            @Parameter(description = "案件ID") @PathVariable Long caseId,
            @Parameter(description = "归档分类代码") @RequestParam(required = false) String categoryCode,
            @Parameter(description = "记录状态") @RequestParam(required = false) String status) {

        permissionChecker.checkArchiveAccessPermission(caseId);
        Long count = archiveService.getArchiveCount(caseId, categoryCode, status);
        return Result.success(count);
    }

    @Operation(summary = "下载归档文件", description = "下载归档文件")
    @GetMapping("/file/{fileId}/download")
    @DataPermission(moduleType = "archive", permissionType = "view")
    public void downloadArchiveFile(
            @Parameter(description = "文件ID") @PathVariable Long fileId,
            HttpServletResponse response) throws IOException {

        var fileRecord = archiveService.getArchiveRecord(fileId);
        java.io.File file = new java.io.File(fileRecord.getFile().getFilePath());

        if (!file.exists()) {
            throw new RuntimeException("文件不存在");
        }

        Resource resource = new org.springframework.core.io.FileSystemResource(file);

        String contentType = fileRecord.getFile().getMimeType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        response.setContentType(contentType);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileRecord.getFile().getOriginalFileName() + "\"");
        response.setContentLengthLong(file.length());

        Files.copy(file.toPath(), response.getOutputStream());
    }

    @Operation(summary = "预览归档文件", description = "预览归档文件")
    @GetMapping("/file/{fileId}/preview")
    @DataPermission(moduleType = "archive", permissionType = "view")
    public ResponseEntity<Resource> previewArchiveFile(
            @Parameter(description = "文件ID") @PathVariable Long fileId) throws IOException {

        var fileRecord = archiveService.getArchiveRecord(fileId);
        java.io.File file = new java.io.File(fileRecord.getFile().getFilePath());

        if (!file.exists()) {
            throw new RuntimeException("文件不存在");
        }

        Resource resource = new org.springframework.core.io.FileSystemResource(file);

        String contentType = fileRecord.getFile().getMimeType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileRecord.getFile().getOriginalFileName() + "\"")
                .body(resource);
    }
}

package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.CurrentUserId;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.LibVersionCreateRequest;
import com.lawbackend2.lawbackend2.dto.response.LibVersionListResponse;
import com.lawbackend2.lawbackend2.dto.response.LibVersionResponse;
import com.lawbackend2.lawbackend2.service.LibDocumentVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文档库版本管理")
@RestController
@RequestMapping("/api/lib")
@RequiredArgsConstructor
public class LibDocumentVersionController {

    private final LibDocumentVersionService versionService;

    @Operation(summary = "上传新版本")
    @PostMapping("/documents/{documentId}/versions")
    public Result<LibVersionResponse> uploadNewVersion(
            @PathVariable Long documentId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "changeSummary", required = false) String changeSummary,
            @RequestParam(value = "isMajor", required = false, defaultValue = "false") Boolean isMajor,
            @CurrentUserId Long userId) {
        LibVersionResponse response = versionService.uploadNewVersion(documentId, file, changeSummary, isMajor, userId);
        return Result.success(response);
    }

    @Operation(summary = "获取文档版本列表")
    @GetMapping("/documents/{documentId}/versions")
    public Result<LibVersionListResponse> getVersionList(@PathVariable Long documentId) {
        LibVersionListResponse response = versionService.getVersionList(documentId);
        return Result.success(response);
    }

    @Operation(summary = "获取指定版本")
    @GetMapping("/documents/{documentId}/versions/{versionNumber}")
    public Result<LibVersionResponse> getVersion(
            @PathVariable Long documentId,
            @PathVariable Integer versionNumber) {
        LibVersionResponse response = versionService.getVersion(documentId, versionNumber);
        return Result.success(response);
    }

    @Operation(summary = "获取最新版本")
    @GetMapping("/documents/{documentId}/versions/latest")
    public Result<LibVersionResponse> getLatestVersion(@PathVariable Long documentId) {
        LibVersionResponse response = versionService.getLatestVersion(documentId);
        return Result.success(response);
    }

    @Operation(summary = "恢复到指定版本")
    @PostMapping("/documents/{documentId}/versions/{versionNumber}/restore")
    public Result<Void> restoreVersion(
            @PathVariable Long documentId,
            @PathVariable Integer versionNumber,
            @CurrentUserId Long userId) {
        versionService.restoreVersion(documentId, versionNumber, userId);
        return Result.success();
    }

    @Operation(summary = "删除版本")
    @DeleteMapping("/versions/{versionId}")
    public Result<Void> deleteVersion(
            @PathVariable Long versionId,
            @CurrentUserId Long userId) {
        versionService.deleteVersion(versionId, userId);
        return Result.success();
    }

    @Operation(summary = "获取下一个版本号")
    @GetMapping("/documents/{documentId}/versions/next-number")
    public Result<Integer> getNextVersionNumber(@PathVariable Long documentId) {
        Integer nextNumber = versionService.getNextVersionNumber(documentId);
        return Result.success(nextNumber);
    }

    @Operation(summary = "获取版本总数")
    @GetMapping("/documents/{documentId}/versions/count")
    public Result<Long> getTotalVersions(@PathVariable Long documentId) {
        Long count = versionService.getTotalVersions(documentId);
        return Result.success(count);
    }
}

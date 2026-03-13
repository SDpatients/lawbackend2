package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.CurrentUserId;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.LibDocumentCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.LibDocumentQueryRequest;
import com.lawbackend2.lawbackend2.dto.request.LibDocumentUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.LibDocumentListResponse;
import com.lawbackend2.lawbackend2.dto.response.LibDocumentResponse;
import com.lawbackend2.lawbackend2.dto.response.LibOfficePreviewResponse;
import com.lawbackend2.lawbackend2.service.LibDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@Tag(name = "文档库文档管理")
@RestController
@RequestMapping("/api/lib/documents")
@RequiredArgsConstructor
public class LibDocumentController {

    private final LibDocumentService documentService;

    @Value("${app.server.url:http://localhost:8080}")
    private String serverUrl;

    @Operation(summary = "创建文档记录")
    @PostMapping
    public Result<LibDocumentResponse> createDocument(@RequestBody LibDocumentCreateRequest request,
                                                       @CurrentUserId Long userId) {
        LibDocumentResponse response = documentService.createDocument(request, userId);
        return Result.success(response);
    }

    @Operation(summary = "上传文档")
    @PostMapping("/upload")
    public Result<LibDocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folderId", required = false) Long folderId,
            @RequestParam(value = "documentName", required = false) String documentName,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "isPublic", required = false, defaultValue = "false") Boolean isPublic,
            @CurrentUserId Long userId) {
        LibDocumentResponse response = documentService.uploadDocument(file, folderId, documentName, description, tags, isPublic, userId);
        return Result.success(response);
    }

    @Operation(summary = "获取文档详情")
    @GetMapping("/{id}")
    public Result<LibDocumentResponse> getDocumentById(
            @PathVariable Long id,
            @CurrentUserId Long userId) {
        LibDocumentResponse response = documentService.getDocumentById(id, userId);
        return Result.success(response);
    }

    @Operation(summary = "根据编码获取文档")
    @GetMapping("/code/{code}")
    public Result<LibDocumentResponse> getDocumentByCode(
            @PathVariable String code,
            @CurrentUserId Long userId) {
        LibDocumentResponse response = documentService.getDocumentByCode(code, userId);
        return Result.success(response);
    }

    @Operation(summary = "更新文档")
    @PutMapping("/{id}")
    public Result<LibDocumentResponse> updateDocument(
            @PathVariable Long id,
            @RequestBody LibDocumentUpdateRequest request,
            @CurrentUserId Long userId) {
        LibDocumentResponse response = documentService.updateDocument(id, request, userId);
        return Result.success(response);
    }

    @Operation(summary = "删除文档")
    @DeleteMapping("/{id}")
    public Result<Void> deleteDocument(
            @PathVariable Long id,
            @CurrentUserId Long userId) {
        documentService.deleteDocument(id, userId);
        return Result.success();
    }

    @Operation(summary = "查询文档列表")
    @PostMapping("/list")
    public Result<LibDocumentListResponse> getDocumentList(
            @RequestBody LibDocumentQueryRequest request,
            @CurrentUserId Long userId) {
        LibDocumentListResponse response = documentService.getDocumentList(request, userId);
        return Result.success(response);
    }

    @Operation(summary = "搜索文档")
    @GetMapping("/search")
    public Result<LibDocumentListResponse> searchDocuments(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @CurrentUserId Long userId) {
        LibDocumentListResponse response = documentService.searchDocuments(keyword, page, size, userId);
        return Result.success(response);
    }

    @Operation(summary = "获取文件夹下的文档")
    @GetMapping("/folder/{folderId}")
    public Result<LibDocumentListResponse> getDocumentsByFolder(
            @PathVariable Long folderId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @CurrentUserId Long userId) {
        LibDocumentListResponse response = documentService.getDocumentsByFolder(folderId, page, size, userId);
        return Result.success(response);
    }

    @Operation(summary = "获取我的文档")
    @GetMapping("/my")
    public Result<LibDocumentListResponse> getMyDocuments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
         @CurrentUserId Long userId) {
        LibDocumentListResponse response = documentService.getMyDocuments(userId, page, size);
        return Result.success(response);
    }

    @Operation(summary = "下载文档")
    @GetMapping("/{id}/download")
    public void downloadDocument(
            @PathVariable Long id,
            HttpServletResponse response,
         @CurrentUserId Long userId) {
        documentService.downloadDocument(id, response, userId);
    }

    @Operation(summary = "预览文档")
    @GetMapping("/{id}/preview")
    public void previewDocument(
            @PathVariable Long id,
            HttpServletResponse response,
         @CurrentUserId Long userId) {
        documentService.previewDocument(id, response, userId);
    }

    @Operation(summary = "获取Office预览配置")
    @GetMapping("/{id}/office-config")
    public Result<LibOfficePreviewResponse> getOfficePreviewConfig(
            @PathVariable Long id,
         @CurrentUserId Long userId) {
        LibOfficePreviewResponse response = documentService.getOfficePreviewConfig(id, userId, serverUrl);
        return Result.success(response);
    }

    @Operation(summary = "锁定文档")
    @PostMapping("/{id}/lock")
    public Result<Void> lockDocument(
            @PathVariable Long id,
         @CurrentUserId Long userId) {
        documentService.lockDocument(id, userId);
        return Result.success();
    }

    @Operation(summary = "解锁文档")
    @PostMapping("/{id}/unlock")
    public Result<Void> unlockDocument(
            @PathVariable Long id,
         @CurrentUserId Long userId) {
        documentService.unlockDocument(id, userId);
        return Result.success();
    }

    @Operation(summary = "移动文档")
    @PostMapping("/{id}/move")
    public Result<Void> moveDocument(
            @PathVariable Long id,
            @RequestParam Long targetFolderId,
         @CurrentUserId Long userId) {
        documentService.moveDocument(id, targetFolderId, userId);
        return Result.success();
    }

    @Operation(summary = "复制文档")
    @PostMapping("/{id}/copy")
    public Result<Void> copyDocument(
            @PathVariable Long id,
            @RequestParam(required = false) Long targetFolderId,
         @CurrentUserId Long userId) {
        documentService.copyDocument(id, targetFolderId, userId);
        return Result.success();
    }

    @Operation(summary = "获取最近上传的文档")
    @GetMapping("/recent")
    public Result<LibDocumentListResponse> getRecentDocuments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @CurrentUserId Long userId) {
        LibDocumentListResponse response = documentService.getRecentDocuments(page, size, userId);
        return Result.success(response);
    }

    @Operation(summary = "获取热门文档")
    @GetMapping("/popular")
    public Result<LibDocumentListResponse> getPopularDocuments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "all") String timeRange,
            @CurrentUserId Long userId) {
        LibDocumentListResponse response = documentService.getPopularDocuments(timeRange, page, size, userId);
        return Result.success(response);
    }
}

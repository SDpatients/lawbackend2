package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.CurrentUserId;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.LibPermissionGrantRequest;
import com.lawbackend2.lawbackend2.dto.response.LibPermissionResponse;
import com.lawbackend2.lawbackend2.service.LibDocumentPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "文档库权限管理")
@RestController
@RequestMapping("/api/lib")
@RequiredArgsConstructor
public class LibDocumentPermissionController {

    private final LibDocumentPermissionService permissionService;

    @Operation(summary = "获取所有权限定义")
    @GetMapping("/permissions")
    public Result<List<LibPermissionResponse>> getAllPermissions() {
        List<LibPermissionResponse> response = permissionService.getAllPermissions();
        return Result.success(response);
    }

    @Operation(summary = "根据ID获取权限定义")
    @GetMapping("/permissions/{id}")
    public Result<LibPermissionResponse> getPermissionById(@PathVariable Long id) {
        LibPermissionResponse response = permissionService.getPermissionById(id);
        return Result.success(response);
    }

    @Operation(summary = "根据编码获取权限定义")
    @GetMapping("/permissions/code/{code}")
    public Result<LibPermissionResponse> getPermissionByCode(@PathVariable String code) {
        LibPermissionResponse response = permissionService.getPermissionByCode(code);
        return Result.success(response);
    }

    @Operation(summary = "授予文件夹权限")
    @PostMapping("/folders/{folderId}/permissions")
    public Result<Void> grantFolderPermission(
            @PathVariable Long folderId,
            @RequestBody LibPermissionGrantRequest request,
            @CurrentUserId Long userId) {
        permissionService.grantFolderPermission(folderId, request, userId);
        return Result.success();
    }

    @Operation(summary = "撤销文件夹权限")
    @DeleteMapping("/folders/{folderId}/permissions/{permissionId}")
    public Result<Void> revokeFolderPermission(
            @PathVariable Long folderId,
            @PathVariable Long permissionId,
            @RequestParam String targetType,
            @RequestParam Long targetId,
            @CurrentUserId Long userId) {
        permissionService.revokeFolderPermission(folderId, permissionId, targetType, targetId);
        return Result.success();
    }

    @Operation(summary = "获取文件夹权限列表")
    @GetMapping("/folders/{folderId}/permissions")
    public Result<List<?>> getFolderPermissions(@PathVariable Long folderId) {
        List<?> response = permissionService.getFolderPermissions(folderId);
        return Result.success(response);
    }

    @Operation(summary = "授予文档权限")
    @PostMapping("/documents/{documentId}/permissions")
    public Result<Void> grantDocumentPermission(
            @PathVariable Long documentId,
            @RequestBody LibPermissionGrantRequest request,
            @CurrentUserId Long userId) {
        permissionService.grantDocumentPermission(documentId, request, userId);
        return Result.success();
    }

    @Operation(summary = "撤销文档权限")
    @DeleteMapping("/documents/{documentId}/permissions/{permissionId}")
    public Result<Void> revokeDocumentPermission(
            @PathVariable Long documentId,
            @PathVariable Long permissionId,
            @RequestParam String targetType,
            @RequestParam Long targetId,
            @CurrentUserId Long userId) {
        permissionService.revokeDocumentPermission(documentId, permissionId, targetType, targetId);
        return Result.success();
    }

    @Operation(summary = "获取文档权限列表")
    @GetMapping("/documents/{documentId}/permissions")
    public Result<List<?>> getDocumentPermissions(@PathVariable Long documentId) {
        List<?> response = permissionService.getDocumentPermissions(documentId);
        return Result.success(response);
    }

    @Operation(summary = "获取用户可访问的文件夹ID列表")
    @GetMapping("/permissions/accessible-folders")
    public Result<List<Long>> getAccessibleFolderIds(
            @RequestParam String permissionType,
            @CurrentUserId Long userId) {
        List<Long> response = permissionService.getAccessibleFolderIds(userId, permissionType);
        return Result.success(response);
    }

    @Operation(summary = "获取用户可访问的文档ID列表")
    @GetMapping("/permissions/accessible-documents")
    public Result<List<Long>> getAccessibleDocumentIds(
            @RequestParam String permissionType,
            @CurrentUserId Long userId) {
        List<Long> response = permissionService.getAccessibleDocumentIds(userId, permissionType);
        return Result.success(response);
    }
}

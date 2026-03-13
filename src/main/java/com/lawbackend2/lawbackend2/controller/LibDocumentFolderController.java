package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.CurrentUserId;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.LibFolderCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.LibFolderUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.LibDocumentTreeResponse;
import com.lawbackend2.lawbackend2.dto.response.LibFolderListResponse;
import com.lawbackend2.lawbackend2.dto.response.LibFolderResponse;
import com.lawbackend2.lawbackend2.service.LibDocumentFolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "文档库文件夹管理")
@RestController
@RequestMapping("/api/lib/folders")
@RequiredArgsConstructor
public class LibDocumentFolderController {

    private final LibDocumentFolderService folderService;

    @Operation(summary = "创建文件夹")
    @PostMapping
    public Result<LibFolderResponse> createFolder(@RequestBody LibFolderCreateRequest request,
                                                      @CurrentUserId Long userId) {
        LibFolderResponse response = folderService.createFolder(request, userId);
        return Result.success(response);
    }

    @Operation(summary = "获取文件夹详情")
    @GetMapping("/{id}")
    public Result<LibFolderResponse> getFolderById(@PathVariable Long id,
                                                      @CurrentUserId Long userId) {
        LibFolderResponse response = folderService.getFolderById(id, userId);
        return Result.success(response);
    }

    @Operation(summary = "更新文件夹")
    @PutMapping("/{id}")
    public Result<LibFolderResponse> updateFolder(@PathVariable Long id,
                                                      @RequestBody LibFolderUpdateRequest request,
                                                      @CurrentUserId Long userId) {
        LibFolderResponse response = folderService.updateFolder(id, request, userId);
        return Result.success(response);
    }

    @Operation(summary = "删除文件夹")
    @DeleteMapping("/{id}")
    public Result<Void> deleteFolder(@PathVariable Long id,
                                                   @CurrentUserId Long userId) {
        folderService.deleteFolder(id, userId);
        return Result.success();
    }

    @Operation(summary = "获取文件夹树")
    @GetMapping("/tree")
    public Result<LibDocumentTreeResponse> getFolderTree(
         @CurrentUserId Long userId) {
        LibDocumentTreeResponse response = folderService.getFolderTree(userId);
        return Result.success(response);
    }

    @Operation(summary = "获取子文件夹列表")
    @GetMapping("/{id}/children")
    public Result<List<LibFolderResponse>> getChildFolders(
            @PathVariable Long id,
         @CurrentUserId Long userId) {
        List<LibFolderResponse> response = folderService.getSubFolders(id, userId);
        return Result.success(response);
    }

    @Operation(summary = "获取根文件夹列表")
    @GetMapping("/root")
    public Result<LibFolderListResponse> getRootFolders(
         @CurrentUserId Long userId) {
        LibFolderListResponse response = folderService.getFolderList(null, null, 1, 100, userId);
        return Result.success(response);
    }

    @Operation(summary = "移动文件夹")
    @PostMapping("/{id}/move")
    public Result<Void> moveFolder(@PathVariable Long id,
                                                      @RequestParam(required = false) Long targetFolderId,
                                                   @CurrentUserId Long userId) {
        folderService.moveFolder(id, targetFolderId, userId);
        return Result.success();
    }

    @Operation(summary = "获取文件夹路径")
    @GetMapping("/{id}/path")
    public Result<List<LibDocumentTreeResponse>> getFolderPath(
            @PathVariable Long id,
         @CurrentUserId Long userId) {
        List<LibDocumentTreeResponse> path = folderService.getBreadcrumbs(id, userId);
        return Result.success(path);
    }

    @Operation(summary = "获取所有子孙文件夹ID")
    @GetMapping("/{id}/descendants")
    public Result<List<Long>> getAllDescendantIds(@PathVariable Long id) {
        List<Long> descendantIds = folderService.getAllDescendantIds(id);
        return Result.success(descendantIds);
    }

    @Operation(summary = "按层级获取文件夹")
    @GetMapping("/level/{level}")
    public Result<List<LibFolderResponse>> getFoldersByLevel(
            @PathVariable Integer level,
         @CurrentUserId Long userId) {
        List<LibFolderResponse> folders = folderService.getFoldersByLevel(level, userId);
        return Result.success(folders);
    }

    @Operation(summary = "更新文件夹排序")
    @PutMapping("/{id}/sort")
    public Result<Void> updateSortOrder(
            @PathVariable Long id,
            @RequestParam Integer sortOrder,
         @CurrentUserId Long userId) {
        folderService.updateSortOrder(id, sortOrder, userId);
        return Result.success();
    }
}

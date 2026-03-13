package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.response.LibFavoriteListResponse;
import com.lawbackend2.lawbackend2.dto.response.LibFavoriteResponse;
import com.lawbackend2.lawbackend2.service.LibDocumentFavoriteService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "文档库收藏管理")
@RestController
@RequestMapping("/api/lib/favorites")
@RequiredArgsConstructor
public class LibDocumentFavoriteController {

    private final LibDocumentFavoriteService favoriteService;

    @Operation(summary = "添加收藏")
    @PostMapping("/{documentId}")
    public Result<LibFavoriteResponse> addFavorite(
            @PathVariable Long documentId,
            @RequestParam(required = false) String folderName) {
        Long userId = SecurityUtil.getCurrentUserId();
        LibFavoriteResponse response = favoriteService.addFavorite(documentId, folderName, userId);
        return Result.success(response);
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/{documentId}")
    public Result<Void> removeFavorite(
            @PathVariable Long documentId) {
        Long userId = SecurityUtil.getCurrentUserId();
        favoriteService.removeFavorite(documentId, userId);
        return Result.success();
    }

    @Operation(summary = "获取我的收藏列表")
    @GetMapping
    public Result<LibFavoriteListResponse> getMyFavorites(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = SecurityUtil.getCurrentUserId();
        LibFavoriteListResponse response = favoriteService.getMyFavorites(userId, page, size);
        return Result.success(response);
    }

    @Operation(summary = "获取收藏夹列表")
    @GetMapping("/folders")
    public Result<List<String>> getFavoriteFolders() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<String> folders = favoriteService.getFavoriteFolders(userId);
        return Result.success(folders);
    }

    @Operation(summary = "获取收藏夹内的文档")
    @GetMapping("/folder/{folderName}")
    public Result<LibFavoriteListResponse> getFavoritesByFolder(
            @PathVariable String folderName,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = SecurityUtil.getCurrentUserId();
        LibFavoriteListResponse response = favoriteService.getFavoritesByFolder(folderName, userId, page, size);
        return Result.success(response);
    }

    @Operation(summary = "检查是否已收藏")
    @GetMapping("/{documentId}/check")
    public Result<Boolean> isFavorited(
            @PathVariable Long documentId) {
        Long userId = SecurityUtil.getCurrentUserId();
        boolean favorited = favoriteService.isFavorited(documentId, userId);
        return Result.success(favorited);
    }

    @Operation(summary = "移动收藏到其他收藏夹")
    @PostMapping("/{documentId}/move")
    public Result<Void> moveFavorite(
            @PathVariable Long documentId,
            @RequestParam String folderName) {
        Long userId = SecurityUtil.getCurrentUserId();
        favoriteService.moveFavorite(documentId, folderName, userId);
        return Result.success();
    }
}

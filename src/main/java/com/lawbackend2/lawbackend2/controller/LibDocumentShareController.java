package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.CurrentUserId;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.LibShareCreateRequest;
import com.lawbackend2.lawbackend2.dto.response.LibDocumentResponse;
import com.lawbackend2.lawbackend2.dto.response.LibShareResponse;
import com.lawbackend2.lawbackend2.service.LibDocumentShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Tag(name = "文档库分享管理")
@RestController
@RequestMapping("/api/lib/shares")
@RequiredArgsConstructor
public class LibDocumentShareController {

    private final LibDocumentShareService shareService;

    @Operation(summary = "创建分享链接")
    @PostMapping
    public Result<LibShareResponse> createShare(
            @RequestBody LibShareCreateRequest request,
            @CurrentUserId Long userId) {
        LibShareResponse response = shareService.createShare(request, userId);
        return Result.success(response);
    }

    @Operation(summary = "根据分享码获取分享信息")
    @GetMapping("/code/{shareCode}")
    public Result<LibShareResponse> getShareByCode(@PathVariable String shareCode) {
        LibShareResponse response = shareService.getShareByCode(shareCode);
        return Result.success(response);
    }

    @Operation(summary = "根据ID获取分享信息")
    @GetMapping("/{id}")
    public Result<LibShareResponse> getShareById(@PathVariable Long id) {
        LibShareResponse response = shareService.getShareById(id);
        return Result.success(response);
    }

    @Operation(summary = "访问分享文档")
    @GetMapping("/{shareCode}/access")
    public Result<LibDocumentResponse> accessSharedDocument(
            @PathVariable String shareCode,
            @RequestParam(required = false) String password,
            @CurrentUserId Long userId) {
        LibDocumentResponse response = shareService.accessSharedDocument(shareCode, password, userId);
        return Result.success(response);
    }

    @Operation(summary = "下载分享文档")
    @GetMapping("/{shareCode}/download")
    public void downloadSharedDocument(
            @PathVariable String shareCode,
            @RequestParam(required = false) String password,
            HttpServletResponse response,
            @CurrentUserId Long userId) {
        shareService.downloadSharedDocument(shareCode, password, response, userId);
    }

    @Operation(summary = "删除分享链接")
    @DeleteMapping("/{id}")
    public Result<Void> deleteShare(
            @PathVariable Long id,
            @CurrentUserId Long userId) {
        shareService.deleteShare(id, userId);
        return Result.success();
    }

    @Operation(summary = "禁用分享链接")
    @PostMapping("/{id}/disable")
    public Result<Void> disableShare(
            @PathVariable Long id,
            @CurrentUserId Long userId) {
        shareService.disableShare(id, userId);
        return Result.success();
    }

    @Operation(summary = "启用分享链接")
    @PostMapping("/{id}/enable")
    public Result<Void> enableShare(
            @PathVariable Long id,
            @CurrentUserId Long userId) {
        shareService.enableShare(id, userId);
        return Result.success();
    }

    @Operation(summary = "验证分享链接是否有效")
    @GetMapping("/{shareCode}/valid")
    public Result<Boolean> isShareValid(@PathVariable String shareCode) {
        boolean valid = shareService.isShareValid(shareCode);
        return Result.success(valid);
    }

    @Operation(summary = "验证分享密码")
    @PostMapping("/{shareCode}/check-password")
    public Result<Boolean> checkSharePassword(
            @PathVariable String shareCode,
            @RequestParam(required = false) String password) {
        boolean valid = shareService.checkSharePassword(shareCode, password);
        return Result.success(valid);
    }
}

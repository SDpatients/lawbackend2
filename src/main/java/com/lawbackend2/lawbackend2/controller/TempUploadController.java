package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.*;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.entity.TempUploadToken;
import com.lawbackend2.lawbackend2.service.TempUploadTokenService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "临时文件上传")
@RestController
@RequestMapping("/temp-upload")
@Validated
public class TempUploadController {

    @Value("${server.servlet.context-path:/api/v1}")
    private String contextPath;

    @Autowired
    private TempUploadTokenService tempUploadTokenService;

    @Operation(summary = "创建临时上传Token")
    @PostMapping("/token")
    public Result<TempUploadTokenResponse> createToken(
            @RequestBody @Validated TempUploadTokenCreateRequest request,
            HttpServletRequest httpRequest) {

        Long userId = SecurityUtil.getCurrentUserId();

        Integer expireMinutes = request.getExpireMinutes() != null ? request.getExpireMinutes() : 30;
        TempUploadToken token = tempUploadTokenService.createToken(
                request.getBizType(),
                userId,
                request.getDescription(),
                expireMinutes
        );

        TempUploadTokenResponse response = convertToResponse(token);

        String baseUrl = getBaseUrl(httpRequest);
        response.setQrCodeContent(baseUrl + "/temp-upload/mobile?token=" + token.getToken());

        return Result.success(response);
    }

    @Operation(summary = "获取Token信息")
    @GetMapping("/token/{token}")
    public Result<TempUploadTokenResponse> getTokenInfo(
            @Parameter(description = "Token") @PathVariable String token) {

        TempUploadToken tokenInfo = tempUploadTokenService.getTokenInfo(token);
        return Result.success(convertToResponse(tokenInfo));
    }

    @Operation(summary = "验证Token是否有效")
    @GetMapping("/token/{token}/validate")
    public Result<Boolean> validateToken(
            @Parameter(description = "Token") @PathVariable String token) {

        try {
            tempUploadTokenService.validateToken(token);
            return Result.success(true);
        } catch (Exception e) {
            return Result.success(false);
        }
    }

    @Operation(summary = "获取Token下的文件列表")
    @GetMapping("/token/{token}/files")
    public Result<List<TempUploadFileResponse>> getFilesByToken(
            @Parameter(description = "Token") @PathVariable String token) {

        List<FileRecord> files = tempUploadTokenService.getFilesByToken(token);
        List<TempUploadFileResponse> responses = files.stream()
                .map(this::convertToFileResponse)
                .collect(Collectors.toList());

        return Result.success(responses);
    }

    @Operation(summary = "手机端上传文件(通过Token)")
    @PostMapping("/mobile/upload")
    public Result<TempUploadFileResponse> mobileUpload(
            @Parameter(description = "Token") @RequestParam("token") String token,
            @Parameter(description = "文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "文件描述") @RequestParam(required = false) String description) {

        FileRecord fileRecord = tempUploadTokenService.uploadFileByToken(token, file, description);
        return Result.success(convertToFileResponse(fileRecord));
    }

    @Operation(summary = "手机端批量上传文件(通过Token)")
    @PostMapping("/mobile/upload-batch")
    public Result<List<TempUploadFileResponse>> mobileUploadBatch(
            @Parameter(description = "Token") @RequestParam("token") String token,
            @Parameter(description = "文件列表") @RequestParam("files") List<MultipartFile> files,
            @Parameter(description = "文件描述列表") @RequestParam(required = false) List<String> descriptions) {

        List<FileRecord> fileRecords = tempUploadTokenService.uploadFilesByToken(token, files, descriptions);
        List<TempUploadFileResponse> responses = fileRecords.stream()
                .map(this::convertToFileResponse)
                .collect(Collectors.toList());

        return Result.success(responses);
    }

    @Operation(summary = "转移临时文件到业务")
    @PostMapping("/transfer")
    public Result<List<TempUploadFileResponse>> transferFiles(
            @RequestBody @Validated TempUploadTransferRequest request) {

        List<FileRecord> transferredFiles = tempUploadTokenService.transferFilesToBiz(
                request.getToken(),
                request.getBizType(),
                request.getBizId()
        );

        List<TempUploadFileResponse> responses = transferredFiles.stream()
                .map(this::convertToFileResponse)
                .collect(Collectors.toList());

        return Result.success(responses);
    }

    @Operation(summary = "取消Token")
    @DeleteMapping("/token/{token}")
    public Result<Void> cancelToken(
            @Parameter(description = "Token") @PathVariable String token) {

        tempUploadTokenService.cancelToken(token);
        return Result.success();
    }

    private TempUploadTokenResponse convertToResponse(TempUploadToken token) {
        TempUploadTokenResponse response = new TempUploadTokenResponse();
        response.setId(token.getId());
        response.setToken(token.getToken());
        response.setBizType(token.getBizType());
        response.setUserId(token.getUserId());
        response.setExpireTime(token.getExpireTime());
        response.setStatus(token.getStatus());
        response.setFileCount(token.getFileCount());
        response.setDescription(token.getDescription());
        response.setCreateTime(token.getCreateTime());
        return response;
    }

    private TempUploadFileResponse convertToFileResponse(FileRecord fileRecord) {
        TempUploadFileResponse response = new TempUploadFileResponse();
        response.setId(fileRecord.getId());
        response.setOriginalFileName(fileRecord.getOriginalFileName());
        response.setFileSize(fileRecord.getFileSize());
        response.setFileExtension(fileRecord.getFileExtension());
        response.setMimeType(fileRecord.getMimeType());
        response.setDescription(fileRecord.getDescription());
        response.setUploadTime(fileRecord.getUploadTime());
        response.setToken(fileRecord.getBizId());
        response.setFilePath(fileRecord.getFilePath());
        return response;
    }

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        StringBuilder baseUrl = new StringBuilder();
        baseUrl.append(scheme).append("://").append(serverName);

        if ((scheme.equals("http") && serverPort != 80) ||
                (scheme.equals("https") && serverPort != 443)) {
            baseUrl.append(":").append(serverPort);
        }

        baseUrl.append(contextPath);

        return baseUrl.toString();
    }
}

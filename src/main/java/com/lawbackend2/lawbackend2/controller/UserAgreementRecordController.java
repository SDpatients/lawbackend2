package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.dto.AgreementCheckResponse;
import com.lawbackend2.lawbackend2.dto.AgreementRecordRequest;
import com.lawbackend2.lawbackend2.dto.response.ApiResponse;
import com.lawbackend2.lawbackend2.entity.UserAgreementRecord;
import com.lawbackend2.lawbackend2.service.UserAgreementRecordService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/agreement")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户协议管理", description = "隐私政策和用户协议同意记录相关接口")
public class UserAgreementRecordController {

    private final UserAgreementRecordService userAgreementRecordService;

    @PostMapping("/agree")
    @Operation(summary = "同意协议", description = "记录用户对隐私政策或用户协议的同意操作")
    public ApiResponse<UserAgreementRecord> agree(
            HttpServletRequest request,
            @Valid @RequestBody AgreementRecordRequest req) {
        Long userId = SecurityUtil.getCurrentUserId();
        String userAccount = SecurityUtil.getCurrentUsername();
        String ipAddress = getClientIp(request);

        UserAgreementRecord record = userAgreementRecordService.recordAgreement(
                userId, userAccount, req.getAgreementType(),
                req.getAgreementVersion(), req.getAgreementContent(),
                ipAddress);

        return ApiResponse.success("协议同意记录成功", record);
    }

    @GetMapping("/history")
    @Operation(summary = "查询协议历史", description = "查询当前用户的所有协议同意历史记录")
    public ApiResponse<List<UserAgreementRecord>> getHistory() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<UserAgreementRecord> records = userAgreementRecordService.getUserAgreementHistory(userId);
        return ApiResponse.success(records);
    }

    @GetMapping("/latest")
    @Operation(summary = "查询最新协议记录", description = "查询用户对指定类型协议的最新同意记录")
    public ApiResponse<UserAgreementRecord> getLatest(
            @Parameter(description = "协议类型: PRIVACY_POLICY 或 USER_AGREEMENT")
            @RequestParam String agreementType) {
        Long userId = SecurityUtil.getCurrentUserId();
        UserAgreementRecord record = userAgreementRecordService.getLatestAgreement(userId, agreementType);
        return ApiResponse.success(record);
    }

    @GetMapping("/check")
    @Operation(summary = "检查协议同意状态", description = "检查当前用户是否已同意隐私政策和用户协议")
    public ApiResponse<AgreementCheckResponse> checkAgreements() {
        Long userId = SecurityUtil.getCurrentUserId();
        Map<String, Boolean> agreements = userAgreementRecordService.checkAllAgreements(userId);

        boolean allAgreed = agreements.values().stream().allMatch(Boolean::booleanValue);

        AgreementCheckResponse response = AgreementCheckResponse.builder()
                .agreements(agreements)
                .allAgreed(allAgreed)
                .checkTime(LocalDateTime.now())
                .build();

        return ApiResponse.success(response);
    }

    @GetMapping("/check/{agreementType}")
    @Operation(summary = "检查指定协议状态", description = "检查用户是否已同意指定类型的协议")
    public ApiResponse<Boolean> checkAgreement(
            @Parameter(description = "协议类型: PRIVACY_POLICY 或 USER_AGREEMENT")
            @PathVariable String agreementType) {
        Long userId = SecurityUtil.getCurrentUserId();
        boolean agreed = userAgreementRecordService.hasAgreed(userId, agreementType);
        return ApiResponse.success(agreed);
    }

    @GetMapping("/version-check")
    @Operation(summary = "版本检查", description = "检查用户是否同意过指定版本的协议")
    public ApiResponse<UserAgreementRecord> checkVersion(
            @Parameter(description = "协议类型: PRIVACY_POLICY 或 USER_AGREEMENT")
            @RequestParam String agreementType,
            @Parameter(description = "协议版本，如 v1.0")
            @RequestParam String agreementVersion) {
        Long userId = SecurityUtil.getCurrentUserId();
        UserAgreementRecord record = userAgreementRecordService.getLatestByTypeAndVersion(
                userId, agreementType, agreementVersion);
        return ApiResponse.success(record);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.CreditorClaimStagesResponse;
import com.lawbackend2.lawbackend2.dto.CreditorCreateRequest;
import com.lawbackend2.lawbackend2.dto.CreditorInfoResponse;
import com.lawbackend2.lawbackend2.dto.CreditorUpdateRequest;
import com.lawbackend2.lawbackend2.entity.CreditorInfo;
import com.lawbackend2.lawbackend2.service.CreditorInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "债权人信息管理")
@RestController
@RequestMapping("/creditor")
@Validated
public class CreditorInfoController {

    private final CreditorInfoService creditorInfoService;

    public CreditorInfoController(CreditorInfoService creditorInfoService) {
        this.creditorInfoService = creditorInfoService;
    }

    @Operation(summary = "创建债权人")
    @PostMapping
    public Result<Map<String, Object>> createCreditor(@Valid @RequestBody CreditorCreateRequest request) {
        Long userId = getCurrentUserId();
        CreditorInfo creditorInfo = creditorInfoService.createCreditor(request, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("creditorId", creditorInfo.getId());

        return Result.success(data);
    }

    @Operation(summary = "获取债权人详情", description = "返回债权人详细信息，包含案件案号和案件名称")
    @GetMapping("/{creditorId}")
    public Result<CreditorInfoResponse> getCreditorById(@Parameter(description = "债权人ID") @PathVariable Long creditorId) {
        Long userId = getCurrentUserId();
        CreditorInfoResponse creditorInfo = creditorInfoService.getCreditorByIdWithCaseInfo(creditorId, userId);
        return Result.success(creditorInfo);
    }

    @Operation(summary = "债权人列表", description = "支持多条件查询债权人信息，所有查询参数都是可选的，返回数据包含案件案号和案件名称")
    @GetMapping("/list")
    public Result<PageResult<CreditorInfoResponse>> getCreditorList(
            @Parameter(description = "页码，从1开始", example = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID，精确查询", example = "1") @RequestParam(required = false) Long caseId,
            @Parameter(description = "债权人类型（个人/企业），精确查询", example = "企业") @RequestParam(required = false) String creditorType,
            @Parameter(description = "债权人名称，模糊查询", example = "张三") @RequestParam(required = false) String creditorName,
            @Parameter(description = "身份证号，模糊查询", example = "110101") @RequestParam(required = false) String idNumber,
            @Parameter(description = "法定代表人，模糊查询", example = "李四") @RequestParam(required = false) String legalRepresentative,
            @Parameter(description = "状态，精确查询", example = "CONFIRMED") @RequestParam(required = false) String status) {

        Long userId = getCurrentUserId();
        PageResult<CreditorInfoResponse> pageResult = creditorInfoService.getCreditorListWithCaseInfo(pageNum, pageSize, caseId, creditorType, creditorName, idNumber, legalRepresentative, status, userId);

        return Result.success(pageResult);
    }

    @Operation(summary = "更新债权人信息")
    @PutMapping("/{creditorId}")
    public Result<Void> updateCreditor(
            @Parameter(description = "债权人ID") @PathVariable Long creditorId,
            @Valid @RequestBody CreditorUpdateRequest request) {

        creditorInfoService.updateCreditor(creditorId, request);
        return Result.success();
    }

    @Operation(summary = "删除债权人")
    @DeleteMapping("/{creditorId}")
    public Result<Void> deleteCreditor(@Parameter(description = "债权人ID") @PathVariable Long creditorId) {
        creditorInfoService.deleteCreditor(creditorId);
        return Result.success();
    }

    @Operation(summary = "查询债权人债权三个阶段数据", description = "根据债权人ID查询债权申报、债权审查、债权确认三个阶段的数据")
    @GetMapping("/{creditorId}/claim-stages")
    public Result<CreditorClaimStagesResponse> getCreditorClaimStages(@Parameter(description = "债权人ID") @PathVariable Long creditorId) {
        Long userId = getCurrentUserId();
        CreditorClaimStagesResponse response = creditorInfoService.getCreditorClaimStages(creditorId, userId);
        return Result.success(response);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            return (Long) authentication.getPrincipal();
        }
        throw new RuntimeException("无法获取当前用户ID");
    }
}

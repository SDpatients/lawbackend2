package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.*;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.service.BankruptCaseService;
import com.lawbackend2.lawbackend2.service.PermissionService;
import com.lawbackend2.lawbackend2.service.UserRoleService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "案件管理")
@RestController
@RequestMapping("/case")
@Validated
public class BankruptCaseController {

    private final BankruptCaseService bankruptCaseService;
    private final PermissionService permissionService;
    private final UserRoleService userRoleService;

    public BankruptCaseController(BankruptCaseService bankruptCaseService, PermissionService permissionService, UserRoleService userRoleService) {
        this.bankruptCaseService = bankruptCaseService;
        this.permissionService = permissionService;
        this.userRoleService = userRoleService;
    }

    @Operation(summary = "创建案件")
    @PostMapping
    public Result<Map<String, Object>> createCase(@Valid @RequestBody CaseCreateRequest request) {
        Long userId = getCurrentUserId();
        BankruptCase bankruptCase = bankruptCaseService.createCase(request, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("caseId", bankruptCase.getId());
        data.put("caseNumber", bankruptCase.getCaseNumber());

        return Result.success(data);
    }

    @Operation(summary = "获取案件详情")
    @GetMapping("/{caseId}")
    public Result<BankruptCase> getCaseById(@Parameter(description = "案件ID") @PathVariable Long caseId) {
        BankruptCase bankruptCase = bankruptCaseService.getCaseById(caseId);
        return Result.success(bankruptCase);
    }

    @Operation(summary = "案件列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<BankruptCase>> getCaseList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件状态") @RequestParam(required = false) String caseStatus,
            @Parameter(description = "案件进度") @RequestParam(required = false) String caseProgress) {

        Long userId = getCurrentUserId();
        List<String> userPermissions = permissionService.getUserPermissions(userId);

        List<BankruptCase> list;
        Long total;

        if (userPermissions.contains("case:query:all")) {
            list = bankruptCaseService.getCaseList(pageNum, pageSize, caseStatus, caseProgress);
            total = bankruptCaseService.getCaseCount(caseStatus, caseProgress);
        } else if (userPermissions.contains("case:query:own")) {
            list = bankruptCaseService.getUserCaseList(userId, pageNum, pageSize, caseStatus, null);
            total = bankruptCaseService.getUserCaseCount(userId, caseStatus, null);
        } else {
            list = List.of();
            total = 0L;
        }

        return Result.success(PageResult.of(total, list));
    }

    @Operation(summary = "更新案件信息")
    @PutMapping("/{caseId}")
    public Result<Void> updateCase(
            @Parameter(description = "案件ID") @PathVariable Long caseId,
            @Valid @RequestBody CaseUpdateRequest request) {

        bankruptCaseService.updateCase(caseId, request);
        return Result.success();
    }

    @Operation(summary = "案件状态流转")
    @PutMapping("/{caseId}/status")
    public Result<Void> updateCaseStatus(
            @Parameter(description = "案件ID") @PathVariable Long caseId,
            @Valid @RequestBody CaseStatusUpdateRequest request) {

        bankruptCaseService.updateCaseStatus(caseId, request);
        return Result.success();
    }

    @Operation(summary = "案件进度更新")
    @PutMapping("/{caseId}/progress")
    public Result<Void> updateCaseProgress(
            @Parameter(description = "案件ID") @PathVariable Long caseId,
            @Valid @RequestBody CaseProgressUpdateRequest request) {

        bankruptCaseService.updateCaseProgress(caseId, request);
        return Result.success();
    }

    @Operation(summary = "案件审核")
    @PostMapping("/{caseId}/review")
    public Result<Void> reviewCase(
            @Parameter(description = "案件ID") @PathVariable Long caseId,
            @Valid @RequestBody CaseReviewRequest request) {

        Long userId = getCurrentUserId();
        bankruptCaseService.reviewCase(caseId, request, userId);
        log.info("案件审核成功, caseId: {}, reviewerId: {}", caseId, userId);
        return Result.success();
    }

    @Operation(summary = "查询案件审核状态")
    @GetMapping("/{caseId}/review-status")
    public Result<BankruptCase> getReviewStatus(@Parameter(description = "案件ID") @PathVariable Long caseId) {
        BankruptCase bankruptCase = bankruptCaseService.getReviewStatus(caseId);
        return Result.success(bankruptCase);
    }

    @Operation(summary = "案件简单信息查询(分页)")
    @GetMapping("/simple-list")
    public Result<PageResult<com.lawbackend2.lawbackend2.dto.CaseSimpleInfo>> getCaseSimpleList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "案号(可选)") @RequestParam(required = false) String caseNumber) {

        Long userId = getCurrentUserId();
        List<com.lawbackend2.lawbackend2.dto.CaseSimpleInfo> list = bankruptCaseService.getCaseSimpleList(userId, page, size, caseNumber);
        Long total = bankruptCaseService.getCaseSimpleCount(userId, caseNumber);

        return Result.success(PageResult.of(total, list));
    }

    @Operation(summary = "根据用户ID查询案件列表(分页)")
    @GetMapping("/user/{userId}/list")
    public Result<PageResult<BankruptCase>> getUserCaseList(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件状态") @RequestParam(required = false) String caseStatus,
            @Parameter(description = "案号") @RequestParam(required = false) String caseNumber) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        List<String> roleCodes = userRoleService.getUserRoleCodes(currentUserId);

        List<BankruptCase> list;
        Long total;

        if (roleCodes.contains("SUPER_ADMIN") || roleCodes.contains("ADMIN")) {
            list = bankruptCaseService.getCaseList(pageNum, pageSize, caseStatus, null);
            total = bankruptCaseService.getCaseCount(caseStatus, null);
        } else {
            list = bankruptCaseService.getUserCaseList(userId, pageNum, pageSize, caseStatus, caseNumber);
            total = bankruptCaseService.getUserCaseCount(userId, caseStatus, caseNumber);
        }

        return Result.success(PageResult.of(total, list));
    }

    private Long getCurrentUserId() {
        return 1L;
    }
}

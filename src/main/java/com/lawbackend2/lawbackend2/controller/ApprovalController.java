package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.AuditLog;
import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.ApprovalCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.ApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.ApprovalStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.ApprovalUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.ApprovalResponse;
import com.lawbackend2.lawbackend2.dto.response.ApprovalHistoryResponse;
import com.lawbackend2.lawbackend2.dto.response.CaseTaskResponse;
import com.lawbackend2.lawbackend2.entity.ApprovalHistory;
import com.lawbackend2.lawbackend2.service.ApprovalHistoryService;
import com.lawbackend2.lawbackend2.service.ApprovalService;
import com.lawbackend2.lawbackend2.service.CaseTaskService;
import com.lawbackend2.lawbackend2.service.UserRoleService;
import com.lawbackend2.lawbackend2.service.impl.ApprovalServiceImpl;
import com.lawbackend2.lawbackend2.util.PermissionChecker;
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
@Tag(name = "审批管理")
@RestController
@RequestMapping("/approval")
@Validated
public class ApprovalController {
    private final ApprovalService approvalService;
    private final ApprovalHistoryService approvalHistoryService;
    private final CaseTaskService caseTaskService;
    private final UserRoleService userRoleService;
    private final PermissionChecker permissionChecker;

    public ApprovalController(ApprovalService approvalService, ApprovalHistoryService approvalHistoryService, CaseTaskService caseTaskService, UserRoleService userRoleService, PermissionChecker permissionChecker) {
        this.approvalService = approvalService;
        this.approvalHistoryService = approvalHistoryService;
        this.caseTaskService = caseTaskService;
        this.userRoleService = userRoleService;
        this.permissionChecker = permissionChecker;
    }

    @Operation(summary = "创建审批")
    @PostMapping
    @AuditLog(module = "approval", moduleName = "审批管理", operationType = "CREATE", operationName = "创建审批")
    public Result<Map<String, Object>> createApproval(@Valid @RequestBody ApprovalCreateRequest request) {
        Long userId = getCurrentUserId();
        Long approvalId = approvalService.createApproval(request, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("approvalId", approvalId);

        return Result.success(data);
    }

    @Operation(summary = "审批列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<ApprovalResponse>> getApprovalList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "律师ID") @RequestParam(required = false) Long lawyerId,
            @Parameter(description = "审核类型") @RequestParam(required = false) String approvalType,
            @Parameter(description = "审核状态") @RequestParam(required = false) String approvalStatus,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "审批标题") @RequestParam(required = false) String approvalTitle) {

        Long currentUserId = getCurrentUserId();
        List<String> roleCodes = userRoleService.getUserRoleCodes(currentUserId);

        if (!roleCodes.contains("ADMIN") && !roleCodes.contains("SUPER_ADMIN")) {
            return Result.error("您没有权限访问审批列表");
        }

        PageResult<ApprovalResponse> result = approvalService.getApprovalList(pageNum, pageSize, caseId, lawyerId, approvalType, approvalStatus, status, approvalTitle);
        return Result.success(result);
    }

    @Operation(summary = "获取审批详情")
    @GetMapping("/{approvalId}")
    public Result<ApprovalResponse> getApprovalDetail(@Parameter(description = "审批ID") @PathVariable Long approvalId) {
        permissionChecker.checkApprovalAccessPermission(approvalId);
        ApprovalResponse approval = approvalService.getApprovalDetail(approvalId);
        return Result.success(approval);
    }

    @Operation(summary = "更新审批信息")
    @PutMapping("/{approvalId}")
    public Result<Void> updateApproval(
            @Parameter(description = "审批ID") @PathVariable Long approvalId,
            @Valid @RequestBody ApprovalUpdateRequest request) {

        permissionChecker.checkApprovalEditPermission(approvalId);
        approvalService.updateApproval(approvalId, request);
        return Result.success();
    }

    @Operation(summary = "审批操作")
    @PostMapping("/{approvalId}/approve")
    @AuditLog(module = "approval", moduleName = "审批管理", operationType = "APPROVE", operationName = "审批操作")
    public Result<Void> approveApproval(
            @Parameter(description = "审批ID") @PathVariable Long approvalId,
            @Valid @RequestBody ApprovalRequest request) {
        Long currentUserId = getCurrentUserId();
        permissionChecker.checkApprovalApprovePermission(approvalId);
        approvalService.approveApproval(approvalId, request, currentUserId);
        return Result.success();
    }

    @Operation(summary = "更新审批状态")
    @PutMapping("/{approvalId}/status")
    public Result<Void> updateApprovalStatus(
            @Parameter(description = "审批ID") @PathVariable Long approvalId,
            @Valid @RequestBody ApprovalStatusRequest request) {

        permissionChecker.checkApprovalEditPermission(approvalId);
        approvalService.updateApprovalStatus(approvalId, request);
        return Result.success();
    }

    @Operation(summary = "删除审批")
    @DeleteMapping("/{approvalId}")
    public Result<Void> deleteApproval(@Parameter(description = "审批ID") @PathVariable Long approvalId) {
        permissionChecker.checkApprovalDeletePermission(approvalId);
        approvalService.deleteApproval(approvalId);
        return Result.success();
    }

    @Operation(summary = "获取审批历史记录")
    @GetMapping("/{approvalId}/history")
    public Result<PageResult<ApprovalHistoryResponse>> getApprovalHistoryList(
            @Parameter(description = "审批ID") @PathVariable Long approvalId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {

        permissionChecker.checkApprovalAccessPermission(approvalId);
        PageResult<ApprovalHistoryResponse> result = approvalHistoryService.getApprovalHistoryList(pageNum, pageSize, approvalId, null, null, null);
        return Result.success(result);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            return (Long) authentication.getPrincipal();
        }
        throw new RuntimeException("无法获取当前用户ID");
    }

    @Operation(summary = "根据案件ID获取审批历史记录")
    @GetMapping("/case/{caseId}/history")
    public Result<PageResult<ApprovalHistoryResponse>> getApprovalHistoryByCaseId(
            @Parameter(description = "案件ID") @PathVariable Long caseId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "审批类型: CASE_SUBMIT-案件审批, TASK_-流程审批(前缀), 不传则查询全部") @RequestParam(required = false) String approvalType) {

        PageResult<ApprovalHistoryResponse> result = approvalHistoryService.getApprovalHistoryList(pageNum, pageSize, null, caseId, null, approvalType);
        return Result.success(result);
    }
    
    @Operation(summary = "根据案件 ID 获取当前提交进度")
    @GetMapping("/case/{caseId}/progress")
    public Result<List<ApprovalResponse>> getApprovalProgressByCaseId(
            @Parameter(description = "案件 ID") @PathVariable Long caseId,
            @Parameter(description = "审批类型: CASE_SUBMIT-案件审批, TASK_-流程审批(前缀), 不传则查询全部") @RequestParam(required = false) String approvalType) {

        List<ApprovalResponse> result = approvalService.getApprovalProgressByCaseId(caseId, approvalType);
        return Result.success(result);
    }

    @Operation(summary = "新增审批历史记录")
    @PostMapping("/history")
    public Result<ApprovalHistory> createApprovalHistory(
            @Valid @RequestBody ApprovalHistory approvalHistory) {

        ApprovalHistory result = approvalHistoryService.createApprovalHistory(approvalHistory);
        return Result.success(result);
    }

    @Operation(summary = "批量获取审批附件")
    @GetMapping("/{approvalId}/attachments")
    public Result<Map<String, Object>> getApprovalAttachments(
            @Parameter(description = "审批ID") @PathVariable Long approvalId,
            @Parameter(description = "是否包含图片二进制数据，默认false") @RequestParam(required = false) Boolean includeImages,
            @Parameter(description = "是否包含所有文件信息，默认true") @RequestParam(required = false) Boolean includeFiles) {

        permissionChecker.checkApprovalAccessPermission(approvalId);
        Map<String, Object> data = approvalService.getApprovalAttachments(approvalId, includeImages, includeFiles);
        return Result.success(data);
    }

    @Operation(summary = "获取审批历史详情")
    @GetMapping("/history/{historyId}")
    public Result<ApprovalHistoryResponse> getApprovalHistoryDetail(
            @Parameter(description = "审批历史ID") @PathVariable Long historyId) {

        ApprovalHistoryResponse result = approvalHistoryService.getApprovalHistoryDetail(historyId);
        return Result.success(result);
    }

    @Operation(summary = "更新审批历史记录")
    @PutMapping("/history/{historyId}")
    public Result<ApprovalHistory> updateApprovalHistory(
            @Parameter(description = "审批历史ID") @PathVariable Long historyId,
            @Valid @RequestBody ApprovalHistory approvalHistory) {

        ApprovalHistory result = approvalHistoryService.updateApprovalHistory(historyId, approvalHistory);
        return Result.success(result);
    }

    @Operation(summary = "删除审批历史记录")
    @DeleteMapping("/history/{historyId}")
    public Result<Void> deleteApprovalHistory(
            @Parameter(description = "审批历史ID") @PathVariable Long historyId) {

        approvalHistoryService.deleteApprovalHistory(historyId);
        return Result.success();
    }

    @Operation(summary = "获取待审批的CASE_SUBMIT类型数量")
    @GetMapping("/pending/case-submit/count")
    public Result<Map<String, Object>> getPendingCaseSubmitCount() {
        Long currentUserId = getCurrentUserId();
        List<String> roleCodes = userRoleService.getUserRoleCodes(currentUserId);
        if (!roleCodes.contains("ADMIN") && !roleCodes.contains("SUPER_ADMIN")) {
            return Result.error("您没有权限访问");
        }
        long count = approvalService.getPendingCaseSubmitCount();
        Map<String, Object> data = new HashMap<>();
        data.put("count", count);
        return Result.success(data);
    }

    @Operation(summary = "获取待审批的TASK_类型数量")
    @GetMapping("/pending/task/count")
    public Result<Map<String, Object>> getPendingTaskCount() {
        Long currentUserId = getCurrentUserId();
        List<String> roleCodes = userRoleService.getUserRoleCodes(currentUserId);
        if (!roleCodes.contains("ADMIN") && !roleCodes.contains("SUPER_ADMIN")) {
            return Result.error("您没有权限访问");
        }
        long count = approvalService.getPendingTaskCount();
        Map<String, Object> data = new HashMap<>();
        data.put("count", count);
        return Result.success(data);
    }

    @Operation(summary = "获取待审批的总数量(CASE_SUBMIT和TASK_类型合并)")
    @GetMapping("/pending/total/count")
    public Result<Map<String, Object>> getPendingTotalCount() {
        Long currentUserId = getCurrentUserId();
        List<String> roleCodes = userRoleService.getUserRoleCodes(currentUserId);
        if (!roleCodes.contains("ADMIN") && !roleCodes.contains("SUPER_ADMIN")) {
            return Result.error("您没有权限访问");
        }
        long count = approvalService.getPendingTotalCount();
        Map<String, Object> data = new HashMap<>();
        data.put("count", count);
        return Result.success(data);
    }
}
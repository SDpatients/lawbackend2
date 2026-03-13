package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.*;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.service.BankruptCaseService;
import com.lawbackend2.lawbackend2.service.PermissionService;
import com.lawbackend2.lawbackend2.service.UserRoleService;
import com.lawbackend2.lawbackend2.util.CasePermissionUtil;
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
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "案件管理")
@RestController
@RequestMapping({"/case"})
@Validated
public class BankruptCaseController {

    private final BankruptCaseService bankruptCaseService;
    private final PermissionService permissionService;
    private final UserRoleService userRoleService;
    private final CasePermissionUtil casePermissionUtil;

    public BankruptCaseController(BankruptCaseService bankruptCaseService, PermissionService permissionService, UserRoleService userRoleService, CasePermissionUtil casePermissionUtil) {
        this.bankruptCaseService = bankruptCaseService;
        this.permissionService = permissionService;
        this.userRoleService = userRoleService;
        this.casePermissionUtil = casePermissionUtil;
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
        casePermissionUtil.checkCaseAccessPermission(caseId);
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

        casePermissionUtil.checkCaseEditPermission(caseId);
        bankruptCaseService.updateCase(caseId, request);
        return Result.success();
    }

    @Operation(summary = "案件状态流转")
    @PutMapping("/{caseId}/status")
    public Result<Void> updateCaseStatus(
            @Parameter(description = "案件ID") @PathVariable Long caseId,
            @Valid @RequestBody CaseStatusUpdateRequest request) {

        casePermissionUtil.checkCaseEditPermission(caseId);
        bankruptCaseService.updateCaseStatus(caseId, request);
        return Result.success();
    }

    @Operation(summary = "案件进度更新")
    @PutMapping("/{caseId}/progress")
    public Result<Void> updateCaseProgressV2(
            @Parameter(description = "案件ID") @PathVariable Long caseId,
            @Valid @RequestBody com.lawbackend2.lawbackend2.dto.request.CaseProgressUpdateRequest request) {

        casePermissionUtil.checkCaseEditPermission(caseId);
        bankruptCaseService.updateCaseProgress(caseId, request.getCaseProgress());
        return Result.success();
    }

    @Operation(summary = "案件审核")
    @PostMapping("/{caseId}/review")
    public Result<Void> reviewCase(
            @Parameter(description = "案件ID") @PathVariable Long caseId,
            @Valid @RequestBody CaseReviewRequest request) {

        Long userId = getCurrentUserId();
        casePermissionUtil.checkCaseAccessPermission(caseId);
        bankruptCaseService.reviewCase(caseId, request, userId);
        log.info("案件审核成功, caseId: {}, reviewerId: {}", caseId, userId);
        return Result.success();
    }

    @Operation(summary = "提交案件审核")
    @PostMapping("/{caseId}/submit-review")
    public Result<Void> submitForReview(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {

        Long userId = getCurrentUserId();
        casePermissionUtil.checkCaseEditPermission(caseId);
        bankruptCaseService.submitForReview(caseId, userId);
        log.info("案件提交审核成功, caseId: {}, userId: {}", caseId, userId);
        return Result.success();
    }

    @Operation(summary = "撤销案件审核")
    @PostMapping("/{caseId}/withdraw-review")
    public Result<Void> withdrawReview(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {

        Long userId = getCurrentUserId();
        casePermissionUtil.checkCaseEditPermission(caseId);
        bankruptCaseService.withdrawReview(caseId, userId);
        log.info("案件撤销审核成功, caseId: {}, userId: {}", caseId, userId);
        return Result.success();
    }

    @Operation(summary = "重新提交案件审核")
    @PostMapping("/{caseId}/resubmit-review")
    public Result<Void> resubmitForReview(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {

        Long userId = getCurrentUserId();
        casePermissionUtil.checkCaseEditPermission(caseId);
        bankruptCaseService.resubmitForReview(caseId, userId);
        log.info("案件重新提交审核成功, caseId: {}, userId: {}", caseId, userId);
        return Result.success();
    }

    @Operation(summary = "批量审核案件")
    @PostMapping("/batch-review")
    public Result<Map<String, Integer>> batchReview(
            @Valid @RequestBody CaseBatchReviewRequest request) {

        Long userId = getCurrentUserId();
        bankruptCaseService.batchReview(request, userId);
        
        Map<String, Integer> result = new HashMap<>();
        result.put("total", request.getCaseIds().size());
        result.put("success", request.getCaseIds().size());
        
        log.info("批量审核成功, reviewerId: {}", userId);
        return Result.success(result);
    }

    @Operation(summary = "撤销审核结果")
    @PostMapping("/{caseId}/revoke-review")
    public Result<Void> revokeReview(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {

        Long userId = getCurrentUserId();
        casePermissionUtil.checkCaseAccessPermission(caseId);
        bankruptCaseService.revokeReview(caseId, userId);
        log.info("案件审核结果撤销成功, caseId: {}, userId: {}", caseId, userId);
        return Result.success();
    }

    @Operation(summary = "查询案件审核状态")
    @GetMapping("/{caseId}/review-status")
    public Result<BankruptCase> getReviewStatus(@Parameter(description = "案件ID") @PathVariable Long caseId) {
        casePermissionUtil.checkCaseAccessPermission(caseId);
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

    @Operation(summary = "根据用户 ID 查询案件列表 (分页)")
    @GetMapping("/user/{userId}/list")
    public Result<PageResult<BankruptCase>> getUserCaseList(
            @Parameter(description = "用户 ID") @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件状态") @RequestParam(required = false) String caseStatus,
            @Parameter(description = "案号") @RequestParam(required = false) String caseNumber) {

        List<BankruptCase> list = bankruptCaseService.getUserCaseList(userId, pageNum, pageSize, caseStatus, caseNumber);
        Long total = bankruptCaseService.getUserCaseCount(userId, caseStatus, caseNumber);

        return Result.success(PageResult.of(total, list));
    }

    @Operation(summary = "根据用户 ID 查询案件数量")
    @GetMapping("/user/{userId}/count")
    public Result<Long> getUserCaseCount(
            @Parameter(description = "用户 ID") @PathVariable Long userId,
            @Parameter(description = "案件状态 (可选)") @RequestParam(required = false) String caseStatus,
            @Parameter(description = "案号 (可选，支持模糊查询)") @RequestParam(required = false) String caseNumber) {

        Long count = bankruptCaseService.getUserCaseCount(userId, caseStatus, caseNumber);
        return Result.success(count);
    }

    @Operation(summary = "查询待审核案件列表")
    @GetMapping("/review/pending")
    public Result<PageResult<BankruptCase>> getPendingReviewCases(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键词（案号）") @RequestParam(required = false) String keyword) {

        List<BankruptCase> list = bankruptCaseService.getCasesByReviewStatus("PENDING", pageNum, pageSize, keyword);
        Long total = bankruptCaseService.getCasesCountByReviewStatus("PENDING", keyword);

        return Result.success(PageResult.of(total, list));
    }

    @Operation(summary = "查询已审核案件列表")
    @GetMapping("/review/approved")
    public Result<PageResult<BankruptCase>> getApprovedReviewCases(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键词（案号）") @RequestParam(required = false) String keyword) {

        List<BankruptCase> list = bankruptCaseService.getCasesByReviewStatus("APPROVED", pageNum, pageSize, keyword);
        Long total = bankruptCaseService.getCasesCountByReviewStatus("APPROVED", keyword);

        return Result.success(PageResult.of(total, list));
    }

    @Operation(summary = "查询已驳回案件列表")
    @GetMapping("/review/rejected")
    public Result<PageResult<BankruptCase>> getRejectedReviewCases(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键词（案号）") @RequestParam(required = false) String keyword) {

        List<BankruptCase> list = bankruptCaseService.getCasesByReviewStatus("REJECTED", pageNum, pageSize, keyword);
        Long total = bankruptCaseService.getCasesCountByReviewStatus("REJECTED", keyword);

        return Result.success(PageResult.of(total, list));
    }

    @Operation(summary = "查询指定审核人审核的案件列表")
    @GetMapping("/review/reviewer/{reviewerId}")
    public Result<PageResult<BankruptCase>> getCasesByReviewer(
            @Parameter(description = "审核人ID") @PathVariable Long reviewerId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "审核状态") @RequestParam(required = false) String reviewStatus) {

        List<BankruptCase> list = bankruptCaseService.getCasesByReviewerId(reviewerId, pageNum, pageSize, reviewStatus);
        Long total = bankruptCaseService.getCasesCountByReviewerId(reviewerId, reviewStatus);

        return Result.success(PageResult.of(total, list));
    }

    @Operation(summary = "查询审核状态统计")
    @GetMapping("/review/statistics")
    public Result<List<Map<String, Object>>> getReviewStatistics() {
        List<Object[]> statistics = bankruptCaseService.getReviewStatusStatistics();

        List<Map<String, Object>> result = statistics.stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("reviewStatus", item[0]);
                    map.put("count", item[1]);
                    return map;
                })
                .collect(Collectors.toList());

        return Result.success(result);
    }

    @Operation(summary = "删除案件")
    @DeleteMapping("/{caseId}")
    public Result<Void> deleteCase(@Parameter(description = "案件ID") @PathVariable Long caseId) {
        casePermissionUtil.checkCaseDeletePermission(caseId);
        bankruptCaseService.deleteCase(caseId);
        log.info("案件删除成功, caseId: {}", caseId);
        return Result.success();
    }

    @Operation(summary = "查询案件关联数据")
    @GetMapping("/{caseId}/related-data")
    public Result<com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse> getCaseRelatedData(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {
        casePermissionUtil.checkCaseAccessPermission(caseId);
        com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse relatedData = 
            bankruptCaseService.getCaseRelatedData(caseId);
        return Result.success(relatedData);
    }

    private Long getCurrentUserId() {
        return SecurityUtil.getCurrentUserId();
    }
}

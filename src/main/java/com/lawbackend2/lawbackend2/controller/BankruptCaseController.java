package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.AuditLog;
import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.*;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.service.BankruptCaseService;
import com.lawbackend2.lawbackend2.service.PermissionService;
import com.lawbackend2.lawbackend2.service.RecentCaseSearchService;
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
    private final RecentCaseSearchService recentCaseSearchService;

    public BankruptCaseController(BankruptCaseService bankruptCaseService, PermissionService permissionService, UserRoleService userRoleService, CasePermissionUtil casePermissionUtil, RecentCaseSearchService recentCaseSearchService) {
        this.bankruptCaseService = bankruptCaseService;
        this.permissionService = permissionService;
        this.userRoleService = userRoleService;
        this.casePermissionUtil = casePermissionUtil;
        this.recentCaseSearchService = recentCaseSearchService;
    }

    @Operation(summary = "创建案件")
    @PostMapping
    @AuditLog(module = "case", moduleName = "案件管理", operationType = "CREATE", operationName = "创建案件")
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

        Long userId = getCurrentUserId();
        recentCaseSearchService.recordCaseSearch(userId, caseId);

        return Result.success(bankruptCase);
    }

    @Operation(summary = "案件列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<BankruptCase>> getCaseList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件状态（PENDING/ONGOING/AWAITING/COMPLETED/ARCHIVED）") @RequestParam(required = false) String caseStatus,
            @Parameter(description = "案件进度（FIRST/SECOND/THIRD/FOURTH/FIFTH/SIXTH/SEVENTH）") @RequestParam(required = false) String caseProgress,
            @Parameter(description = "关键词（支持案号、案件名称模糊搜索）") @RequestParam(required = false) String keyword) {

        if (caseStatus != null && com.lawbackend2.lawbackend2.enums.CaseStatus.fromString(caseStatus) == null) {
            throw new com.lawbackend2.lawbackend2.exception.BusinessException("无效的案件状态值: " + caseStatus);
        }
        if (caseProgress != null && com.lawbackend2.lawbackend2.enums.CaseProgress.fromString(caseProgress) == null) {
            throw new com.lawbackend2.lawbackend2.exception.BusinessException("无效的案件进度值: " + caseProgress);
        }

        Long userId = getCurrentUserId();
        List<String> userPermissions = permissionService.getUserPermissions(userId);

        List<BankruptCase> list;
        Long total;

        if (userPermissions.contains("case:query:all")) {
            list = bankruptCaseService.getCaseList(pageNum, pageSize, caseStatus, caseProgress, keyword);
            total = bankruptCaseService.getCaseCount(caseStatus, caseProgress, keyword);
        } else if (userPermissions.contains("case:query:own")) {
            list = bankruptCaseService.getUserCaseList(userId, pageNum, pageSize, caseStatus, keyword, caseProgress);
            total = bankruptCaseService.getUserCaseCount(userId, caseStatus, keyword, caseProgress);
        } else {
            list = List.of();
            total = 0L;
        }

        return Result.success(PageResult.of(total, list, pageNum, pageSize));
    }

    @Operation(summary = "更新案件信息")
    @PutMapping("/{caseId}")
    @AuditLog(module = "case", moduleName = "案件管理", operationType = "UPDATE", operationName = "更新案件信息")
    public Result<Void> updateCase(
            @Parameter(description = "案件ID") @PathVariable Long caseId,
            @Valid @RequestBody CaseUpdateRequest request) {

        casePermissionUtil.checkCaseEditPermission(caseId);
        bankruptCaseService.updateCase(caseId, request);
        return Result.success();
    }

    @Operation(summary = "案件状态流转")
    @PutMapping("/{caseId}/status")
    @AuditLog(module = "case", moduleName = "案件管理", operationType = "UPDATE", operationName = "案件状态流转")
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
    @AuditLog(module = "case", moduleName = "案件管理", operationType = "REVIEW", operationName = "案件审核")
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

    @Operation(summary = "案件归档（已结案→已归档）")
    @PostMapping("/{caseId}/archive")
    @AuditLog(module = "case", moduleName = "案件管理", operationType = "UPDATE", operationName = "案件归档")
    public Result<Void> archiveCase(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {

        Long userId = getCurrentUserId();
        casePermissionUtil.checkCaseAccessPermission(caseId);
        bankruptCaseService.archiveCase(caseId, userId);
        log.info("案件归档成功, caseId: {}, userId: {}", caseId, userId);
        return Result.success();
    }

    @Operation(summary = "撤销案件归档（已归档→已结案）")
    @PostMapping("/{caseId}/unarchive")
    @AuditLog(module = "case", moduleName = "案件管理", operationType = "UPDATE", operationName = "撤销案件归档")
    public Result<Void> unarchiveCase(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {

        Long userId = getCurrentUserId();
        casePermissionUtil.checkCaseAccessPermission(caseId);
        bankruptCaseService.unarchiveCase(caseId, userId);
        log.info("案件撤销归档成功, caseId: {}, userId: {}", caseId, userId);
        return Result.success();
    }

    @Operation(summary = "查询案件审核状态")
    @GetMapping("/{caseId}/review-status")
    public Result<BankruptCase> getReviewStatus(@Parameter(description = "案件ID") @PathVariable Long caseId) {
        casePermissionUtil.checkCaseAccessPermission(caseId);
        BankruptCase bankruptCase = bankruptCaseService.getReviewStatus(caseId);
        return Result.success(bankruptCase);
    }

    @Operation(summary = "获取当前用户的案件统计数据", description = "返回当前登录用户的所有案件数量、进行中数量、已结案数量")
    @GetMapping("/my-stats")
    public Result<com.lawbackend2.lawbackend2.dto.MyCaseStatisticsResponse> getMyCaseStatistics() {
        Long userId = getCurrentUserId();
        log.info("获取当前用户的案件统计数据, userId: {}", userId);
        com.lawbackend2.lawbackend2.dto.MyCaseStatisticsResponse response = 
            bankruptCaseService.getMyCaseStatistics(userId);
        return Result.success(response);
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
            @Parameter(description = "案件状态（PENDING/ONGOING/AWAITING/COMPLETED/ARCHIVED）") @RequestParam(required = false) String caseStatus,
            @Parameter(description = "案件进度（FIRST/SECOND/THIRD/FOURTH/FIFTH/SIXTH/SEVENTH）") @RequestParam(required = false) String caseProgress,
            @Parameter(description = "关键词（支持案号、案件名称模糊搜索）") @RequestParam(required = false) String keyword) {

        if (caseStatus != null && com.lawbackend2.lawbackend2.enums.CaseStatus.fromString(caseStatus) == null) {
            throw new com.lawbackend2.lawbackend2.exception.BusinessException("无效的案件状态值: " + caseStatus);
        }
        if (caseProgress != null && com.lawbackend2.lawbackend2.enums.CaseProgress.fromString(caseProgress) == null) {
            throw new com.lawbackend2.lawbackend2.exception.BusinessException("无效的案件进度值: " + caseProgress);
        }

        List<BankruptCase> list = bankruptCaseService.getUserCaseList(userId, pageNum, pageSize, caseStatus, keyword, caseProgress);
        Long total = bankruptCaseService.getUserCaseCount(userId, caseStatus, keyword, caseProgress);

        return Result.success(PageResult.of(total, list, pageNum, pageSize));
    }

    @Operation(summary = "根据用户 ID 查询案件数量")
    @GetMapping("/user/{userId}/count")
    public Result<Long> getUserCaseCount(
            @Parameter(description = "用户 ID") @PathVariable Long userId,
            @Parameter(description = "案件状态 (PENDING/ONGOING/AWAITING/COMPLETED/ARCHIVED)") @RequestParam(required = false) String caseStatus,
            @Parameter(description = "案件进度 (FIRST/SECOND/THIRD/FOURTH/FIFTH/SIXTH/SEVENTH)") @RequestParam(required = false) String caseProgress,
            @Parameter(description = "关键词（支持案号、案件名称模糊搜索）") @RequestParam(required = false) String keyword) {

        if (caseStatus != null && com.lawbackend2.lawbackend2.enums.CaseStatus.fromString(caseStatus) == null) {
            throw new com.lawbackend2.lawbackend2.exception.BusinessException("无效的案件状态值: " + caseStatus);
        }
        if (caseProgress != null && com.lawbackend2.lawbackend2.enums.CaseProgress.fromString(caseProgress) == null) {
            throw new com.lawbackend2.lawbackend2.exception.BusinessException("无效的案件进度值: " + caseProgress);
        }

        Long count = bankruptCaseService.getUserCaseCount(userId, caseStatus, keyword, caseProgress);
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
    @AuditLog(module = "case", moduleName = "案件管理", operationType = "DELETE", operationName = "删除案件")
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

    @Operation(summary = "获取用户最近查询的案件记录")
    @GetMapping("/recent-searches")
    public Result<List<com.lawbackend2.lawbackend2.dto.RecentCaseSearchRecord>> getRecentCaseSearches(
            @Parameter(description = "返回记录数量，默认10条") @RequestParam(defaultValue = "10") Integer limit) {
        Long userId = getCurrentUserId();
        List<com.lawbackend2.lawbackend2.dto.RecentCaseSearchRecord> records =
            recentCaseSearchService.getRecentSearches(userId, limit);
        return Result.success(records);
    }

    @Operation(summary = "清除用户最近查询的案件记录")
    @DeleteMapping("/recent-searches")
    public Result<Void> clearRecentCaseSearches() {
        Long userId = getCurrentUserId();
        recentCaseSearchService.clearRecentSearches(userId);
        log.info("清除用户最近查询记录, userId: {}", userId);
        return Result.success();
    }

    @Operation(summary = "移除指定的最近查询案件记录")
    @DeleteMapping("/recent-searches/{caseId}")
    public Result<Void> removeRecentCaseSearch(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {
        Long userId = getCurrentUserId();
        recentCaseSearchService.removeRecentSearch(userId, caseId);
        log.info("移除用户最近查询记录, userId: {}, caseId: {}", userId, caseId);
        return Result.success();
    }

    private Long getCurrentUserId() {
        return SecurityUtil.getCurrentUserId();
    }
}

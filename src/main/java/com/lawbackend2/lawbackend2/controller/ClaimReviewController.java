package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.ClaimReviewCreateRequest;
import com.lawbackend2.lawbackend2.dto.ClaimReviewUpdateRequest;
import com.lawbackend2.lawbackend2.entity.ClaimReview;
import com.lawbackend2.lawbackend2.service.ClaimReviewService;
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
@Tag(name = "债权审查管理")
@RestController
@RequestMapping("/claim-review")
@Validated
public class ClaimReviewController {

    private final ClaimReviewService claimReviewService;

    public ClaimReviewController(ClaimReviewService claimReviewService) {
        this.claimReviewService = claimReviewService;
    }

    @Operation(summary = "创建债权审查记录")
    @PostMapping
    public Result<Map<String, Object>> createReview(@Valid @RequestBody ClaimReviewCreateRequest request) {
        Long userId = getCurrentUserId();
        ClaimReview claimReview = claimReviewService.createReview(request, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("reviewId", claimReview.getId());
        data.put("reviewRound", claimReview.getReviewRound());

        return Result.success(data);
    }

    @Operation(summary = "获取债权审查详情")
    @GetMapping("/{reviewId}")
    public Result<ClaimReview> getReviewById(@Parameter(description = "审查记录ID") @PathVariable Long reviewId) {
        ClaimReview claimReview = claimReviewService.getReviewById(reviewId);
        return Result.success(claimReview);
    }

    @Operation(summary = "获取债权的所有审查记录")
    @GetMapping("/claim/{claimId}")
    public Result<List<ClaimReview>> getReviewListByClaimId(
            @Parameter(description = "债权申报ID") @PathVariable Long claimId) {
        List<ClaimReview> list = claimReviewService.getReviewListByClaimId(claimId);
        return Result.success(list);
    }

    @Operation(summary = "获取案件的审查记录列表(分页)")
    @GetMapping("/case/{caseId}")
    public Result<PageResult<ClaimReview>> getReviewListByCaseId(
            @Parameter(description = "案件ID") @PathVariable Long caseId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {

        List<ClaimReview> list = claimReviewService.getReviewListByCaseId(caseId, pageNum, pageSize);
        Long total = claimReviewService.getReviewCount(caseId, null);

        return Result.success(PageResult.of(total, list));
    }

    @Operation(summary = "更新债权审查记录")
    @PutMapping("/{reviewId}")
    public Result<Void> updateReview(
            @Parameter(description = "审查记录ID") @PathVariable Long reviewId,
            @Valid @RequestBody ClaimReviewUpdateRequest request) {

        claimReviewService.updateReview(reviewId, request);
        return Result.success();
    }

    @Operation(summary = "删除债权审查记录")
    @DeleteMapping("/{reviewId}")
    public Result<Void> deleteReview(@Parameter(description = "审查记录ID") @PathVariable Long reviewId) {
        claimReviewService.deleteReview(reviewId);
        return Result.success();
    }

    @Operation(summary = "提交债权审查")
    @PostMapping("/{reviewId}/submit")
    public Result<Void> submitReview(@Parameter(description = "审查记录ID") @PathVariable Long reviewId) {
        Long userId = getCurrentUserId();
        claimReviewService.submitReview(reviewId, userId);
        log.info("债权审查提交成功, reviewId: {}, reviewerId: {}", reviewId, userId);
        return Result.success();
    }

    @Operation(summary = "获取待审查的债权")
    @GetMapping("/pending/{caseId}")
    public Result<List<ClaimReview>> getPendingReviews(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {
        List<ClaimReview> list = claimReviewService.getPendingReviews(caseId);
        return Result.success(list);
    }

    @Operation(summary = "获取审查统计")
    @GetMapping("/statistics/{caseId}")
    public Result<Map<String, Object>> getReviewStatistics(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {
        
        Long totalCount = claimReviewService.getReviewCount(caseId, null);
        Long pendingCount = claimReviewService.getReviewCount(caseId, "PENDING");
        Long inProgressCount = claimReviewService.getReviewCount(caseId, "IN_PROGRESS");
        Long completedCount = claimReviewService.getReviewCount(caseId, "COMPLETED");
        Long supplementCount = claimReviewService.getReviewCount(caseId, "SUPPLEMENT");

        Map<String, Object> data = new HashMap<>();
        data.put("totalCount", totalCount);
        data.put("pendingCount", pendingCount);
        data.put("inProgressCount", inProgressCount);
        data.put("completedCount", completedCount);
        data.put("supplementCount", supplementCount);

        return Result.success(data);
    }
    
    @Operation(summary = "获取审查记录列表")
    @GetMapping("/list")
    public Result<PageResult<ClaimReview>> getReviewList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        
        // 这里可以根据实际需求添加查询条件
        // 目前暂时返回空列表，后续可以扩展
        List<ClaimReview> list = claimReviewService.getReviewListByCaseId(null, pageNum, pageSize);
        Long total = claimReviewService.getReviewCount(null, null);

        return Result.success(PageResult.of(total, list));
    }

    private Long getCurrentUserId() {
        return 1L;
    }
}

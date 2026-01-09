package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.CreditorClaimCreateRequest;
import com.lawbackend2.lawbackend2.dto.CreditorClaimReviewRequest;
import com.lawbackend2.lawbackend2.dto.CreditorClaimUpdateRequest;
import com.lawbackend2.lawbackend2.entity.CreditorClaim;
import com.lawbackend2.lawbackend2.service.CreditorClaimService;
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
@Tag(name = "债权申报管理")
@RestController
@RequestMapping("/creditor-claim")
@Validated
public class CreditorClaimController {

    private final CreditorClaimService creditorClaimService;

    public CreditorClaimController(CreditorClaimService creditorClaimService) {
        this.creditorClaimService = creditorClaimService;
    }

    @Operation(summary = "创建债权申报")
    @PostMapping
    public Result<Map<String, Object>> createClaim(@Valid @RequestBody CreditorClaimCreateRequest request) {
        Long userId = getCurrentUserId();
        CreditorClaim creditorClaim = creditorClaimService.createClaim(request, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("claimId", creditorClaim.getId());

        return Result.success(data);
    }

    @Operation(summary = "获取债权申报详情")
    @GetMapping("/{claimId}")
    public Result<CreditorClaim> getClaimById(@Parameter(description = "债权申报ID") @PathVariable Long claimId) {
        CreditorClaim creditorClaim = creditorClaimService.getClaimById(claimId);
        return Result.success(creditorClaim);
    }

    @Operation(summary = "债权申报列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<CreditorClaim>> getClaimList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "登记状态") @RequestParam(required = false) String registrationStatus) {

        List<CreditorClaim> list = creditorClaimService.getClaimList(pageNum, pageSize, caseId, registrationStatus);
        Long total = creditorClaimService.getClaimCount(caseId, registrationStatus);

        return Result.success(PageResult.of(total, list));
    }

    @Operation(summary = "更新债权申报")
    @PutMapping("/{claimId}")
    public Result<Void> updateClaim(
            @Parameter(description = "债权申报ID") @PathVariable Long claimId,
            @Valid @RequestBody CreditorClaimUpdateRequest request) {

        creditorClaimService.updateClaim(claimId, request);
        return Result.success();
    }

    @Operation(summary = "债权申报审核")
    @PostMapping("/{claimId}/review")
    public Result<Void> reviewClaim(
            @Parameter(description = "债权申报ID") @PathVariable Long claimId,
            @Valid @RequestBody CreditorClaimReviewRequest request) {

        Long userId = getCurrentUserId();
        creditorClaimService.reviewClaim(claimId, request, userId);
        log.info("债权申报审核成功, claimId: {}, reviewerId: {}", claimId, userId);
        return Result.success();
    }

    @Operation(summary = "查询债权申报审核状态")
    @GetMapping("/{claimId}/review-status")
    public Result<CreditorClaim> getClaimReviewStatus(@Parameter(description = "债权申报ID") @PathVariable Long claimId) {
        CreditorClaim creditorClaim = creditorClaimService.getClaimReviewStatus(claimId);
        return Result.success(creditorClaim);
    }

    private Long getCurrentUserId() {
        return 1L;
    }
}

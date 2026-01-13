package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.ClaimDetailResponse;
import com.lawbackend2.lawbackend2.dto.ClaimRegistrationCreateRequest;
import com.lawbackend2.lawbackend2.dto.ClaimRegistrationUpdateRequest;
import com.lawbackend2.lawbackend2.entity.ClaimRegistration;
import com.lawbackend2.lawbackend2.service.ClaimRegistrationService;
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
@Tag(name = "债权申报登记管理")
@RestController
@RequestMapping("/claim-registration")
@Validated
public class ClaimRegistrationController {

    private final ClaimRegistrationService claimRegistrationService;

    public ClaimRegistrationController(ClaimRegistrationService claimRegistrationService) {
        this.claimRegistrationService = claimRegistrationService;
    }

    @Operation(summary = "创建债权申报登记")
    @PostMapping
    public Result<Map<String, Object>> createClaim(@Valid @RequestBody ClaimRegistrationCreateRequest request) {
        Long userId = getCurrentUserId();
        ClaimRegistration claimRegistration = claimRegistrationService.createClaim(request, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("claimId", claimRegistration.getId());
        data.put("claimNo", claimRegistration.getClaimNo());

        return Result.success(data);
    }

    @Operation(summary = "获取债权申报详情")
    @GetMapping("/{claimId}")
    public Result<ClaimDetailResponse> getClaimById(@Parameter(description = "债权申报ID") @PathVariable Long claimId) {
        ClaimDetailResponse claimDetail = claimRegistrationService.getClaimDetailById(claimId);
        return Result.success(claimDetail);
    }

    @Operation(summary = "获取债权申报基本信息")
    @GetMapping("/{claimId}/basic")
    public Result<ClaimRegistration> getClaimBasicInfo(@Parameter(description = "债权申报ID") @PathVariable Long claimId) {
        ClaimRegistration claimRegistration = claimRegistrationService.getClaimById(claimId);
        return Result.success(claimRegistration);
    }

    @Operation(summary = "债权申报列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<ClaimRegistration>> getClaimList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "登记状态") @RequestParam(required = false) String registrationStatus) {

        List<ClaimRegistration> list = claimRegistrationService.getClaimList(pageNum, pageSize, caseId, registrationStatus);
        Long total = claimRegistrationService.getClaimCount(caseId, registrationStatus);

        return Result.success(PageResult.of(total, list));
    }

    @Operation(summary = "更新债权申报")
    @PutMapping("/{claimId}")
    public Result<Void> updateClaim(
            @Parameter(description = "债权申报ID") @PathVariable Long claimId,
            @Valid @RequestBody ClaimRegistrationUpdateRequest request) {

        claimRegistrationService.updateClaim(claimId, request);
        return Result.success();
    }

    @Operation(summary = "删除债权申报")
    @DeleteMapping("/{claimId}")
    public Result<Void> deleteClaim(@Parameter(description = "债权申报ID") @PathVariable Long claimId) {
        claimRegistrationService.deleteClaim(claimId);
        return Result.success();
    }

    @Operation(summary = "更新债权申报状态")
    @PutMapping("/{claimId}/status")
    public Result<Void> updateRegistrationStatus(
            @Parameter(description = "债权申报ID") @PathVariable Long claimId,
            @Parameter(description = "状态") @RequestParam String status) {
        Long userId = getCurrentUserId();
        claimRegistrationService.updateRegistrationStatus(claimId, status, userId);
        return Result.success();
    }

    @Operation(summary = "接收申报材料")
    @PostMapping("/{claimId}/material")
    public Result<Void> receiveMaterial(
            @Parameter(description = "债权申报ID") @PathVariable Long claimId,
            @Parameter(description = "接收人") @RequestParam String receiver,
            @Parameter(description = "材料完整性") @RequestParam String completeness) {
        Long userId = getCurrentUserId();
        claimRegistrationService.receiveMaterial(claimId, receiver, completeness, userId);
        return Result.success();
    }

    private Long getCurrentUserId() {
        return 1L;
    }
}

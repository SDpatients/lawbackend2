package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.CommonDebtApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.CommonDebtCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.CommonDebtRepaymentRequest;
import com.lawbackend2.lawbackend2.dto.request.CommonDebtUpdateRequest;
import com.lawbackend2.lawbackend2.entity.CommonDebt;
import com.lawbackend2.lawbackend2.service.CommonDebtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Tag(name = "共益债务管理")
@RestController
@RequestMapping("/common-debt")
@Validated
public class CommonDebtController {

    private final CommonDebtService commonDebtService;

    public CommonDebtController(CommonDebtService commonDebtService) {
        this.commonDebtService = commonDebtService;
    }

    @Operation(summary = "创建共益债务")
    @PostMapping
    public Result<Map<String, Object>> createCommonDebt(@Valid @RequestBody CommonDebtCreateRequest request) {
        Long debtId = commonDebtService.createCommonDebt(request);

        Map<String, Object> data = new HashMap<>();
        data.put("debtId", debtId);

        return Result.success(data);
    }

    @Operation(summary = "共益债务列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<CommonDebt>> getCommonDebtList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "债务类型") @RequestParam(required = false) String debtType,
            @Parameter(description = "审批状态") @RequestParam(required = false) String approvalStatus,
            @Parameter(description = "清偿状态") @RequestParam(required = false) String repaymentStatus) {

        PageResult<CommonDebt> result = commonDebtService.getCommonDebtList(pageNum, pageSize, caseId, debtType, approvalStatus, repaymentStatus);
        return Result.success(result);
    }

    @Operation(summary = "获取共益债务详情")
    @GetMapping("/{debtId}")
    public Result<CommonDebt> getCommonDebtDetail(@Parameter(description = "债务ID") @PathVariable Long debtId) {
        CommonDebt debt = commonDebtService.getCommonDebtDetail(debtId);
        return Result.success(debt);
    }

    @Operation(summary = "更新共益债务信息")
    @PutMapping("/{debtId}")
    public Result<Void> updateCommonDebt(
            @Parameter(description = "债务ID") @PathVariable Long debtId,
            @Valid @RequestBody CommonDebtUpdateRequest request) {

        commonDebtService.updateCommonDebt(debtId, request);
        return Result.success();
    }

    @Operation(summary = "审批共益债务")
    @PutMapping("/{debtId}/approval")
    public Result<Void> approveCommonDebt(
            @Parameter(description = "债务ID") @PathVariable Long debtId,
            @Valid @RequestBody CommonDebtApprovalRequest request) {

        commonDebtService.approveCommonDebt(debtId, request);
        return Result.success();
    }

    @Operation(summary = "清偿共益债务")
    @PutMapping("/{debtId}/repayment")
    public Result<Void> repayCommonDebt(
            @Parameter(description = "债务ID") @PathVariable Long debtId,
            @Valid @RequestBody CommonDebtRepaymentRequest request) {

        commonDebtService.repayCommonDebt(debtId, request);
        return Result.success();
    }

    @Operation(summary = "删除共益债务")
    @DeleteMapping("/{debtId}")
    public Result<Void> deleteCommonDebt(@Parameter(description = "债务ID") @PathVariable Long debtId) {
        commonDebtService.deleteCommonDebt(debtId);
        return Result.success();
    }
}
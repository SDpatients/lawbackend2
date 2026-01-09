package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.FundApprovalCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.FundApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.FundApprovalStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.FundApprovalUpdateRequest;
import com.lawbackend2.lawbackend2.entity.FundApproval;
import com.lawbackend2.lawbackend2.service.FundApprovalService;
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
@Tag(name = "资金审批管理")
@RestController
@RequestMapping("/fund-approval")
@Validated
public class FundApprovalController {

    private final FundApprovalService fundApprovalService;

    public FundApprovalController(FundApprovalService fundApprovalService) {
        this.fundApprovalService = fundApprovalService;
    }

    @Operation(summary = "创建资金审批")
    @PostMapping
    public Result<Map<String, Object>> createFundApproval(@Valid @RequestBody FundApprovalCreateRequest request) {
        Long approvalId = fundApprovalService.createFundApproval(request);

        Map<String, Object> data = new HashMap<>();
        data.put("approvalId", approvalId);

        return Result.success(data);
    }

    @Operation(summary = "资金审批列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<FundApproval>> getFundApprovalList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "审批状态") @RequestParam(required = false) String approvalStatus,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {

        PageResult<FundApproval> result = fundApprovalService.getFundApprovalList(pageNum, pageSize, caseId, approvalStatus, status);
        return Result.success(result);
    }

    @Operation(summary = "获取资金审批详情")
    @GetMapping("/{approvalId}")
    public Result<FundApproval> getFundApprovalDetail(@Parameter(description = "审批ID") @PathVariable Long approvalId) {
        FundApproval fundApproval = fundApprovalService.getFundApprovalDetail(approvalId);
        return Result.success(fundApproval);
    }

    @Operation(summary = "更新资金审批信息")
    @PutMapping("/{approvalId}")
    public Result<Void> updateFundApproval(
            @Parameter(description = "审批ID") @PathVariable Long approvalId,
            @Valid @RequestBody FundApprovalUpdateRequest request) {

        fundApprovalService.updateFundApproval(approvalId, request);
        return Result.success();
    }

    @Operation(summary = "资金审批")
    @PostMapping("/{approvalId}/approve")
    public Result<Void> approveFundApproval(
            @Parameter(description = "审批ID") @PathVariable Long approvalId,
            @Valid @RequestBody FundApprovalRequest request) {

        fundApprovalService.approveFundApproval(approvalId, request);
        return Result.success();
    }

    @Operation(summary = "更新资金审批状态")
    @PutMapping("/{approvalId}/status")
    public Result<Void> updateFundApprovalStatus(
            @Parameter(description = "审批ID") @PathVariable Long approvalId,
            @Valid @RequestBody FundApprovalStatusRequest request) {

        fundApprovalService.updateFundApprovalStatus(approvalId, request);
        return Result.success();
    }

    @Operation(summary = "删除资金审批")
    @DeleteMapping("/{approvalId}")
    public Result<Void> deleteFundApproval(@Parameter(description = "审批ID") @PathVariable Long approvalId) {
        fundApprovalService.deleteFundApproval(approvalId);
        return Result.success();
    }
}

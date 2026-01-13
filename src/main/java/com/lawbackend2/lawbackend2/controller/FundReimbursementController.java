package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.FundReimbursementApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.FundReimbursementCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.FundReimbursementPaymentRequest;
import com.lawbackend2.lawbackend2.entity.FundReimbursement;
import com.lawbackend2.lawbackend2.service.FundReimbursementService;
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
@Tag(name = "费用报销管理")
@RestController
@RequestMapping("/fund-reimbursement")
@Validated
public class FundReimbursementController {

    private final FundReimbursementService fundReimbursementService;

    public FundReimbursementController(FundReimbursementService fundReimbursementService) {
        this.fundReimbursementService = fundReimbursementService;
    }

    @Operation(summary = "创建费用报销")
    @PostMapping
    public Result<Map<String, Object>> createFundReimbursement(@Valid @RequestBody FundReimbursementCreateRequest request) {
        Long reimbursementId = fundReimbursementService.createFundReimbursement(request);

        Map<String, Object> data = new HashMap<>();
        data.put("reimbursementId", reimbursementId);

        return Result.success(data);
    }

    @Operation(summary = "费用报销列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<FundReimbursement>> getFundReimbursementList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "报销类型") @RequestParam(required = false) String reimbursementType,
            @Parameter(description = "申请人ID") @RequestParam(required = false) Long applicantId,
            @Parameter(description = "审批状态") @RequestParam(required = false) String approvalStatus,
            @Parameter(description = "支付状态") @RequestParam(required = false) String paymentStatus) {

        PageResult<FundReimbursement> result = fundReimbursementService.getFundReimbursementList(pageNum, pageSize, caseId, reimbursementType, applicantId, approvalStatus, paymentStatus);
        return Result.success(result);
    }

    @Operation(summary = "获取费用报销详情")
    @GetMapping("/{reimbursementId}")
    public Result<FundReimbursement> getFundReimbursementDetail(@Parameter(description = "报销ID") @PathVariable Long reimbursementId) {
        FundReimbursement reimbursement = fundReimbursementService.getFundReimbursementDetail(reimbursementId);
        return Result.success(reimbursement);
    }

    @Operation(summary = "审批费用报销")
    @PutMapping("/{reimbursementId}/approval")
    public Result<Void> approveFundReimbursement(
            @Parameter(description = "报销ID") @PathVariable Long reimbursementId,
            @Valid @RequestBody FundReimbursementApprovalRequest request) {

        fundReimbursementService.approveFundReimbursement(reimbursementId, request);
        return Result.success();
    }

    @Operation(summary = "支付费用报销")
    @PutMapping("/{reimbursementId}/payment")
    public Result<Void> payFundReimbursement(
            @Parameter(description = "报销ID") @PathVariable Long reimbursementId,
            @Valid @RequestBody FundReimbursementPaymentRequest request) {

        fundReimbursementService.payFundReimbursement(reimbursementId, request);
        return Result.success();
    }

    @Operation(summary = "删除费用报销")
    @DeleteMapping("/{reimbursementId}")
    public Result<Void> deleteFundReimbursement(@Parameter(description = "报销ID") @PathVariable Long reimbursementId) {
        fundReimbursementService.deleteFundReimbursement(reimbursementId);
        return Result.success();
    }
}
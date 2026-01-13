package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.BankruptcyExpenseApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.BankruptcyExpenseCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.BankruptcyExpensePaymentRequest;
import com.lawbackend2.lawbackend2.dto.request.BankruptcyExpenseUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankruptcyExpense;
import com.lawbackend2.lawbackend2.service.BankruptcyExpenseService;
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
@Tag(name = "破产费用管理")
@RestController
@RequestMapping("/bankruptcy-expense")
@Validated
public class BankruptcyExpenseController {

    private final BankruptcyExpenseService bankruptcyExpenseService;

    public BankruptcyExpenseController(BankruptcyExpenseService bankruptcyExpenseService) {
        this.bankruptcyExpenseService = bankruptcyExpenseService;
    }

    @Operation(summary = "创建破产费用")
    @PostMapping
    public Result<Map<String, Object>> createBankruptcyExpense(@Valid @RequestBody BankruptcyExpenseCreateRequest request) {
        Long expenseId = bankruptcyExpenseService.createBankruptcyExpense(request);

        Map<String, Object> data = new HashMap<>();
        data.put("expenseId", expenseId);

        return Result.success(data);
    }

    @Operation(summary = "破产费用列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<BankruptcyExpense>> getBankruptcyExpenseList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "费用类型") @RequestParam(required = false) String expenseType,
            @Parameter(description = "审批状态") @RequestParam(required = false) String approvalStatus,
            @Parameter(description = "支付状态") @RequestParam(required = false) String paymentStatus) {

        PageResult<BankruptcyExpense> result = bankruptcyExpenseService.getBankruptcyExpenseList(pageNum, pageSize, caseId, expenseType, approvalStatus, paymentStatus);
        return Result.success(result);
    }

    @Operation(summary = "获取破产费用详情")
    @GetMapping("/{expenseId}")
    public Result<BankruptcyExpense> getBankruptcyExpenseDetail(@Parameter(description = "费用ID") @PathVariable Long expenseId) {
        BankruptcyExpense expense = bankruptcyExpenseService.getBankruptcyExpenseDetail(expenseId);
        return Result.success(expense);
    }

    @Operation(summary = "更新破产费用信息")
    @PutMapping("/{expenseId}")
    public Result<Void> updateBankruptcyExpense(
            @Parameter(description = "费用ID") @PathVariable Long expenseId,
            @Valid @RequestBody BankruptcyExpenseUpdateRequest request) {

        bankruptcyExpenseService.updateBankruptcyExpense(expenseId, request);
        return Result.success();
    }

    @Operation(summary = "审批破产费用")
    @PutMapping("/{expenseId}/approval")
    public Result<Void> approveBankruptcyExpense(
            @Parameter(description = "费用ID") @PathVariable Long expenseId,
            @Valid @RequestBody BankruptcyExpenseApprovalRequest request) {

        bankruptcyExpenseService.approveBankruptcyExpense(expenseId, request);
        return Result.success();
    }

    @Operation(summary = "支付破产费用")
    @PutMapping("/{expenseId}/payment")
    public Result<Void> payBankruptcyExpense(
            @Parameter(description = "费用ID") @PathVariable Long expenseId,
            @Valid @RequestBody BankruptcyExpensePaymentRequest request) {

        bankruptcyExpenseService.payBankruptcyExpense(expenseId, request);
        return Result.success();
    }

    @Operation(summary = "删除破产费用")
    @DeleteMapping("/{expenseId}")
    public Result<Void> deleteBankruptcyExpense(@Parameter(description = "费用ID") @PathVariable Long expenseId) {
        bankruptcyExpenseService.deleteBankruptcyExpense(expenseId);
        return Result.success();
    }
}
package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.BankAccountTransactionCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountTransactionUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.BankAccountTransactionResponse;
import com.lawbackend2.lawbackend2.entity.BankAccountTransaction;
import com.lawbackend2.lawbackend2.service.BankAccountTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Tag(name = "账户交易明细管理")
@RestController
@RequestMapping("/bank-account-transaction")
@Validated
public class BankAccountTransactionController {

    private final BankAccountTransactionService transactionService;

    public BankAccountTransactionController(BankAccountTransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "创建交易记录")
    @PostMapping
    public Result<Map<String, Object>> createTransaction(@RequestBody BankAccountTransactionCreateRequest request) {
        Long userId = getCurrentUserId();
        Long transactionId = transactionService.createTransaction(request, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("transactionId", transactionId);

        return Result.success(data);
    }

    @Operation(summary = "交易记录列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<BankAccountTransactionResponse>> getTransactionList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "账户ID") @RequestParam(required = false) Long accountId,
            @Parameter(description = "交易类型(IN-流入/OUT-流出)") @RequestParam(required = false) String transactionType,
            @Parameter(description = "业务类型") @RequestParam(required = false) String businessType,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId) {

        Long userId = getCurrentUserId();
        PageResult<BankAccountTransactionResponse> result = transactionService.getTransactionList(
                pageNum, pageSize, accountId, transactionType, businessType, startDate, endDate, caseId, userId);
        return Result.success(result);
    }

    @Operation(summary = "获取交易记录详情")
    @GetMapping("/{transactionId}")
    public Result<BankAccountTransaction> getTransactionDetail(@Parameter(description = "交易记录ID") @PathVariable Long transactionId) {
        Long userId = getCurrentUserId();
        BankAccountTransaction transaction = transactionService.getTransactionDetail(transactionId, userId);
        return Result.success(transaction);
    }

    @Operation(summary = "更新交易记录")
    @PutMapping("/{transactionId}")
    public Result<Void> updateTransaction(
            @Parameter(description = "交易记录ID") @PathVariable Long transactionId,
            @Valid @RequestBody BankAccountTransactionUpdateRequest request) {

        Long userId = getCurrentUserId();
        transactionService.updateTransaction(transactionId, request, userId);
        return Result.success();
    }

    @Operation(summary = "删除交易记录")
    @DeleteMapping("/{transactionId}")
    public Result<Void> deleteTransaction(@Parameter(description = "交易记录ID") @PathVariable Long transactionId) {
        Long userId = getCurrentUserId();
        transactionService.deleteTransaction(transactionId, userId);
        return Result.success();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            return (Long) authentication.getPrincipal();
        }
        throw new RuntimeException("无法获取当前用户ID");
    }
}

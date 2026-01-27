package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.BankAccountCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountPasswordRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.BankAccountResponse;
import com.lawbackend2.lawbackend2.dto.response.BankAccountTransactionResponse;
import com.lawbackend2.lawbackend2.dto.response.BankAccountWithTransactionsResponse;
import com.lawbackend2.lawbackend2.entity.BankAccount;
import com.lawbackend2.lawbackend2.service.BankAccountService;
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
@Tag(name = "银行账户管理")
@RestController
@RequestMapping("/bank-account")
@Validated
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final BankAccountTransactionService transactionService;

    public BankAccountController(BankAccountService bankAccountService, BankAccountTransactionService transactionService) {
        this.bankAccountService = bankAccountService;
        this.transactionService = transactionService;
    }

    @Operation(summary = "创建银行账户")
    @PostMapping
    public Result<Map<String, Object>> createBankAccount(@RequestBody BankAccountCreateRequest request) {
        Long userId = getCurrentUserId();
        Long accountId = bankAccountService.createBankAccount(request, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("accountId", accountId);

        return Result.success(data);
    }

    @Operation(summary = "银行账户列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<BankAccountResponse>> getBankAccountList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "账户类型") @RequestParam(required = false) String accountType,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "账户名称(模糊查询)") @RequestParam(required = false) String accountName,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId) {

        Long userId = getCurrentUserId();
        PageResult<BankAccountResponse> result = bankAccountService.getBankAccountList(pageNum, pageSize, accountType, status, accountName, caseId, userId);
        return Result.success(result);
    }

    @Operation(summary = "获取银行账户详情")
    @GetMapping("/{accountId}")
    public Result<BankAccount> getBankAccountDetail(@Parameter(description = "账户ID") @PathVariable Long accountId) {
        Long userId = getCurrentUserId();
        BankAccount bankAccount = bankAccountService.getBankAccountDetail(accountId, userId);
        return Result.success(bankAccount);
    }

    @Operation(summary = "更新银行账户信息")
    @PutMapping("/{accountId}")
    public Result<Void> updateBankAccount(
            @Parameter(description = "账户ID") @PathVariable Long accountId,
            @Valid @RequestBody BankAccountUpdateRequest request) {

        Long userId = getCurrentUserId();
        bankAccountService.updateBankAccount(accountId, request, userId);
        return Result.success();
    }

    @Operation(summary = "修改银行账户密码")
    @PutMapping("/{accountId}/password")
    public Result<Void> updateBankAccountPassword(
            @Parameter(description = "账户ID") @PathVariable Long accountId,
            @Valid @RequestBody BankAccountPasswordRequest request) {

        Long userId = getCurrentUserId();
        bankAccountService.updateBankAccountPassword(accountId, request, userId);
        return Result.success();
    }

    @Operation(summary = "银行账户状态管理")
    @PutMapping("/{accountId}/status")
    public Result<Void> updateBankAccountStatus(
            @Parameter(description = "账户ID") @PathVariable Long accountId,
            @Valid @RequestBody BankAccountStatusRequest request) {

        Long userId = getCurrentUserId();
        bankAccountService.updateBankAccountStatus(accountId, request, userId);
        return Result.success();
    }

    @Operation(summary = "删除银行账户")
    @DeleteMapping("/{accountId}")
    public Result<Void> deleteBankAccount(@Parameter(description = "账户ID") @PathVariable Long accountId) {
        bankAccountService.deleteBankAccount(accountId);
        return Result.success();
    }

    @Operation(summary = "获取银行账户交易明细")
    @GetMapping("/{accountId}/transactions")
    public Result<PageResult<BankAccountTransactionResponse>> getAccountTransactions(
            @Parameter(description = "账户ID") @PathVariable Long accountId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "交易类型(IN-流入/OUT-流出)") @RequestParam(required = false) String transactionType,
            @Parameter(description = "业务类型") @RequestParam(required = false) String businessType,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        Long userId = getCurrentUserId();
        PageResult<BankAccountTransactionResponse> result = transactionService.getTransactionList(
                pageNum, pageSize, accountId, transactionType, businessType, startDate, endDate, null, userId);
        return Result.success(result);
    }

    @Operation(summary = "获取银行账户及全部交易明细(含总流入总流出)")
    @GetMapping("/{accountId}/with-transactions")
    public Result<BankAccountWithTransactionsResponse> getBankAccountWithTransactions(
            @Parameter(description = "账户ID") @PathVariable Long accountId) {

        Long userId = getCurrentUserId();
        BankAccountWithTransactionsResponse result = bankAccountService.getBankAccountWithTransactions(accountId, userId);
        return Result.success(result);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            return (Long) authentication.getPrincipal();
        }
        throw new RuntimeException("无法获取当前用户ID");
    }
}

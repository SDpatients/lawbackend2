package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.BankAccountCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountPasswordRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankAccount;
import com.lawbackend2.lawbackend2.service.BankAccountService;
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
@Tag(name = "银行账户管理")
@RestController
@RequestMapping("/bank-account")
@Validated
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @Operation(summary = "创建银行账户")
    @PostMapping
    public Result<Map<String, Object>> createBankAccount(@RequestBody BankAccountCreateRequest request) {
        Long accountId = bankAccountService.createBankAccount(request);

        Map<String, Object> data = new HashMap<>();
        data.put("accountId", accountId);

        return Result.success(data);
    }

    @Operation(summary = "银行账户列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<BankAccount>> getBankAccountList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "账户类型") @RequestParam(required = false) String accountType,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "账户名称(模糊查询)") @RequestParam(required = false) String accountName) {

        PageResult<BankAccount> result = bankAccountService.getBankAccountList(pageNum, pageSize, accountType, status, accountName);
        return Result.success(result);
    }

    @Operation(summary = "获取银行账户详情")
    @GetMapping("/{accountId}")
    public Result<BankAccount> getBankAccountDetail(@Parameter(description = "账户ID") @PathVariable Long accountId) {
        BankAccount bankAccount = bankAccountService.getBankAccountDetail(accountId);
        return Result.success(bankAccount);
    }

    @Operation(summary = "更新银行账户信息")
    @PutMapping("/{accountId}")
    public Result<Void> updateBankAccount(
            @Parameter(description = "账户ID") @PathVariable Long accountId,
            @Valid @RequestBody BankAccountUpdateRequest request) {

        bankAccountService.updateBankAccount(accountId, request);
        return Result.success();
    }

    @Operation(summary = "修改银行账户密码")
    @PutMapping("/{accountId}/password")
    public Result<Void> updateBankAccountPassword(
            @Parameter(description = "账户ID") @PathVariable Long accountId,
            @Valid @RequestBody BankAccountPasswordRequest request) {

        bankAccountService.updateBankAccountPassword(accountId, request);
        return Result.success();
    }

    @Operation(summary = "银行账户状态管理")
    @PutMapping("/{accountId}/status")
    public Result<Void> updateBankAccountStatus(
            @Parameter(description = "账户ID") @PathVariable Long accountId,
            @Valid @RequestBody BankAccountStatusRequest request) {

        bankAccountService.updateBankAccountStatus(accountId, request);
        return Result.success();
    }

    @Operation(summary = "删除银行账户")
    @DeleteMapping("/{accountId}")
    public Result<Void> deleteBankAccount(@Parameter(description = "账户ID") @PathVariable Long accountId) {
        bankAccountService.deleteBankAccount(accountId);
        return Result.success();
    }
}

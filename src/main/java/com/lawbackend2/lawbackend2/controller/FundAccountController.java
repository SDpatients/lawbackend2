package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.AuditLog;
import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.FundAccountBalanceRequest;
import com.lawbackend2.lawbackend2.dto.request.FundAccountCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.FundAccountStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.FundAccountUpdateRequest;
import com.lawbackend2.lawbackend2.entity.FundAccount;
import com.lawbackend2.lawbackend2.service.FundAccountService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
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
@Tag(name = "资金账户管理")
@RestController
@RequestMapping("/fund-account")
@Validated
public class FundAccountController {

    private final FundAccountService fundAccountService;

    public FundAccountController(FundAccountService fundAccountService) {
        this.fundAccountService = fundAccountService;
    }

    @Operation(summary = "创建资金账户")
    @PostMapping
    @AuditLog(module = "fund-account", moduleName = "资金账户管理", operationType = "CREATE", operationName = "创建资金账户")
    public Result<Map<String, Object>> createFundAccount(@Valid @RequestBody FundAccountCreateRequest request) {
        Long userId = getCurrentUserId();
        Long fundAccountId = fundAccountService.createFundAccount(request, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("fundAccountId", fundAccountId);

        return Result.success(data);
    }

    @Operation(summary = "资金账户列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<FundAccount>> getFundAccountList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {

        PageResult<FundAccount> result = fundAccountService.getFundAccountList(pageNum, pageSize, caseId, status);
        return Result.success(result);
    }

    @Operation(summary = "案件资金账户简单列表")
    @GetMapping("/list/simple")
    public Result<java.util.List<com.lawbackend2.lawbackend2.dto.response.FundAccountSimpleResponse>> getSimpleFundAccountList(
            @Parameter(description = "案件ID") @RequestParam(required = true) Long caseId) {

        java.util.List<com.lawbackend2.lawbackend2.dto.response.FundAccountSimpleResponse> result = fundAccountService.getSimpleFundAccountListByCaseId(caseId);
        return Result.success(result);
    }

    @Operation(summary = "获取资金账户详情")
    @GetMapping("/{fundAccountId}")
    public Result<FundAccount> getFundAccountDetail(@Parameter(description = "资金账户ID") @PathVariable Long fundAccountId) {
        FundAccount fundAccount = fundAccountService.getFundAccountDetail(fundAccountId);
        return Result.success(fundAccount);
    }

    @Operation(summary = "更新资金账户信息")
    @PutMapping("/{fundAccountId}")
    @AuditLog(module = "fund-account", moduleName = "资金账户管理", operationType = "UPDATE", operationName = "更新资金账户信息")
    public Result<Void> updateFundAccount(
            @Parameter(description = "资金账户ID") @PathVariable Long fundAccountId,
            @Valid @RequestBody FundAccountUpdateRequest request) {

        Long userId = getCurrentUserId();
        fundAccountService.updateFundAccount(fundAccountId, request, userId);
        return Result.success();
    }

    @Operation(summary = "更新资金账户余额")
    @PutMapping("/{fundAccountId}/balance")
    @AuditLog(module = "fund-account", moduleName = "资金账户管理", operationType = "UPDATE", operationName = "更新资金账户余额")
    public Result<Void> updateFundAccountBalance(
            @Parameter(description = "资金账户ID") @PathVariable Long fundAccountId,
            @Valid @RequestBody FundAccountBalanceRequest request) {

        Long userId = getCurrentUserId();
        fundAccountService.updateFundAccountBalance(fundAccountId, request, userId);
        return Result.success();
    }

    @Operation(summary = "更新资金账户状态")
    @PutMapping("/{fundAccountId}/status")
    public Result<Void> updateFundAccountStatus(
            @Parameter(description = "资金账户ID") @PathVariable Long fundAccountId,
            @Valid @RequestBody FundAccountStatusRequest request) {

        Long userId = getCurrentUserId();
        fundAccountService.updateFundAccountStatus(fundAccountId, request, userId);
        return Result.success();
    }

    @Operation(summary = "删除资金账户")
    @DeleteMapping("/{fundAccountId}")
    @AuditLog(module = "fund-account", moduleName = "资金账户管理", operationType = "DELETE", operationName = "删除资金账户")
    public Result<Void> deleteFundAccount(@Parameter(description = "资金账户ID") @PathVariable Long fundAccountId) {
        Long userId = getCurrentUserId();
        fundAccountService.deleteFundAccount(fundAccountId, userId);
        return Result.success();
    }

    private Long getCurrentUserId() {
        return SecurityUtil.getCurrentUserId();
    }
}

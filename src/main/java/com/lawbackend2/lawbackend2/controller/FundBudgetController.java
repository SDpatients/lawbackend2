package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.FundBudgetApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.FundBudgetCreateRequest;
import com.lawbackend2.lawbackend2.entity.FundBudget;
import com.lawbackend2.lawbackend2.service.FundBudgetService;
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
@Tag(name = "资金预算管理")
@RestController
@RequestMapping("/fund-budget")
@Validated
public class FundBudgetController {

    private final FundBudgetService fundBudgetService;

    public FundBudgetController(FundBudgetService fundBudgetService) {
        this.fundBudgetService = fundBudgetService;
    }

    @Operation(summary = "创建资金预算")
    @PostMapping
    public Result<Map<String, Object>> createFundBudget(@Valid @RequestBody FundBudgetCreateRequest request) {
        Long budgetId = fundBudgetService.createFundBudget(request);

        Map<String, Object> data = new HashMap<>();
        data.put("budgetId", budgetId);

        return Result.success(data);
    }

    @Operation(summary = "资金预算列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<FundBudget>> getFundBudgetList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "预算类型") @RequestParam(required = false) String budgetType,
            @Parameter(description = "预算状态") @RequestParam(required = false) String budgetStatus,
            @Parameter(description = "审批状态") @RequestParam(required = false) String approvalStatus) {

        PageResult<FundBudget> result = fundBudgetService.getFundBudgetList(pageNum, pageSize, caseId, budgetType, budgetStatus, approvalStatus);
        return Result.success(result);
    }

    @Operation(summary = "获取资金预算详情")
    @GetMapping("/{budgetId}")
    public Result<FundBudget> getFundBudgetDetail(@Parameter(description = "预算ID") @PathVariable Long budgetId) {
        FundBudget budget = fundBudgetService.getFundBudgetDetail(budgetId);
        return Result.success(budget);
    }

    @Operation(summary = "审批资金预算")
    @PutMapping("/{budgetId}/approval")
    public Result<Void> approveFundBudget(
            @Parameter(description = "预算ID") @PathVariable Long budgetId,
            @Valid @RequestBody FundBudgetApprovalRequest request) {

        fundBudgetService.approveFundBudget(budgetId, request);
        return Result.success();
    }

    @Operation(summary = "删除资金预算")
    @DeleteMapping("/{budgetId}")
    public Result<Void> deleteFundBudget(@Parameter(description = "预算ID") @PathVariable Long budgetId) {
        fundBudgetService.deleteFundBudget(budgetId);
        return Result.success();
    }
}
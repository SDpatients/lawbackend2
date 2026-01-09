package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.response.FundAccountStatistics;
import com.lawbackend2.lawbackend2.dto.response.FundApprovalStatistics;
import com.lawbackend2.lawbackend2.dto.response.FundTransactionExport;
import com.lawbackend2.lawbackend2.dto.response.FundTransactionStatistics;
import com.lawbackend2.lawbackend2.dto.response.WorkPlanStatistics;
import com.lawbackend2.lawbackend2.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "统计管理")
@RestController
@RequestMapping("/statistics")
@Validated
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Operation(summary = "资金流水统计")
    @GetMapping("/fund-transaction")
    public Result<FundTransactionStatistics> getFundTransactionStatistics(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {

        FundTransactionStatistics statistics = statisticsService.getFundTransactionStatistics(caseId);
        return Result.success(statistics);
    }

    @Operation(summary = "资金流水导出")
    @GetMapping("/fund-transaction/export")
    public Result<FundTransactionExport> exportFundTransactions(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {

        FundTransactionExport export = statisticsService.exportFundTransactions(caseId);
        return Result.success(export);
    }

    @Operation(summary = "资金审批统计")
    @GetMapping("/fund-approval")
    public Result<FundApprovalStatistics> getFundApprovalStatistics(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {

        FundApprovalStatistics statistics = statisticsService.getFundApprovalStatistics(caseId);
        return Result.success(statistics);
    }

    @Operation(summary = "资金账户统计")
    @GetMapping("/fund-account")
    public Result<FundAccountStatistics> getFundAccountStatistics(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {

        FundAccountStatistics statistics = statisticsService.getFundAccountStatistics(caseId);
        return Result.success(statistics);
    }

    @Operation(summary = "工作计划统计")
    @GetMapping("/work-plan")
    public Result<WorkPlanStatistics> getWorkPlanStatistics(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {

        WorkPlanStatistics statistics = statisticsService.getWorkPlanStatistics(caseId);
        return Result.success(statistics);
    }
}

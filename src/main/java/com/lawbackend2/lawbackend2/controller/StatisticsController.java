package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.response.FundAccountStatistics;
import com.lawbackend2.lawbackend2.dto.response.FundApprovalStatistics;
import com.lawbackend2.lawbackend2.dto.response.FundTransactionExport;
import com.lawbackend2.lawbackend2.dto.response.FundTransactionStatistics;
import com.lawbackend2.lawbackend2.dto.response.TimeTrendStatistics;
import com.lawbackend2.lawbackend2.dto.response.WorkPlanStatistics;
import com.lawbackend2.lawbackend2.dto.response.CrossAnalysisStatistics;
import com.lawbackend2.lawbackend2.dto.response.RankingStatistics;
import com.lawbackend2.lawbackend2.dto.response.FundApprovalExport;
import com.lawbackend2.lawbackend2.dto.response.FundAccountExport;
import com.lawbackend2.lawbackend2.dto.response.WorkPlanExport;
import com.lawbackend2.lawbackend2.service.StatisticsService;
import com.lawbackend2.lawbackend2.util.StatisticsPermissionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "统计管理")
@RestController
@RequestMapping("/statistics")
@Validated
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final StatisticsPermissionUtil statisticsPermissionUtil;

    public StatisticsController(StatisticsService statisticsService, StatisticsPermissionUtil statisticsPermissionUtil) {
        this.statisticsService = statisticsService;
        this.statisticsPermissionUtil = statisticsPermissionUtil;
    }

    @Operation(summary = "资金流水统计")
    @GetMapping("/fund-transaction")
    public Result<FundTransactionStatistics> getFundTransactionStatistics(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {

        statisticsPermissionUtil.checkStatisticsPermission();
        statisticsPermissionUtil.checkCaseAccessPermission(caseId);
        FundTransactionStatistics statistics = statisticsService.getFundTransactionStatistics(caseId);
        return Result.success(statistics);
    }

    @Operation(summary = "资金流水导出")
    @GetMapping("/fund-transaction/export")
    public Result<FundTransactionExport> exportFundTransactions(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {

        statisticsPermissionUtil.checkStatisticsPermission();
        statisticsPermissionUtil.checkCaseAccessPermission(caseId);
        FundTransactionExport export = statisticsService.exportFundTransactions(caseId);
        return Result.success(export);
    }

    @Operation(summary = "资金审批统计")
    @GetMapping("/fund-approval")
    public Result<FundApprovalStatistics> getFundApprovalStatistics(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {

        statisticsPermissionUtil.checkStatisticsPermission();
        statisticsPermissionUtil.checkCaseAccessPermission(caseId);
        FundApprovalStatistics statistics = statisticsService.getFundApprovalStatistics(caseId);
        return Result.success(statistics);
    }

    @Operation(summary = "资金账户统计")
    @GetMapping("/fund-account")
    public Result<FundAccountStatistics> getFundAccountStatistics(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {

        statisticsPermissionUtil.checkStatisticsPermission();
        statisticsPermissionUtil.checkCaseAccessPermission(caseId);
        FundAccountStatistics statistics = statisticsService.getFundAccountStatistics(caseId);
        return Result.success(statistics);
    }

    @Operation(summary = "工作计划统计")
    @GetMapping("/work-plan")
    public Result<WorkPlanStatistics> getWorkPlanStatistics(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {

        statisticsPermissionUtil.checkStatisticsPermission();
        statisticsPermissionUtil.checkCaseAccessPermission(caseId);
        WorkPlanStatistics statistics = statisticsService.getWorkPlanStatistics(caseId);
        return Result.success(statistics);
    }

    @Operation(summary = "资金流水趋势分析")
    @GetMapping("/fund-transaction/trend")
    public Result<TimeTrendStatistics> getFundTransactionTrend(
            @Parameter(description = "案件ID") @RequestParam Long caseId,
            @Parameter(description = "周期类型: month/quarter/year") @RequestParam(defaultValue = "month") String period) {

        statisticsPermissionUtil.checkStatisticsPermission();
        statisticsPermissionUtil.checkCaseAccessPermission(caseId);
        TimeTrendStatistics trend = statisticsService.getFundTransactionTrend(caseId, period);
        return Result.success(trend);
    }

    @Operation(summary = "资金审批趋势分析")
    @GetMapping("/fund-approval/trend")
    public Result<TimeTrendStatistics> getFundApprovalTrend(
            @Parameter(description = "案件ID") @RequestParam Long caseId,
            @Parameter(description = "周期类型: month/quarter/year") @RequestParam(defaultValue = "month") String period) {

        statisticsPermissionUtil.checkStatisticsPermission();
        statisticsPermissionUtil.checkCaseAccessPermission(caseId);
        TimeTrendStatistics trend = statisticsService.getFundApprovalTrend(caseId, period);
        return Result.success(trend);
    }

    @Operation(summary = "案件趋势分析")
    @GetMapping("/case/trend")
    public Result<TimeTrendStatistics> getCaseTrend(
            @Parameter(description = "周期类型: month/quarter/year") @RequestParam(defaultValue = "month") String period) {

        statisticsPermissionUtil.checkStatisticsPermission();
        TimeTrendStatistics trend = statisticsService.getCaseTrend(period);
        return Result.success(trend);
    }

    @Operation(summary = "案件状态与进度交叉分析")
    @GetMapping("/case/cross-analysis")
    public Result<CrossAnalysisStatistics> getCaseCrossAnalysis() {
        statisticsPermissionUtil.checkStatisticsPermission();
        CrossAnalysisStatistics analysis = statisticsService.getCaseCrossAnalysis();
        return Result.success(analysis);
    }

    @Operation(summary = "案件金额排名")
    @GetMapping("/case/amount-ranking")
    public Result<RankingStatistics> getCaseAmountRanking(
            @Parameter(description = "前N名") @RequestParam(defaultValue = "10") Integer topN) {
        statisticsPermissionUtil.checkStatisticsPermission();
        RankingStatistics ranking = statisticsService.getCaseAmountRanking(topN);
        return Result.success(ranking);
    }

    @Operation(summary = "债权申报金额排名")
    @GetMapping("/creditor-claim/amount-ranking")
    public Result<RankingStatistics> getCreditorClaimAmountRanking(
            @Parameter(description = "前N名") @RequestParam(defaultValue = "10") Integer topN) {
        statisticsPermissionUtil.checkStatisticsPermission();
        RankingStatistics ranking = statisticsService.getCreditorClaimAmountRanking(topN);
        return Result.success(ranking);
    }

    @Operation(summary = "资金审批导出")
    @GetMapping("/fund-approval/export")
    public Result<FundApprovalExport> exportFundApprovals(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {
        statisticsPermissionUtil.checkStatisticsPermission();
        statisticsPermissionUtil.checkCaseAccessPermission(caseId);
        FundApprovalExport export = statisticsService.exportFundApprovals(caseId);
        return Result.success(export);
    }

    @Operation(summary = "资金账户导出")
    @GetMapping("/fund-account/export")
    public Result<FundAccountExport> exportFundAccounts(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {
        statisticsPermissionUtil.checkStatisticsPermission();
        statisticsPermissionUtil.checkCaseAccessPermission(caseId);
        FundAccountExport export = statisticsService.exportFundAccounts(caseId);
        return Result.success(export);
    }

    @Operation(summary = "工作计划导出")
    @GetMapping("/work-plan/export")
    public Result<WorkPlanExport> exportWorkPlans(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {
        statisticsPermissionUtil.checkStatisticsPermission();
        statisticsPermissionUtil.checkCaseAccessPermission(caseId);
        WorkPlanExport export = statisticsService.exportWorkPlans(caseId);
        return Result.success(export);
    }

    @Operation(summary = "资金审批导出（分页）")
    @GetMapping("/fund-approval/export-paged")
    public Result<FundApprovalExport> exportFundApprovalsPaged(
            @Parameter(description = "案件ID") @RequestParam Long caseId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "100") Integer size) {
        statisticsPermissionUtil.checkStatisticsPermission();
        statisticsPermissionUtil.checkCaseAccessPermission(caseId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        FundApprovalExport export = statisticsService.exportFundApprovals(caseId, pageable);
        return Result.success(export);
    }

    @Operation(summary = "资金账户导出（分页）")
    @GetMapping("/fund-account/export-paged")
    public Result<FundAccountExport> exportFundAccountsPaged(
            @Parameter(description = "案件ID") @RequestParam Long caseId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "100") Integer size) {
        statisticsPermissionUtil.checkStatisticsPermission();
        statisticsPermissionUtil.checkCaseAccessPermission(caseId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        FundAccountExport export = statisticsService.exportFundAccounts(caseId, pageable);
        return Result.success(export);
    }

    @Operation(summary = "工作计划导出（分页）")
    @GetMapping("/work-plan/export-paged")
    public Result<WorkPlanExport> exportWorkPlansPaged(
            @Parameter(description = "案件ID") @RequestParam Long caseId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "100") Integer size) {
        statisticsPermissionUtil.checkStatisticsPermission();
        statisticsPermissionUtil.checkCaseAccessPermission(caseId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        WorkPlanExport export = statisticsService.exportWorkPlans(caseId, pageable);
        return Result.success(export);
    }
}

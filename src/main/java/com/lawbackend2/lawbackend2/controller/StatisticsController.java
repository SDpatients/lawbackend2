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
import com.lawbackend2.lawbackend2.dto.response.LawyerCaseStatistics;
import com.lawbackend2.lawbackend2.dto.response.YearlyTransactionStatistics;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.service.BankruptCaseService;
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
    private final BankruptCaseService bankruptCaseService;

    public StatisticsController(StatisticsService statisticsService, StatisticsPermissionUtil statisticsPermissionUtil, BankruptCaseService bankruptCaseService) {
        this.statisticsService = statisticsService;
        this.statisticsPermissionUtil = statisticsPermissionUtil;
        this.bankruptCaseService = bankruptCaseService;
    }

    @Operation(summary = "资金流水统计")
    @GetMapping("/fund-transaction")
    public Result<FundTransactionStatistics> getFundTransactionStatistics(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {

        statisticsPermissionUtil.checkStatisticsPermission();
        BankruptCase bankruptCase = bankruptCaseService.getCaseById(caseId);
        if (bankruptCase != null) {
            statisticsPermissionUtil.checkCaseAccessPermission(caseId, bankruptCase.getCreateUserId());
        }
        FundTransactionStatistics statistics = statisticsService.getFundTransactionStatistics(caseId);
        return Result.success(statistics);
    }

    @Operation(summary = "资金流水导出")
    @GetMapping("/fund-transaction/export")
    public Result<FundTransactionExport> exportFundTransactions(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {

        statisticsPermissionUtil.checkStatisticsPermission();
        BankruptCase bankruptCase = bankruptCaseService.getCaseById(caseId);
        if (bankruptCase != null) {
            statisticsPermissionUtil.checkCaseAccessPermission(caseId, bankruptCase.getCreateUserId());
        }
        FundTransactionExport export = statisticsService.exportFundTransactions(caseId);
        return Result.success(export);
    }

    @Operation(summary = "资金审批统计")
    @GetMapping("/fund-approval")
    public Result<FundApprovalStatistics> getFundApprovalStatistics(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {

        statisticsPermissionUtil.checkStatisticsPermission();
        BankruptCase bankruptCase = bankruptCaseService.getCaseById(caseId);
        if (bankruptCase != null) {
            statisticsPermissionUtil.checkCaseAccessPermission(caseId, bankruptCase.getCreateUserId());
        }
        FundApprovalStatistics statistics = statisticsService.getFundApprovalStatistics(caseId);
        return Result.success(statistics);
    }

    @Operation(summary = "资金账户统计")
    @GetMapping("/fund-account")
    public Result<FundAccountStatistics> getFundAccountStatistics(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {

        statisticsPermissionUtil.checkStatisticsPermission();
        BankruptCase bankruptCase = bankruptCaseService.getCaseById(caseId);
        if (bankruptCase != null) {
            statisticsPermissionUtil.checkCaseAccessPermission(caseId, bankruptCase.getCreateUserId());
        }
        FundAccountStatistics statistics = statisticsService.getFundAccountStatistics(caseId);
        return Result.success(statistics);
    }

    @Operation(summary = "工作计划统计")
    @GetMapping("/work-plan")
    public Result<WorkPlanStatistics> getWorkPlanStatistics(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {

        statisticsPermissionUtil.checkStatisticsPermission();
        BankruptCase bankruptCase = bankruptCaseService.getCaseById(caseId);
        if (bankruptCase != null) {
            statisticsPermissionUtil.checkCaseAccessPermission(caseId, bankruptCase.getCreateUserId());
        }
        WorkPlanStatistics statistics = statisticsService.getWorkPlanStatistics(caseId);
        return Result.success(statistics);
    }

    @Operation(summary = "资金流水趋势分析")
    @GetMapping("/fund-transaction/trend")
    public Result<TimeTrendStatistics> getFundTransactionTrend(
            @Parameter(description = "案件ID") @RequestParam Long caseId,
            @Parameter(description = "周期类型: month/quarter/year") @RequestParam(defaultValue = "month") String period) {

        statisticsPermissionUtil.checkStatisticsPermission();
        BankruptCase bankruptCase = bankruptCaseService.getCaseById(caseId);
        if (bankruptCase != null) {
            statisticsPermissionUtil.checkCaseAccessPermission(caseId, bankruptCase.getCreateUserId());
        }
        TimeTrendStatistics trend = statisticsService.getFundTransactionTrend(caseId, period);
        return Result.success(trend);
    }

    @Operation(summary = "资金审批趋势分析")
    @GetMapping("/fund-approval/trend")
    public Result<TimeTrendStatistics> getFundApprovalTrend(
            @Parameter(description = "案件ID") @RequestParam Long caseId,
            @Parameter(description = "周期类型: month/quarter/year") @RequestParam(defaultValue = "month") String period) {

        statisticsPermissionUtil.checkStatisticsPermission();
        BankruptCase bankruptCase = bankruptCaseService.getCaseById(caseId);
        if (bankruptCase != null) {
            statisticsPermissionUtil.checkCaseAccessPermission(caseId, bankruptCase.getCreateUserId());
        }
        TimeTrendStatistics trend = statisticsService.getFundApprovalTrend(caseId, period);
        return Result.success(trend);
    }

    @Operation(summary = "案件趋势分析", description = "分析案件数量随时间的变化趋势。传入 userId 参数只返回该用户的案件数据，不传则返回所有案件数据")
    @GetMapping("/case/trend")
    public Result<TimeTrendStatistics> getCaseTrend(
            @Parameter(description = "周期类型: month/quarter/year") @RequestParam(defaultValue = "month") String period,
            @Parameter(description = "用户 ID（可选，传入则只返回该用户的案件数据）") @RequestParam(required = false) Long userId) {
        
        log.info("获取案件趋势分析，period：{}，userId：{}", period, userId);
        TimeTrendStatistics trend = statisticsService.getCaseTrend(period, userId);
        return Result.success(trend);
    }

    @Operation(summary = "案件状态与进度交叉分析", description = "分析案件状态与进度的交叉分布情况。传入 userId 参数只返回该用户的案件数据，不传则返回所有案件数据")
    @GetMapping("/case/cross-analysis")
    public Result<CrossAnalysisStatistics> getCaseCrossAnalysis(
            @Parameter(description = "用户 ID（可选，传入则只返回该用户的案件数据）") @RequestParam(required = false) Long userId) {
        
        log.info("获取案件交叉分析，userId：{}", userId);
        CrossAnalysisStatistics analysis = statisticsService.getCaseCrossAnalysis(userId);
        return Result.success(analysis);
    }

    @Operation(summary = "案件金额排名")
    @GetMapping("/case/amount-ranking")
    public Result<RankingStatistics> getCaseAmountRanking(
            @Parameter(description = "用户ID（可选，传入则只返回该用户的案件数据）") @RequestParam(required = false) Long userId,
            @Parameter(description = "前N名") @RequestParam(defaultValue = "10") Integer topN) {
        if (userId == null) {
            userId = statisticsPermissionUtil.isAdmin() ? null : statisticsPermissionUtil.getCurrentUserId();
        }
        RankingStatistics ranking = statisticsService.getCaseAmountRanking(topN, userId);
        return Result.success(ranking);
    }

    @Operation(summary = "债权申报金额排名")
    @GetMapping("/creditor-claim/amount-ranking")
    public Result<RankingStatistics> getCreditorClaimAmountRanking(
            @Parameter(description = "用户ID（可选，传入则只返回该用户的案件数据）") @RequestParam(required = false) Long userId,
            @Parameter(description = "前N名") @RequestParam(defaultValue = "10") Integer topN) {
        if (userId == null) {
            userId = statisticsPermissionUtil.isAdmin() ? null : statisticsPermissionUtil.getCurrentUserId();
        }
        RankingStatistics ranking = statisticsService.getCreditorClaimAmountRanking(topN, userId);
        return Result.success(ranking);
    }

    @Operation(summary = "资金审批导出")
    @GetMapping("/fund-approval/export")
    public Result<FundApprovalExport> exportFundApprovals(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {
        statisticsPermissionUtil.checkStatisticsPermission();
        BankruptCase bankruptCase = bankruptCaseService.getCaseById(caseId);
        if (bankruptCase != null) {
            statisticsPermissionUtil.checkCaseAccessPermission(caseId, bankruptCase.getCreateUserId());
        }
        FundApprovalExport export = statisticsService.exportFundApprovals(caseId);
        return Result.success(export);
    }

    @Operation(summary = "资金账户导出")
    @GetMapping("/fund-account/export")
    public Result<FundAccountExport> exportFundAccounts(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {
        statisticsPermissionUtil.checkStatisticsPermission();
        BankruptCase bankruptCase = bankruptCaseService.getCaseById(caseId);
        if (bankruptCase != null) {
            statisticsPermissionUtil.checkCaseAccessPermission(caseId, bankruptCase.getCreateUserId());
        }
        FundAccountExport export = statisticsService.exportFundAccounts(caseId);
        return Result.success(export);
    }

    @Operation(summary = "工作计划导出")
    @GetMapping("/work-plan/export")
    public Result<WorkPlanExport> exportWorkPlans(
            @Parameter(description = "案件ID") @RequestParam Long caseId) {
        statisticsPermissionUtil.checkStatisticsPermission();
        BankruptCase bankruptCase = bankruptCaseService.getCaseById(caseId);
        if (bankruptCase != null) {
            statisticsPermissionUtil.checkCaseAccessPermission(caseId, bankruptCase.getCreateUserId());
        }
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
        BankruptCase bankruptCase = bankruptCaseService.getCaseById(caseId);
        if (bankruptCase != null) {
            statisticsPermissionUtil.checkCaseAccessPermission(caseId, bankruptCase.getCreateUserId());
        }
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
        BankruptCase bankruptCase = bankruptCaseService.getCaseById(caseId);
        if (bankruptCase != null) {
            statisticsPermissionUtil.checkCaseAccessPermission(caseId, bankruptCase.getCreateUserId());
        }
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
        BankruptCase bankruptCase = bankruptCaseService.getCaseById(caseId);
        if (bankruptCase != null) {
            statisticsPermissionUtil.checkCaseAccessPermission(caseId, bankruptCase.getCreateUserId());
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        WorkPlanExport export = statisticsService.exportWorkPlans(caseId, pageable);
        return Result.success(export);
    }

    @Operation(summary = "律师年度案件统计", description = "统计每个律师承办的案件数量（作为负责人或管理人）")
    @GetMapping("/lawyer-case")
    public Result<java.util.List<LawyerCaseStatistics>> getLawyerCaseStatistics(
            @Parameter(description = "年份（可选，不传则统计所有年份）") @RequestParam(required = false) Integer year) {
        statisticsPermissionUtil.checkStatisticsPermission();
        java.util.List<LawyerCaseStatistics> statistics = statisticsService.getLawyerCaseStatistics(year);
        return Result.success(statistics);
    }

    @Operation(summary = "年度交易金额统计", description = "统计所有账户的年度总交易金额")
    @GetMapping("/yearly-transaction")
    public Result<YearlyTransactionStatistics> getYearlyTransactionStatistics(
            @Parameter(description = "年份（可选，不传则默认当前年份）") @RequestParam(required = false) Integer year) {
        statisticsPermissionUtil.checkStatisticsPermission();
        YearlyTransactionStatistics statistics = statisticsService.getYearlyTransactionStatistics(year);
        return Result.success(statistics);
    }
}

package com.lawbackend2.lawbackend2.service;

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
import org.springframework.data.domain.Pageable;

public interface StatisticsService {

    FundTransactionStatistics getFundTransactionStatistics(Long caseId);

    FundTransactionExport exportFundTransactions(Long caseId);

    FundApprovalStatistics getFundApprovalStatistics(Long caseId);

    FundAccountStatistics getFundAccountStatistics(Long caseId);

    WorkPlanStatistics getWorkPlanStatistics(Long caseId);

    TimeTrendStatistics getFundTransactionTrend(Long caseId, String period);

    TimeTrendStatistics getFundApprovalTrend(Long caseId, String period);

    TimeTrendStatistics getCaseTrend(String period);

    TimeTrendStatistics getCaseTrend(String period, Long userId);

    CrossAnalysisStatistics getCaseCrossAnalysis();

    CrossAnalysisStatistics getCaseCrossAnalysis(Long userId);

    RankingStatistics getCaseAmountRanking(Integer topN);

    RankingStatistics getCaseAmountRanking(Integer topN, Long userId);

    RankingStatistics getCreditorClaimAmountRanking(Integer topN);

    RankingStatistics getCreditorClaimAmountRanking(Integer topN, Long userId);

    FundApprovalExport exportFundApprovals(Long caseId);

    FundAccountExport exportFundAccounts(Long caseId);

    WorkPlanExport exportWorkPlans(Long caseId);

    FundApprovalExport exportFundApprovals(Long caseId, Pageable pageable);

    FundAccountExport exportFundAccounts(Long caseId, Pageable pageable);

    WorkPlanExport exportWorkPlans(Long caseId, Pageable pageable);
}

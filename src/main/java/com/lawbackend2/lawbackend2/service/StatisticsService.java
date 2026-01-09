package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.response.FundAccountStatistics;
import com.lawbackend2.lawbackend2.dto.response.FundApprovalStatistics;
import com.lawbackend2.lawbackend2.dto.response.FundTransactionExport;
import com.lawbackend2.lawbackend2.dto.response.FundTransactionStatistics;
import com.lawbackend2.lawbackend2.dto.response.WorkPlanStatistics;

public interface StatisticsService {

    FundTransactionStatistics getFundTransactionStatistics(Long caseId);

    FundTransactionExport exportFundTransactions(Long caseId);

    FundApprovalStatistics getFundApprovalStatistics(Long caseId);

    FundAccountStatistics getFundAccountStatistics(Long caseId);

    WorkPlanStatistics getWorkPlanStatistics(Long caseId);
}

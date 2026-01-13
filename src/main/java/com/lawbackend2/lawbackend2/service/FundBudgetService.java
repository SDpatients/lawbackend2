package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.FundBudgetApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.FundBudgetCreateRequest;
import com.lawbackend2.lawbackend2.entity.FundBudget;

public interface FundBudgetService {

    Long createFundBudget(FundBudgetCreateRequest request);

    PageResult<FundBudget> getFundBudgetList(Integer pageNum, Integer pageSize, Long caseId, String budgetType, String budgetStatus, String approvalStatus);

    FundBudget getFundBudgetDetail(Long budgetId);

    void approveFundBudget(Long budgetId, FundBudgetApprovalRequest request);

    void deleteFundBudget(Long budgetId);
}
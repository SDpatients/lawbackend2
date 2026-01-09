package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.response.*;
import com.lawbackend2.lawbackend2.entity.*;
import com.lawbackend2.lawbackend2.repository.*;
import com.lawbackend2.lawbackend2.service.StatisticsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class StatisticsServiceImpl implements StatisticsService {

    private final FundFlowRepository fundFlowRepository;
    private final FundApprovalRepository fundApprovalRepository;
    private final FundAccountRepository fundAccountRepository;
    private final WorkPlanRepository workPlanRepository;

    public StatisticsServiceImpl(FundFlowRepository fundFlowRepository,
                                 FundApprovalRepository fundApprovalRepository,
                                 FundAccountRepository fundAccountRepository,
                                 WorkPlanRepository workPlanRepository) {
        this.fundFlowRepository = fundFlowRepository;
        this.fundApprovalRepository = fundApprovalRepository;
        this.fundAccountRepository = fundAccountRepository;
        this.workPlanRepository = workPlanRepository;
    }

    @Override
    public FundTransactionStatistics getFundTransactionStatistics(Long caseId) {
        List<FundFlow> transactions = fundFlowRepository.findByCaseIdAndIsDeleted(caseId, false);

        FundTransactionStatistics statistics = new FundTransactionStatistics();
        statistics.setTotalTransactions((long) transactions.size());

        long incomeCount = transactions.stream()
                .filter(t -> "INCOME".equals(t.getFlowType()))
                .count();
        long expenseCount = transactions.stream()
                .filter(t -> "EXPENSE".equals(t.getFlowType()))
                .count();

        statistics.setTotalIncome(incomeCount);
        statistics.setTotalExpense(expenseCount);

        double totalIncomeAmount = transactions.stream()
                .filter(t -> "INCOME".equals(t.getFlowType()))
                .mapToDouble(t -> t.getAmount() != null ? t.getAmount().doubleValue() : 0.0)
                .sum();
        double totalExpenseAmount = transactions.stream()
                .filter(t -> "EXPENSE".equals(t.getFlowType()))
                .mapToDouble(t -> t.getAmount() != null ? t.getAmount().doubleValue() : 0.0)
                .sum();

        statistics.setTotalIncomeAmount(totalIncomeAmount);
        statistics.setTotalExpenseAmount(totalExpenseAmount);
        statistics.setNetAmount(totalIncomeAmount - totalExpenseAmount);

        Map<String, Long> byTransactionType = transactions.stream()
                .collect(Collectors.groupingBy(
                        FundFlow::getFlowType,
                        Collectors.counting()
                ));
        statistics.setByTransactionType(byTransactionType);

        return statistics;
    }

    @Override
    public FundTransactionExport exportFundTransactions(Long caseId) {
        List<FundFlow> transactions = fundFlowRepository.findByCaseIdAndIsDeleted(caseId, false);

        FundTransactionExport export = new FundTransactionExport();
        export.setFileName("fund_transactions_" + LocalDateTime.now().toString() + ".xlsx");
        export.setTotalCount((long) transactions.size());

        List<FundTransactionExportItem> items = transactions.stream()
                .map(this::convertToExportItem)
                .collect(Collectors.toList());
        export.setTransactions(items);

        return export;
    }

    private FundTransactionExportItem convertToExportItem(FundFlow transaction) {
        FundTransactionExportItem item = new FundTransactionExportItem();
        item.setId(transaction.getId());
        item.setTransactionNumber(transaction.getId().toString());
        item.setTransactionType(transaction.getFlowType());
        item.setTransactionAmount(transaction.getAmount());
        item.setTransactionDirection(transaction.getFlowType());
        item.setTransactionTime(transaction.getTransactionDate());
        item.setTransactionDescription(transaction.getDescription());
        item.setStatus(transaction.getStatus());
        return item;
    }

    @Override
    public FundApprovalStatistics getFundApprovalStatistics(Long caseId) {
        List<FundApproval> approvals = fundApprovalRepository.findByCaseIdAndIsDeleted(caseId, false);

        FundApprovalStatistics statistics = new FundApprovalStatistics();
        statistics.setTotalApprovals((long) approvals.size());

        long pendingCount = approvals.stream()
                .filter(a -> "PENDING".equals(a.getApprovalStatus()))
                .count();
        long approvedCount = approvals.stream()
                .filter(a -> "APPROVED".equals(a.getApprovalStatus()))
                .count();
        long rejectedCount = approvals.stream()
                .filter(a -> "REJECTED".equals(a.getApprovalStatus()))
                .count();

        statistics.setPendingApprovals(pendingCount);
        statistics.setApprovedApprovals(approvedCount);
        statistics.setRejectedApprovals(rejectedCount);

        double totalApprovedAmount = approvals.stream()
                .filter(a -> "APPROVED".equals(a.getApprovalStatus()))
                .mapToDouble(a -> a.getAmount() != null ? a.getAmount().doubleValue() : 0.0)
                .sum();
        double totalRejectedAmount = approvals.stream()
                .filter(a -> "REJECTED".equals(a.getApprovalStatus()))
                .mapToDouble(a -> a.getAmount() != null ? a.getAmount().doubleValue() : 0.0)
                .sum();

        statistics.setTotalApprovedAmount(totalApprovedAmount);
        statistics.setTotalRejectedAmount(totalRejectedAmount);

        Map<String, Long> byApprovalType = new HashMap<>();
        byApprovalType.put("PAYMENT", approvals.stream().filter(a -> "PAYMENT".equals(a.getApprovalStatus())).count());
        byApprovalType.put("TRANSFER", approvals.stream().filter(a -> "TRANSFER".equals(a.getApprovalStatus())).count());
        statistics.setByApprovalType(byApprovalType);

        return statistics;
    }

    @Override
    public FundAccountStatistics getFundAccountStatistics(Long caseId) {
        List<FundAccount> accounts = fundAccountRepository.findByCaseIdAndIsDeleted(caseId, false);

        FundAccountStatistics statistics = new FundAccountStatistics();
        statistics.setTotalAccounts((long) accounts.size());

        long activeCount = accounts.stream()
                .filter(a -> "ACTIVE".equals(a.getStatus()))
                .count();
        long inactiveCount = accounts.stream()
                .filter(a -> "INACTIVE".equals(a.getStatus()))
                .count();

        statistics.setActiveAccounts(activeCount);
        statistics.setInactiveAccounts(inactiveCount);

        double totalBalance = accounts.stream()
                .mapToDouble(a -> a.getCurrentBalance() != null ? a.getCurrentBalance().doubleValue() : 0.0)
                .sum();

        statistics.setTotalBalance(totalBalance);
        statistics.setTotalFrozenAmount(0.0);

        Map<String, Long> byAccountType = accounts.stream()
                .collect(Collectors.groupingBy(
                        FundAccount::getAccountType,
                        Collectors.counting()
                ));
        statistics.setByAccountType(byAccountType);

        return statistics;
    }

    @Override
    public WorkPlanStatistics getWorkPlanStatistics(Long caseId) {
        List<WorkPlan> plans = workPlanRepository.findByCaseIdAndIsDeleted(caseId, false);

        WorkPlanStatistics statistics = new WorkPlanStatistics();
        statistics.setTotalPlans((long) plans.size());

        long notStartedCount = plans.stream()
                .filter(p -> "NOT_STARTED".equals(p.getExecutionStatus()))
                .count();
        long inProgressCount = plans.stream()
                .filter(p -> "IN_PROGRESS".equals(p.getExecutionStatus()))
                .count();
        long completedCount = plans.stream()
                .filter(p -> "COMPLETED".equals(p.getExecutionStatus()))
                .count();
        long delayedCount = plans.stream()
                .filter(p -> "DELAYED".equals(p.getExecutionStatus()))
                .count();
        long cancelledCount = plans.stream()
                .filter(p -> "CANCELLED".equals(p.getExecutionStatus()))
                .count();

        statistics.setNotStartedPlans(notStartedCount);
        statistics.setInProgressPlans(inProgressCount);
        statistics.setCompletedPlans(completedCount);
        statistics.setDelayedPlans(delayedCount);
        statistics.setCancelledPlans(cancelledCount);

        Map<String, Long> byPlanType = plans.stream()
                .collect(Collectors.groupingBy(
                        WorkPlan::getPlanType,
                        Collectors.counting()
                ));
        statistics.setByPlanType(byPlanType);

        return statistics;
    }
}

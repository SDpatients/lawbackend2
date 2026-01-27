package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.response.*;
import com.lawbackend2.lawbackend2.entity.*;
import com.lawbackend2.lawbackend2.repository.*;
import com.lawbackend2.lawbackend2.service.StatisticsService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private final BankruptCaseRepository bankruptCaseRepository;
    private final CreditorClaimRepository creditorClaimRepository;

    public StatisticsServiceImpl(FundFlowRepository fundFlowRepository,
                                 FundApprovalRepository fundApprovalRepository,
                                 FundAccountRepository fundAccountRepository,
                                 WorkPlanRepository workPlanRepository,
                                 BankruptCaseRepository bankruptCaseRepository,
                                 CreditorClaimRepository creditorClaimRepository) {
        this.fundFlowRepository = fundFlowRepository;
        this.fundApprovalRepository = fundApprovalRepository;
        this.fundAccountRepository = fundAccountRepository;
        this.workPlanRepository = workPlanRepository;
        this.bankruptCaseRepository = bankruptCaseRepository;
        this.creditorClaimRepository = creditorClaimRepository;
    }

    @Override
    @CacheEvict(value = "fundTransactionStatistics", key = "#caseId", beforeInvocation = true)
    @Cacheable(value = "fundTransactionStatistics", key = "#caseId")
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
    @CacheEvict(value = "fundApprovalStatistics", key = "#caseId", beforeInvocation = true)
    @Cacheable(value = "fundApprovalStatistics", key = "#caseId")
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
    @CacheEvict(value = "fundAccountStatistics", key = "#caseId", beforeInvocation = true)
    @Cacheable(value = "fundAccountStatistics", key = "#caseId")
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

        double totalFrozenAmount = accounts.stream()
                .filter(a -> a.getIsFrozen() != null && a.getIsFrozen())
                .mapToDouble(a -> a.getCurrentBalance() != null ? a.getCurrentBalance().doubleValue() : 0.0)
                .sum();

        statistics.setTotalBalance(totalBalance);
        statistics.setTotalFrozenAmount(totalFrozenAmount);

        Map<String, Long> byAccountType = accounts.stream()
                .collect(Collectors.groupingBy(
                        FundAccount::getAccountType,
                        Collectors.counting()
                ));
        statistics.setByAccountType(byAccountType);

        return statistics;
    }

    @Override
    @CacheEvict(value = "workPlanStatistics", key = "#caseId", beforeInvocation = true)
    @Cacheable(value = "workPlanStatistics", key = "#caseId")
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

    @Override
    @CacheEvict(value = "fundTransactionTrend", key = "#caseId + '_' + #period", beforeInvocation = true)
    @Cacheable(value = "fundTransactionTrend", key = "#caseId + '_' + #period")
    public TimeTrendStatistics getFundTransactionTrend(Long caseId, String period) {
        List<TrendData> trendDataList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        int periods = 12;

        for (int i = periods - 1; i >= 0; i--) {
            LocalDateTime startDate;
            LocalDateTime endDate;
            String periodLabel;

            if ("month".equals(period)) {
                YearMonth yearMonth = YearMonth.now().minusMonths(i);
                startDate = yearMonth.atDay(1).atStartOfDay();
                endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
                periodLabel = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            } else if ("quarter".equals(period)) {
                int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;
                int targetQuarter = currentQuarter - i;
                int targetYear = now.getYear();
                while (targetQuarter <= 0) {
                    targetQuarter += 4;
                    targetYear--;
                }
                startDate = LocalDate.of(targetYear, (targetQuarter - 1) * 3 + 1, 1).atStartOfDay();
                endDate = LocalDate.of(targetYear, targetQuarter * 3, 1).plusMonths(1).minusDays(1).atTime(23, 59, 59);
                periodLabel = targetYear + "-Q" + targetQuarter;
            } else {
                int targetYear = now.getYear() - i;
                startDate = LocalDate.of(targetYear, 1, 1).atStartOfDay();
                endDate = LocalDate.of(targetYear, 12, 31).atTime(23, 59, 59);
                periodLabel = String.valueOf(targetYear);
            }

            Long count = fundFlowRepository.countByCaseIdAndDateRange(caseId, startDate, endDate);
            BigDecimal amount = fundFlowRepository.sumAmountByCaseIdAndDateRange(caseId, startDate, endDate);
            if (amount == null) {
                amount = BigDecimal.ZERO;
            }

            LocalDateTime prevStartDate = startDate.minusDays(1);
            LocalDateTime prevEndDate = startDate.minusMonths(1).plusDays(1);
            Long previousCount = fundFlowRepository.countByCaseIdAndDateRange(caseId, prevStartDate, prevEndDate);
            BigDecimal previousAmount = fundFlowRepository.sumAmountByCaseIdAndDateRange(caseId, prevStartDate, prevEndDate);
            if (previousAmount == null) {
                previousAmount = BigDecimal.ZERO;
            }

            Double growthRate = null;
            if (previousCount != null && previousCount > 0) {
                growthRate = ((double) (count - previousCount) / previousCount) * 100;
                growthRate = Math.round(growthRate * 100.0) / 100.0;
            }

            TrendData trendData = new TrendData();
            trendData.setPeriod(periodLabel);
            trendData.setStartDate(startDate.toLocalDate());
            trendData.setEndDate(endDate.toLocalDate());
            trendData.setCount(count);
            trendData.setAmount(amount);
            trendData.setPreviousCount(previousCount);
            trendData.setPreviousAmount(previousAmount);
            trendData.setGrowthRate(growthRate);
            trendDataList.add(trendData);
        }

        TimeTrendStatistics statistics = new TimeTrendStatistics();
        statistics.setType("fund_transaction");
        statistics.setTrendData(trendDataList);
        statistics.setTotalCount(trendDataList.stream().mapToLong(TrendData::getCount).sum());
        statistics.setTotalAmount(trendDataList.stream().map(TrendData::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

        double avgGrowthRate = trendDataList.stream()
                .filter(t -> t.getGrowthRate() != null)
                .mapToDouble(TrendData::getGrowthRate)
                .average()
                .orElse(0.0);
        statistics.setAverageGrowthRate(Math.round(avgGrowthRate * 100.0) / 100.0);

        return statistics;
    }

    @Override
    @CacheEvict(value = "fundApprovalTrend", key = "#caseId + '_' + #period", beforeInvocation = true)
    @Cacheable(value = "fundApprovalTrend", key = "#caseId + '_' + #period")
    public TimeTrendStatistics getFundApprovalTrend(Long caseId, String period) {
        List<TrendData> trendDataList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        int periods = 12;

        for (int i = periods - 1; i >= 0; i--) {
            LocalDateTime startDate;
            LocalDateTime endDate;
            String periodLabel;

            if ("month".equals(period)) {
                YearMonth yearMonth = YearMonth.now().minusMonths(i);
                startDate = yearMonth.atDay(1).atStartOfDay();
                endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
                periodLabel = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            } else if ("quarter".equals(period)) {
                int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;
                int targetQuarter = currentQuarter - i;
                int targetYear = now.getYear();
                while (targetQuarter <= 0) {
                    targetQuarter += 4;
                    targetYear--;
                }
                startDate = LocalDate.of(targetYear, (targetQuarter - 1) * 3 + 1, 1).atStartOfDay();
                endDate = LocalDate.of(targetYear, targetQuarter * 3, 1).plusMonths(1).minusDays(1).atTime(23, 59, 59);
                periodLabel = targetYear + "-Q" + targetQuarter;
            } else {
                int targetYear = now.getYear() - i;
                startDate = LocalDate.of(targetYear, 1, 1).atStartOfDay();
                endDate = LocalDate.of(targetYear, 12, 31).atTime(23, 59, 59);
                periodLabel = String.valueOf(targetYear);
            }

            Long count = fundApprovalRepository.countByCaseIdAndDateRange(caseId, startDate, endDate);
            BigDecimal amount = fundApprovalRepository.sumAmountByCaseIdAndDateRange(caseId, startDate, endDate);
            if (amount == null) {
                amount = BigDecimal.ZERO;
            }

            LocalDateTime prevStartDate = startDate.minusDays(1);
            LocalDateTime prevEndDate = startDate.minusMonths(1).plusDays(1);
            Long previousCount = fundApprovalRepository.countByCaseIdAndDateRange(caseId, prevStartDate, prevEndDate);
            BigDecimal previousAmount = fundApprovalRepository.sumAmountByCaseIdAndDateRange(caseId, prevStartDate, prevEndDate);
            if (previousAmount == null) {
                previousAmount = BigDecimal.ZERO;
            }

            Double growthRate = null;
            if (previousCount != null && previousCount > 0) {
                growthRate = ((double) (count - previousCount) / previousCount) * 100;
                growthRate = Math.round(growthRate * 100.0) / 100.0;
            }

            TrendData trendData = new TrendData();
            trendData.setPeriod(periodLabel);
            trendData.setStartDate(startDate.toLocalDate());
            trendData.setEndDate(endDate.toLocalDate());
            trendData.setCount(count);
            trendData.setAmount(amount);
            trendData.setPreviousCount(previousCount);
            trendData.setPreviousAmount(previousAmount);
            trendData.setGrowthRate(growthRate);
            trendDataList.add(trendData);
        }

        TimeTrendStatistics statistics = new TimeTrendStatistics();
        statistics.setType("fund_approval");
        statistics.setTrendData(trendDataList);
        statistics.setTotalCount(trendDataList.stream().mapToLong(TrendData::getCount).sum());
        statistics.setTotalAmount(trendDataList.stream().map(TrendData::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

        double avgGrowthRate = trendDataList.stream()
                .filter(t -> t.getGrowthRate() != null)
                .mapToDouble(TrendData::getGrowthRate)
                .average()
                .orElse(0.0);
        statistics.setAverageGrowthRate(Math.round(avgGrowthRate * 100.0) / 100.0);

        return statistics;
    }

    @Override
    @CacheEvict(value = "caseTrend", key = "#period", beforeInvocation = true)
    @Cacheable(value = "caseTrend", key = "#period")
    public TimeTrendStatistics getCaseTrend(String period) {
        return getCaseTrend(period, null);
    }

    @Override
    public TimeTrendStatistics getCaseTrend(String period, Long userId) {
        List<TrendData> trendDataList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        int periods = 12;

        for (int i = periods - 1; i >= 0; i--) {
            LocalDateTime startDate;
            LocalDateTime endDate;
            String periodLabel;

            if ("month".equals(period)) {
                YearMonth yearMonth = YearMonth.now().minusMonths(i);
                startDate = yearMonth.atDay(1).atStartOfDay();
                endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
                periodLabel = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            } else if ("quarter".equals(period)) {
                int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;
                int targetQuarter = currentQuarter - i;
                int targetYear = now.getYear();
                while (targetQuarter <= 0) {
                    targetQuarter += 4;
                    targetYear--;
                }
                startDate = LocalDate.of(targetYear, (targetQuarter - 1) * 3 + 1, 1).atStartOfDay();
                endDate = LocalDate.of(targetYear, targetQuarter * 3, 1).plusMonths(1).minusDays(1).atTime(23, 59, 59);
                periodLabel = targetYear + "-Q" + targetQuarter;
            } else {
                int targetYear = now.getYear() - i;
                startDate = LocalDate.of(targetYear, 1, 1).atStartOfDay();
                endDate = LocalDate.of(targetYear, 12, 31).atTime(23, 59, 59);
                periodLabel = String.valueOf(targetYear);
            }

            Long count;
            if (userId != null) {
                count = bankruptCaseRepository.countByUserIdAndCreateTimeBetween(userId, startDate, endDate);
            } else {
                count = bankruptCaseRepository.countByCreateTimeBetween(startDate, endDate);
            }

            LocalDateTime prevStartDate = startDate.minusDays(1);
            LocalDateTime prevEndDate = startDate.minusMonths(1).plusDays(1);
            Long previousCount;
            if (userId != null) {
                previousCount = bankruptCaseRepository.countByUserIdAndCreateTimeBetween(userId, prevStartDate, prevEndDate);
            } else {
                previousCount = bankruptCaseRepository.countByCreateTimeBetween(prevStartDate, prevEndDate);
            }

            Double growthRate = null;
            if (previousCount != null && previousCount > 0) {
                growthRate = ((double) (count - previousCount) / previousCount) * 100;
                growthRate = Math.round(growthRate * 100.0) / 100.0;
            }

            TrendData trendData = new TrendData();
            trendData.setPeriod(periodLabel);
            trendData.setStartDate(startDate.toLocalDate());
            trendData.setEndDate(endDate.toLocalDate());
            trendData.setCount(count);
            trendData.setAmount(BigDecimal.ZERO);
            trendData.setPreviousCount(previousCount);
            trendData.setPreviousAmount(BigDecimal.ZERO);
            trendData.setGrowthRate(growthRate);
            trendDataList.add(trendData);
        }

        TimeTrendStatistics statistics = new TimeTrendStatistics();
        statistics.setType("case");
        statistics.setTrendData(trendDataList);
        statistics.setTotalCount(trendDataList.stream().mapToLong(TrendData::getCount).sum());
        statistics.setTotalAmount(BigDecimal.ZERO);

        double avgGrowthRate = trendDataList.stream()
                .filter(t -> t.getGrowthRate() != null)
                .mapToDouble(TrendData::getGrowthRate)
                .average()
                .orElse(0.0);
        statistics.setAverageGrowthRate(Math.round(avgGrowthRate * 100.0) / 100.0);

        return statistics;
    }

    @Override
    @CacheEvict(value = "caseCrossAnalysis", allEntries = true, beforeInvocation = true)
    @Cacheable(value = "caseCrossAnalysis")
    public CrossAnalysisStatistics getCaseCrossAnalysis() {
        return getCaseCrossAnalysis(null);
    }

    @Override
    public CrossAnalysisStatistics getCaseCrossAnalysis(Long userId) {
        List<Object[]> statusProgressData;
        List<Object[]> statusData;
        List<Object[]> progressData;

        if (userId != null) {
            statusProgressData = bankruptCaseRepository.countByUserIdAndStatusAndProgressGroup(userId);
            statusData = bankruptCaseRepository.countByUserIdAndStatusGroup(userId);
            progressData = bankruptCaseRepository.countByUserIdAndProgressGroup(userId);
        } else {
            statusProgressData = bankruptCaseRepository.countByStatusAndProgressGroup();
            statusData = bankruptCaseRepository.countByStatusGroup();
            progressData = bankruptCaseRepository.countByProgressGroup();
        }

        Map<String, Map<String, Long>> crossData = new HashMap<>();
        for (Object[] row : statusProgressData) {
            String status = (String) row[0];
            String progress = (String) row[1];
            Long count = (Long) row[2];

            crossData.computeIfAbsent(status, k -> new HashMap<>()).put(progress, count);
        }

        Map<String, Long> statusDistribution = new HashMap<>();
        for (Object[] row : statusData) {
            statusDistribution.put((String) row[0], (Long) row[1]);
        }

        Map<String, Long> progressDistribution = new HashMap<>();
        for (Object[] row : progressData) {
            progressDistribution.put((String) row[0], (Long) row[1]);
        }

        Long totalCount = statusDistribution.values().stream().mapToLong(Long::longValue).sum();

        CrossAnalysisStatistics statistics = new CrossAnalysisStatistics();
        statistics.setType("case_status_progress");
        statistics.setCrossData(crossData);
        statistics.setTotalCount(totalCount);
        statistics.setStatusDistribution(statusDistribution);
        statistics.setProgressDistribution(progressDistribution);

        return statistics;
    }

    @Override
    @CacheEvict(value = "caseAmountRanking", key = "#topN", beforeInvocation = true)
    @Cacheable(value = "caseAmountRanking", key = "#topN")
    public RankingStatistics getCaseAmountRanking(Integer topN) {
        return getCaseAmountRanking(topN, null);
    }

    @Override
    public RankingStatistics getCaseAmountRanking(Integer topN, Long userId) {
        if (topN == null || topN <= 0) {
            topN = 10;
        }

        List<Object[]> caseAmountData;
        if (userId != null) {
            caseAmountData = creditorClaimRepository.sumTotalAmountByCaseIdWithNameGroupByUserId(userId);
        } else {
            caseAmountData = creditorClaimRepository.sumTotalAmountByCaseIdWithNameGroup();
        }

        List<RankingItem> rankings = new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (int i = 0; i < Math.min(topN, caseAmountData.size()); i++) {
            Object[] row = caseAmountData.get(i);
            Long caseId = (Long) row[0];
            String caseName = (String) row[1];
            BigDecimal amount = (BigDecimal) row[2];

            if (amount == null) {
                amount = BigDecimal.ZERO;
            }

            totalAmount = totalAmount.add(amount);

            RankingItem item = new RankingItem();
            item.setId(caseId);
            item.setName(caseName);
            item.setAmount(amount);
            item.setRank(i + 1);
            rankings.add(item);
        }

        RankingStatistics statistics = new RankingStatistics();
        statistics.setType("case_amount");
        statistics.setSortBy("total_amount");
        statistics.setTopN(topN);
        statistics.setRankings(rankings);
        statistics.setTotalAmount(totalAmount);

        return statistics;
    }

    @Override
    @CacheEvict(value = "creditorClaimAmountRanking", key = "#topN", beforeInvocation = true)
    @Cacheable(value = "creditorClaimAmountRanking", key = "#topN")
    public RankingStatistics getCreditorClaimAmountRanking(Integer topN) {
        return getCreditorClaimAmountRanking(topN, null);
    }

    @Override
    public RankingStatistics getCreditorClaimAmountRanking(Integer topN, Long userId) {
        if (topN == null || topN <= 0) {
            topN = 10;
        }

        List<Object[]> claimAmountData;
        if (userId != null) {
            claimAmountData = creditorClaimRepository.findTopClaimsByAmountByUserId(userId, PageRequest.of(0, topN));
        } else {
            claimAmountData = creditorClaimRepository.findTopClaimsByAmount(PageRequest.of(0, topN));
        }

        List<RankingItem> rankings = new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (int i = 0; i < claimAmountData.size(); i++) {
            Object[] row = claimAmountData.get(i);
            Long claimId = (Long) row[0];
            String creditorName = (String) row[1];
            BigDecimal amount = (BigDecimal) row[2];

            if (amount == null) {
                amount = BigDecimal.ZERO;
            }

            totalAmount = totalAmount.add(amount);

            RankingItem item = new RankingItem();
            item.setId(claimId);
            item.setName(creditorName);
            item.setAmount(amount);
            item.setRank(i + 1);
            rankings.add(item);
        }

        RankingStatistics statistics = new RankingStatistics();
        statistics.setType("creditor_claim_amount");
        statistics.setSortBy("total_amount");
        statistics.setTopN(topN);
        statistics.setRankings(rankings);
        statistics.setTotalAmount(totalAmount);

        return statistics;
    }

    @Override
    public FundApprovalExport exportFundApprovals(Long caseId) {
        List<FundApproval> approvals = fundApprovalRepository.findByCaseIdAndIsDeleted(caseId, false);

        FundApprovalExport export = new FundApprovalExport();
        export.setFileName("fund_approvals_" + LocalDateTime.now().toString() + ".xlsx");
        export.setTotalCount((long) approvals.size());

        List<FundApprovalExportItem> items = approvals.stream()
                .map(this::convertToApprovalExportItem)
                .collect(Collectors.toList());
        export.setApprovals(items);

        return export;
    }

    private FundApprovalExportItem convertToApprovalExportItem(FundApproval approval) {
        FundApprovalExportItem item = new FundApprovalExportItem();
        item.setId(approval.getId());
        item.setApprovalNumber(approval.getId().toString());
        item.setApprovalType(approval.getApprovalStage());
        item.setAmount(approval.getAmount());
        item.setApprovalStatus(approval.getApprovalStatus());
        item.setApproverName(approval.getApproverId() != null ? approval.getApproverId().toString() : "");
        item.setApprovalTime(approval.getApprovalTime());
        item.setApprovalOpinion(approval.getApprovalOpinion());
        item.setRejectionReason(approval.getRejectionReason());
        item.setStatus(approval.getStatus());
        return item;
    }

    @Override
    public FundAccountExport exportFundAccounts(Long caseId) {
        List<FundAccount> accounts = fundAccountRepository.findByCaseIdAndIsDeleted(caseId, false);

        FundAccountExport export = new FundAccountExport();
        export.setFileName("fund_accounts_" + LocalDateTime.now().toString() + ".xlsx");
        export.setTotalCount((long) accounts.size());

        List<FundAccountExportItem> items = accounts.stream()
                .map(this::convertToAccountExportItem)
                .collect(Collectors.toList());
        export.setAccounts(items);

        return export;
    }

    private FundAccountExportItem convertToAccountExportItem(FundAccount account) {
        FundAccountExportItem item = new FundAccountExportItem();
        item.setId(account.getId());
        item.setAccountName(account.getAccountName());
        item.setAccountType(account.getAccountType());
        item.setAccountPurpose(account.getAccountPurpose());
        item.setCurrentBalance(account.getCurrentBalance());
        item.setInitialBalance(account.getInitialBalance());
        item.setStatus(account.getStatus());
        item.setIsFrozen(account.getIsFrozen());
        item.setFreezeDate(account.getFreezeDate());
        item.setFreezeReason(account.getFreezeReason());
        item.setBankName(account.getBankName());
        item.setBankAccount(account.getBankAccount());
        item.setOpeningDate(account.getOpeningDate());
        return item;
    }

    @Override
    public WorkPlanExport exportWorkPlans(Long caseId) {
        List<WorkPlan> plans = workPlanRepository.findByCaseIdAndIsDeleted(caseId, false);

        WorkPlanExport export = new WorkPlanExport();
        export.setFileName("work_plans_" + LocalDateTime.now().toString() + ".xlsx");
        export.setTotalCount((long) plans.size());

        List<WorkPlanExportItem> items = plans.stream()
                .map(this::convertToWorkPlanExportItem)
                .collect(Collectors.toList());
        export.setPlans(items);

        return export;
    }

    private WorkPlanExportItem convertToWorkPlanExportItem(WorkPlan plan) {
        WorkPlanExportItem item = new WorkPlanExportItem();
        item.setId(plan.getId());
        item.setPlanNumber(plan.getPlanNumber());
        item.setPlanType(plan.getPlanType());
        item.setPlanName(plan.getPlanContent());
        item.setExecutionStatus(plan.getExecutionStatus());
        item.setPlannedStartDate(plan.getStartDate());
        item.setPlannedEndDate(plan.getEndDate());
        item.setResponsiblePerson(plan.getResponsibleUserId() != null ? plan.getResponsibleUserId().toString() : "");
        item.setStatus(plan.getStatus());
        return item;
    }

    @Override
    public FundApprovalExport exportFundApprovals(Long caseId, Pageable pageable) {
        Page<FundApproval> approvalPage = fundApprovalRepository.findByCaseIdAndIsDeletedWithPage(caseId, pageable);

        FundApprovalExport export = new FundApprovalExport();
        export.setFileName("fund_approvals_" + LocalDateTime.now().toString() + ".xlsx");
        export.setTotalCount(approvalPage.getTotalElements());

        List<FundApprovalExportItem> items = approvalPage.getContent().stream()
                .map(this::convertToApprovalExportItem)
                .collect(Collectors.toList());
        export.setApprovals(items);

        return export;
    }

    @Override
    public FundAccountExport exportFundAccounts(Long caseId, Pageable pageable) {
        Page<FundAccount> accountPage = fundAccountRepository.findByConditions(caseId, null, pageable);

        FundAccountExport export = new FundAccountExport();
        export.setFileName("fund_accounts_" + LocalDateTime.now().toString() + ".xlsx");
        export.setTotalCount(accountPage.getTotalElements());

        List<FundAccountExportItem> items = accountPage.getContent().stream()
                .map(this::convertToAccountExportItem)
                .collect(Collectors.toList());
        export.setAccounts(items);

        return export;
    }

    @Override
    public WorkPlanExport exportWorkPlans(Long caseId, Pageable pageable) {
        Page<WorkPlan> planPage = workPlanRepository.findByCaseIdAndIsDeletedWithPage(caseId, pageable);

        WorkPlanExport export = new WorkPlanExport();
        export.setFileName("work_plans_" + LocalDateTime.now().toString() + ".xlsx");
        export.setTotalCount(planPage.getTotalElements());

        List<WorkPlanExportItem> items = planPage.getContent().stream()
                .map(this::convertToWorkPlanExportItem)
                .collect(Collectors.toList());
        export.setPlans(items);

        return export;
    }
}

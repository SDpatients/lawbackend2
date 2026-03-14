package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.response.*;
import com.lawbackend2.lawbackend2.entity.*;
import com.lawbackend2.lawbackend2.repository.*;
import com.lawbackend2.lawbackend2.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    private final FundFlowRepository fundFlowRepository;
    private final FundApprovalRepository fundApprovalRepository;
    private final FundAccountRepository fundAccountRepository;
    private final WorkPlanRepository workPlanRepository;
    private final BankruptCaseRepository bankruptCaseRepository;
    private final CreditorClaimRepository creditorClaimRepository;
    private final ClaimConfirmationRepository claimConfirmationRepository;
    private final UserRepository userRepository;
    private final WorkTeamMemberRepository workTeamMemberRepository;
    private final BankAccountTransactionRepository bankAccountTransactionRepository;

    public StatisticsServiceImpl(FundFlowRepository fundFlowRepository,
                                 FundApprovalRepository fundApprovalRepository,
                                 FundAccountRepository fundAccountRepository,
                                 WorkPlanRepository workPlanRepository,
                                 BankruptCaseRepository bankruptCaseRepository,
                                 CreditorClaimRepository creditorClaimRepository,
                                 ClaimConfirmationRepository claimConfirmationRepository,
                                 UserRepository userRepository,
                                 WorkTeamMemberRepository workTeamMemberRepository,
                                 BankAccountTransactionRepository bankAccountTransactionRepository) {
        this.fundFlowRepository = fundFlowRepository;
        this.fundApprovalRepository = fundApprovalRepository;
        this.fundAccountRepository = fundAccountRepository;
        this.workPlanRepository = workPlanRepository;
        this.bankruptCaseRepository = bankruptCaseRepository;
        this.creditorClaimRepository = creditorClaimRepository;
        this.claimConfirmationRepository = claimConfirmationRepository;
        this.userRepository = userRepository;
        this.workTeamMemberRepository = workTeamMemberRepository;
        this.bankAccountTransactionRepository = bankAccountTransactionRepository;
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
        LocalDate now = LocalDate.now();
        int periods = 12;

        String realName = null;
        if (userId != null) {
            Optional<String> realNameOpt = userRepository.findRealNameById(userId);
            if (realNameOpt.isPresent()) {
                realName = realNameOpt.get();
            }
            log.info("案件趋势分析 - 用户ID: {}, realName: {}", userId, realName);
        }

        for (int i = periods - 1; i >= 0; i--) {
            LocalDate startDate;
            LocalDate endDate;
            String periodLabel;

            if ("month".equals(period)) {
                YearMonth yearMonth = YearMonth.now().minusMonths(i);
                startDate = yearMonth.atDay(1);
                endDate = yearMonth.atEndOfMonth();
                periodLabel = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            } else if ("quarter".equals(period)) {
                int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;
                int targetQuarter = currentQuarter - i;
                int targetYear = now.getYear();
                while (targetQuarter <= 0) {
                    targetQuarter += 4;
                    targetYear--;
                }
                startDate = LocalDate.of(targetYear, (targetQuarter - 1) * 3 + 1, 1);
                endDate = LocalDate.of(targetYear, targetQuarter * 3, 1).plusMonths(1).minusDays(1);
                periodLabel = targetYear + "-Q" + targetQuarter;
            } else {
                int targetYear = now.getYear() - i;
                startDate = LocalDate.of(targetYear, 1, 1);
                endDate = LocalDate.of(targetYear, 12, 31);
                periodLabel = String.valueOf(targetYear);
            }

            Long count;
            if (userId != null) {
                Long countByCreator = bankruptCaseRepository.countByUserIdAndAcceptanceDateBetween(userId, startDate, endDate);
                Long countByUndertaking = 0L;
                if (realName != null) {
                    countByUndertaking = bankruptCaseRepository.countByUndertakingPersonnelAndAcceptanceDateBetween(realName, startDate, endDate);
                }
                count = countByCreator + countByUndertaking;
            } else {
                count = bankruptCaseRepository.countByAcceptanceDateBetween(startDate, endDate);
            }

            LocalDate prevStartDate = startDate.minusDays(1);
            LocalDate prevEndDate = startDate.minusMonths(1).plusDays(1);
            Long previousCount;
            if (userId != null) {
                Long prevCountByCreator = bankruptCaseRepository.countByUserIdAndAcceptanceDateBetween(userId, prevStartDate, prevEndDate);
                Long prevCountByUndertaking = 0L;
                if (realName != null) {
                    prevCountByUndertaking = bankruptCaseRepository.countByUndertakingPersonnelAndAcceptanceDateBetween(realName, prevStartDate, prevEndDate);
                }
                previousCount = prevCountByCreator + prevCountByUndertaking;
            } else {
                previousCount = bankruptCaseRepository.countByAcceptanceDateBetween(prevStartDate, prevEndDate);
            }

            Double growthRate = null;
            if (previousCount != null && previousCount > 0) {
                growthRate = ((double) (count - previousCount) / previousCount) * 100;
                growthRate = Math.round(growthRate * 100.0) / 100.0;
            }

            TrendData trendData = new TrendData();
            trendData.setPeriod(periodLabel);
            trendData.setStartDate(startDate);
            trendData.setEndDate(endDate);
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

        Map<String, Map<String, Long>> crossData = new HashMap<>();
        Map<String, Long> statusDistribution = new HashMap<>();
        Map<String, Long> progressDistribution = new HashMap<>();

        if (userId != null) {
            String realName = userRepository.findRealNameById(userId).orElse(null);
            log.info("案件交叉分析 - 用户ID: {}, realName: {}", userId, realName);

            List<Object[]> statusProgressDataByCreator = bankruptCaseRepository.countByUserIdAndStatusAndProgressGroup(userId);
            for (Object[] row : statusProgressDataByCreator) {
                String status = (String) row[0];
                String progress = (String) row[1];
                Long count = (Long) row[2];
                crossData.computeIfAbsent(status, k -> new HashMap<>()).merge(progress, count, Long::sum);
            }

            List<Object[]> statusDataByCreator = bankruptCaseRepository.countByUserIdAndStatusGroup(userId);
            for (Object[] row : statusDataByCreator) {
                statusDistribution.merge((String) row[0], (Long) row[1], Long::sum);
            }

            List<Object[]> progressDataByCreator = bankruptCaseRepository.countByUserIdAndProgressGroup(userId);
            for (Object[] row : progressDataByCreator) {
                progressDistribution.merge((String) row[0], (Long) row[1], Long::sum);
            }

            if (realName != null) {
                List<Object[]> statusProgressDataByUndertaking = bankruptCaseRepository.countByUndertakingPersonnelAndStatusAndProgressGroup(realName);
                for (Object[] row : statusProgressDataByUndertaking) {
                    String status = (String) row[0];
                    String progress = (String) row[1];
                    Long count = (Long) row[2];
                    crossData.computeIfAbsent(status, k -> new HashMap<>()).merge(progress, count, Long::sum);
                }

                List<Object[]> statusDataByUndertaking = bankruptCaseRepository.countByUndertakingPersonnelAndStatusGroup(realName);
                for (Object[] row : statusDataByUndertaking) {
                    statusDistribution.merge((String) row[0], (Long) row[1], Long::sum);
                }

                List<Object[]> progressDataByUndertaking = bankruptCaseRepository.countByUndertakingPersonnelAndProgressGroup(realName);
                for (Object[] row : progressDataByUndertaking) {
                    progressDistribution.merge((String) row[0], (Long) row[1], Long::sum);
                }
            }
        } else {
            statusProgressData = bankruptCaseRepository.countByStatusAndProgressGroup();
            for (Object[] row : statusProgressData) {
                String status = (String) row[0];
                String progress = (String) row[1];
                Long count = (Long) row[2];

                crossData.computeIfAbsent(status, k -> new HashMap<>()).put(progress, count);
            }

            statusData = bankruptCaseRepository.countByStatusGroup();
            for (Object[] row : statusData) {
                statusDistribution.put((String) row[0], (Long) row[1]);
            }

            progressData = bankruptCaseRepository.countByProgressGroup();
            for (Object[] row : progressData) {
                progressDistribution.put((String) row[0], (Long) row[1]);
            }
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
            caseAmountData = claimConfirmationRepository.sumFinalConfirmedAmountByCaseIdGroupByUserId(userId);
        } else {
            caseAmountData = claimConfirmationRepository.sumFinalConfirmedAmountByCaseIdGroup();
        }

        Map<String, String> caseNameMap = new HashMap<>();
        List<String> caseIdOrNames = new ArrayList<>();
        
        for (Object[] row : caseAmountData) {
            Object caseIdObj = row[0];
            if (caseIdObj instanceof String) {
                String caseIdOrName = (String) caseIdObj;
                caseIdOrNames.add(caseIdOrName);
                caseNameMap.put(caseIdOrName, caseIdOrName);
            } else if (caseIdObj instanceof Number) {
                Long caseId = ((Number) caseIdObj).longValue();
                caseIdOrNames.add(String.valueOf(caseId));
            }
        }

        if (!caseIdOrNames.isEmpty()) {
            try {
                List<Long> caseIds = caseIdOrNames.stream()
                        .filter(s -> {
                            try {
                                Long.parseLong(s);
                                return true;
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        })
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                
                if (!caseIds.isEmpty()) {
                    List<BankruptCase> cases = bankruptCaseRepository.findAllById(caseIds);
                    for (BankruptCase c : cases) {
                        caseNameMap.put(String.valueOf(c.getId()), c.getCaseName());
                    }
                }
            } catch (Exception e) {
                log.warn("查询案件名称失败", e);
            }
        }

        List<RankingItem> rankings = new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (int i = 0; i < Math.min(topN, caseAmountData.size()); i++) {
            Object[] row = caseAmountData.get(i);
            String caseIdStr = String.valueOf(row[0]);
            BigDecimal amount = (BigDecimal) row[1];
            
            Long caseId = null;
            String caseName;
            try {
                caseId = Long.parseLong(caseIdStr);
                caseName = caseNameMap.getOrDefault(caseIdStr, "");
            } catch (NumberFormatException e) {
                caseName = caseIdStr;
            }

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
            claimAmountData = claimConfirmationRepository.findTopConfirmationsByAmountByUserId(userId);
        } else {
            claimAmountData = claimConfirmationRepository.findTopConfirmationsByAmount();
        }

        List<RankingItem> rankings = new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (int i = 0; i < Math.min(topN, claimAmountData.size()); i++) {
            Object[] row = claimAmountData.get(i);
            String creditorName = (String) row[0];
            BigDecimal amount = (BigDecimal) row[1];

            if (amount == null) {
                amount = BigDecimal.ZERO;
            }

            totalAmount = totalAmount.add(amount);

            RankingItem item = new RankingItem();
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

    @Override
    public List<LawyerCaseStatistics> getLawyerCaseStatistics(Integer year) {
        List<Object[]> caseData;
        if (year != null) {
            caseData = workTeamMemberRepository.countCasesByUserAndRoleInYear(year);
        } else {
            caseData = workTeamMemberRepository.countCasesByUserAndRole();
        }

        Map<Long, LawyerCaseStatistics> statisticsMap = new HashMap<>();

        for (Object[] row : caseData) {
            Long userId = (Long) row[0];
            String teamRole = (String) row[1];
            Long caseCount = (Long) row[2];

            LawyerCaseStatistics statistics = statisticsMap.computeIfAbsent(userId, id -> {
                LawyerCaseStatistics stats = new LawyerCaseStatistics();
                stats.setUserId(id);
                stats.setTotalCaseCount(0L);
                stats.setLeaderCaseCount(0L);
                stats.setAdminCaseCount(0L);
                stats.setYear(year);
                return stats;
            });

            statistics.setTotalCaseCount(statistics.getTotalCaseCount() + caseCount);
            if ("LEADER".equals(teamRole)) {
                statistics.setLeaderCaseCount(statistics.getLeaderCaseCount() + caseCount);
            } else if ("ADMIN".equals(teamRole)) {
                statistics.setAdminCaseCount(statistics.getAdminCaseCount() + caseCount);
            }
        }

        List<Long> userIds = new ArrayList<>(statisticsMap.keySet());
        if (!userIds.isEmpty()) {
            List<User> users = userRepository.findAllById(userIds);
            for (User user : users) {
                LawyerCaseStatistics stats = statisticsMap.get(user.getId());
                if (stats != null) {
                    stats.setRealName(user.getRealName());
                    stats.setUsername(user.getUsername());
                }
            }
        }

        List<LawyerCaseStatistics> result = new ArrayList<>(statisticsMap.values());
        result.sort((a, b) -> Long.compare(b.getTotalCaseCount(), a.getTotalCaseCount()));

        return result;
    }

    @Override
    public YearlyTransactionStatistics getYearlyTransactionStatistics(Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }

        YearlyTransactionStatistics statistics = new YearlyTransactionStatistics();
        statistics.setYear(year);
        statistics.setTotalIncomeAmount(BigDecimal.ZERO);
        statistics.setTotalExpenseAmount(BigDecimal.ZERO);
        statistics.setNetAmount(BigDecimal.ZERO);
        statistics.setTotalIncomeCount(0L);
        statistics.setTotalExpenseCount(0L);
        statistics.setTotalTransactionCount(0L);

        List<Object[]> yearlyData = bankAccountTransactionRepository.sumAmountByTransactionTypeInYear(year);
        for (Object[] row : yearlyData) {
            String transactionType = (String) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            Long count = (Long) row[2];

            if (amount == null) {
                amount = BigDecimal.ZERO;
            }

            if ("IN".equals(transactionType)) {
                statistics.setTotalIncomeAmount(amount);
                statistics.setTotalIncomeCount(count);
            } else if ("OUT".equals(transactionType)) {
                statistics.setTotalExpenseAmount(amount);
                statistics.setTotalExpenseCount(count);
            }
        }

        statistics.setNetAmount(statistics.getTotalIncomeAmount().subtract(statistics.getTotalExpenseAmount()));
        statistics.setTotalTransactionCount(statistics.getTotalIncomeCount() + statistics.getTotalExpenseCount());

        List<Object[]> monthlyData = bankAccountTransactionRepository.sumAmountByMonthAndTransactionTypeInYear(year);
        Map<Integer, YearlyTransactionStatistics.MonthlyTransactionData> monthlyMap = new HashMap<>();

        for (int i = 1; i <= 12; i++) {
            YearlyTransactionStatistics.MonthlyTransactionData monthData = new YearlyTransactionStatistics.MonthlyTransactionData();
            monthData.setMonth(i);
            monthData.setMonthLabel(String.format("%d-%02d", year, i));
            monthData.setIncomeAmount(BigDecimal.ZERO);
            monthData.setExpenseAmount(BigDecimal.ZERO);
            monthData.setNetAmount(BigDecimal.ZERO);
            monthData.setIncomeCount(0L);
            monthData.setExpenseCount(0L);
            monthlyMap.put(i, monthData);
        }

        for (Object[] row : monthlyData) {
            Integer month = (Integer) row[0];
            String transactionType = (String) row[1];
            BigDecimal amount = (BigDecimal) row[2];
            Long count = (Long) row[3];

            if (amount == null) {
                amount = BigDecimal.ZERO;
            }

            YearlyTransactionStatistics.MonthlyTransactionData monthData = monthlyMap.get(month);
            if (monthData != null) {
                if ("IN".equals(transactionType)) {
                    monthData.setIncomeAmount(amount);
                    monthData.setIncomeCount(count);
                } else if ("OUT".equals(transactionType)) {
                    monthData.setExpenseAmount(amount);
                    monthData.setExpenseCount(count);
                }
                monthData.setNetAmount(monthData.getIncomeAmount().subtract(monthData.getExpenseAmount()));
            }
        }

        List<YearlyTransactionStatistics.MonthlyTransactionData> monthlyList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            monthlyList.add(monthlyMap.get(i));
        }
        statistics.setMonthlyData(monthlyList);

        return statistics;
    }
}

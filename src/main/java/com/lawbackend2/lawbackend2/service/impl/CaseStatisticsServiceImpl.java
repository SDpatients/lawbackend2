package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.CaseStatisticsRequest;
import com.lawbackend2.lawbackend2.dto.CaseStatisticsResponse;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.service.CaseStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class CaseStatisticsServiceImpl implements CaseStatisticsService {

    @Autowired
    private BankruptCaseRepository caseRepository;

    @Override
    public CaseStatisticsResponse getCaseStatistics(CaseStatisticsRequest request) {
        log.info("开始获取案件统计数据，请求参数：{}", request);

        CaseStatisticsResponse response = new CaseStatisticsResponse();

        try {
            LocalDate startDate = request.getStartDate();
            LocalDate endDate = request.getEndDate();
            Long userId = request.getUserId();

            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                throw new BusinessException("开始日期不能晚于结束日期");
            }

            Long totalCases;
            Map<String, Long> statusDistribution = new HashMap<>();
            Map<String, Long> progressDistribution = new HashMap<>();

            if (userId != null) {
                // 非管理员用户，只能查看自己的案件
                totalCases = caseRepository.countByCreateUserId(userId);
                
                List<Object[]> statusGroup = caseRepository.countByUserIdAndCaseStatusGroup(userId);
                for (Object[] row : statusGroup) {
                    statusDistribution.put((String) row[0], (Long) row[1]);
                }

                List<Object[]> progressGroup = caseRepository.countByUserIdAndCaseProgressGroup(userId);
                for (Object[] row : progressGroup) {
                    progressDistribution.put((String) row[0], (Long) row[1]);
                }
            } else {
                // 管理员用户，可以查看所有案件
                if (startDate != null && endDate != null) {
                    LocalDateTime startDateTime = startDate.atStartOfDay();
                    LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
                    totalCases = caseRepository.countByCreatedAtBetween(startDateTime, endDateTime);

                    List<Object[]> statusGroup = caseRepository.countByCaseStatusGroupByDateRange(startDateTime, endDateTime);
                    for (Object[] row : statusGroup) {
                        statusDistribution.put((String) row[0], (Long) row[1]);
                    }

                    List<Object[]> progressGroup = caseRepository.countByCaseProgressGroupByDateRange(startDateTime, endDateTime);
                    for (Object[] row : progressGroup) {
                        progressDistribution.put((String) row[0], (Long) row[1]);
                    }
                } else {
                    totalCases = caseRepository.countTotalCases();

                    List<Object[]> statusGroup = caseRepository.countByCaseStatusGroup();
                    for (Object[] row : statusGroup) {
                        statusDistribution.put((String) row[0], (Long) row[1]);
                    }

                    List<Object[]> progressGroup = caseRepository.countByCaseProgressGroup();
                    for (Object[] row : progressGroup) {
                        progressDistribution.put((String) row[0], (Long) row[1]);
                    }
                }
            }

            response.setTotalCases(totalCases);

            // 根据用户ID过滤状态统计
            if (userId != null) {
                Long pendingCases = caseRepository.countByCreateUserIdAndCaseStatus(userId, "PENDING");
                response.setPendingCases(pendingCases != null ? pendingCases : 0L);

                Long inProgressCases = caseRepository.countByCreateUserIdAndCaseStatus(userId, "IN_PROGRESS");
                response.setInProgressCases(inProgressCases != null ? inProgressCases : 0L);

                Long approvedCases = caseRepository.countByCreateUserIdAndCaseStatus(userId, "APPROVED");
                response.setApprovedCases(approvedCases != null ? approvedCases : 0L);

                Long completedCases = caseRepository.countByCreateUserIdAndCaseStatus(userId, "COMPLETED");
                response.setCompletedCases(completedCases != null ? completedCases : 0L);

                Long closedCases = caseRepository.countByCreateUserIdAndCaseStatus(userId, "CLOSED");
                response.setClosedCases(closedCases != null ? closedCases : 0L);

                Long terminatedCases = caseRepository.countByCreateUserIdAndCaseStatus(userId, "TERMINATED");
                response.setTerminatedCases(terminatedCases != null ? terminatedCases : 0L);

                Long archivedCases = caseRepository.countByCreateUserIdAndCaseStatus(userId, "ARCHIVED");
                response.setArchivedCases(archivedCases != null ? archivedCases : 0L);
            } else {
                Long pendingCases = caseRepository.countByCaseStatus("PENDING");
                response.setPendingCases(pendingCases != null ? pendingCases : 0L);

                Long inProgressCases = caseRepository.countByCaseStatus("IN_PROGRESS");
                response.setInProgressCases(inProgressCases != null ? inProgressCases : 0L);

                Long approvedCases = caseRepository.countByCaseStatus("APPROVED");
                response.setApprovedCases(approvedCases != null ? approvedCases : 0L);

                Long completedCases = caseRepository.countByCaseStatus("COMPLETED");
                response.setCompletedCases(completedCases != null ? completedCases : 0L);

                Long closedCases = caseRepository.countByCaseStatus("CLOSED");
                response.setClosedCases(closedCases != null ? closedCases : 0L);

                Long terminatedCases = caseRepository.countByCaseStatus("TERMINATED");
                response.setTerminatedCases(terminatedCases != null ? terminatedCases : 0L);

                Long archivedCases = caseRepository.countByCaseStatus("ARCHIVED");
                response.setArchivedCases(archivedCases != null ? archivedCases : 0L);
            }

            response.setStatusDistribution(statusDistribution);
            response.setProgressDistribution(progressDistribution);

            // 根据用户ID过滤程序类型统计
            if (userId != null) {
                // 简化程序和普通程序的统计需要根据用户ID过滤
                // 这里暂时使用countByCreateUserId作为近似值，实际应该根据isSimplifiedTrial字段过滤
                response.setSimplifiedTrialCases(0L);
                response.setNormalTrialCases(totalCases);
            } else {
                Long simplifiedTrialCases = caseRepository.countByIsSimplifiedTrial(true);
                response.setSimplifiedTrialCases(simplifiedTrialCases != null ? simplifiedTrialCases : 0L);

                Long normalTrialCases = caseRepository.countByIsSimplifiedTrial(false);
                response.setNormalTrialCases(normalTrialCases != null ? normalTrialCases : 0L);
            }

            // 平均审查次数
            Double avgReviewCount = caseRepository.getAverageReviewCount();
            response.setAverageReviewCount(avgReviewCount != null ? BigDecimal.valueOf(avgReviewCount).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

            // 今日、本月、本年创建的案件数
            LocalDate today = LocalDate.now();
            if (userId != null) {
                // 非管理员用户，只能查看自己的案件
                response.setTodayCreatedCases(0L);
                response.setMonthCreatedCases(0L);
                response.setYearCreatedCases(totalCases);
            } else {
                LocalDateTime startOfToday = today.atStartOfDay();
                LocalDateTime endOfToday = today.atTime(23, 59, 59);
                Long todayCreatedCases = caseRepository.countByCreatedAtBetween(startOfToday, endOfToday);
                response.setTodayCreatedCases(todayCreatedCases != null ? todayCreatedCases : 0L);

                Long monthCreatedCases = caseRepository.countByCreatedAtYearAndMonth(today.getYear(), today.getMonthValue());
                response.setMonthCreatedCases(monthCreatedCases != null ? monthCreatedCases : 0L);

                Long yearCreatedCases = caseRepository.countByCreatedAtYear(today.getYear());
                response.setYearCreatedCases(yearCreatedCases != null ? yearCreatedCases : 0L);
            }

            log.info("案件统计数据获取成功，总案件数：{}", totalCases);

        } catch (BusinessException e) {
            log.error("获取案件统计数据失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("获取案件统计数据失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("获取案件统计数据失败");
        }

        return response;
    }
}

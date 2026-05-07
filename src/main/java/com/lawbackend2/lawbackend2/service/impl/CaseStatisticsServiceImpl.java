package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.CaseStatisticsRequest;
import com.lawbackend2.lawbackend2.dto.CaseStatisticsResponse;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.enums.CaseStatus;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
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
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class CaseStatisticsServiceImpl implements CaseStatisticsService {

    @Autowired
    private BankruptCaseRepository caseRepository;

    @Autowired
    private UserRepository userRepository;

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
                String realName = null;
                Optional<String> realNameOpt = userRepository.findRealNameById(userId);
                if (realNameOpt.isPresent()) {
                    realName = realNameOpt.get();
                }
                log.info("用户ID: {}, realName: {}", userId, realName);

                Long casesByCreator = caseRepository.countByCreateUserId(userId);
                Long casesByUndertaking = 0L;
                if (realName != null) {
                    casesByUndertaking = caseRepository.countByUndertakingPersonnel(realName);
                }
                totalCases = casesByCreator + casesByUndertaking;
                log.info("案件统计 - 创建人案件: {}, 承办人案件: {}, 总计: {}", casesByCreator, casesByUndertaking, totalCases);

                List<Object[]> statusGroupByCreator = caseRepository.countByUserIdAndCaseStatusGroup(userId);
                for (Object[] row : statusGroupByCreator) {
                    String status = (String) row[0];
                    Long count = (Long) row[1];
                    statusDistribution.merge(status, count, Long::sum);
                }

                List<Object[]> progressGroupByCreator = caseRepository.countByUserIdAndCaseProgressGroup(userId);
                for (Object[] row : progressGroupByCreator) {
                    String progress = (String) row[0];
                    Long count = (Long) row[1];
                    progressDistribution.merge(progress, count, Long::sum);
                }

                if (realName != null) {
                    List<Object[]> statusGroupByUndertaking = caseRepository.countByUndertakingPersonnelAndCaseStatusGroup(realName);
                    for (Object[] row : statusGroupByUndertaking) {
                        String status = (String) row[0];
                        Long count = (Long) row[1];
                        statusDistribution.merge(status, count, Long::sum);
                    }

                    List<Object[]> progressGroupByUndertaking = caseRepository.countByUndertakingPersonnelAndCaseProgressGroup(realName);
                    for (Object[] row : progressGroupByUndertaking) {
                        String progress = (String) row[0];
                        Long count = (Long) row[1];
                        progressDistribution.merge(progress, count, Long::sum);
                    }
                }
            } else {
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

            if (userId != null) {
                String realName = userRepository.findRealNameById(userId).orElse(null);

                Long pendingCases = caseRepository.countByCreateUserIdAndCaseStatus(userId, CaseStatus.PENDING.name());
                if (realName != null) {
                    pendingCases += caseRepository.countByUndertakingPersonnelAndCaseStatus(realName, CaseStatus.PENDING.name());
                }
                response.setPendingCases(pendingCases != null ? pendingCases : 0L);

                Long ongoingCases = caseRepository.countByCreateUserIdAndCaseStatus(userId, CaseStatus.ONGOING.name());
                if (realName != null) {
                    ongoingCases += caseRepository.countByUndertakingPersonnelAndCaseStatus(realName, CaseStatus.ONGOING.name());
                }
                response.setOngoingCases(ongoingCases != null ? ongoingCases : 0L);

                Long awaitingCases = caseRepository.countByCreateUserIdAndCaseStatus(userId, CaseStatus.AWAITING.name());
                if (realName != null) {
                    awaitingCases += caseRepository.countByUndertakingPersonnelAndCaseStatus(realName, CaseStatus.AWAITING.name());
                }
                response.setAwaitingCases(awaitingCases != null ? awaitingCases : 0L);

                Long completedCases = caseRepository.countByCreateUserIdAndCaseStatus(userId, CaseStatus.COMPLETED.name());
                if (realName != null && !realName.isEmpty()) {
                    completedCases += caseRepository.countByUndertakingPersonnelAndCaseStatus(realName, CaseStatus.COMPLETED.name());
                }
                response.setCompletedCases(completedCases != null ? completedCases : 0L);

                Long archivedCases = caseRepository.countByCreateUserIdAndCaseStatus(userId, CaseStatus.ARCHIVED.name());
                if (realName != null) {
                    archivedCases += caseRepository.countByUndertakingPersonnelAndCaseStatus(realName, CaseStatus.ARCHIVED.name());
                }
                response.setArchivedCases(archivedCases != null ? archivedCases : 0L);
            } else {
                Long pendingCases = caseRepository.countByCaseStatus(CaseStatus.PENDING.name());
                response.setPendingCases(pendingCases != null ? pendingCases : 0L);

                Long ongoingCases = caseRepository.countByCaseStatus(CaseStatus.ONGOING.name());
                response.setOngoingCases(ongoingCases != null ? ongoingCases : 0L);

                Long awaitingCases = caseRepository.countByCaseStatus(CaseStatus.AWAITING.name());
                response.setAwaitingCases(awaitingCases != null ? awaitingCases : 0L);

                Long completedCases = caseRepository.countByCaseStatus(CaseStatus.COMPLETED.name());
                response.setCompletedCases(completedCases != null ? completedCases : 0L);

                Long archivedCases = caseRepository.countByCaseStatus(CaseStatus.ARCHIVED.name());
                response.setArchivedCases(archivedCases != null ? archivedCases : 0L);
            }

            response.setStatusDistribution(statusDistribution);
            response.setProgressDistribution(progressDistribution);

            if (userId != null) {
                response.setSimplifiedTrialCases(0L);
                response.setNormalTrialCases(totalCases);
            } else {
                Long simplifiedTrialCases = caseRepository.countByIsSimplifiedTrial(true);
                response.setSimplifiedTrialCases(simplifiedTrialCases != null ? simplifiedTrialCases : 0L);

                Long normalTrialCases = caseRepository.countByIsSimplifiedTrial(false);
                response.setNormalTrialCases(normalTrialCases != null ? normalTrialCases : 0L);
            }

            Double avgReviewCount = caseRepository.getAverageReviewCount();
            response.setAverageReviewCount(avgReviewCount != null ? BigDecimal.valueOf(avgReviewCount).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

            LocalDate today = LocalDate.now();
            if (userId != null) {
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

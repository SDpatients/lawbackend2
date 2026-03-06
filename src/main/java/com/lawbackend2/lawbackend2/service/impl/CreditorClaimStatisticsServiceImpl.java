package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.CreditorClaimStatisticsRequest;
import com.lawbackend2.lawbackend2.dto.CreditorClaimStatisticsResponse;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.CreditorClaimRepository;
import com.lawbackend2.lawbackend2.service.CreditorClaimStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class CreditorClaimStatisticsServiceImpl implements CreditorClaimStatisticsService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CreditorClaimStatisticsServiceImpl.class);

    @Autowired
    private CreditorClaimRepository creditorClaimRepository;

    @Override
    public CreditorClaimStatisticsResponse getCreditorClaimStatistics(CreditorClaimStatisticsRequest request) {
        logger.info("开始获取债权申报统计数据，请求参数：{}", request);

        CreditorClaimStatisticsResponse response = new CreditorClaimStatisticsResponse();

        try {
            LocalDate startDate = request.getStartDate();
            LocalDate endDate = request.getEndDate();

            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                throw new BusinessException("开始日期不能晚于结束日期");
            }

            Long totalClaims;
            Map<String, Long> statusDistribution = new HashMap<>();
            Map<String, Long> claimTypeDistribution = new HashMap<>();
            Map<String, Long> claimNatureDistribution = new HashMap<>();

            if (startDate != null && endDate != null) {
                totalClaims = creditorClaimRepository.countByCreatedAtBetween(startDate, endDate);

                List<Object[]> statusGroup = creditorClaimRepository.countByRegistrationStatusGroupByDateRange(startDate, endDate);
                for (Object[] row : statusGroup) {
                    statusDistribution.put((String) row[0], (Long) row[1]);
                }

                List<Object[]> claimTypeGroup = creditorClaimRepository.countByClaimTypeGroupByDateRange(startDate, endDate);
                for (Object[] row : claimTypeGroup) {
                    claimTypeDistribution.put((String) row[0], (Long) row[1]);
                }

                List<Object[]> claimNatureGroup = creditorClaimRepository.countByClaimNatureGroupByDateRange(startDate, endDate);
                for (Object[] row : claimNatureGroup) {
                    claimNatureDistribution.put((String) row[0], (Long) row[1]);
                }
            } else {
                totalClaims = creditorClaimRepository.countTotalClaims();

                List<Object[]> statusGroup = creditorClaimRepository.countByRegistrationStatusGroup();
                for (Object[] row : statusGroup) {
                    statusDistribution.put((String) row[0], (Long) row[1]);
                }

                List<Object[]> claimTypeGroup = creditorClaimRepository.countByClaimTypeGroup();
                for (Object[] row : claimTypeGroup) {
                    claimTypeDistribution.put((String) row[0], (Long) row[1]);
                }

                List<Object[]> claimNatureGroup = creditorClaimRepository.countByClaimNatureGroup();
                for (Object[] row : claimNatureGroup) {
                    claimNatureDistribution.put((String) row[0], (Long) row[1]);
                }
            }

            response.setTotalClaims(totalClaims);

            Long pendingClaims = creditorClaimRepository.countByRegistrationStatus("PENDING");
            response.setPendingClaims(pendingClaims != null ? pendingClaims : 0L);

            Long registeredClaims = creditorClaimRepository.countByRegistrationStatus("REGISTERED");
            response.setRegisteredClaims(registeredClaims != null ? registeredClaims : 0L);

            Long rejectedClaims = creditorClaimRepository.countByRegistrationStatus("REJECTED");
            response.setRejectedClaims(rejectedClaims != null ? rejectedClaims : 0L);

            response.setStatusDistribution(statusDistribution);
            response.setClaimTypeDistribution(claimTypeDistribution);
            response.setClaimNatureDistribution(claimNatureDistribution);

            Optional<BigDecimal> principalSum = creditorClaimRepository.sumPrincipal();
            response.setTotalPrincipalAmount(principalSum.orElse(BigDecimal.ZERO));

            Optional<BigDecimal> interestSum = creditorClaimRepository.sumInterest();
            response.setTotalInterestAmount(interestSum.orElse(BigDecimal.ZERO));

            Optional<BigDecimal> penaltySum = creditorClaimRepository.sumPenalty();
            response.setTotalPenaltyAmount(penaltySum.orElse(BigDecimal.ZERO));

            Optional<BigDecimal> otherLossesSum = creditorClaimRepository.sumOtherLosses();
            response.setTotalOtherLossesAmount(otherLossesSum.orElse(BigDecimal.ZERO));

            Optional<BigDecimal> totalAmountSum = creditorClaimRepository.sumTotalAmount();
            response.setTotalClaimAmount(totalAmountSum.orElse(BigDecimal.ZERO));

            Optional<Double> avgClaimAmount = creditorClaimRepository.getAverageClaimAmount();
            response.setAverageClaimAmount(avgClaimAmount.isPresent() ? BigDecimal.valueOf(avgClaimAmount.get()).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

            LocalDate today = LocalDate.now();
            Long todayCreatedClaims = creditorClaimRepository.countByCreatedAtDate(today);
            response.setTodayCreatedClaims(todayCreatedClaims != null ? todayCreatedClaims : 0L);

            Long monthCreatedClaims = creditorClaimRepository.countByCreatedAtYearAndMonth(today.getYear(), today.getMonthValue());
            response.setMonthCreatedClaims(monthCreatedClaims != null ? monthCreatedClaims : 0L);

            Long yearCreatedClaims = creditorClaimRepository.countByCreatedAtYear(today.getYear());
            response.setYearCreatedClaims(yearCreatedClaims != null ? yearCreatedClaims : 0L);

            Long hasCourtJudgmentClaims = creditorClaimRepository.countByHasCourtJudgment(true);
            response.setHasCourtJudgmentClaims(hasCourtJudgmentClaims != null ? hasCourtJudgmentClaims : 0L);

            Long hasExecutionClaims = creditorClaimRepository.countByHasExecution(true);
            response.setHasExecutionClaims(hasExecutionClaims != null ? hasExecutionClaims : 0L);

            Long hasCollateralClaims = creditorClaimRepository.countByHasCollateral(true);
            response.setHasCollateralClaims(hasCollateralClaims != null ? hasCollateralClaims : 0L);

            logger.info("债权申报统计数据获取成功，总申报数：{}", totalClaims);

        } catch (BusinessException e) {
            logger.error("获取债权申报统计数据失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("获取债权申报统计数据失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("获取债权申报统计数据失败");
        }

        return response;
    }
}

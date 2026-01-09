package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class CreditorClaimStatisticsResponse {
    private Long totalClaims;

    private Long pendingClaims;

    private Long registeredClaims;

    private Long rejectedClaims;

    private Map<String, Long> statusDistribution;

    private Map<String, Long> claimTypeDistribution;

    private Map<String, Long> claimNatureDistribution;

    private BigDecimal totalPrincipalAmount;

    private BigDecimal totalInterestAmount;

    private BigDecimal totalPenaltyAmount;

    private BigDecimal totalOtherLossesAmount;

    private BigDecimal totalClaimAmount;

    private BigDecimal averageClaimAmount;

    private Long todayCreatedClaims;

    private Long monthCreatedClaims;

    private Long yearCreatedClaims;

    private Long hasCourtJudgmentClaims;

    private Long hasExecutionClaims;

    private Long hasCollateralClaims;
}

package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class CaseStatisticsResponse {
    private Long totalCases;

    private Long pendingCases;

    private Long inProgressCases;

    private Long approvedCases;

    private Long completedCases;

    private Long closedCases;

    private Long terminatedCases;

    private Long archivedCases;

    private Map<String, Long> statusDistribution;

    private Map<String, Long> progressDistribution;

    private Long simplifiedTrialCases;

    private Long normalTrialCases;

    private BigDecimal averageReviewCount;

    private Long todayCreatedCases;

    private Long monthCreatedCases;

    private Long yearCreatedCases;
}

package com.lawbackend2.lawbackend2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRegistrationStatsResponse {
    private Long totalCount;
    private BigDecimal totalPrincipal;
    private BigDecimal totalInterest;
    private BigDecimal totalPenalty;
    private BigDecimal totalOtherLosses;
    private BigDecimal totalAmount;
    private Map<String, Long> statusDistribution;
    private Map<String, Long> typeDistribution;
    private Map<String, Long> natureDistribution;
}

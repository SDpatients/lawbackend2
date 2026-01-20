package com.lawbackend2.lawbackend2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendData {
    private String period;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long count;
    private BigDecimal amount;
    private Long previousCount;
    private BigDecimal previousAmount;
    private Double growthRate;
}
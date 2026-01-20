package com.lawbackend2.lawbackend2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeTrendStatistics {
    private String type;
    private List<TrendData> trendData;
    private Long totalCount;
    private BigDecimal totalAmount;
    private Double averageGrowthRate;
}

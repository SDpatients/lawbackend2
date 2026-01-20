package com.lawbackend2.lawbackend2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankingStatistics {
    private String type;
    private String sortBy;
    private Integer topN;
    private List<RankingItem> rankings;
    private BigDecimal totalAmount;
}

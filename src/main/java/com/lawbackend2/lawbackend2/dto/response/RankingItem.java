package com.lawbackend2.lawbackend2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankingItem {
    private Long id;
    private String name;
    private BigDecimal amount;
    private Integer rank;
}
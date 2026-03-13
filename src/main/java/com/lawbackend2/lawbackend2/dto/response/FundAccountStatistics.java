package com.lawbackend2.lawbackend2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundAccountStatistics {

    private Long totalAccounts;
    private Long activeAccounts;
    private Long inactiveAccounts;
    private Double totalBalance;
    private Double totalFrozenAmount;
    private Map<String, Long> byAccountType;
}

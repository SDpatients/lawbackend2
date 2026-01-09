package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.util.Map;

@Data
public class FundAccountStatistics {

    private Long totalAccounts;
    private Long activeAccounts;
    private Long inactiveAccounts;
    private Double totalBalance;
    private Double totalFrozenAmount;
    private Map<String, Long> byAccountType;
}

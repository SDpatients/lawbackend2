package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.util.Map;

@Data
public class FundTransactionStatistics {

    private Long totalTransactions;
    private Long totalIncome;
    private Long totalExpense;
    private Double totalIncomeAmount;
    private Double totalExpenseAmount;
    private Double netAmount;
    private Map<String, Long> byTransactionType;
}

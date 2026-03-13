package com.lawbackend2.lawbackend2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundTransactionStatistics {

    private Long totalTransactions;
    private Long totalIncome;
    private Long totalExpense;
    private Double totalIncomeAmount;
    private Double totalExpenseAmount;
    private Double netAmount;
    private Map<String, Long> byTransactionType;
}

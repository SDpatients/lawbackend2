package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class YearlyTransactionStatistics {

    private Integer year;

    private BigDecimal totalIncomeAmount;

    private BigDecimal totalExpenseAmount;

    private BigDecimal netAmount;

    private Long totalIncomeCount;

    private Long totalExpenseCount;

    private Long totalTransactionCount;

    private List<MonthlyTransactionData> monthlyData;

    @Data
    public static class MonthlyTransactionData {
        private Integer month;
        private String monthLabel;
        private BigDecimal incomeAmount;
        private BigDecimal expenseAmount;
        private BigDecimal netAmount;
        private Long incomeCount;
        private Long expenseCount;
    }
}

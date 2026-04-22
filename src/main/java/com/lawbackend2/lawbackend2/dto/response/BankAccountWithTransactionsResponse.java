package com.lawbackend2.lawbackend2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountWithTransactionsResponse {
    private Long id;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUserId;
    private Long updateUserId;
    private String accountName;
    private String bankName;
    private String accountNumber;
    private String accountType;
    private String currency;
    private BigDecimal currentBalance;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private Long caseId;
    private String caseNumber;
    private String caseName;
    private List<BankAccountTransactionResponse> transactions;
    private BigDecimal totalInflow;
    private BigDecimal totalOutflow;
}
package com.lawbackend2.lawbackend2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BankAccountResponse {
    private Long id;
    private String status;
    private Boolean isDeleted;
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
}

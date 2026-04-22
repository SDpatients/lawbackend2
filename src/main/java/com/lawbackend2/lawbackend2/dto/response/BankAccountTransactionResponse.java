package com.lawbackend2.lawbackend2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BankAccountTransactionResponse {
    private Long id;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUserId;
    private Long updateUserId;
    private Long accountId;
    private String accountName;
    private String accountNumber;
    private String bankName;
    private String transactionType;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private String summary;
    private String businessType;
    private String counterpartyAccount;
    private String counterpartyName;
    private BigDecimal balanceAfter;
    private Long attachmentId;
    private Long relatedBusinessId;
    private String remark;
    private Long caseId;
    private String caseNumber;
    private String caseName;
}

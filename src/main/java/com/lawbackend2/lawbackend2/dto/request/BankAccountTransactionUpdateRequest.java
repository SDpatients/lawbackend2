package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BankAccountTransactionUpdateRequest {

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
}

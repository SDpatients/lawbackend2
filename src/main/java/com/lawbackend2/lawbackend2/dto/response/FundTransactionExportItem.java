package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FundTransactionExportItem {

    private Long id;
    private String transactionNumber;
    private String transactionType;
    private BigDecimal transactionAmount;
    private String transactionDirection;
    private LocalDateTime transactionTime;
    private String accountName;
    private String accountNumber;
    private String transactionDescription;
    private String status;
}

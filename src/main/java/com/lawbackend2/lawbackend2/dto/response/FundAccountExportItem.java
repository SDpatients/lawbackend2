package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FundAccountExportItem {
    private Long id;
    private String accountName;
    private String accountType;
    private String accountPurpose;
    private BigDecimal currentBalance;
    private BigDecimal initialBalance;
    private String status;
    private Boolean isFrozen;
    private LocalDateTime freezeDate;
    private String freezeReason;
    private String bankName;
    private String bankAccount;
    private LocalDateTime openingDate;
}

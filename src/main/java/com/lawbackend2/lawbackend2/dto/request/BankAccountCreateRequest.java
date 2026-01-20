package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BankAccountCreateRequest {

    private String accountName;

    private String bankName;

    private String accountNumber;

    private String accountType;

    private String currency = "CNY";

    private BigDecimal currentBalance;

    private LocalDate openingDate;

    private String password;

    private Long caseId;
}

package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BankAccountUpdateRequest {

    @NotBlank(message = "账户名称不能为空")
    private String accountName;

    @NotNull(message = "当前余额不能为空")
    @Positive(message = "当前余额必须大于0")
    private BigDecimal currentBalance;

    private Long caseId;
    
    private String accountNumber;
    
    private String accountType;
    
    private String password;
    
    private String currency;
    
    private LocalDate openingDate;
    
    private LocalDate closingDate;
    
    private String status;
    
    private String bankName;
}

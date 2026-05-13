package com.lawbackend2.lawbackend2.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lawbackend2.lawbackend2.annotation.Mask;
import com.lawbackend2.lawbackend2.annotation.MaskType;
import com.lawbackend2.lawbackend2.util.MaskSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountResponse {
    private Long id;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUserId;
    private Long updateUserId;
    private String accountName;
    private String bankName;

    @Mask(MaskType.BANK_ACCOUNT)
    @JsonSerialize(using = MaskSerializer.class)
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

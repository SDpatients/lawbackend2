package com.lawbackend2.lawbackend2.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lawbackend2.lawbackend2.annotation.Mask;
import com.lawbackend2.lawbackend2.annotation.MaskType;
import com.lawbackend2.lawbackend2.util.MaskSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreditorClaimQueryResponse {
    private Long creditorId;

    private Long caseId;

    private String creditorName;

    private String creditorType;

    private String creditorStatus;

    @Mask(MaskType.PHONE)
    @JsonSerialize(using = MaskSerializer.class)
    private String contactPhone;

    private String contactEmail;

    private String address;

    @Mask(MaskType.ID_CARD)
    @JsonSerialize(using = MaskSerializer.class)
    private String idNumber;

    private String legalRepresentative;

    private BigDecimal registeredCapital;

    private String caseNumber;

    private String caseName;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String claimType;

    private String accountName;

    @Mask(MaskType.BANK_ACCOUNT)
    @JsonSerialize(using = MaskSerializer.class)
    private String creditorBankAccount;

    private String bankName;

    private BigDecimal declaredPrincipal;

    private BigDecimal declaredInterest;

    private BigDecimal declaredPenalty;

    private BigDecimal declaredOtherLosses;

    private BigDecimal declaredTotalAmount;

    private String remarks;

    private BigDecimal confirmedPrincipal;

    private BigDecimal confirmedInterest;

    private BigDecimal confirmedPenalty;

    private BigDecimal confirmedOtherLosses;

    private BigDecimal confirmedTotalAmount;

    private BigDecimal reductionAmount;
}

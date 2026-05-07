package com.lawbackend2.lawbackend2.dto;

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

    private String contactPhone;

    private String contactEmail;

    private String address;

    private String idNumber;

    private String legalRepresentative;

    private BigDecimal registeredCapital;

    private String caseNumber;

    private String caseName;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String claimType;

    private String accountName;

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

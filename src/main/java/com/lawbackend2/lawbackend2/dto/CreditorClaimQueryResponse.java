package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditorClaimQueryResponse {
    private Long creditorId;
    
    private String creditorName;
    
    private String creditorType;
    
    private String creditorStatus;
    
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

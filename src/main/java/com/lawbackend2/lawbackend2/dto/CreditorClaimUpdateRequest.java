package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditorClaimUpdateRequest {
    private String creditorName;

    private BigDecimal principal;

    private BigDecimal interest;

    private BigDecimal penalty;

    private BigDecimal otherLosses;

    private BigDecimal totalAmount;

    private String claimType;

    private String claimFacts;

    private String remarks;

    private String claimNature;

    private Integer hasCourtJudgment;

    private Integer hasExecution;

    private Integer hasCollateral;
}

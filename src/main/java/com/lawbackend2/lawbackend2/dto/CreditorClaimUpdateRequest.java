package com.lawbackend2.lawbackend2.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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

    @JsonDeserialize(using = BooleanDeserializer.class)
    private Boolean hasCourtJudgment;

    @JsonDeserialize(using = BooleanDeserializer.class)
    private Boolean hasExecution;

    @JsonDeserialize(using = BooleanDeserializer.class)
    private Boolean hasCollateral;
}

package com.lawbackend2.lawbackend2.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ClaimRegistrationUpdateRequest {
    private String creditorName;

    private String creditorType;

    private String creditCode;

    private String legalRepresentative;

    private String serviceAddress;

    private String agentName;

    private String agentPhone;

    private String agentIdCard;

    private String agentAddress;

    private String accountName;

    private String creditorBankAccount;

    private String bankName;

    private BigDecimal principal;

    private BigDecimal interest;

    private BigDecimal penalty;

    private BigDecimal otherLosses;

    private BigDecimal totalAmount;

    @JsonDeserialize(using = BooleanDeserializer.class)
    private Boolean hasCourtJudgment;

    @JsonDeserialize(using = BooleanDeserializer.class)
    private Boolean hasExecution;

    @JsonDeserialize(using = BooleanDeserializer.class)
    private Boolean hasCollateral;

    private String claimNature;

    private String claimType;

    private String claimFacts;

    private String claimIdentifier;

    private String evidenceList;

    private String evidenceMaterials;

    private String evidenceAttachments;

    private LocalDateTime registrationDate;

    private LocalDateTime registrationDeadline;

    private String materialReceiver;

    private LocalDateTime materialReceiveDate;

    private String materialCompleteness;

    private String registrationStatus;

    private String remarks;
}

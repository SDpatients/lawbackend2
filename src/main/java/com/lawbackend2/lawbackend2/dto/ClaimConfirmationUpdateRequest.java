package com.lawbackend2.lawbackend2.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ClaimConfirmationUpdateRequest {
    private String meetingType;

    private LocalDateTime meetingDate;

    private String meetingLocation;

    private String voteResult;

    private String voteNotes;

    @JsonDeserialize(using = BooleanDeserializer.class)
    private Boolean hasObjection;

    private String objector;

    private String objectionReason;

    private BigDecimal objectionAmount;

    private LocalDateTime objectionDate;

    private String negotiationResult;

    private LocalDateTime negotiationDate;

    private String negotiationParticipants;

    private LocalDateTime courtRulingDate;

    private String courtRulingNo;

    private String courtRulingResult;

    private BigDecimal courtRulingAmount;

    private String courtRulingNotes;

    @JsonDeserialize(using = BooleanDeserializer.class)
    private Boolean hasLawsuit;

    private String lawsuitCaseNo;

    private String lawsuitStatus;

    private String lawsuitResult;

    private BigDecimal lawsuitAmount;

    private String lawsuitNotes;

    private BigDecimal finalConfirmedAmount;

    private LocalDateTime finalConfirmationDate;

    private String finalConfirmationBasis;

    private List<String> confirmationAttachments;

    private String confirmationStatus;

    private String remarks;

    private BigDecimal declaredPrincipal;

    private BigDecimal declaredInterest;

    private BigDecimal declaredPenalty;

    private BigDecimal declaredOtherLosses;

    private BigDecimal declaredTotalAmount;

    private BigDecimal confirmedPrincipal;

    private BigDecimal confirmedInterest;

    private BigDecimal confirmedPenalty;

    private BigDecimal confirmedOtherLosses;

    private BigDecimal confirmedTotalAmount;

    private BigDecimal unconfirmedPrincipal;

    private BigDecimal unconfirmedInterest;

    private BigDecimal unconfirmedPenalty;

    private BigDecimal unconfirmedOtherLosses;

    private BigDecimal unconfirmedTotalAmount;
}

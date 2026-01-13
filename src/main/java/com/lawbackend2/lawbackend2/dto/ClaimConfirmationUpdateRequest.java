package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ClaimConfirmationUpdateRequest {
    private String meetingType;

    private LocalDateTime meetingDate;

    private String meetingLocation;

    private String voteResult;

    private String voteNotes;

    private Integer hasObjection;

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

    private Integer hasLawsuit;

    private String lawsuitCaseNo;

    private String lawsuitStatus;

    private String lawsuitResult;

    private BigDecimal lawsuitAmount;

    private String lawsuitNotes;

    private BigDecimal finalConfirmedAmount;

    private LocalDateTime finalConfirmationDate;

    private String finalConfirmationBasis;

    private String confirmationAttachments;

    private String confirmationStatus;

    private String remarks;
}

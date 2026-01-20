package com.lawbackend2.lawbackend2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ClaimConfirmationCreateRequest {
    @NotNull(message = "债权申报ID不能为空")
    private Long claimRegistrationId;

    private Long caseId;

    private String creditorName;

    private String meetingType;

    private LocalDateTime meetingDate;

    private String meetingLocation;

    private String voteResult;

    private String voteNotes;

    private Integer hasObjection = 0;

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

    private Integer hasLawsuit = 0;

    private String lawsuitCaseNo;

    private String lawsuitStatus;

    private String lawsuitResult;

    private BigDecimal lawsuitAmount;

    private String lawsuitNotes;

    private BigDecimal finalConfirmedAmount;

    private LocalDateTime finalConfirmationDate;

    private String finalConfirmationBasis;

    private String confirmationAttachments;

    private String confirmationStatus = "PENDING";

    private String remarks;
}

package com.lawbackend2.lawbackend2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ClaimConfirmationCreateRequest {
    @NotNull(message = "债权申报 ID 不能为空")
    private Long claimRegistrationId;

    private Long caseId;

    private String creditorName;

    private String meetingType;

    private LocalDateTime meetingDate;

    private String meetingLocation;

    private String voteResult;

    private String voteNotes;

    @JsonDeserialize(using = BooleanDeserializer.class)
    private Boolean hasObjection = false;

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
    private Boolean hasLawsuit = false;

    private String lawsuitCaseNo;

    private String lawsuitStatus;

    private String lawsuitResult;

    private BigDecimal lawsuitAmount;

    private String lawsuitNotes;

    private BigDecimal finalConfirmedAmount;

    private LocalDateTime finalConfirmationDate;

    private String finalConfirmationBasis;

    private List<ReviewAttachmentDto> confirmationAttachments;

    private String confirmationStatus = "PENDING";

    private String remarks;
}

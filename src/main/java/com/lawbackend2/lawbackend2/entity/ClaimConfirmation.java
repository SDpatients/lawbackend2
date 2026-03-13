package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_claim_confirmation")
public class ClaimConfirmation extends BaseEntity {
    @Column(name = "claim_registration_id", nullable = false)
    private Long claimRegistrationId;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "creditor_name", length = 255)
    private String creditorName;

    @Column(name = "meeting_type", length = 50)
    private String meetingType;

    @Column(name = "meeting_date")
    private LocalDateTime meetingDate;

    @Column(name = "meeting_location", length = 255)
    private String meetingLocation;

    @Column(name = "vote_result", length = 20)
    private String voteResult;

    @Column(name = "vote_notes", columnDefinition = "TEXT")
    private String voteNotes;

    @Column(name = "has_objection")
    private Boolean hasObjection = false;

    @Column(name = "objector", length = 255)
    private String objector;

    @Column(name = "objection_reason", columnDefinition = "TEXT")
    private String objectionReason;

    @Column(name = "objection_amount", precision = 18, scale = 2)
    private BigDecimal objectionAmount;

    @Column(name = "objection_date")
    private LocalDateTime objectionDate;

    @Column(name = "negotiation_result", columnDefinition = "TEXT")
    private String negotiationResult;

    @Column(name = "negotiation_date")
    private LocalDateTime negotiationDate;

    @Column(name = "negotiation_participants", columnDefinition = "TEXT")
    private String negotiationParticipants;

    @Column(name = "court_ruling_date")
    private LocalDateTime courtRulingDate;

    @Column(name = "court_ruling_no", length = 100)
    private String courtRulingNo;

    @Column(name = "court_ruling_result", length = 20)
    private String courtRulingResult;

    @Column(name = "court_ruling_amount", precision = 18, scale = 2)
    private BigDecimal courtRulingAmount;

    @Column(name = "court_ruling_notes", columnDefinition = "TEXT")
    private String courtRulingNotes;

    @Column(name = "has_lawsuit")
    private Boolean hasLawsuit = false;

    @Column(name = "lawsuit_case_no", length = 100)
    private String lawsuitCaseNo;

    @Column(name = "lawsuit_status", length = 20)
    private String lawsuitStatus;

    @Column(name = "lawsuit_result", length = 20)
    private String lawsuitResult;

    @Column(name = "lawsuit_amount", precision = 18, scale = 2)
    private BigDecimal lawsuitAmount;

    @Column(name = "lawsuit_notes", columnDefinition = "TEXT")
    private String lawsuitNotes;

    @Column(name = "final_confirmed_amount", precision = 18, scale = 2)
    private BigDecimal finalConfirmedAmount;

    @Column(name = "final_confirmation_date")
    private LocalDateTime finalConfirmationDate;

    @Column(name = "final_confirmation_basis", length = 50)
    private String finalConfirmationBasis;

    @Column(name = "confirmation_attachments", columnDefinition = "TEXT")
    private String confirmationAttachments;

    @Column(name = "confirmation_status", length = 20)
    private String confirmationStatus = "PENDING";

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "declared_principal", precision = 18, scale = 2)
    private BigDecimal declaredPrincipal;

    @Column(name = "declared_interest", precision = 18, scale = 2)
    private BigDecimal declaredInterest;

    @Column(name = "declared_penalty", precision = 18, scale = 2)
    private BigDecimal declaredPenalty;

    @Column(name = "declared_other_losses", precision = 18, scale = 2)
    private BigDecimal declaredOtherLosses;

    @Column(name = "declared_total_amount", precision = 18, scale = 2)
    private BigDecimal declaredTotalAmount;

    @Column(name = "confirmed_principal", precision = 18, scale = 2)
    private BigDecimal confirmedPrincipal;

    @Column(name = "confirmed_interest", precision = 18, scale = 2)
    private BigDecimal confirmedInterest;

    @Column(name = "confirmed_penalty", precision = 18, scale = 2)
    private BigDecimal confirmedPenalty;

    @Column(name = "confirmed_other_losses", precision = 18, scale = 2)
    private BigDecimal confirmedOtherLosses;

    @Column(name = "confirmed_total_amount", precision = 18, scale = 2)
    private BigDecimal confirmedTotalAmount;

    @Column(name = "unconfirmed_principal", precision = 18, scale = 2)
    private BigDecimal unconfirmedPrincipal;

    @Column(name = "unconfirmed_interest", precision = 18, scale = 2)
    private BigDecimal unconfirmedInterest;

    @Column(name = "unconfirmed_penalty", precision = 18, scale = 2)
    private BigDecimal unconfirmedPenalty;

    @Column(name = "unconfirmed_other_losses", precision = 18, scale = 2)
    private BigDecimal unconfirmedOtherLosses;

    @Column(name = "unconfirmed_total_amount", precision = 18, scale = 2)
    private BigDecimal unconfirmedTotalAmount;
}

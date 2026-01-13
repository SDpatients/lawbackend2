package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_claim_review")
public class ClaimReview extends BaseEntity {
    @Column(name = "claim_registration_id", nullable = false)
    private Long claimRegistrationId;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "creditor_name", length = 255)
    private String creditorName;

    @Column(name = "review_date")
    private LocalDateTime reviewDate;

    @Column(name = "reviewer", length = 100)
    private String reviewer;

    @Column(name = "review_round")
    private Integer reviewRound = 1;

    @Column(name = "review_basis", columnDefinition = "TEXT")
    private String reviewBasis;

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

    @Column(name = "adjustment_reason", columnDefinition = "TEXT")
    private String adjustmentReason;

    @Column(name = "unconfirmed_reason", columnDefinition = "TEXT")
    private String unconfirmedReason;

    @Column(name = "insufficient_evidence_reason", columnDefinition = "TEXT")
    private String insufficientEvidenceReason;

    @Column(name = "expired_reason", columnDefinition = "TEXT")
    private String expiredReason;

    @Column(name = "evidence_authenticity", length = 20)
    private String evidenceAuthenticity;

    @Column(name = "evidence_relevance", length = 20)
    private String evidenceRelevance;

    @Column(name = "evidence_legality", length = 20)
    private String evidenceLegality;

    @Column(name = "evidence_review_notes", columnDefinition = "TEXT")
    private String evidenceReviewNotes;

    @Column(name = "confirmed_claim_nature", length = 50)
    private String confirmedClaimNature;

    @Column(name = "is_joint_liability")
    private Boolean isJointLiability = false;

    @Column(name = "is_conditional")
    private Boolean isConditional = false;

    @Column(name = "is_term")
    private Boolean isTerm = false;

    @Column(name = "collateral_type", length = 50)
    private String collateralType;

    @Column(name = "collateral_property", length = 255)
    private String collateralProperty;

    @Column(name = "collateral_amount", precision = 18, scale = 2)
    private BigDecimal collateralAmount;

    @Column(name = "collateral_term", length = 100)
    private String collateralTerm;

    @Column(name = "collateral_validity", length = 20)
    private String collateralValidity;

    @Column(name = "review_conclusion", length = 20)
    private String reviewConclusion;

    @Column(name = "review_summary", columnDefinition = "TEXT")
    private String reviewSummary;

    @Column(name = "review_report", columnDefinition = "TEXT")
    private String reviewReport;

    @Column(name = "review_attachments", columnDefinition = "TEXT")
    private String reviewAttachments;

    @Column(name = "review_status", length = 20)
    private String reviewStatus = "COMPLETED";

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
}

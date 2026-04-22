package com.lawbackend2.lawbackend2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ClaimReviewUpdateRequest {
    private LocalDateTime reviewDate;

    private String reviewer;

    private String reviewBasis;

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

    private String adjustmentReason;

    private String unconfirmedReason;

    private String insufficientEvidenceReason;

    private String expiredReason;

    private String evidenceAuthenticity;

    private String evidenceRelevance;

    private String evidenceLegality;

    private String evidenceReviewNotes;

    private String confirmedClaimNature;

    private Integer isJointLiability;

    private Integer isConditional;

    private Integer isTerm;

    private String collateralType;

    private String collateralProperty;

    private BigDecimal collateralAmount;

    private String collateralTerm;

    private String collateralValidity;

    private String reviewConclusion;

    private String reviewSummary;

    private String reviewReport;

    private List<ReviewAttachmentDto> reviewAttachments;

    private String reviewStatus;

    private String remarks;
}

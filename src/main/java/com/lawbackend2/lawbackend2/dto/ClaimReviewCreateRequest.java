package com.lawbackend2.lawbackend2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ClaimReviewCreateRequest {
    @NotNull(message = "债权申报ID不能为空")
    private Long claimRegistrationId;

    private Long caseId;

    private String creditorName;

    private LocalDateTime reviewDate;

    private String reviewer;

    private Integer reviewRound = 1;

    private String reviewBasis;

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

    private String adjustmentReason;

    private String unconfirmedReason;

    private String insufficientEvidenceReason;

    private String expiredReason;

    private String evidenceAuthenticity;

    private String evidenceRelevance;

    private String evidenceLegality;

    private String evidenceReviewNotes;

    private String confirmedClaimNature;

    private Integer isJointLiability = 0;

    private Integer isConditional = 0;

    private Integer isTerm = 0;

    private String collateralType;

    private String collateralProperty;

    private BigDecimal collateralAmount;

    private String collateralTerm;

    private String collateralValidity;

    private String reviewConclusion;

    private String reviewSummary;

    private String reviewReport;

    private List<String> reviewAttachments;

    private String reviewStatus = "PENDING";

    private String remarks;
}

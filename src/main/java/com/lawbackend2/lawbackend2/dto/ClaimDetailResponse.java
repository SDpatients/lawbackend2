package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ClaimDetailResponse {
    private Long id;
    private String claimNo;
    private Long caseId;
    private String caseName;
    private String debtor;
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
    private Boolean hasCourtJudgment;
    private Boolean hasExecution;
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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private ClaimReviewInfo reviewInfo;
    private ClaimConfirmationInfo confirmationInfo;

    @Data
    public static class ClaimReviewInfo {
        private Long id;
        private LocalDateTime reviewDate;
        private String reviewer;
        private Integer reviewRound;
        private String reviewBasis;
        private BigDecimal declaredTotalAmount;
        private BigDecimal confirmedTotalAmount;
        private BigDecimal unconfirmedTotalAmount;
        private String adjustmentReason;
        private String unconfirmedReason;
        private String evidenceAuthenticity;
        private String evidenceRelevance;
        private String evidenceLegality;
        private String confirmedClaimNature;
        private Boolean isJointLiability;
        private Boolean isConditional;
        private Boolean isTerm;
        private String collateralType;
        private String collateralProperty;
        private BigDecimal collateralAmount;
        private String collateralValidity;
        private String reviewConclusion;
        private String reviewSummary;
        private String reviewStatus;
    }

    @Data
    public static class ClaimConfirmationInfo {
        private Long id;
        private String meetingType;
        private LocalDateTime meetingDate;
        private String meetingLocation;
        private String voteResult;
        private Boolean hasObjection;
        private String objector;
        private String objectionReason;
        private BigDecimal objectionAmount;
        private LocalDateTime objectionDate;
        private String negotiationResult;
        private LocalDateTime negotiationDate;
        private String courtRulingNo;
        private String courtRulingResult;
        private BigDecimal courtRulingAmount;
        private LocalDateTime courtRulingDate;
        private Boolean hasLawsuit;
        private String lawsuitCaseNo;
        private String lawsuitStatus;
        private String lawsuitResult;
        private BigDecimal lawsuitAmount;
        private BigDecimal finalConfirmedAmount;
        private LocalDateTime finalConfirmationDate;
        private String finalConfirmationBasis;
        private String confirmationStatus;
    }
}

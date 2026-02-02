package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreditorClaimStagesResponse {
    private Long creditorId;
    private String creditorName;
    
    private List<ClaimRegistrationInfo> claimRegistrations;
    private List<ClaimReviewInfo> claimReviews;
    private List<ClaimConfirmationInfo> claimConfirmations;
    
    @Data
    public static class ClaimRegistrationInfo {
        private Long id;
        private String claimNo;
        private Long caseId;
        private String caseName;
        private String debtor;
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
    }
    
    @Data
    public static class ClaimReviewInfo {
        private Long id;
        private Long claimRegistrationId;
        private Long caseId;
        private String creditorName;
        private LocalDateTime reviewDate;
        private String reviewer;
        private Integer reviewRound;
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
        private Boolean isJointLiability;
        private Boolean isConditional;
        private Boolean isTerm;
        private String collateralType;
        private String collateralProperty;
        private BigDecimal collateralAmount;
        private String collateralTerm;
        private String collateralValidity;
        private String reviewConclusion;
        private String reviewSummary;
        private String reviewReport;
        private String reviewAttachments;
        private String reviewStatus;
        private String remarks;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
    }
    
    @Data
    public static class ClaimConfirmationInfo {
        private Long id;
        private Long claimRegistrationId;
        private Long caseId;
        private String creditorName;
        private String meetingType;
        private LocalDateTime meetingDate;
        private String meetingLocation;
        private String voteResult;
        private String voteNotes;
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
        private Boolean hasLawsuit;
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
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
    }
}

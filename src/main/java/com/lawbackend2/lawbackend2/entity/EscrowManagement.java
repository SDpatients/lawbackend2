package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_escrow_management", indexes = {
    @Index(name = "uk_escrow_no", columnList = "escrow_no", unique = true),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_escrow_type", columnList = "escrow_type"),
    @Index(name = "idx_creditor_name", columnList = "creditor_name"),
    @Index(name = "idx_release_status", columnList = "release_status"),
    @Index(name = "idx_escrow_date", columnList = "escrow_date"),
    @Index(name = "idx_release_date", columnList = "release_date"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class EscrowManagement extends BaseEntity {

    @Column(name = "escrow_no", length = 50, unique = true)
    private String escrowNo;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_name", length = 200)
    private String caseName;

    @Column(name = "escrow_type", length = 50)
    private String escrowType;

    @Column(name = "escrow_name", length = 200)
    private String escrowName;

    @Column(name = "escrow_description", columnDefinition = "TEXT")
    private String escrowDescription;

    @Column(name = "creditor_name", length = 200)
    private String creditorName;

    @Column(name = "creditor_claim_id")
    private Long creditorClaimId;

    @Column(name = "escrow_amount", precision = 18, scale = 2)
    private BigDecimal escrowAmount;

    @Column(name = "released_amount", precision = 18, scale = 2)
    private BigDecimal releasedAmount = BigDecimal.ZERO;

    @Column(name = "unreleased_amount", precision = 18, scale = 2)
    private BigDecimal unreleasedAmount;

    @Column(name = "escrow_reason", columnDefinition = "TEXT")
    private String escrowReason;

    @Column(name = "escrow_institution", length = 200)
    private String escrowInstitution;

    @Column(name = "escrow_account", length = 100)
    private String escrowAccount;

    @Column(name = "escrow_date")
    private LocalDateTime escrowDate;

    @Column(name = "release_condition", columnDefinition = "TEXT")
    private String releaseCondition;

    @Column(name = "is_condition_met")
    private Boolean isConditionMet = false;

    @Column(name = "condition_met_date")
    private LocalDateTime conditionMetDate;

    @Column(name = "release_status", length = 20)
    private String releaseStatus = "UNRELEASED";

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @Column(name = "release_account_id")
    private Long releaseAccountId;

    @Column(name = "release_voucher", length = 200)
    private String releaseVoucher;

    @Column(name = "related_distribution_id")
    private Long relatedDistributionId;

    @Column(name = "related_flow_id")
    private Long relatedFlowId;

    @Column(name = "attachments", columnDefinition = "TEXT")
    private String attachments;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
}
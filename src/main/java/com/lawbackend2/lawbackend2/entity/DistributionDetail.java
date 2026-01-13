package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_distribution_detail", indexes = {
    @Index(name = "idx_distribution_execution_id", columnList = "distribution_execution_id"),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_creditor_claim_id", columnList = "creditor_claim_id"),
    @Index(name = "idx_creditor_name", columnList = "creditor_name"),
    @Index(name = "idx_creditor_type", columnList = "creditor_type"),
    @Index(name = "idx_payment_status", columnList = "payment_status"),
    @Index(name = "idx_payment_date", columnList = "payment_date"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class DistributionDetail extends BaseEntity {

    @Column(name = "distribution_execution_id")
    private Long distributionExecutionId;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_name", length = 200)
    private String caseName;

    @Column(name = "creditor_claim_id")
    private Long creditorClaimId;

    @Column(name = "creditor_name", length = 200)
    private String creditorName;

    @Column(name = "creditor_type", length = 50)
    private String creditorType;

    @Column(name = "claim_amount", precision = 18, scale = 2)
    private BigDecimal claimAmount;

    @Column(name = "confirmed_amount", precision = 18, scale = 2)
    private BigDecimal confirmedAmount;

    @Column(name = "current_distribution_amount", precision = 18, scale = 2)
    private BigDecimal currentDistributionAmount;

    @Column(name = "accumulated_distribution_amount", precision = 18, scale = 2)
    private BigDecimal accumulatedDistributionAmount = BigDecimal.ZERO;

    @Column(name = "distribution_ratio", precision = 5, scale = 2)
    private BigDecimal distributionRatio;

    @Column(name = "payment_status", length = 20)
    private String paymentStatus = "UNPAID";

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "payment_account_id")
    private Long paymentAccountId;

    @Column(name = "payment_voucher", length = 200)
    private String paymentVoucher;

    @Column(name = "payee_account_name", length = 200)
    private String payeeAccountName;

    @Column(name = "payee_account_number", length = 100)
    private String payeeAccountNumber;

    @Column(name = "payee_bank_name", length = 200)
    private String payeeBankName;

    @Column(name = "is_escrowed")
    private Boolean isEscrowed = false;

    @Column(name = "escrow_id")
    private Long escrowId;

    @Column(name = "escrow_reason", columnDefinition = "TEXT")
    private String escrowReason;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
}
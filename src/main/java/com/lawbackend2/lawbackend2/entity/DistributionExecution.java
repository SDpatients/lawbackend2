package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_distribution_execution", indexes = {
    @Index(name = "uk_distribution_no", columnList = "distribution_no", unique = true),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_distribution_batch", columnList = "distribution_batch"),
    @Index(name = "idx_approval_status", columnList = "approval_status"),
    @Index(name = "idx_execution_status", columnList = "execution_status"),
    @Index(name = "idx_distribution_date", columnList = "distribution_date"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class DistributionExecution extends BaseEntity {

    @Column(name = "distribution_no", length = 50, unique = true)
    private String distributionNo;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_name", length = 200)
    private String caseName;

    @Column(name = "distribution_plan_id")
    private Long distributionPlanId;

    @Column(name = "distribution_plan_name", length = 200)
    private String distributionPlanName;

    @Column(name = "distribution_batch", length = 50)
    private String distributionBatch;

    @Column(name = "distribution_date")
    private LocalDateTime distributionDate;

    @Column(name = "distribution_method", length = 50)
    private String distributionMethod;

    @Column(name = "total_distributable_amount", precision = 18, scale = 2)
    private BigDecimal totalDistributableAmount;

    @Column(name = "total_distribution_amount", precision = 18, scale = 2)
    private BigDecimal totalDistributionAmount;

    @Column(name = "accumulated_distribution_amount", precision = 18, scale = 2)
    private BigDecimal accumulatedDistributionAmount = BigDecimal.ZERO;

    @Column(name = "expense_amount", precision = 18, scale = 2)
    private BigDecimal expenseAmount = BigDecimal.ZERO;

    @Column(name = "expense_ratio", precision = 5, scale = 2)
    private BigDecimal expenseRatio;

    @Column(name = "expense_count")
    private Integer expenseCount = 0;

    @Column(name = "common_debt_amount", precision = 18, scale = 2)
    private BigDecimal commonDebtAmount = BigDecimal.ZERO;

    @Column(name = "common_debt_ratio", precision = 5, scale = 2)
    private BigDecimal commonDebtRatio;

    @Column(name = "common_debt_count")
    private Integer commonDebtCount = 0;

    @Column(name = "employee_claim_amount", precision = 18, scale = 2)
    private BigDecimal employeeClaimAmount = BigDecimal.ZERO;

    @Column(name = "employee_claim_ratio", precision = 5, scale = 2)
    private BigDecimal employeeClaimRatio;

    @Column(name = "employee_claim_count")
    private Integer employeeClaimCount = 0;

    @Column(name = "tax_claim_amount", precision = 18, scale = 2)
    private BigDecimal taxClaimAmount = BigDecimal.ZERO;

    @Column(name = "tax_claim_ratio", precision = 5, scale = 2)
    private BigDecimal taxClaimRatio;

    @Column(name = "tax_claim_count")
    private Integer taxClaimCount = 0;

    @Column(name = "secured_claim_amount", precision = 18, scale = 2)
    private BigDecimal securedClaimAmount = BigDecimal.ZERO;

    @Column(name = "secured_claim_ratio", precision = 5, scale = 2)
    private BigDecimal securedClaimRatio;

    @Column(name = "secured_claim_count")
    private Integer securedClaimCount = 0;

    @Column(name = "unsecured_claim_amount", precision = 18, scale = 2)
    private BigDecimal unsecuredClaimAmount = BigDecimal.ZERO;

    @Column(name = "unsecured_claim_ratio", precision = 5, scale = 2)
    private BigDecimal unsecuredClaimRatio;

    @Column(name = "unsecured_claim_count")
    private Integer unsecuredClaimCount = 0;

    @Column(name = "approval_status", length = 20)
    private String approvalStatus = "PENDING";

    @Column(name = "approver_id")
    private Long approverId;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "approval_opinion", columnDefinition = "TEXT")
    private String approvalOpinion;

    @Column(name = "execution_status", length = 20)
    private String executionStatus = "PENDING";

    @Column(name = "related_flow_id")
    private Long relatedFlowId;

    @Column(name = "attachments", columnDefinition = "TEXT")
    private String attachments;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
}
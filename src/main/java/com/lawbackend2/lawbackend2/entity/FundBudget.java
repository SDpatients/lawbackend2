package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_fund_budget", indexes = {
    @Index(name = "uk_budget_no", columnList = "budget_no", unique = true),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_budget_type", columnList = "budget_type"),
    @Index(name = "idx_budget_status", columnList = "budget_status"),
    @Index(name = "idx_approval_status", columnList = "approval_status"),
    @Index(name = "idx_budget_start_date", columnList = "budget_start_date"),
    @Index(name = "idx_budget_end_date", columnList = "budget_end_date"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class FundBudget extends BaseEntity {

    @Column(name = "budget_no", length = 50, unique = true)
    private String budgetNo;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_name", length = 200)
    private String caseName;

    @Column(name = "budget_type", length = 50)
    private String budgetType;

    @Column(name = "budget_name", length = 200)
    private String budgetName;

    @Column(name = "budget_description", columnDefinition = "TEXT")
    private String budgetDescription;

    @Column(name = "budget_start_date")
    private LocalDateTime budgetStartDate;

    @Column(name = "budget_end_date")
    private LocalDateTime budgetEndDate;

    @Column(name = "total_budget_amount", precision = 18, scale = 2)
    private BigDecimal totalBudgetAmount;

    @Column(name = "used_amount", precision = 18, scale = 2)
    private BigDecimal usedAmount = BigDecimal.ZERO;

    @Column(name = "remaining_amount", precision = 18, scale = 2)
    private BigDecimal remainingAmount;

    @Column(name = "litigation_budget", precision = 18, scale = 2)
    private BigDecimal litigationBudget = BigDecimal.ZERO;

    @Column(name = "arbitration_budget", precision = 18, scale = 2)
    private BigDecimal arbitrationBudget = BigDecimal.ZERO;

    @Column(name = "manager_fee_budget", precision = 18, scale = 2)
    private BigDecimal managerFeeBudget = BigDecimal.ZERO;

    @Column(name = "execution_budget", precision = 18, scale = 2)
    private BigDecimal executionBudget = BigDecimal.ZERO;

    @Column(name = "evaluation_budget", precision = 18, scale = 2)
    private BigDecimal evaluationBudget = BigDecimal.ZERO;

    @Column(name = "auction_budget", precision = 18, scale = 2)
    private BigDecimal auctionBudget = BigDecimal.ZERO;

    @Column(name = "announcement_budget", precision = 18, scale = 2)
    private BigDecimal announcementBudget = BigDecimal.ZERO;

    @Column(name = "storage_budget", precision = 18, scale = 2)
    private BigDecimal storageBudget = BigDecimal.ZERO;

    @Column(name = "insurance_budget", precision = 18, scale = 2)
    private BigDecimal insuranceBudget = BigDecimal.ZERO;

    @Column(name = "meeting_budget", precision = 18, scale = 2)
    private BigDecimal meetingBudget = BigDecimal.ZERO;

    @Column(name = "other_budget", precision = 18, scale = 2)
    private BigDecimal otherBudget = BigDecimal.ZERO;

    @Column(name = "approval_status", length = 20)
    private String approvalStatus = "PENDING";

    @Column(name = "approver_id")
    private Long approverId;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "approval_opinion", columnDefinition = "TEXT")
    private String approvalOpinion;

    @Column(name = "budget_status", length = 20)
    private String budgetStatus = "ACTIVE";

    @Column(name = "related_budget_id")
    private Long relatedBudgetId;

    @Column(name = "attachments", columnDefinition = "TEXT")
    private String attachments;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
}
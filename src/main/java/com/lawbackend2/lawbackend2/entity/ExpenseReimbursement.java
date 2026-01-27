package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_expense_reimbursement", indexes = {
    @Index(name = "uk_reimbursement_number", columnList = "reimbursement_number", unique = true),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_applicant_id", columnList = "applicant_id"),
    @Index(name = "idx_fund_account_id", columnList = "fund_account_id"),
    @Index(name = "idx_approval_status", columnList = "approval_status"),
    @Index(name = "idx_reimbursement_date", columnList = "reimbursement_date"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class ExpenseReimbursement extends BaseEntity {

    @Column(name = "reimbursement_number", length = 50, nullable = false, unique = true)
    private String reimbursementNumber;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "case_name", length = 255)
    private String caseName;

    @Column(name = "applicant_id", nullable = false)
    private Long applicantId;

    @Column(name = "applicant_name", length = 100)
    private String applicantName;

    @Column(name = "fund_account_id", nullable = false)
    private Long fundAccountId;

    @Column(name = "fund_account_name", length = 100)
    private String fundAccountName;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "bank_account", length = 50)
    private String bankAccount;

    @Column(name = "total_amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "reimbursement_date", nullable = false)
    private LocalDate reimbursementDate;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "approval_status", length = 20)
    private String approvalStatus = "PENDING";

    @Column(name = "approver_id")
    private Long approverId;

    @Column(name = "approver_name", length = 100)
    private String approverName;

    @Column(name = "approval_time")
    private java.time.LocalDateTime approvalTime;

    @Column(name = "approval_opinion", length = 500)
    private String approvalOpinion;
}

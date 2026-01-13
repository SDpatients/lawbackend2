package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_fund_reimbursement", indexes = {
    @Index(name = "uk_reimbursement_no", columnList = "reimbursement_no", unique = true),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_reimbursement_type", columnList = "reimbursement_type"),
    @Index(name = "idx_applicant_id", columnList = "applicant_id"),
    @Index(name = "idx_approval_status", columnList = "approval_status"),
    @Index(name = "idx_payment_status", columnList = "payment_status"),
    @Index(name = "idx_apply_date", columnList = "apply_date"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class FundReimbursement extends BaseEntity {

    @Column(name = "reimbursement_no", length = 50, unique = true)
    private String reimbursementNo;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_name", length = 200)
    private String caseName;

    @Column(name = "reimbursement_type", length = 50)
    private String reimbursementType;

    @Column(name = "reimbursement_name", length = 200)
    private String reimbursementName;

    @Column(name = "reimbursement_description", columnDefinition = "TEXT")
    private String reimbursementDescription;

    @Column(name = "applicant_id")
    private Long applicantId;

    @Column(name = "applicant_name", length = 100)
    private String applicantName;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "applied_amount", precision = 18, scale = 2)
    private BigDecimal appliedAmount;

    @Column(name = "approved_amount", precision = 18, scale = 2)
    private BigDecimal approvedAmount;

    @Column(name = "reimbursed_amount", precision = 18, scale = 2)
    private BigDecimal reimbursedAmount = BigDecimal.ZERO;

    @Column(name = "expense_date")
    private LocalDateTime expenseDate;

    @Column(name = "expense_location", length = 200)
    private String expenseLocation;

    @Column(name = "expense_purpose", columnDefinition = "TEXT")
    private String expensePurpose;

    @Column(name = "budget_id")
    private Long budgetId;

    @Column(name = "budget_item", length = 50)
    private String budgetItem;

    @Column(name = "approval_status", length = 20)
    private String approvalStatus = "PENDING";

    @Column(name = "approver_id")
    private Long approverId;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "approval_opinion", columnDefinition = "TEXT")
    private String approvalOpinion;

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

    @Column(name = "related_expense_id")
    private Long relatedExpenseId;

    @Column(name = "related_flow_id")
    private Long relatedFlowId;

    @Column(name = "attachments", columnDefinition = "TEXT")
    private String attachments;

    @Column(name = "apply_date")
    private LocalDateTime applyDate;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
}
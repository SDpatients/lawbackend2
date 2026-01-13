package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_bankruptcy_expense", indexes = {
    @Index(name = "uk_expense_no", columnList = "expense_no", unique = true),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_expense_type", columnList = "expense_type"),
    @Index(name = "idx_approval_status", columnList = "approval_status"),
    @Index(name = "idx_payment_status", columnList = "payment_status"),
    @Index(name = "idx_approver_id", columnList = "approver_id"),
    @Index(name = "idx_payment_date", columnList = "payment_date"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class BankruptcyExpense extends BaseEntity {

    @Column(name = "expense_no", length = 50, unique = true)
    private String expenseNo;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_name", length = 200)
    private String caseName;

    @Column(name = "expense_type", length = 50)
    private String expenseType;

    @Column(name = "expense_category", length = 50)
    private String expenseCategory;

    @Column(name = "expense_name", length = 200)
    private String expenseName;

    @Column(name = "expense_description", columnDefinition = "TEXT")
    private String expenseDescription;

    @Column(name = "applied_amount", precision = 18, scale = 2)
    private BigDecimal appliedAmount;

    @Column(name = "approved_amount", precision = 18, scale = 2)
    private BigDecimal approvedAmount;

    @Column(name = "paid_amount", precision = 18, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "unpaid_amount", precision = 18, scale = 2)
    private BigDecimal unpaidAmount;

    @Column(name = "expense_basis", length = 50)
    private String expenseBasis;

    @Column(name = "basis_document", length = 200)
    private String basisDocument;

    @Column(name = "basis_description", columnDefinition = "TEXT")
    private String basisDescription;

    @Column(name = "expense_purpose", columnDefinition = "TEXT")
    private String expensePurpose;

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

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "payment_account_id")
    private Long paymentAccountId;

    @Column(name = "payee_name", length = 200)
    private String payeeName;

    @Column(name = "payee_account", length = 100)
    private String payeeAccount;

    @Column(name = "payee_bank", length = 200)
    private String payeeBank;

    @Column(name = "payment_voucher", length = 200)
    private String paymentVoucher;

    @Column(name = "related_business_type", length = 50)
    private String relatedBusinessType;

    @Column(name = "related_business_id")
    private Long relatedBusinessId;

    @Column(name = "related_flow_id")
    private Long relatedFlowId;

    @Column(name = "attachments", columnDefinition = "TEXT")
    private String attachments;

    @Column(name = "expense_date")
    private LocalDateTime expenseDate;

    @Column(name = "apply_date")
    private LocalDateTime applyDate;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
}
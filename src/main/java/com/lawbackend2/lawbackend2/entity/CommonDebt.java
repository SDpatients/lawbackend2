package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_common_debt", indexes = {
    @Index(name = "uk_debt_no", columnList = "debt_no", unique = true),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_debt_type", columnList = "debt_type"),
    @Index(name = "idx_creditor_name", columnList = "creditor_name"),
    @Index(name = "idx_approval_status", columnList = "approval_status"),
    @Index(name = "idx_repayment_status", columnList = "repayment_status"),
    @Index(name = "idx_approver_id", columnList = "approver_id"),
    @Index(name = "idx_repayment_date", columnList = "repayment_date"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class CommonDebt extends BaseEntity {

    @Column(name = "debt_no", length = 50, unique = true)
    private String debtNo;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_name", length = 200)
    private String caseName;

    @Column(name = "debt_type", length = 50)
    private String debtType;

    @Column(name = "debt_name", length = 200)
    private String debtName;

    @Column(name = "debt_description", columnDefinition = "TEXT")
    private String debtDescription;

    @Column(name = "creditor_name", length = 200)
    private String creditorName;

    @Column(name = "creditor_type", length = 50)
    private String creditorType;

    @Column(name = "creditor_contact", length = 100)
    private String creditorContact;

    @Column(name = "debt_amount", precision = 18, scale = 2)
    private BigDecimal debtAmount;

    @Column(name = "repaid_amount", precision = 18, scale = 2)
    private BigDecimal repaidAmount = BigDecimal.ZERO;

    @Column(name = "unrepaid_amount", precision = 18, scale = 2)
    private BigDecimal unrepaidAmount;

    @Column(name = "debt_basis", length = 50)
    private String debtBasis;

    @Column(name = "basis_document", length = 200)
    private String basisDocument;

    @Column(name = "basis_description", columnDefinition = "TEXT")
    private String basisDescription;

    @Column(name = "debt_start_date")
    private LocalDateTime debtStartDate;

    @Column(name = "debt_due_date")
    private LocalDateTime debtDueDate;

    @Column(name = "is_overdue")
    private Boolean isOverdue = false;

    @Column(name = "approval_status", length = 20)
    private String approvalStatus = "PENDING";

    @Column(name = "approver_id")
    private Long approverId;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "approval_opinion", columnDefinition = "TEXT")
    private String approvalOpinion;

    @Column(name = "repayment_status", length = 20)
    private String repaymentStatus = "UNREPAID";

    @Column(name = "repayment_method", length = 50)
    private String repaymentMethod;

    @Column(name = "repayment_date")
    private LocalDateTime repaymentDate;

    @Column(name = "repayment_account_id")
    private Long repaymentAccountId;

    @Column(name = "repayment_voucher", length = 200)
    private String repaymentVoucher;

    @Column(name = "related_business_type", length = 50)
    private String relatedBusinessType;

    @Column(name = "related_business_id")
    private Long relatedBusinessId;

    @Column(name = "related_flow_id")
    private Long relatedFlowId;

    @Column(name = "attachments", columnDefinition = "TEXT")
    private String attachments;

    @Column(name = "debt_date")
    private LocalDateTime debtDate;

    @Column(name = "apply_date")
    private LocalDateTime applyDate;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
}
package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_fund_flow", indexes = {
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_account_id", columnList = "account_id"),
    @Index(name = "idx_flow_type", columnList = "flow_type"),
    @Index(name = "idx_transaction_date", columnList = "transaction_date"),
    @Index(name = "idx_operator_id", columnList = "operator_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time"),
    @Index(name = "idx_fund_account_id", columnList = "fund_account_id")
})
public class FundFlow extends BaseEntity {

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_name", length = 200)
    private String caseName;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "flow_type", length = 20)
    private String flowType;

    @Column(name = "flow_category", length = 50)
    private String flowCategory;

    @Column(name = "expense_type", length = 50)
    private String expenseType;

    @Column(name = "related_business_type", length = 50)
    private String relatedBusinessType;

    @Column(name = "related_business_id")
    private Long relatedBusinessId;

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_before", precision = 18, scale = 2)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", precision = 18, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "related_document", length = 200)
    private String relatedDocument;

    @Column(name = "voucher_no", length = 100)
    private String voucherNo;

    @Column(name = "attachments", columnDefinition = "TEXT")
    private String attachments;

    @Column(name = "is_reversed")
    private Boolean isReversed = false;

    @Column(name = "reversed_flow_id")
    private Long reversedFlowId;

    @Column(name = "reversal_reason", columnDefinition = "TEXT")
    private String reversalReason;

    @Column(name = "reversal_date")
    private LocalDateTime reversalDate;

    @Column(name = "check_status", length = 20)
    private String checkStatus = "UNCHECKED";

    @Column(name = "check_date")
    private LocalDateTime checkDate;

    @Column(name = "operator_id")
    private Long operatorId;

    @Column(name = "operation_time")
    private LocalDateTime operationTime;

    @Column(name = "remark", length = 500)
    private String remark;

    @Column(name = "fund_account_id")
    private Long fundAccountId;
}

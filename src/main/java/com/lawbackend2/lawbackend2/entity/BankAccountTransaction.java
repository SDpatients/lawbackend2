package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_bank_account_transaction", indexes = {
    @Index(name = "idx_account_id", columnList = "account_id"),
    @Index(name = "idx_transaction_type", columnList = "transaction_type"),
    @Index(name = "idx_transaction_date", columnList = "transaction_date"),
    @Index(name = "idx_business_type", columnList = "business_type"),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_create_time", columnList = "create_time"),
    @Index(name = "idx_status", columnList = "status")
})
public class BankAccountTransaction extends BaseEntity {

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "transaction_type", nullable = false, length = 50)
    private String transactionType;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "summary", length = 500)
    private String summary;

    @Column(name = "business_type", length = 50)
    private String businessType;

    @Column(name = "counterparty_account", length = 100)
    private String counterpartyAccount;

    @Column(name = "counterparty_name", length = 100)
    private String counterpartyName;

    @Column(name = "balance_after", precision = 18, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "attachment_id")
    private Long attachmentId;

    @Column(name = "related_business_id")
    private Long relatedBusinessId;

    @Column(name = "remark", length = 500)
    private String remark;

    @Column(name = "case_id")
    private Long caseId;
}

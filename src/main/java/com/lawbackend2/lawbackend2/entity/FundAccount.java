package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_fund_account", indexes = {
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class FundAccount extends BaseEntity {

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_name", length = 200)
    private String caseName;

    @Column(name = "account_name", length = 100)
    private String accountName;

    @Column(name = "account_type", length = 50)
    private String accountType;

    @Column(name = "account_purpose", length = 50)
    private String accountPurpose;

    @Column(name = "account_permission", length = 50)
    private String accountPermission;

    @Column(name = "is_frozen")
    private Boolean isFrozen = false;

    @Column(name = "freeze_date")
    private LocalDateTime freezeDate;

    @Column(name = "freeze_reason", columnDefinition = "TEXT")
    private String freezeReason;

    @Column(name = "unfreeze_date")
    private LocalDateTime unfreezeDate;

    @Column(name = "initial_balance", precision = 18, scale = 2)
    private BigDecimal initialBalance = BigDecimal.ZERO;

    @Column(name = "current_balance", precision = 18, scale = 2)
    private BigDecimal currentBalance = BigDecimal.ZERO;

    @Column(name = "account_balance_limit", precision = 18, scale = 2)
    private BigDecimal accountBalanceLimit;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Convert(converter = com.lawbackend2.lawbackend2.util.EncryptedStringConverter.class)
    @Column(name = "bank_account", length = 255)
    private String bankAccount;

    @Column(name = "opening_date")
    private LocalDateTime openingDate;

    @Column(name = "closing_date")
    private LocalDateTime closingDate;
}

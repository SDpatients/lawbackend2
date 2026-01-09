package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 资金账户实体类
 * 用于管理案件相关的资金账户信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_fund_account", indexes = {
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class FundAccount extends BaseEntity {

    /** 案件ID */
    @Column(name = "case_id")
    private Long caseId;

    /** 案件名称 */
    @Column(name = "case_name", length = 200)
    private String caseName;

    /** 账户名称 */
    @Column(name = "account_name", length = 100)
    private String accountName;

    /** 账户类型 */
    @Column(name = "account_type", length = 50)
    private String accountType;

    /** 初始余额，默认为0 */
    @Column(name = "initial_balance", precision = 18, scale = 2)
    private BigDecimal initialBalance = BigDecimal.ZERO;

    /** 当前余额，默认为0 */
    @Column(name = "current_balance", precision = 18, scale = 2)
    private BigDecimal currentBalance = BigDecimal.ZERO;

    /** 银行名称 */
    @Column(name = "bank_name", length = 100)
    private String bankName;

    /** 银行账号 */
    @Column(name = "bank_account", length = 50)
    private String bankAccount;
}

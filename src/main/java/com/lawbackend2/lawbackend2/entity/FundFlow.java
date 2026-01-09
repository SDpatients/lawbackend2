package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资金流水实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_fund_flow", indexes = {
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_fund_account_id", columnList = "fund_account_id"),
    @Index(name = "idx_flow_type", columnList = "flow_type"),
    @Index(name = "idx_transaction_date", columnList = "transaction_date"),
    @Index(name = "idx_operator_id", columnList = "operator_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class FundFlow extends BaseEntity {

    /** 案件ID */
    @Column(name = "case_id")
    private Long caseId;

    /** 案件名称 */
    @Column(name = "case_name", length = 200)
    private String caseName;

    /** 资金账户ID */
    @Column(name = "fund_account_id")
    private Long fundAccountId;

    /** 流水类型 */
    @Column(name = "flow_type", length = 20)
    private String flowType;

    /** 金额 */
    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    /** 交易前余额 */
    @Column(name = "balance_before", precision = 18, scale = 2)
    private BigDecimal balanceBefore;

    /** 交易后余额 */
    @Column(name = "balance_after", precision = 18, scale = 2)
    private BigDecimal balanceAfter;

    /** 交易日期 */
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    /** 描述 */
    @Column(name = "description", length = 500)
    private String description;

    /** 关联文档 */
    @Column(name = "related_document", length = 200)
    private String relatedDocument;

    /** 操作人ID */
    @Column(name = "operator_id")
    private Long operatorId;

    /** 操作时间 */
    @Column(name = "operation_time")
    private LocalDateTime operationTime;

    /** 备注 */
    @Column(name = "remark", length = 500)
    private String remark;
}

package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 资金审批实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_fund_approval", indexes = {
    @Index(name = "idx_flow_id", columnList = "flow_id"),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_approval_status", columnList = "approval_status"),
    @Index(name = "idx_approver_id", columnList = "approver_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class FundApproval extends BaseEntity {

    /** 流程ID */
    @Column(name = "flow_id")
    private Long flowId;

    /** 案件ID */
    @Column(name = "case_id")
    private Long caseId;

    /** 金额 */
    @Column(name = "amount", precision = 18, scale = 2)
    private java.math.BigDecimal amount;

    /** 审批状态 */
    @Column(name = "approval_status", length = 20)
    private String approvalStatus;

    /** 审批内容 */
    @Column(name = "approval_content", columnDefinition = "TEXT")
    private String approvalContent;

    /** 审批人ID */
    @Column(name = "approver_id")
    private Long approverId;

    /** 审批时间 */
    @Column(name = "approval_time")
    private LocalDateTime approvalTime;

    /** 审批意见 */
    @Column(name = "approval_opinion", length = 500)
    private String approvalOpinion;
}

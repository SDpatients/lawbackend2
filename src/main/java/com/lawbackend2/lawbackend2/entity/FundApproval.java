package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(name = "flow_id")
    private Long flowId;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "approval_status", length = 20)
    private String approvalStatus = "PENDING";

    @Column(name = "approval_level")
    private Integer approvalLevel = 1;

    @Column(name = "approval_stage", length = 50)
    private String approvalStage;

    @Column(name = "approval_content", length = 500)
    private String approvalContent;

    @Column(name = "approver_id")
    private Long approverId;

    @Column(name = "approval_time")
    private LocalDateTime approvalTime;

    @Column(name = "approval_opinion", length = 500)
    private String approvalOpinion;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "attachments", columnDefinition = "TEXT")
    private String attachments;

    @Column(name = "is_timeout")
    private Boolean isTimeout = false;

    @Column(name = "timeout_date")
    private LocalDateTime timeoutDate;

    @Column(name = "related_business_type", length = 50)
    private String relatedBusinessType;

    @Column(name = "related_business_id")
    private Long relatedBusinessId;
}

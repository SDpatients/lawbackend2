package com.lawbackend2.lawbackend2.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tb_approval_history", indexes = {
        @Index(name = "idx_approval_id", columnList = "approval_id"),
        @Index(name = "idx_case_id", columnList = "case_id"),
        @Index(name = "idx_approver_id", columnList = "approver_id"),
        @Index(name = "idx_approval_date", columnList = "approval_date")
})
public class ApprovalHistory extends BaseEntity {
    @Column(name = "approval_id", nullable = false)
    private Long approvalId;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "approver_id", nullable = false)
    private Long approverId;

    @Column(name = "approval_type", nullable = false, length = 50)
    private String approvalType;

    @Column(name = "approval_title", length = 255)
    private String approvalTitle;

    @Column(name = "approval_attachment", length = 4000)
    private String approvalAttachment;

    @Column(name = "approval_status", nullable = false, length = 20)
    private String approvalStatus;

    @Column(name = "approval_opinion", columnDefinition = "TEXT")
    private String approvalOpinion;

    @Column(name = "approval_date", nullable = false)
    private java.time.LocalDateTime approvalDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_id", insertable = false, updatable = false)
    private Approval approval;
}
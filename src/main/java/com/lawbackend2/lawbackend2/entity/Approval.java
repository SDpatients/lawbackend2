package com.lawbackend2.lawbackend2.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tb_approval", indexes = {
        @Index(name = "idx_case_type", columnList = "case_id, approval_type"),
        @Index(name = "idx_lawyer_id", columnList = "lawyer_id"),
        @Index(name = "idx_approval_status", columnList = "approval_status"),
        @Index(name = "idx_approver_id", columnList = "approver_id"),
        @Index(name = "idx_approval_date", columnList = "approval_date")
})
public class Approval extends BaseEntity {
    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "lawyer_id", nullable = false)
    private Long lawyerId;

    @Column(name = "approval_type", nullable = false, length = 50)
    private String approvalType;

    @Column(name = "approval_status", length = 20)
    private String approvalStatus = "PENDING";

    @Column(name = "approval_title", length = 255)
    private String approvalTitle;

    @Column(name = "approval_content", columnDefinition = "TEXT")
    private String approvalContent;

    @Column(name = "approval_attachment", length = 4000)
    private String approvalAttachment;

    @Column(name = "approval_result", length = 20)
    private String approvalResult;

    @Column(name = "approval_count")
    private Integer approvalCount = 0;

    @Column(name = "approver_id")
    private Long approverId;

    @Column(name = "approval_date")
    private java.time.LocalDateTime approvalDate;

    @Column(name = "remark", length = 500)
    private String remark;
}
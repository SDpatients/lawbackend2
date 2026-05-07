package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_case_node_extension", indexes = {
    @Index(name = "idx_node_instance_id", columnList = "node_instance_id"),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_approval_status", columnList = "approval_status"),
    @Index(name = "idx_approver_id", columnList = "approver_id"),
    @Index(name = "idx_apply_user_id", columnList = "apply_user_id"),
    @Index(name = "idx_apply_time", columnList = "apply_time"),
    @Index(name = "idx_case_id_approval_status", columnList = "case_id, approval_status")
})
public class CaseNodeExtension extends BaseEntity {

    @Column(name = "node_instance_id", nullable = false)
    private Long nodeInstanceId;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "extension_days", nullable = false)
    private Integer extensionDays;

    @Column(name = "original_deadline", nullable = false)
    private LocalDate originalDeadline;

    @Column(name = "new_deadline", nullable = false)
    private LocalDate newDeadline;

    @Column(name = "apply_reason", columnDefinition = "TEXT", nullable = false)
    private String applyReason;

    @Column(name = "attachment_path", length = 1000)
    private String attachmentPath;

    @Column(name = "approval_status", length = 20, nullable = false)
    private String approvalStatus = "PENDING";

    @Column(name = "approver_id")
    private Long approverId;

    @Column(name = "approver_name", length = 100)
    private String approverName;

    @Column(name = "approval_time")
    private LocalDateTime approvalTime;

    @Column(name = "approval_opinion", columnDefinition = "TEXT")
    private String approvalOpinion;

    @Column(name = "apply_user_id", nullable = false)
    private Long applyUserId;

    @Column(name = "apply_user_name", length = 100, nullable = false)
    private String applyUserName;

    @Column(name = "apply_time")
    private LocalDateTime applyTime;
}

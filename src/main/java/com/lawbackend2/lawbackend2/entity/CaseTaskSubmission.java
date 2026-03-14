package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_case_task_submission", indexes = {
    @Index(name = "idx_case_task_id", columnList = "case_task_id"),
    @Index(name = "idx_create_time", columnList = "create_time"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_user_id", columnList = "create_user_id"),
    @Index(name = "idx_case_task_id_submission_number", columnList = "case_task_id,submission_number")
})
public class CaseTaskSubmission extends BaseEntity {

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(name = "case_task_id", nullable = false)
    private Long caseTaskId;

    @Column(name = "submission_title", length = 500, nullable = false)
    private String submissionTitle;

    @Column(name = "submission_content", columnDefinition = "TEXT")
    private String submissionContent;

    @Column(name = "submission_type", length = 50)
    private String submissionType = "NORMAL";

    @Column(name = "submission_number")
    private Integer submissionNumber = 1;

    @Column(name = "status", length = 20)
    private String status = "APPROVED";

    @Column(name = "reviewer_id")
    private Long reviewerId;

    @Column(name = "review_opinion", columnDefinition = "TEXT")
    private String reviewOpinion;

    @Column(name = "review_time")
    private LocalDateTime reviewTime;
}

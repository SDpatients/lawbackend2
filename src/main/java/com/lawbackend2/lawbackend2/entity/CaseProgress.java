package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_case_progress")
public class CaseProgress extends BaseEntity {
    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_name", length = 255)
    private String caseName;

    @Column(name = "case_number", length = 100)
    private String caseNumber;

    @Column(name = "progress_stage", length = 50)
    private String progressStage;

    @Column(name = "stage_name", length = 255)
    private String stageName;

    @Column(name = "stage_description", columnDefinition = "TEXT")
    private String stageDescription;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "expected_end_date")
    private LocalDate expectedEndDate;

    @Column(name = "progress_status", length = 50)
    private String progressStatus;

    @Column(name = "completion_percentage", columnDefinition = "INT DEFAULT 0")
    private Integer completionPercentage;

    @Column(name = "key_tasks", columnDefinition = "TEXT")
    private String keyTasks;

    @Column(name = "completed_tasks", columnDefinition = "TEXT")
    private String completedTasks;

    @Column(name = "pending_tasks", columnDefinition = "TEXT")
    private String pendingTasks;

    @Column(name = "issues", columnDefinition = "TEXT")
    private String issues;

    @Column(name = "solutions", columnDefinition = "TEXT")
    private String solutions;

    @Column(name = "attachments", columnDefinition = "TEXT")
    private String attachments;

    @Column(name = "responsible_person", length = 100)
    private String responsiblePerson;

    @Column(name = "responsible_person_id")
    private Long responsiblePersonId;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "completed_by")
    private Long completedBy;

    @Column(name = "completed_by_name", length = 100)
    private String completedByName;
}

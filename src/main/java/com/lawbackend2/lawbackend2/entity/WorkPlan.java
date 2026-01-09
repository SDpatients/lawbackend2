package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_work_plan", indexes = {
    @Index(name = "idx_plan_number", columnList = "plan_number"),
    @Index(name = "idx_plan_type", columnList = "plan_type"),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_responsible_user_id", columnList = "responsible_user_id"),
    @Index(name = "idx_execution_status", columnList = "execution_status"),
    @Index(name = "idx_start_date", columnList = "start_date"),
    @Index(name = "idx_end_date", columnList = "end_date"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class WorkPlan extends BaseEntity {

    @Column(name = "plan_number", length = 50, nullable = false, unique = true)
    private String planNumber;

    @Column(name = "plan_type", length = 20, nullable = false)
    private String planType;

    @Column(name = "plan_content", columnDefinition = "TEXT")
    private String planContent;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "responsible_user_id")
    private Long responsibleUserId;

    @Column(name = "execution_status", length = 20)
    private String executionStatus;

    @Column(name = "case_id")
    private Long caseId;
}

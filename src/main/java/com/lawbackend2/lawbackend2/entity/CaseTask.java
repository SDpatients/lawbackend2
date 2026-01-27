package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_case_task", indexes = {
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_task_code", columnList = "task_code"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class CaseTask extends BaseEntity {

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "task_code", length = 20, nullable = false)
    private String taskCode;

    @Column(name = "task_name", length = 255, nullable = false)
    private String taskName;

    @Column(name = "task_description", columnDefinition = "TEXT")
    private String taskDescription;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "IN_PROGRESS";

    @Column(name = "sort_order")
    private Integer sortOrder;
}

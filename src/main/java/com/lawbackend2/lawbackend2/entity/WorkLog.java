package com.lawbackend2.lawbackend2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_work_log", indexes = {
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_work_date", columnList = "work_date"),
    @Index(name = "idx_work_type", columnList = "work_type"),
    @Index(name = "idx_create_user_id", columnList = "create_user_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class WorkLog extends BaseEntity {

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "work_type", length = 50, nullable = false)
    private String workType;

    @Column(name = "work_content", columnDefinition = "TEXT", nullable = false)
    private String workContent;

    @Column(name = "work_result", length = 500)
    private String workResult;

    @Column(name = "attachment_ids", length = 1000)
    private String attachmentIds;

    @Column(name = "remark", length = 500)
    private String remark;

    @Transient
    private String creatorName;
}

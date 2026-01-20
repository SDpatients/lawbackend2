package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_case_process_stage")
public class CaseProcessStage extends BaseEntity {

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "stage_num", nullable = false)
    private Integer stageNum;

    @Column(name = "stage_name", nullable = false, length = 50)
    private String stageName;

    @Column(name = "module_code", nullable = false, length = 50)
    private String moduleCode;

    @Column(name = "module_name", nullable = false, length = 50)
    private String moduleName;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "process_date")
    private LocalDateTime processDate;

    @Column(name = "attachments", columnDefinition = "JSON")
    private String attachments;

    @Column(name = "field_data", nullable = false, columnDefinition = "JSON")
    private String fieldData;
}

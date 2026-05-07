package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_case_node_template", indexes = {
    @Index(name = "idx_case_type", columnList = "case_type"),
    @Index(name = "idx_case_stage", columnList = "case_stage"),
    @Index(name = "idx_calculation_base", columnList = "calculation_base"),
    @Index(name = "idx_prev_node_code", columnList = "prev_node_code"),
    @Index(name = "idx_sort_order", columnList = "sort_order"),
    @Index(name = "idx_status", columnList = "status")
})
public class CaseNodeTemplate extends BaseEntity {

    @Column(name = "node_code", length = 50, nullable = false)
    private String nodeCode;

    @Column(name = "node_name", length = 255, nullable = false)
    private String nodeName;

    @Column(name = "node_description", columnDefinition = "TEXT")
    private String nodeDescription;

    @Column(name = "case_type", length = 20, nullable = false)
    private String caseType;

    @Column(name = "case_stage", length = 50, nullable = false)
    private String caseStage;

    @Column(name = "legal_deadline_days", nullable = false)
    private Integer legalDeadlineDays;

    @Column(name = "legal_deadline_days_max")
    private Integer legalDeadlineDaysMax;

    @Column(name = "calculation_base", length = 50, nullable = false)
    private String calculationBase;

    @Column(name = "prev_node_code", length = 50)
    private String prevNodeCode;

    @Column(name = "responsible_role", length = 50)
    private String responsibleRole;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory = true;
}

package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_case_node_instance", indexes = {
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_template_id", columnList = "template_id"),
    @Index(name = "idx_node_code", columnList = "node_code"),
    @Index(name = "idx_node_status", columnList = "node_status"),
    @Index(name = "idx_deadline_date", columnList = "deadline_date"),
    @Index(name = "idx_alert_level", columnList = "alert_level"),
    @Index(name = "idx_responsible_person_id", columnList = "responsible_person_id"),
    @Index(name = "idx_prev_node_instance_id", columnList = "prev_node_instance_id"),
    @Index(name = "idx_sort_order", columnList = "sort_order"),
    @Index(name = "idx_case_id_node_status", columnList = "case_id, node_status"),
    @Index(name = "idx_case_id_alert_level", columnList = "case_id, alert_level")
})
public class CaseNodeInstance extends BaseEntity {

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(name = "node_code", length = 50, nullable = false)
    private String nodeCode;

    @Column(name = "node_name", length = 255, nullable = false)
    private String nodeName;

    @Column(name = "node_status", length = 20, nullable = false)
    private String nodeStatus = "PENDING";

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "deadline_date", nullable = false)
    private LocalDate deadlineDate;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    @Column(name = "extension_count", nullable = false)
    private Integer extensionCount = 0;

    @Column(name = "extension_days", nullable = false)
    private Integer extensionDays = 0;

    @Column(name = "responsible_person_id")
    private Long responsiblePersonId;

    @Column(name = "responsible_person_name", length = 100)
    private String responsiblePersonName;

    @Column(name = "completion_remark", columnDefinition = "TEXT")
    private String completionRemark;

    @Column(name = "alert_level", length = 20)
    private String alertLevel;

    @Column(name = "alert_triggered_at")
    private LocalDateTime alertTriggeredAt;

    @Column(name = "prev_node_instance_id")
    private Long prevNodeInstanceId;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}

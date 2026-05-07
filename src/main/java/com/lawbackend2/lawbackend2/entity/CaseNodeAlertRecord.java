package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_case_node_alert_record", indexes = {
    @Index(name = "idx_node_instance_id", columnList = "node_instance_id"),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_alert_level", columnList = "alert_level"),
    @Index(name = "idx_alert_date", columnList = "alert_date"),
    @Index(name = "idx_is_notified", columnList = "is_notified"),
    @Index(name = "idx_recipient_id", columnList = "recipient_id"),
    @Index(name = "idx_case_id_alert_date", columnList = "case_id, alert_date")
})
public class CaseNodeAlertRecord extends BaseEntity {

    @Column(name = "node_instance_id", nullable = false)
    private Long nodeInstanceId;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "alert_level", length = 20, nullable = false)
    private String alertLevel;

    @Column(name = "remaining_days", nullable = false)
    private Integer remainingDays;

    @Column(name = "deadline_date", nullable = false)
    private LocalDate deadlineDate;

    @Column(name = "alert_date", nullable = false)
    private LocalDate alertDate;

    @Column(name = "is_notified", nullable = false)
    private Boolean isNotified = false;

    @Column(name = "notification_type", length = 50)
    private String notificationType;

    @Column(name = "notification_time")
    private LocalDateTime notificationTime;

    @Column(name = "notification_content", columnDefinition = "TEXT")
    private String notificationContent;

    @Column(name = "recipient_id")
    private Long recipientId;

    @Column(name = "recipient_name", length = 100)
    private String recipientName;
}

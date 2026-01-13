package com.lawbackend2.lawbackend2.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_message_recall_config", indexes = {
    @Index(name = "idx_config_type", columnList = "config_type"),
    @Index(name = "idx_target_id", columnList = "target_id"),
    @Index(name = "idx_status", columnList = "status")
})
public class MessageRecallConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "config_type", nullable = false, length = 50)
    private String configType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "recall_time_limit")
    private Integer recallTimeLimit = 120;

    @Column(name = "allow_recall")
    private Boolean allowRecall = true;

    @Column(name = "max_recall_times")
    private Integer maxRecallTimes = 10;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE";

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "remark", length = 500)
    private String remark;
}

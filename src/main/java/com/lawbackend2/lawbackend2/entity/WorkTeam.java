package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_work_team", indexes = {
    @Index(name = "idx_team_leader_id", columnList = "team_leader_id"),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class WorkTeam extends BaseEntity {

    @Column(name = "team_name", length = 100, nullable = false)
    private String teamName;

    @Column(name = "team_leader_id")
    private Long teamLeaderId;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "team_description", columnDefinition = "TEXT")
    private String teamDescription;
}

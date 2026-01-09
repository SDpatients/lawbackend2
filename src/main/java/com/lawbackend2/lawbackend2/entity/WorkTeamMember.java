package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_work_team_member", indexes = {
    @Index(name = "idx_team_id", columnList = "team_id"),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_team_role", columnList = "team_role"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class WorkTeamMember extends BaseEntity {

    @Column(name = "team_id")
    private Long teamId;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "team_role", length = 50)
    private String teamRole;

    @Column(name = "permission_level", length = 20)
    private String permissionLevel;

    @Column(name = "is_active")
    private Integer isActive = 1;
}

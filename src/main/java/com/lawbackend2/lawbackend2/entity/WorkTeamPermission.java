package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_work_team_permission", indexes = {
    @Index(name = "idx_team_member_id", columnList = "team_member_id"),
    @Index(name = "idx_module_type", columnList = "module_type"),
    @Index(name = "idx_permission_type", columnList = "permission_type"),
    @Index(name = "idx_status", columnList = "status")
})
public class WorkTeamPermission extends BaseEntity {

    @Column(name = "team_member_id")
    private Long teamMemberId;

    @Column(name = "module_type", length = 50)
    private String moduleType;

    @Column(name = "permission_type", length = 50)
    private String permissionType;

    @Column(name = "is_allowed")
    private Integer isAllowed = 1;
}

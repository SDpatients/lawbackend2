package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_login_fail", indexes = {
    @Index(name = "idx_login_account", columnList = "login_account"),
    @Index(name = "idx_fail_ip", columnList = "fail_ip"),
    @Index(name = "idx_last_fail_time", columnList = "last_fail_time")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginFail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_account", nullable = false, length = 100)
    private String loginAccount;

    @Column(name = "fail_ip", nullable = false, length = 50)
    private String failIp;

    @Column(name = "fail_count")
    @Builder.Default
    private Integer failCount = 1;

    @Column(name = "fail_type", nullable = false, length = 20)
    private String failType;

    @Column(name = "last_fail_time")
    @Builder.Default
    private LocalDateTime lastFailTime = LocalDateTime.now();

    @Column(name = "unlock_time")
    private LocalDateTime unlockTime;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "create_time", updatable = false)
    @Builder.Default
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(name = "update_time")
    @Builder.Default
    private LocalDateTime updateTime = LocalDateTime.now();

    @Column(name = "create_user_id")
    private Long createUserId;

    @Column(name = "update_user_id")
    private Long updateUserId;

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}

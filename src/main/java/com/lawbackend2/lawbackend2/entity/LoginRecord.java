package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_login_record", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_user_account", columnList = "user_account"),
    @Index(name = "idx_login_time", columnList = "login_time"),
    @Index(name = "idx_login_status", columnList = "login_status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_account", length = 50)
    private String userAccount;

    @Column(name = "user_name", length = 100)
    private String userName;

    @Column(name = "login_type", length = 10)
    private String loginType;

    @Column(name = "login_ip", length = 50)
    private String loginIp;

    @Column(name = "login_location", length = 200)
    private String loginLocation;

    @Column(name = "login_device", length = 500)
    private String loginDevice;

    @Column(name = "login_browser", length = 100)
    private String loginBrowser;

    @Column(name = "login_os", length = 50)
    private String loginOs;

    @Column(name = "login_status", length = 20)
    private String loginStatus;

    @Column(name = "error_msg", length = 500)
    private String errorMsg;

    @Column(name = "risk_level", length = 20)
    private String riskLevel;

    @Column(name = "is_known_device", length = 20)
    private String isKnownDevice;

    @Column(name = "login_time")
    private LocalDateTime loginTime;

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

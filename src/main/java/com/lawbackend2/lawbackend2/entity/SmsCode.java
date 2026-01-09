package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_sms_code", indexes = {
    @Index(name = "idx_mobile", columnList = "mobile"),
    @Index(name = "idx_expire_time", columnList = "expire_time"),
    @Index(name = "idx_used_status", columnList = "used_status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mobile", nullable = false, length = 20)
    private String mobile;

    @Column(name = "code", nullable = false, length = 10)
    private String code;

    @Column(name = "sms_type", nullable = false, length = 1)
    private String smsType;

    @Column(name = "expire_time", nullable = false)
    private LocalDateTime expireTime;

    @Column(name = "used_time")
    private LocalDateTime usedTime;

    @Column(name = "used_status", length = 1)
    @Builder.Default
    private Character usedStatus = '0';

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

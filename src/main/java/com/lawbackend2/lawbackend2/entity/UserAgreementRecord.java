package com.lawbackend2.lawbackend2.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_user_agreement_record", indexes = {
    @Index(name = "idx_uagr_user_id", columnList = "user_id"),
    @Index(name = "idx_uagr_agreement_type", columnList = "agreement_type"),
    @Index(name = "idx_uagr_agree_time", columnList = "agree_time"),
    @Index(name = "idx_uagr_create_time", columnList = "create_time")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAgreementRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_account", length = 100)
    private String userAccount;

    @Column(name = "agreement_type", length = 50)
    private String agreementType;

    @Column(name = "agreement_version", length = 50)
    private String agreementVersion;

    @Column(name = "agreed", nullable = false)
    @Builder.Default
    private Boolean agreed = true;

    @Column(name = "agreement_content", columnDefinition = "TEXT")
    private String agreementContent;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "agree_time")
    @Builder.Default
    private LocalDateTime agreeTime = LocalDateTime.now();

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

    public static final String TYPE_PRIVACY_POLICY = "PRIVACY_POLICY";
    public static final String TYPE_USER_AGREEMENT = "USER_AGREEMENT";
}
package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_temp_upload_token", indexes = {
    @Index(name = "idx_token", columnList = "token"),
    @Index(name = "idx_biz_type", columnList = "biz_type"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_expire_time", columnList = "expire_time"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class TempUploadToken extends BaseEntity {

    @Column(name = "token", length = 64, nullable = false, unique = true)
    private String token;

    @Column(name = "biz_type", length = 50, nullable = false)
    private String bizType;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "expire_time", nullable = false)
    private LocalDateTime expireTime;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "ACTIVE";

    @Column(name = "file_count", nullable = false)
    private Integer fileCount = 0;

    @Column(name = "description", length = 500)
    private String description;
}

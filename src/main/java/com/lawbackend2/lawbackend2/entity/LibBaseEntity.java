package com.lawbackend2.lawbackend2.entity;

import javax.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class LibBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE";

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "create_user_id")
    private Long createUserId;

    @Column(name = "create_user_name", length = 100)
    private String createUserName;

    @Column(name = "update_user_id")
    private Long updateUserId;

    @CreatedDate
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @LastModifiedDate
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}

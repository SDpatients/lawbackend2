package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_ai_chat_session")
@EntityListeners(AuditingEntityListener.class)
public class AiChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "session_name")
    private String sessionName;

    @Column(name = "last_message_time")
    private LocalDateTime lastMessageTime;

    @Column(name = "message_count")
    private Integer messageCount = 0;

    @CreatedDate
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @LastModifiedDate
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "create_user_id")
    private Long createUserId;

    @Column(name = "update_user_id")
    private Long updateUserId;

    @Column(name = "is_deleted")
    private Integer isDeleted = 0;

    @Column(name = "status")
    private String status = "ACTIVE";
}

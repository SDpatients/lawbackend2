package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_ai_chat_message")
@EntityListeners(AuditingEntityListener.class)
public class AiChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "sender_type", nullable = false)
    private String senderType; // user-用户, ai-人工智能

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "message_status")
    private String messageStatus = "SENT";

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

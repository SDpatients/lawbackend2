package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_conversation", indexes = {
    @Index(name = "idx_user_id1", columnList = "user_id1"),
    @Index(name = "idx_user_id2", columnList = "user_id2"),
    @Index(name = "idx_last_message_time", columnList = "last_message_time"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time"),
    @Index(name = "uk_conversation", columnList = "user_id1,user_id2", unique = true)
})
public class Conversation extends BaseEntity {

    @Column(name = "user_id1", nullable = false)
    private Long userId1;

    @Column(name = "user_id2", nullable = false)
    private Long userId2;

    @Column(name = "last_message_id")
    private Long lastMessageId;

    @Column(name = "last_message_content", columnDefinition = "TEXT")
    private String lastMessageContent;

    @Column(name = "last_message_type", length = 20)
    private String lastMessageType;

    @Column(name = "last_message_time")
    private LocalDateTime lastMessageTime;

    @Column(name = "user1_unread_count")
    private Integer user1UnreadCount = 0;

    @Column(name = "user2_unread_count")
    private Integer user2UnreadCount = 0;

    @Column(name = "user1_deleted")
    private Boolean user1Deleted = false;

    @Column(name = "user2_deleted")
    private Boolean user2Deleted = false;

    @Column(name = "user1_pinned")
    private Boolean user1Pinned = false;

    @Column(name = "user2_pinned")
    private Boolean user2Pinned = false;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE";
}

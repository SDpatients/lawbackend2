package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_chat_message", indexes = {
    @Index(name = "idx_conversation_id", columnList = "conversation_id"),
    @Index(name = "idx_sender_id", columnList = "sender_id"),
    @Index(name = "idx_receiver_id", columnList = "receiver_id"),
    @Index(name = "idx_create_time", columnList = "create_time"),
    @Index(name = "idx_message_status", columnList = "message_status"),
    @Index(name = "idx_is_deleted", columnList = "is_deleted"),
    @Index(name = "idx_is_recalled", columnList = "is_recalled")
})
public class ChatMessage extends BaseEntity {

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(name = "message_type", nullable = false, length = 20)
    private String messageType;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "message_status", length = 20)
    private String messageStatus = "SENT";

    @Column(name = "read_time")
    private LocalDateTime readTime;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "deleted_by")
    private Long deletedBy;

    @Column(name = "deleted_time")
    private LocalDateTime deletedTime;

    @Column(name = "is_recalled")
    private Boolean isRecalled = false;

    @Column(name = "recall_time")
    private LocalDateTime recallTime;
}

package com.lawbackend2.lawbackend2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    private Long id;

    private Long conversationId;

    private Long senderId;

    private String senderName;

    private Long receiverId;

    private String receiverName;

    private String messageType;

    private String content;

    private Long fileId;

    private String fileName;

    private Long fileSize;

    private String fileUrl;

    private String messageStatus;

    private LocalDateTime readTime;

    private Boolean isDeleted;

    private Boolean isRecalled;

    private LocalDateTime createTime;
}

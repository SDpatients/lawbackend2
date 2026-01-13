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
public class ConversationResponse {

    private Long id;

    private Long userId1;

    private String userId1Name;

    private Long userId2;

    private String userId2Name;

    private Long lastMessageId;

    private String lastMessageContent;

    private String lastMessageType;

    private LocalDateTime lastMessageTime;

    private Integer user1UnreadCount;

    private Integer user2UnreadCount;

    private Boolean user1Deleted;

    private Boolean user2Deleted;

    private Boolean user1Pinned;

    private Boolean user2Pinned;

    private String status;

    private LocalDateTime createTime;
}

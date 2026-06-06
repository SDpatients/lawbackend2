package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiChatSessionResponse {

    private Long id;

    private Long caseId;

    private Long userId;

    private String sessionName;

    private LocalDateTime lastMessageTime;

    private Integer messageCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
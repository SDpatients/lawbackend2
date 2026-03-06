package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiChatMessageResponse {

    private Long id;

    private Long sessionId;

    private Long caseId;

    private String content;

    private String sender;

    private LocalDateTime timestamp;

    private String messageStatus;
}

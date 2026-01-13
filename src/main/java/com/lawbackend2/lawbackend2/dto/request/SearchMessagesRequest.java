package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class SearchMessagesRequest {

    @NotNull(message = "会话ID不能为空")
    private Long conversationId;

    private String keyword;

    private Long senderId;

    private String messageType;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Boolean includeDeleted = false;

    private Boolean includeRecalled = false;

    private Integer pageNum = 1;

    private Integer pageSize = 20;
}

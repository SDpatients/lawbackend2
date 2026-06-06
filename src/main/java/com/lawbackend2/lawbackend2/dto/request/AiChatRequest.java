package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AiChatRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "消息内容不能为空")
    private String content;

    private Long userId;

    private Long sessionId;
}

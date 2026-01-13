package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RecallMessageRequest {

    @NotNull(message = "消息ID不能为空")
    private Long messageId;
}

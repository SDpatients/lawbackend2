package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SendMessageRequest {

    @NotNull(message = "接收者ID不能为空")
    private Long receiverId;

    @NotNull(message = "消息类型不能为空")
    private String messageType;

    private String content;

    private Long fileId;

    private String fileName;

    private Long fileSize;

    private String fileUrl;
}

package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ForwardMessagesRequest {

    @NotNull(message = "目标会话ID不能为空")
    private Long targetConversationId;

    @NotNull(message = "目标接收者ID不能为空")
    private Long targetReceiverId;

    @NotNull(message = "转发消息ID列表不能为空")
    private List<Long> messageIds;

    private String forwardComment;
}

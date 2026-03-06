package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.AiChatRequest;
import com.lawbackend2.lawbackend2.dto.response.AiChatMessageResponse;
import com.lawbackend2.lawbackend2.service.AiChatService;
import com.lawbackend2.lawbackend2.service.impl.AiChatServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Tag(name = "AI聊天管理", description = "AI聊天功能相关接口")
@RestController
@RequestMapping("/ai/chat")
@Validated
public class AiChatController {

    private final AiChatService aiChatService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    @Autowired
    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @Operation(summary = "发送消息（流式）", description = "发送消息到AI并以流式获取回复，超时时间30秒")
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendMessageStream(
            @Parameter(description = "聊天请求") @RequestBody @Validated AiChatRequest request) {
        // 创建SseEmitter，设置超时时间为30秒
        SseEmitter emitter = new SseEmitter(30000L);

        // 在单独的线程中处理AI回复
        scheduler.execute(() -> {
            try {
                // 使用Service的流式方法
                ((AiChatServiceImpl) aiChatService).sendMessageStream(request, partialResponse -> {
                    try {
                        // 发送部分响应
                        emitter.send(SseEmitter.event()
                                .name("chunk")
                                .data(partialResponse));
                    } catch (IOException e) {
                        log.error("发送流式数据异常", e);
                        emitter.completeWithError(e);
                    }
                });
                
                // 发送完成事件
                emitter.send(SseEmitter.event()
                        .name("complete")
                        .data("AI回复完成"));
                
                // 完成流式响应
                emitter.complete();
            } catch (Exception e) {
                log.error("流式响应异常", e);
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    @Operation(summary = "发送消息", description = "发送消息到AI并获取回复（默认流式）")
    @PostMapping
    public SseEmitter sendMessage(
            @Parameter(description = "聊天请求") @RequestBody @Validated AiChatRequest request) {
        // 默认使用流式响应
        return sendMessageStream(request);
    }

    @Operation(summary = "获取聊天历史", description = "获取指定案件的聊天历史记录")
    @GetMapping("/history")
    public Result<List<AiChatMessageResponse>> getChatHistory(
            @Parameter(description = "案件ID") @RequestParam @NotNull(message = "案件ID不能为空") Long caseId) {
        List<AiChatMessageResponse> messages = aiChatService.getChatHistory(caseId);
        return Result.success(messages);
    }
}

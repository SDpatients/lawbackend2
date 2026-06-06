package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.AiChatRequest;
import com.lawbackend2.lawbackend2.dto.response.AiChatMessageResponse;
import com.lawbackend2.lawbackend2.dto.response.AiChatSessionResponse;
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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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

    @Operation(summary = "发送消息（流式）", description = "发送消息到AI并以流式获取回复，超时时间120秒")
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendMessageStream(
            @Parameter(description = "聊天请求") @RequestBody @Validated AiChatRequest request) {
        SseEmitter emitter = new SseEmitter(120000L);

        emitter.onTimeout(() -> {
            log.error("SSE连接超时，caseId: {}, sessionId: {}", request.getCaseId(), request.getSessionId());
            try {
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("请求超时，请重试"));
                emitter.complete();
            } catch (IOException e) {
                log.error("超时后发送失败消息异常", e);
            }
        });
        
        emitter.onError(throwable -> {
            log.error("SSE连接异常", throwable);
        });

        scheduler.execute(() -> {
            try {
                ((AiChatServiceImpl) aiChatService).sendMessageStream(request, partialResponse -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .name("chunk")
                                .data(partialResponse));
                    } catch (IOException e) {
                        log.error("发送流式数据异常", e);
                    }
                });

                emitter.send(SseEmitter.event()
                        .name("complete")
                        .data("AI回复完成"));

                emitter.complete();
            } catch (Exception e) {
                log.error("流式响应处理异常", e);
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("AI处理出错：" + e.getMessage()));
                    emitter.complete();
                } catch (IOException ex) {
                    log.error("发送错误信息异常", ex);
                    emitter.completeWithError(ex);
                }
            }
        });

        return emitter;
    }

    @Operation(summary = "发送消息", description = "发送消息到AI并获取回复（默认流式）")
    @PostMapping
    public SseEmitter sendMessage(
            @Parameter(description = "聊天请求") @RequestBody @Validated AiChatRequest request) {
        return sendMessageStream(request);
    }

    @Operation(summary = "获取聊天历史（全局）", description = "获取指定案件的所有聊天历史记录")
    @GetMapping("/history")
    public Result<List<AiChatMessageResponse>> getChatHistory(
            @Parameter(description = "案件ID") @RequestParam @NotNull(message = "案件ID不能为空") Long caseId) {
        List<AiChatMessageResponse> messages = aiChatService.getChatHistory(caseId);
        return Result.success(messages);
    }

    @Operation(summary = "获取会话消息列表", description = "获取指定会话的消息历史")
    @GetMapping("/session/{sessionId}/messages")
    public Result<List<AiChatMessageResponse>> getSessionMessages(
            @Parameter(description = "会话ID") @PathVariable @NotNull Long sessionId) {
        List<AiChatMessageResponse> messages = aiChatService.getMessagesBySessionId(sessionId);
        return Result.success(messages);
    }

    @Operation(summary = "获取会话列表", description = "获取用户在指定案件下的所有聊天窗口")
    @GetMapping("/sessions")
    public Result<List<AiChatSessionResponse>> listSessions(
            @Parameter(description = "案件ID") @RequestParam @NotNull(message = "案件ID不能为空") Long caseId,
            @Parameter(description = "用户ID") @RequestParam @NotNull(message = "用户ID不能为空") Long userId) {
        List<AiChatSessionResponse> sessions = aiChatService.listSessions(caseId, userId);
        return Result.success(sessions);
    }

    @Operation(summary = "创建聊天窗口", description = "在指定案件下创建新的AI聊天窗口")
    @PostMapping("/session")
    public Result<AiChatSessionResponse> createSession(
            @Parameter(description = "案件ID") @RequestParam @NotNull(message = "案件ID不能为空") Long caseId,
            @Parameter(description = "用户ID") @RequestParam @NotNull(message = "用户ID不能为空") Long userId,
            @Parameter(description = "窗口名称") @RequestParam(required = false) String sessionName) {
        AiChatSessionResponse session = aiChatService.createSession(caseId, userId, sessionName);
        return Result.success(session);
    }

    @Operation(summary = "重命名聊天窗口", description = "修改聊天窗口的名称")
    @PutMapping("/session/{sessionId}/rename")
    public Result<AiChatSessionResponse> renameSession(
            @Parameter(description = "会话ID") @PathVariable @NotNull Long sessionId,
            @Parameter(description = "新名称") @RequestParam @NotBlank(message = "名称不能为空") String sessionName,
            @Parameter(description = "用户ID") @RequestParam @NotNull(message = "用户ID不能为空") Long userId) {
        AiChatSessionResponse session = aiChatService.renameSession(sessionId, sessionName, userId);
        return Result.success(session);
    }

    @Operation(summary = "删除聊天窗口", description = "删除聊天窗口及其所有消息")
    @DeleteMapping("/session/{sessionId}")
    public Result<Void> deleteSession(
            @Parameter(description = "会话ID") @PathVariable @NotNull Long sessionId,
            @Parameter(description = "用户ID") @RequestParam @NotNull(message = "用户ID不能为空") Long userId) {
        aiChatService.deleteSession(sessionId, userId);
        return Result.success();
    }
}
package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.*;
import com.lawbackend2.lawbackend2.dto.response.ChatMessageResponse;
import com.lawbackend2.lawbackend2.dto.response.ConversationResponse;
import com.lawbackend2.lawbackend2.dto.response.UnreadCountResponse;
import com.lawbackend2.lawbackend2.entity.Conversation;
import com.lawbackend2.lawbackend2.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "聊天管理", description = "聊天功能相关接口")
@RestController
@RequestMapping("/chat")
@Validated
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(summary = "获取或创建会话", description = "根据两个用户ID获取或创建会话")
    @GetMapping("/conversation")
    public Result<Conversation> getOrCreateConversation(
            @Parameter(description = "用户1 ID") @RequestParam Long userId1,
            @Parameter(description = "用户2 ID") @RequestParam Long userId2) {
        Conversation conversation = chatService.getOrCreateConversation(userId1, userId2);
        return Result.success(conversation);
    }

    @Operation(summary = "获取用户会话列表", description = "获取用户的所有会话列表")
    @GetMapping("/conversations")
    public Result<List<ConversationResponse>> getUserConversations(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        List<ConversationResponse> conversations = chatService.getUserConversations(userId);
        return Result.success(conversations);
    }

    @Operation(summary = "获取置顶会话", description = "获取用户的置顶会话列表")
    @GetMapping("/conversations/pinned")
    public Result<List<ConversationResponse>> getPinnedConversations(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        List<ConversationResponse> conversations = chatService.getPinnedConversations(userId);
        return Result.success(conversations);
    }

    @Operation(summary = "获取未置顶会话", description = "获取用户的未置顶会话列表")
    @GetMapping("/conversations/unpinned")
    public Result<List<ConversationResponse>> getUnpinnedConversations(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        List<ConversationResponse> conversations = chatService.getUnpinnedConversations(userId);
        return Result.success(conversations);
    }

    @Operation(summary = "获取会话消息", description = "分页获取指定会话的消息列表")
    @GetMapping("/messages")
    public Result<PageResult<ChatMessageResponse>> getConversationMessages(
            @Parameter(description = "会话ID") @RequestParam Long conversationId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        PageResult<ChatMessageResponse> messages = chatService.getConversationMessages(conversationId, pageNum, pageSize);
        return Result.success(messages);
    }

    @Operation(summary = "发送消息", description = "发送聊天消息")
    @PostMapping("/messages")
    public Result<ChatMessageResponse> sendMessage(
            @Parameter(description = "发送者ID") @RequestParam Long senderId,
            @RequestBody SendMessageRequest request) {
        ChatMessageResponse message = chatService.sendMessage(senderId, request);
        return Result.success(message);
    }

    @Operation(summary = "标记消息已读", description = "标记指定消息为已读状态")
    @PutMapping("/messages/read")
    public Result<ChatMessageResponse> markMessageAsRead(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @RequestBody MarkMessageReadRequest request) {
        ChatMessageResponse message = chatService.markMessageAsRead(userId, request.getMessageId());
        return Result.success(message);
    }

    @Operation(summary = "标记会话已读", description = "标记会话中的所有消息为已读状态")
    @PutMapping("/conversations/read")
    public Result<Void> markConversationAsRead(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "会话ID") @RequestParam Long conversationId) {
        chatService.markConversationAsRead(userId, conversationId);
        return Result.success();
    }

    @Operation(summary = "撤回消息", description = "撤回已发送的消息（2分钟内）")
    @PutMapping("/messages/recall")
    public Result<ChatMessageResponse> recallMessage(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @RequestBody RecallMessageRequest request) {
        ChatMessageResponse message = chatService.recallMessage(userId, request.getMessageId());
        return Result.success(message);
    }

    @Operation(summary = "删除消息", description = "删除指定消息")
    @DeleteMapping("/messages")
    public Result<Void> deleteMessage(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @RequestBody DeleteMessageRequest request) {
        chatService.deleteMessage(userId, request.getMessageId());
        return Result.success();
    }

    @Operation(summary = "删除会话", description = "删除指定会话")
    @DeleteMapping("/conversations")
    public Result<Void> deleteConversation(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "会话ID") @RequestParam Long conversationId) {
        chatService.deleteConversation(userId, conversationId);
        return Result.success();
    }

    @Operation(summary = "置顶/取消置顶会话", description = "设置会话的置顶状态")
    @PutMapping("/conversations/pin")
    public Result<Void> pinConversation(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @RequestBody PinConversationRequest request) {
        chatService.pinConversation(userId, request.getConversationId(), request.getPinned());
        return Result.success();
    }

    @Operation(summary = "获取未读消息数", description = "获取用户的总未读消息数")
    @GetMapping("/unread/count")
    public Result<UnreadCountResponse> getUnreadCount(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        UnreadCountResponse response = chatService.getUnreadCount(userId);
        return Result.success(response);
    }

    @Operation(summary = "获取会话未读消息数", description = "获取指定会话的未读消息数")
    @GetMapping("/conversations/unread/count")
    public Result<UnreadCountResponse> getConversationUnreadCount(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "会话ID") @RequestParam Long conversationId) {
        UnreadCountResponse response = chatService.getConversationUnreadCount(userId, conversationId);
        return Result.success(response);
    }
}

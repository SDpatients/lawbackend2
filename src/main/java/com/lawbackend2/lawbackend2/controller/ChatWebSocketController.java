package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.dto.request.SendMessageRequest;
import com.lawbackend2.lawbackend2.dto.response.ChatMessageResponse;
import com.lawbackend2.lawbackend2.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class ChatWebSocketController {

    private final ChatService chatService;

    @Autowired
    public ChatWebSocketController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat/send")
    public void sendMessage(Authentication authentication, SendMessageRequest request) {
        Long senderId = getUserIdFromAuthentication(authentication);
        log.info("收到WebSocket消息发送请求, 发送者: {}, 接收者: {}, 类型: {}", senderId, request.getReceiverId(), request.getMessageType());

        try {
            ChatMessageResponse messageResponse = chatService.sendMessage(senderId, request);
            log.info("消息发送成功, 消息ID: {}", messageResponse.getId());
        } catch (Exception e) {
            log.error("消息发送失败", e);
        }
    }

    @MessageMapping("/chat/read/{messageId}")
    public void markMessageAsRead(Authentication authentication, @DestinationVariable Long messageId) {
        Long userId = getUserIdFromAuthentication(authentication);
        log.info("收到WebSocket消息已读请求, 用户: {}, 消息ID: {}", userId, messageId);

        try {
            chatService.markMessageAsRead(userId, messageId);
            log.info("消息已读标记成功");
        } catch (Exception e) {
            log.error("消息已读标记失败", e);
        }
    }

    @MessageMapping("/chat/recall/{messageId}")
    public void recallMessage(Authentication authentication, @DestinationVariable Long messageId) {
        Long userId = getUserIdFromAuthentication(authentication);
        log.info("收到WebSocket消息撤回请求, 用户: {}, 消息ID: {}", userId, messageId);

        try {
            chatService.recallMessage(userId, messageId);
            log.info("消息撤回成功");
        } catch (Exception e) {
            log.error("消息撤回失败", e);
        }
    }

    @MessageMapping("/chat/typing/{conversationId}")
    @SendTo("/topic/chat/typing")
    public String sendTypingStatus(Authentication authentication, @DestinationVariable Long conversationId) {
        Long userId = getUserIdFromAuthentication(authentication);
        log.info("用户 {} 在会话 {} 中正在输入", userId, conversationId);
        return userId + ":" + conversationId;
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("用户未登录");
        }
        return Long.valueOf(authentication.getPrincipal().toString());
    }
}

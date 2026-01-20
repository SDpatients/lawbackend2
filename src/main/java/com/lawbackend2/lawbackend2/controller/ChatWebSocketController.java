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
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
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
    public void sendMessage(Authentication authentication, StompHeaderAccessor accessor, SendMessageRequest request) {
        Long senderId = getUserId(authentication, accessor);
        log.info("收到WebSocket消息发送请求, 发送者: {}, 接收者: {}, 类型: {}", senderId, request.getReceiverId(), request.getMessageType());

        try {
            ChatMessageResponse messageResponse = chatService.sendMessage(senderId, request);
            log.info("消息发送成功, 消息ID: {}", messageResponse.getId());
        } catch (Exception e) {
            log.error("消息发送失败", e);
        }
    }

    @MessageMapping("/chat/read/{messageId}")
    public void markMessageAsRead(Authentication authentication, StompHeaderAccessor accessor, @DestinationVariable Long messageId) {
        Long userId = getUserId(authentication, accessor);
        log.info("收到WebSocket消息已读请求, 用户: {}, 消息ID: {}", userId, messageId);

        try {
            chatService.markMessageAsRead(userId, messageId);
            log.info("消息已读标记成功");
        } catch (Exception e) {
            log.error("消息已读标记失败", e);
        }
    }

    @MessageMapping("/chat/recall/{messageId}")
    public void recallMessage(Authentication authentication, StompHeaderAccessor accessor, @DestinationVariable Long messageId) {
        Long userId = getUserId(authentication, accessor);
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
    public String sendTypingStatus(Authentication authentication, StompHeaderAccessor accessor, @DestinationVariable Long conversationId) {
        Long userId = getUserId(authentication, accessor);
        log.info("用户 {} 在会话 {} 中正在输入", userId, conversationId);
        return userId + ":" + conversationId;
    }

    private Long getUserId(Authentication authentication, StompHeaderAccessor accessor) {
        // 首先尝试从Authentication中获取
        if (authentication != null && authentication.getPrincipal() != null) {
            try {
                String principal = authentication.getPrincipal().toString();
                return Long.valueOf(principal);
            } catch (NumberFormatException e) {
                log.error("无法解析用户ID: {}", authentication.getPrincipal());
            }
        }
        
        // 如果Authentication中没有，尝试从StompHeaderAccessor中获取
        if (accessor != null && accessor.getUser() != null) {
            try {
                String principal = accessor.getUser().getName();
                return Long.valueOf(principal);
            } catch (NumberFormatException e) {
                log.error("无法解析用户ID: {}", accessor.getUser().getName());
            }
        }
        
        // 如果StompHeaderAccessor中没有，尝试从session attributes中获取
        if (accessor != null && accessor.getSessionAttributes() != null) {
            Object userId = accessor.getSessionAttributes().get("userId");
            if (userId != null) {
                try {
                    return Long.valueOf(userId.toString());
                } catch (NumberFormatException e) {
                    log.error("无法解析用户ID: {}", userId);
                }
            }
        }
        
        throw new RuntimeException("用户未登录，请提供有效的JWT Token");
    }
    
    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() != null) {
            try {
                String principal = authentication.getPrincipal().toString();
                return Long.valueOf(principal);
            } catch (NumberFormatException e) {
                log.error("无法解析用户ID: {}", authentication.getPrincipal());
                throw new RuntimeException("用户ID格式错误");
            }
        }
        throw new RuntimeException("用户未登录，请提供有效的JWT Token");
    }
}

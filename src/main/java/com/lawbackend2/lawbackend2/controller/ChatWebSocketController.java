package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.dto.WebSocketMessage;
import com.lawbackend2.lawbackend2.dto.response.ChatMessageResponse;
import com.lawbackend2.lawbackend2.dto.request.SendMessageRequest;
import com.lawbackend2.lawbackend2.service.ChatService;
import com.lawbackend2.lawbackend2.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class ChatWebSocketController {

    private final ChatService chatService;
    private final WebSocketService webSocketService;

    @Autowired
    public ChatWebSocketController(ChatService chatService, WebSocketService webSocketService) {
        this.chatService = chatService;
        this.webSocketService = webSocketService;
    }

    @MessageMapping("/chat/onlineUsers")
    @SendTo("/topic/onlineUsers")
    public Map<String, Object> getOnlineUsers(Authentication authentication, StompHeaderAccessor accessor) {
        Long userId = getUserId(authentication, accessor);
        log.info("收到获取在线用户列表请求, 用户: {}", userId);
        
        List<Long> onlineUsersList = webSocketService.getOnlineUsersList();
        int onlineUserCount = webSocketService.getOnlineUserCount();
        
        log.debug("在线用户ID列表: {}", onlineUsersList);
        log.debug("在线用户数量: {}", onlineUserCount);

        Map<String, Object> response = new HashMap<>();
        response.put("type", "ONLINE_USERS_LIST");
        response.put("users", onlineUsersList);
        response.put("count", onlineUserCount);
        response.put("timestamp", System.currentTimeMillis());
        
        log.info("返回在线用户列表, 用户数: {}, 用户列表: {}", onlineUserCount, onlineUsersList);
        return response;
    }

    @MessageMapping("/chat/send")
    public void sendMessage(Authentication authentication, StompHeaderAccessor accessor, SendMessageRequest request) {
        Long senderId = getUserId(authentication, accessor);
        log.info("=== 收到WebSocket消息发送请求 ===");
        log.info("请求信息 - 发送者: {}, 接收者: {}, 类型: {}, 内容: {}", 
                senderId, request.getReceiverId(), request.getMessageType(), request.getContent());
        log.debug("完整请求对象: {}", request);
        log.debug("认证信息: {}", authentication);
        log.debug("STOMP头信息: {}", accessor);

        try {
            log.info("准备调用 chatService.sendMessage() 处理消息");
            ChatMessageResponse messageResponse = chatService.sendMessage(senderId, request);
            log.info("✅ chatService.sendMessage() 调用成功");
            log.info("消息处理结果 - 消息ID: {}, 会话ID: {}", 
                    messageResponse.getId(), messageResponse.getConversationId());
            log.debug("完整消息响应: {}", messageResponse);
            
            log.info("=== WebSocket消息发送请求处理完成 ===");
        } catch (Exception e) {
            log.error("❌ 消息发送失败", e);
            log.error("失败原因: {}", e.getMessage());
            log.error("堆栈跟踪:", e);
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
        if (authentication != null && authentication.getPrincipal() != null) {
            try {
                String principal = authentication.getPrincipal().toString();
                return Long.valueOf(principal);
            } catch (NumberFormatException e) {
                log.error("无法解析用户ID: {}", authentication.getPrincipal());
            }
        }
        
        if (accessor != null && accessor.getUser() != null) {
            try {
                String principal = accessor.getUser().getName();
                return Long.valueOf(principal);
            } catch (NumberFormatException e) {
                log.error("无法解析用户ID: {}", accessor.getUser().getName());
            }
        }
        
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
        
        Long defaultUserId = 1L;
        log.warn("未获取到用户ID，使用默认用户ID: {} (仅开发环境)", defaultUserId);
        return defaultUserId;
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

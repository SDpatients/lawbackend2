package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.WebSocketMessage;
import com.lawbackend2.lawbackend2.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class WebSocketServiceImpl implements WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    
    // 存储在线用户
    private final Map<String, Long> onlineUsers = new ConcurrentHashMap<>();

    @Autowired
    public WebSocketServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void sendNotificationToUser(Long userId, WebSocketMessage message) {
        log.info("发送WebSocket通知给用户: {}, 类型: {}", userId, message.getType());
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                message
        );
    }

    @Override
    public void broadcastNotification(WebSocketMessage message) {
        log.info("广播WebSocket通知, 类型: {}", message.getType());
        messagingTemplate.convertAndSend("/topic/broadcast", message);
    }

    @Override
    public void sendTodoUpdate(Long userId, WebSocketMessage message) {
        log.info("发送待办更新给用户: {}, 类型: {}", userId, message.getType());
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/todos",
                message
        );
    }
    
    @Override
    public void sendChatMessage(Long senderId, Long receiverId, WebSocketMessage message) {
            log.info("发送聊天消息 - 发送者: {}, 接收者: {}, 消息类型: {}", 
                senderId, receiverId, message.getType());
        
        // 发送给接收者
        log.debug("准备发送消息给用户 {}，目的地: /user/{}/queue/chat，消息: {}", 
                receiverId, receiverId, message);
        messagingTemplate.convertAndSendToUser(
                receiverId.toString(),
                "/queue/chat",
                message
        );
        log.info("消息已发送给接收者: {}", receiverId);
        
        // 同时发送给发送者（用于确认和同步）
        log.debug("准备发送消息给发送者 {}，目的地: /user/{}/queue/chat，消息: {}", 
                senderId, senderId, message);
        messagingTemplate.convertAndSendToUser(
                senderId.toString(),
                "/queue/chat",
                message
        );
        log.info("消息已发送给发送者: {}", senderId);
    }
    
    @Override
    public void sendConversationMessage(Long conversationId, WebSocketMessage message) {
        log.info("发送会话消息到会话: {}, 消息类型: {}", conversationId, message.getType());
        messagingTemplate.convertAndSend("/topic/conversation/" + conversationId, message);
    }
    
    @Override
    public void sendTypingStatus(Long conversationId, WebSocketMessage message) {
        log.info("发送正在输入状态到会话: {}", conversationId);
        messagingTemplate.convertAndSend("/topic/typing/" + conversationId, message);
    }
    
    @Override
    public void addUserToOnline(String sessionId, Long userId) {
        onlineUsers.put(sessionId, userId);
        log.info("用户 {} 上线，当前在线用户数: {}", userId, onlineUsers.size());
        
        // 广播用户上线状态
        WebSocketMessage onlineMessage = WebSocketMessage.builder()
                .type("USER_ONLINE")
                .userId(userId)
                .title("用户上线")
                .content("用户 " + userId + " 已上线")
                .data(Map.of("userId", userId, "onlineCount", onlineUsers.size()))
                .timestamp(System.currentTimeMillis())
                .build();
        
        broadcastNotification(onlineMessage);
    }
    
    @Override
    public void removeUserFromOnline(String sessionId) {
        Long userId = onlineUsers.remove(sessionId);
        if (userId != null) {
            log.info("用户 {} 下线，当前在线用户数: {}", userId, onlineUsers.size());
            
            // 广播用户下线状态
            WebSocketMessage offlineMessage = WebSocketMessage.builder()
                    .type("USER_OFFLINE")
                    .userId(userId)
                    .title("用户下线")
                    .content("用户 " + userId + " 已下线")
                    .data(Map.of("userId", userId, "onlineCount", onlineUsers.size()))
                    .timestamp(System.currentTimeMillis())
                    .build();
            
            broadcastNotification(offlineMessage);
        }
    }
    
    @Override
    public boolean isUserOnline(Long userId) {
        return onlineUsers.containsValue(userId);
    }
    
    @Override
    public int getOnlineUserCount() {
        return onlineUsers.size();
    }
}

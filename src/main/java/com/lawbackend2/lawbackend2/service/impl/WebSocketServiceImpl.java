package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.WebSocketMessage;
import com.lawbackend2.lawbackend2.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class WebSocketServiceImpl implements WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    
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
            log.info("=== 开始发送聊天消息 ===");
            log.info("发送聊天消息 - 发送者: {}, 接收者: {}, 消息类型: {}", 
                senderId, receiverId, message.getType());
            log.debug("完整消息内容: {}", message);
            log.debug("messagingTemplate实例: {}", messagingTemplate != null ? "有效" : "无效");
        
        try {
            // 发送给接收者
            log.debug("准备发送消息给接收者 {}，目的地: /user/{}/queue/chat", receiverId, receiverId);
            
            // 详细记录发送前的状态
            log.debug("准备调用 convertAndSendToUser() 方法发送消息给接收者 {}", receiverId);
            
            // 强制发送消息，确保消息被发送
            try {
                messagingTemplate.convertAndSendToUser(
                        receiverId.toString(),
                        "/queue/chat",
                        message
                );
                log.info("✅ 消息已成功发送给接收者: {}", receiverId);
                log.info("发送给接收者的消息类型: {}, 目的地: /user/{}/queue/chat", message.getType(), receiverId);
            } catch (Exception e) {
                log.error("❌ 发送消息给接收者 {} 失败", receiverId, e);
            }
            
            // 同时发送给发送者（用于确认和同步）
            log.debug("准备发送消息给发送者 {}，目的地: /user/{}/queue/chat", senderId, senderId);
            
            // 详细记录发送前的状态
            log.debug("准备调用 convertAndSendToUser() 方法发送消息给发送者 {}", senderId);
            
            // 强制发送消息，确保消息被发送
            try {
                messagingTemplate.convertAndSendToUser(
                        senderId.toString(),
                        "/queue/chat",
                        message
                );
                log.info("✅ 消息已成功发送给发送者: {}", senderId);
                log.info("发送给发送者的消息类型: {}, 目的地: /user/{}/queue/chat", message.getType(), senderId);
            } catch (Exception e) {
                log.error("❌ 发送消息给发送者 {} 失败", senderId, e);
            }
            
            // 同时发送到广播主题，确保前端能收到消息
            log.debug("准备发送消息到广播主题: /topic/chat/broadcast");
            try {
                messagingTemplate.convertAndSend("/topic/chat/broadcast", message);
                log.info("✅ 消息已成功发送到广播主题: /topic/chat/broadcast");
            } catch (Exception e) {
                log.error("❌ 发送消息到广播主题失败", e);
            }
            
            // 验证消息格式
            log.debug("消息格式验证: type字段 = {}, data字段 = {}", message.getType(), message.getData());
            
            log.info("=== 聊天消息发送完成 ===");
        } catch (Exception e) {
            log.error("❌ 发送WebSocket聊天消息失败", e);
            log.error("失败原因: {}", e.getMessage());
            log.error("堆栈跟踪:", e);
        }
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
        // 检查用户是否已经在线，避免重复添加
        if (!onlineUsers.containsKey(sessionId)) {
            onlineUsers.put(sessionId, userId);
            log.info("用户 {} 上线，当前在线用户数: {}", userId, onlineUsers.size());
            
            WebSocketMessage onlineMessage = WebSocketMessage.builder()
                    .type("USER_ONLINE")
                    .userId(userId)
                    .title("用户上线")
                    .content("用户 " + userId + " 已上线")
                    .data(Map.of("userId", userId, "onlineCount", onlineUsers.size()))
                    .timestamp(System.currentTimeMillis())
                    .build();
            
            broadcastNotification(onlineMessage);
        } else {
            log.debug("用户 {} 已经在线，无需重复添加", userId);
        }
    }
    
    @Override
    public void removeUserFromOnline(String sessionId) {
        Long userId = onlineUsers.remove(sessionId);
        if (userId != null) {
            log.info("用户 {} 下线，当前在线用户数: {}", userId, onlineUsers.size());
            
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
    
    @Override
    public List<Long> getOnlineUsersList() {
        return new ArrayList<>(onlineUsers.values());
    }
}

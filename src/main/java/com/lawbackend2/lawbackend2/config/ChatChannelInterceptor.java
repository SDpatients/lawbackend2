package com.lawbackend2.lawbackend2.config;

import com.lawbackend2.lawbackend2.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ChatChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private WebSocketService webSocketService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 对于所有消息，检查是否已经设置了用户
        if (accessor.getUser() != null) {
            log.debug("用户已存在: {}", accessor.getUser().getName());
            return message;
        }

        // 尝试从session attributes中获取（握手时设置的）
        if (accessor.getSessionAttributes() != null) {
            Object userId = accessor.getSessionAttributes().get("userId");
            if (userId != null) {
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userId.toString(), null, List.of());
                accessor.setUser(authentication);
                log.debug("从session attributes设置WebSocket用户ID: {}", userId);
                return message;
            }
        }

        // 处理CONNECT消息，设置用户信息
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String userIdFromHeader = accessor.getFirstNativeHeader("userId");
            if (userIdFromHeader != null) {
                try {
                    Long userId = Long.valueOf(userIdFromHeader);
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userIdFromHeader, null, List.of());
                    accessor.setUser(authentication);
                    accessor.getSessionAttributes().put("userId", userIdFromHeader);
                    log.info("CONNECT消息 - 从连接头设置用户ID: {}", userIdFromHeader);
                    
                    // 添加用户到在线列表
                    webSocketService.addUserToOnline(accessor.getSessionId(), userId);
                    
                    return message;
                } catch (Exception e) {
                    log.warn("处理CONNECT消息失败: {}", userIdFromHeader, e);
                }
            }
        }

        // 尝试从连接头中获取userId
        String userIdFromHeader = accessor.getFirstNativeHeader("userId");
        if (userIdFromHeader != null) {
            try {
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userIdFromHeader, null, List.of());
                accessor.setUser(authentication);
                accessor.getSessionAttributes().put("userId", userIdFromHeader);
                log.debug("从连接头设置WebSocket用户ID: {}", userIdFromHeader);
                return message;
            } catch (Exception e) {
                log.warn("解析连接头中的userId失败: {}", userIdFromHeader, e);
            }
        }

        log.warn("WebSocket消息未包含用户ID - 可能是匿名连接");
        return message;
    }
    
    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        
        // 处理DISCONNECT消息
        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            log.info("用户断开WebSocket连接: {}", accessor.getSessionId());
            webSocketService.removeUserFromOnline(accessor.getSessionId());
        }
    }
}

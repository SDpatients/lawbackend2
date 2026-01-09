package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.WebSocketMessage;
import com.lawbackend2.lawbackend2.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WebSocketServiceImpl implements WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

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
}

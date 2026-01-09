package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.WebSocketMessage;

public interface WebSocketService {

    void sendNotificationToUser(Long userId, WebSocketMessage message);

    void broadcastNotification(WebSocketMessage message);

    void sendTodoUpdate(Long userId, WebSocketMessage message);
}

package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.WebSocketMessage;

public interface WebSocketService {

    void sendNotificationToUser(Long userId, WebSocketMessage message);

    void broadcastNotification(WebSocketMessage message);

    void sendTodoUpdate(Long userId, WebSocketMessage message);
    
    void sendChatMessage(Long senderId, Long receiverId, WebSocketMessage message);
    
    void sendConversationMessage(Long conversationId, WebSocketMessage message);
    
    void sendTypingStatus(Long conversationId, WebSocketMessage message);
    
    void addUserToOnline(String sessionId, Long userId);
    
    void removeUserFromOnline(String sessionId);
    
    boolean isUserOnline(Long userId);
    
    int getOnlineUserCount();
}

package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.dto.WebSocketMessage;
import com.lawbackend2.lawbackend2.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/websocket")
public class WebSocketController {

    private final WebSocketService webSocketService;

    @Autowired
    public WebSocketController(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getWebSocketStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("onlineUserCount", webSocketService.getOnlineUserCount());
        response.put("status", "running");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/broadcast")
    public ResponseEntity<String> broadcastMessage(@RequestBody WebSocketMessage message) {
        try {
            webSocketService.broadcastNotification(message);
            return ResponseEntity.ok("广播消息发送成功");
        } catch (Exception e) {
            log.error("广播消息发送失败", e);
            return ResponseEntity.internalServerError().body("广播消息发送失败: " + e.getMessage());
        }
    }

    @PostMapping("/send/{userId}")
    public ResponseEntity<String> sendMessageToUser(@PathVariable Long userId, @RequestBody WebSocketMessage message) {
        try {
            webSocketService.sendNotificationToUser(userId, message);
            return ResponseEntity.ok("消息发送成功");
        } catch (Exception e) {
            log.error("消息发送失败", e);
            return ResponseEntity.internalServerError().body("消息发送失败: " + e.getMessage());
        }
    }

    @GetMapping("/online/{userId}")
    public ResponseEntity<Map<String, Object>> checkUserOnlineStatus(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("isOnline", webSocketService.isUserOnline(userId));
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
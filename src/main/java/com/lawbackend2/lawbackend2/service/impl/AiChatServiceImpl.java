package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.AiChatRequest;
import com.lawbackend2.lawbackend2.dto.response.AiChatMessageResponse;
import com.lawbackend2.lawbackend2.entity.AiChatMessage;
import com.lawbackend2.lawbackend2.entity.AiChatSession;
import com.lawbackend2.lawbackend2.repository.AiChatMessageRepository;
import com.lawbackend2.lawbackend2.repository.AiChatSessionRepository;
import com.lawbackend2.lawbackend2.service.AiChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class AiChatServiceImpl implements AiChatService {

    private final AiChatSessionRepository sessionRepository;
    private final AiChatMessageRepository messageRepository;

    @Autowired
    public AiChatServiceImpl(AiChatSessionRepository sessionRepository, AiChatMessageRepository messageRepository) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    @Transactional
    public AiChatMessageResponse sendMessage(AiChatRequest request) {
        // 获取或创建聊天会话
        AiChatSession session = getOrCreateSession(request.getCaseId(), request.getUserId());

        // 创建用户消息
        AiChatMessage userMessage = createUserMessage(session, request.getContent(), request.getUserId());
        messageRepository.save(userMessage);

        // 生成AI回复
        String aiResponseContent = generateAiResponse(request.getContent(), request.getCaseId());

        // 创建AI消息
        AiChatMessage aiMessage = createAiMessage(session, aiResponseContent);
        messageRepository.save(aiMessage);

        // 更新会话信息
        updateSessionInfo(session, aiMessage.getTimestamp());

        // 转换为响应DTO
        return convertToResponse(aiMessage);
    }

    /**
     * 流式生成AI回复（用于前端流式显示）
     * @param request 聊天请求
     * @param callback 回调函数，用于逐段返回AI回复
     * @throws Exception 异常信息
     */
    public void sendMessageStream(AiChatRequest request, java.util.function.Consumer<String> callback) throws Exception {
        // 获取或创建聊天会话
        AiChatSession session = getOrCreateSession(request.getCaseId(), request.getUserId());

        // 创建用户消息
        AiChatMessage userMessage = createUserMessage(session, request.getContent(), request.getUserId());
        messageRepository.save(userMessage);

        // 生成AI回复
        String aiResponseContent = generateAiResponse(request.getContent(), request.getCaseId());

        // 模拟流式输出，逐字或逐句返回
        String[] words = aiResponseContent.split("(?<=\\W)");
        StringBuilder responseBuilder = new StringBuilder();
        
        for (String word : words) {
            responseBuilder.append(word);
            callback.accept(responseBuilder.toString());
            // 模拟打字速度
            Thread.sleep(50);
        }

        // 创建AI消息
        AiChatMessage aiMessage = createAiMessage(session, aiResponseContent);
        messageRepository.save(aiMessage);

        // 更新会话信息
        updateSessionInfo(session, aiMessage.getTimestamp());
    }

    @Override
    public List<AiChatMessageResponse> getChatHistory(Long caseId) {
        List<AiChatMessage> messages = messageRepository.findByCaseIdOrderByTimestampAsc(caseId);
        List<AiChatMessageResponse> responses = new ArrayList<>();
        
        for (AiChatMessage message : messages) {
            responses.add(convertToResponse(message));
        }
        
        return responses;
    }

    @Override
    public AiChatSession getOrCreateSession(Long caseId, Long userId) {
        Optional<AiChatSession> existingSession = sessionRepository.findByCaseIdAndUserId(caseId, userId);
        
        if (existingSession.isPresent()) {
            return existingSession.get();
        }
        
        // 创建新会话
        AiChatSession newSession = new AiChatSession();
        newSession.setCaseId(caseId);
        newSession.setUserId(userId);
        newSession.setSessionName("案件AI聊天会话");
        newSession.setMessageCount(0);
        newSession.setCreateUserId(userId);
        newSession.setUpdateUserId(userId);
        
        return sessionRepository.save(newSession);
    }

    @Override
    public String generateAiResponse(String userMessage, Long caseId) {
        try {
            // 构建API请求
            RestTemplate restTemplate = new RestTemplate();
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer XC9P4NWOC5TLSJ0GC9DNZMATGNNKOLSBLYRIOF14");
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "LegalOne-8B");
            
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 系统提示
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一个专业的法律助手，负责回答用户关于案件的问题。请根据用户的问题提供专业、准确的法律建议。案件ID：" + caseId);
            messages.add(systemMessage);
            
            // 用户消息
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);
            
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1000);
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            // 发送请求
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://ai.gitee.com/v1/chat/completions",
                    requestEntity,
                    Map.class
            );
            
            // 处理响应
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null) {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                    if (!choices.isEmpty()) {
                        Map<String, Object> choice = choices.get(0);
                        Map<String, Object> message = (Map<String, Object>) choice.get("message");
                        return (String) message.get("content");
                    }
                }
            }
            
            log.error("AI API调用失败，状态码：{}", response.getStatusCode());
            return "抱歉，AI服务暂时不可用，请稍后重试。";
            
        } catch (Exception e) {
            log.error("AI API调用异常", e);
            return "抱歉，AI服务暂时不可用，请稍后重试。";
        }
    }

    private AiChatMessage createUserMessage(AiChatSession session, String content, Long userId) {
        AiChatMessage message = new AiChatMessage();
        message.setSessionId(session.getId());
        message.setCaseId(session.getCaseId());
        message.setContent(content);
        message.setSenderType("user");
        message.setSenderId(userId);
        message.setTimestamp(LocalDateTime.now());
        message.setMessageStatus("SENT");
        message.setCreateUserId(userId);
        message.setUpdateUserId(userId);
        return message;
    }

    private AiChatMessage createAiMessage(AiChatSession session, String content) {
        AiChatMessage message = new AiChatMessage();
        message.setSessionId(session.getId());
        message.setCaseId(session.getCaseId());
        message.setContent(content);
        message.setSenderType("ai");
        message.setTimestamp(LocalDateTime.now());
        message.setMessageStatus("SENT");
        return message;
    }

    private void updateSessionInfo(AiChatSession session, LocalDateTime lastMessageTime) {
        session.setLastMessageTime(lastMessageTime);
        session.setMessageCount(session.getMessageCount() + 2); // 增加两条消息（用户和AI）
        session.setUpdateTime(LocalDateTime.now());
        sessionRepository.save(session);
    }

    private AiChatMessageResponse convertToResponse(AiChatMessage message) {
        AiChatMessageResponse response = new AiChatMessageResponse();
        response.setId(message.getId());
        response.setSessionId(message.getSessionId());
        response.setCaseId(message.getCaseId());
        response.setContent(message.getContent());
        response.setSender(message.getSenderType());
        response.setTimestamp(message.getTimestamp());
        response.setMessageStatus(message.getMessageStatus());
        return response;
    }
}

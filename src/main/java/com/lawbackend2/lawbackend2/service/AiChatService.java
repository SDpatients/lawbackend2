package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.AiChatRequest;
import com.lawbackend2.lawbackend2.dto.response.AiChatMessageResponse;
import com.lawbackend2.lawbackend2.dto.response.AiChatSessionResponse;
import com.lawbackend2.lawbackend2.entity.AiChatSession;

import java.util.List;

public interface AiChatService {

    AiChatMessageResponse sendMessage(AiChatRequest request);

    List<AiChatMessageResponse> getChatHistory(Long caseId);

    List<AiChatMessageResponse> getMessagesBySessionId(Long sessionId);

    AiChatSession getOrCreateSession(Long caseId, Long userId);

    String generateAiResponse(String userMessage, Long caseId, Long sessionId);

    List<AiChatSessionResponse> listSessions(Long caseId, Long userId);

    AiChatSessionResponse createSession(Long caseId, Long userId, String sessionName);

    AiChatSessionResponse renameSession(Long sessionId, String sessionName, Long userId);

    void deleteSession(Long sessionId, Long userId);
}

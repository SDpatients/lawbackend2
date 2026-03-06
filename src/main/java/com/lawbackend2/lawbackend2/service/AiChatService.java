package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.AiChatRequest;
import com.lawbackend2.lawbackend2.dto.response.AiChatMessageResponse;
import com.lawbackend2.lawbackend2.entity.AiChatSession;

import java.util.List;

public interface AiChatService {

    /**
     * 发送消息到AI并获取回复
     * @param request 聊天请求
     * @return AI回复消息
     */
    AiChatMessageResponse sendMessage(AiChatRequest request);

    /**
     * 获取聊天历史记录
     * @param caseId 案件ID
     * @return 聊天消息列表
     */
    List<AiChatMessageResponse> getChatHistory(Long caseId);

    /**
     * 获取或创建聊天会话
     * @param caseId 案件ID
     * @param userId 用户ID
     * @return 聊天会话
     */
    AiChatSession getOrCreateSession(Long caseId, Long userId);

    /**
     * 生成AI回复
     * @param userMessage 用户消息内容
     * @param caseId 案件ID
     * @return AI回复内容
     */
    String generateAiResponse(String userMessage, Long caseId);
}

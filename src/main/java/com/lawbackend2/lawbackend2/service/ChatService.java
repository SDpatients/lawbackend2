package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.*;
import com.lawbackend2.lawbackend2.dto.response.ChatMessageResponse;
import com.lawbackend2.lawbackend2.dto.response.ConversationResponse;
import com.lawbackend2.lawbackend2.dto.response.UnreadCountResponse;
import com.lawbackend2.lawbackend2.entity.ChatMessage;
import com.lawbackend2.lawbackend2.entity.Conversation;

import java.util.List;

public interface ChatService {

    Conversation getOrCreateConversation(Long userId1, Long userId2);

    List<ConversationResponse> getUserConversations(Long userId);

    List<ConversationResponse> getPinnedConversations(Long userId);

    List<ConversationResponse> getUnpinnedConversations(Long userId);

    PageResult<ChatMessageResponse> getConversationMessages(Long conversationId, Integer pageNum, Integer pageSize);

    ChatMessageResponse sendMessage(Long senderId, SendMessageRequest request);

    ChatMessageResponse markMessageAsRead(Long userId, Long messageId);

    void markConversationAsRead(Long userId, Long conversationId);

    ChatMessageResponse recallMessage(Long userId, Long messageId);

    void deleteMessage(Long userId, Long messageId);

    void deleteConversation(Long userId, Long conversationId);

    void pinConversation(Long userId, Long conversationId, Boolean pinned);

    UnreadCountResponse getUnreadCount(Long userId);

    UnreadCountResponse getConversationUnreadCount(Long userId, Long conversationId);
}

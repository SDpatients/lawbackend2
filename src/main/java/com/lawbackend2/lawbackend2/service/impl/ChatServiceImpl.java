package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.SendMessageRequest;
import com.lawbackend2.lawbackend2.dto.response.ChatMessageResponse;
import com.lawbackend2.lawbackend2.dto.response.ConversationResponse;
import com.lawbackend2.lawbackend2.dto.response.UnreadCountResponse;
import com.lawbackend2.lawbackend2.entity.ChatMessage;
import com.lawbackend2.lawbackend2.entity.Conversation;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.ChatMessageRepository;
import com.lawbackend2.lawbackend2.repository.ConversationRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.ChatService;
import com.lawbackend2.lawbackend2.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;

    @Autowired
    public ChatServiceImpl(ConversationRepository conversationRepository,
                           ChatMessageRepository chatMessageRepository,
                           UserRepository userRepository,
                           WebSocketService webSocketService) {
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.webSocketService = webSocketService;
    }

    @Override
    @Transactional
    public Conversation getOrCreateConversation(Long userId1, Long userId2) {
        if (userId1.equals(userId2)) {
            throw new BusinessException("不能与自己创建会话");
        }

        final Long finalUserId1;
        final Long finalUserId2;
        if (userId1 > userId2) {
            finalUserId1 = userId2;
            finalUserId2 = userId1;
        } else {
            finalUserId1 = userId1;
            finalUserId2 = userId2;
        }

        return conversationRepository.findByUserId1AndUserId2(finalUserId1, finalUserId2)
                .orElseGet(() -> {
                    Conversation conversation = new Conversation();
                    conversation.setUserId1(finalUserId1);
                    conversation.setUserId2(finalUserId2);
                    conversation.setStatus("ACTIVE");
                    return conversationRepository.save(conversation);
                });
    }

    @Override
    public List<ConversationResponse> getUserConversations(Long userId) {
        List<Conversation> conversations = conversationRepository.findActiveUserConversations(userId, "ACTIVE");
        return conversations.stream()
                .map(this::convertToConversationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConversationResponse> getPinnedConversations(Long userId) {
        List<Conversation> conversations = conversationRepository.findPinnedUserConversations(userId, "ACTIVE");
        return conversations.stream()
                .map(this::convertToConversationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConversationResponse> getUnpinnedConversations(Long userId) {
        List<Conversation> conversations = conversationRepository.findUnpinnedUserConversations(userId, "ACTIVE");
        return conversations.stream()
                .map(this::convertToConversationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<ChatMessageResponse> getConversationMessages(Long conversationId, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<ChatMessage> messagePage = chatMessageRepository.findByConversationIdAndIsDeletedAndIsRecalledOrderByCreateTimeDesc(
                conversationId, false, false, pageable);

        List<ChatMessageResponse> responses = messagePage.getContent().stream()
                .map(this::convertToChatMessageResponse)
                .collect(Collectors.toList());

        return new PageResult<>(messagePage.getTotalElements(), responses);
    }

    @Override
    @Transactional
    public ChatMessageResponse sendMessage(Long senderId, SendMessageRequest request) {
        Long receiverId = request.getReceiverId();

        if (senderId.equals(receiverId)) {
            throw new BusinessException("不能给自己发送消息");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new BusinessException("发送者不存在"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new BusinessException("接收者不存在"));

        Conversation conversation = getOrCreateConversation(senderId, receiverId);

        ChatMessage message = new ChatMessage();
        message.setConversationId(conversation.getId());
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setMessageType(request.getMessageType());
        message.setContent(request.getContent());
        message.setFileId(request.getFileId());
        message.setFileName(request.getFileName());
        message.setFileSize(request.getFileSize());
        message.setFileUrl(request.getFileUrl());
        message.setMessageStatus("SENT");
        message.setIsDeleted(false);
        message.setIsRecalled(false);

        ChatMessage savedMessage = chatMessageRepository.save(message);

        String lastContent = request.getMessageType().equals("TEXT") ? request.getContent() :
                request.getMessageType().equals("IMAGE") ? "[图片]" :
                request.getMessageType().equals("FILE") ? request.getFileName() :
                request.getMessageType().equals("VOICE") ? "[语音]" : "[视频]";

        conversationRepository.updateLastMessage(
                conversation.getId(),
                savedMessage.getId(),
                lastContent,
                request.getMessageType(),
                savedMessage.getCreateTime()
        );

        if (conversation.getUserId1().equals(senderId)) {
            conversationRepository.incrementUser2UnreadCount(conversation.getId(), senderId);
        } else {
            conversationRepository.incrementUser1UnreadCount(conversation.getId(), senderId);
        }

        ChatMessageResponse response = convertToChatMessageResponse(savedMessage);

        com.lawbackend2.lawbackend2.dto.WebSocketMessage wsMessage = com.lawbackend2.lawbackend2.dto.WebSocketMessage.builder()
                .type("NEW_MESSAGE")
                .userId(receiverId)
                .title("新消息")
                .content(lastContent)
                .data(response)
                .timestamp(System.currentTimeMillis())
                .build();

        webSocketService.sendNotificationToUser(receiverId, wsMessage);

        return response;
    }

    @Override
    @Transactional
    public ChatMessageResponse markMessageAsRead(Long userId, Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException("消息不存在"));

        if (!message.getReceiverId().equals(userId)) {
            throw new BusinessException("只能标记发送给自己的消息");
        }

        if (!message.getMessageStatus().equals("READ")) {
            chatMessageRepository.markMessageAsRead(messageId, userId, LocalDateTime.now());
            message.setMessageStatus("READ");
            message.setReadTime(LocalDateTime.now());

            if (message.getSenderId().equals(message.getConversationId())) {
                conversationRepository.clearUser1UnreadCount(message.getConversationId());
            } else {
                conversationRepository.clearUser2UnreadCount(message.getConversationId());
            }

            com.lawbackend2.lawbackend2.dto.WebSocketMessage wsMessage = com.lawbackend2.lawbackend2.dto.WebSocketMessage.builder()
                    .type("MESSAGE_READ")
                    .userId(message.getSenderId())
                    .title("消息已读")
                    .content("对方已阅读您的消息")
                    .data(messageId)
                    .timestamp(System.currentTimeMillis())
                    .build();

            webSocketService.sendNotificationToUser(message.getSenderId(), wsMessage);
        }

        return convertToChatMessageResponse(message);
    }

    @Override
    @Transactional
    public void markConversationAsRead(Long userId, Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException("会话不存在"));

        if (!conversation.getUserId1().equals(userId) && !conversation.getUserId2().equals(userId)) {
            throw new BusinessException("无权操作此会话");
        }

        int updated = chatMessageRepository.markConversationAsRead(conversationId, userId, LocalDateTime.now());

        if (conversation.getUserId1().equals(userId)) {
            conversationRepository.clearUser1UnreadCount(conversationId);
        } else {
            conversationRepository.clearUser2UnreadCount(conversationId);
        }
    }

    @Override
    @Transactional
    public ChatMessageResponse recallMessage(Long userId, Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException("消息不存在"));

        if (!message.getSenderId().equals(userId)) {
            throw new BusinessException("只能撤回自己发送的消息");
        }

        if (message.getIsRecalled()) {
            throw new BusinessException("消息已被撤回");
        }

        LocalDateTime now = LocalDateTime.now();
        if (message.getCreateTime().plusMinutes(2).isBefore(now)) {
            throw new BusinessException("超过2分钟，无法撤回消息");
        }

        chatMessageRepository.recallMessage(messageId, userId, now);
        message.setIsRecalled(true);
        message.setRecallTime(now);

        com.lawbackend2.lawbackend2.dto.WebSocketMessage wsMessage = com.lawbackend2.lawbackend2.dto.WebSocketMessage.builder()
                .type("MESSAGE_RECALLED")
                .userId(message.getReceiverId())
                .title("消息撤回")
                .content("对方撤回了一条消息")
                .data(messageId)
                .timestamp(System.currentTimeMillis())
                .build();

        webSocketService.sendNotificationToUser(message.getReceiverId(), wsMessage);

        return convertToChatMessageResponse(message);
    }

    @Override
    @Transactional
    public void deleteMessage(Long userId, Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException("消息不存在"));

        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new BusinessException("无权删除此消息");
        }

        chatMessageRepository.deleteMessage(messageId, userId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void deleteConversation(Long userId, Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException("会话不存在"));

        if (conversation.getUserId1().equals(userId)) {
            conversationRepository.deleteUser1Conversation(conversationId, userId);
        } else if (conversation.getUserId2().equals(userId)) {
            conversationRepository.deleteUser2Conversation(conversationId, userId);
        } else {
            throw new BusinessException("无权操作此会话");
        }
    }

    @Override
    @Transactional
    public void pinConversation(Long userId, Long conversationId, Boolean pinned) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException("会话不存在"));

        if (conversation.getUserId1().equals(userId)) {
            conversationRepository.updateUser1PinnedStatus(conversationId, userId, pinned);
        } else if (conversation.getUserId2().equals(userId)) {
            conversationRepository.updateUser2PinnedStatus(conversationId, userId, pinned);
        } else {
            throw new BusinessException("无权操作此会话");
        }
    }

    @Override
    public UnreadCountResponse getUnreadCount(Long userId) {
        Long totalUnread = conversationRepository.countTotalUnreadMessages(userId, "ACTIVE");
        return UnreadCountResponse.builder()
                .totalUnread(totalUnread)
                .conversationUnread(0L)
                .build();
    }

    @Override
    public UnreadCountResponse getConversationUnreadCount(Long userId, Long conversationId) {
        Long conversationUnread = chatMessageRepository.countUnreadMessages(conversationId, userId);
        return UnreadCountResponse.builder()
                .totalUnread(0L)
                .conversationUnread(conversationUnread)
                .build();
    }

    private ConversationResponse convertToConversationResponse(Conversation conversation) {
        User user1 = userRepository.findById(conversation.getUserId1()).orElse(null);
        User user2 = userRepository.findById(conversation.getUserId2()).orElse(null);

        return ConversationResponse.builder()
                .id(conversation.getId())
                .userId1(conversation.getUserId1())
                .userId1Name(user1 != null ? user1.getUsername() : null)
                .userId2(conversation.getUserId2())
                .userId2Name(user2 != null ? user2.getUsername() : null)
                .lastMessageId(conversation.getLastMessageId())
                .lastMessageContent(conversation.getLastMessageContent())
                .lastMessageType(conversation.getLastMessageType())
                .lastMessageTime(conversation.getLastMessageTime())
                .user1UnreadCount(conversation.getUser1UnreadCount())
                .user2UnreadCount(conversation.getUser2UnreadCount())
                .user1Deleted(conversation.getUser1Deleted())
                .user2Deleted(conversation.getUser2Deleted())
                .user1Pinned(conversation.getUser1Pinned())
                .user2Pinned(conversation.getUser2Pinned())
                .status(conversation.getStatus())
                .createTime(conversation.getCreateTime())
                .build();
    }

    private ChatMessageResponse convertToChatMessageResponse(ChatMessage message) {
        User sender = userRepository.findById(message.getSenderId()).orElse(null);
        User receiver = userRepository.findById(message.getReceiverId()).orElse(null);

        return ChatMessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversationId())
                .senderId(message.getSenderId())
                .senderName(sender != null ? sender.getUsername() : null)
                .receiverId(message.getReceiverId())
                .receiverName(receiver != null ? receiver.getUsername() : null)
                .messageType(message.getMessageType())
                .content(message.getContent())
                .fileId(message.getFileId())
                .fileName(message.getFileName())
                .fileSize(message.getFileSize())
                .fileUrl(message.getFileUrl())
                .messageStatus(message.getMessageStatus())
                .readTime(message.getReadTime())
                .isDeleted(message.getIsDeleted())
                .isRecalled(message.getIsRecalled())
                .createTime(message.getCreateTime())
                .build();
    }
}

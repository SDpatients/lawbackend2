package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Optional<ChatMessage> findTopByConversationIdOrderByCreateTimeDesc(Long conversationId);

    List<ChatMessage> findByConversationIdAndIsDeletedOrderByCreateTimeDesc(Long conversationId, Boolean isDeleted);

    Page<ChatMessage> findByConversationIdAndIsDeletedOrderByCreateTimeDesc(Long conversationId, Boolean isDeleted, Pageable pageable);

    List<ChatMessage> findByConversationIdAndIsDeletedAndIsRecalledOrderByCreateTimeDesc(Long conversationId, Boolean isDeleted, Boolean isRecalled);

    Page<ChatMessage> findByConversationIdAndIsDeletedAndIsRecalledOrderByCreateTimeDesc(Long conversationId, Boolean isDeleted, Boolean isRecalled, Pageable pageable);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.conversationId = :conversationId AND m.receiverId = :userId AND m.messageStatus != 'READ' AND m.isDeleted = false")
    Long countUnreadMessages(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.receiverId = :userId AND m.messageStatus != 'READ' AND m.isDeleted = false")
    Long countTotalUnreadMessages(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.messageStatus = 'READ', m.readTime = :readTime WHERE m.conversationId = :conversationId AND m.receiverId = :userId AND m.messageStatus != 'READ' AND m.isDeleted = false")
    int markConversationAsRead(@Param("conversationId") Long conversationId, @Param("userId") Long userId, @Param("readTime") LocalDateTime readTime);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.messageStatus = 'READ', m.readTime = :readTime WHERE m.id = :messageId AND m.receiverId = :userId")
    int markMessageAsRead(@Param("messageId") Long messageId, @Param("userId") Long userId, @Param("readTime") LocalDateTime readTime);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRecalled = true, m.recallTime = :recallTime WHERE m.id = :messageId AND m.senderId = :senderId AND m.isRecalled = false")
    int recallMessage(@Param("messageId") Long messageId, @Param("senderId") Long senderId, @Param("recallTime") LocalDateTime recallTime);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.isDeleted = true, m.deletedBy = :userId, m.deletedTime = :deletedTime WHERE m.id = :messageId")
    int deleteMessage(@Param("messageId") Long messageId, @Param("userId") Long userId, @Param("deletedTime") LocalDateTime deletedTime);

    List<ChatMessage> findBySenderIdAndReceiverIdAndCreateTimeAfter(Long senderId, Long receiverId, LocalDateTime startTime);

    List<ChatMessage> findBySenderIdOrReceiverIdAndIsDeletedOrderByCreateTimeDesc(Long userId1, Long userId2, Boolean isDeleted);
}

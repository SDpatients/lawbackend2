package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByUserId1AndUserId2(Long userId1, Long userId2);

    Optional<Conversation> findByUserId1AndUserId2AndStatus(Long userId1, Long userId2, String status);

    List<Conversation> findByUserId1OrUserId2AndStatusOrderByLastMessageTimeDesc(Long userId1, Long userId2, String status);

    @Query("SELECT c FROM Conversation c WHERE (c.userId1 = :userId OR c.userId2 = :userId) AND c.status = :status ORDER BY c.lastMessageTime DESC")
    List<Conversation> findUserConversations(@Param("userId") Long userId, @Param("status") String status);

    @Query("SELECT c FROM Conversation c WHERE (c.userId1 = :userId OR c.userId2 = :userId) AND c.status = :status AND (c.userId1 = :userId AND c.user1Deleted = false OR c.userId2 = :userId AND c.user2Deleted = false) ORDER BY c.lastMessageTime DESC")
    List<Conversation> findActiveUserConversations(@Param("userId") Long userId, @Param("status") String status);

    @Query("SELECT c FROM Conversation c WHERE (c.userId1 = :userId OR c.userId2 = :userId) AND c.status = :status AND (c.userId1 = :userId AND c.user1Pinned = true OR c.userId2 = :userId AND c.user2Pinned = true) ORDER BY c.lastMessageTime DESC")
    List<Conversation> findPinnedUserConversations(@Param("userId") Long userId, @Param("status") String status);

    @Query("SELECT c FROM Conversation c WHERE (c.userId1 = :userId OR c.userId2 = :userId) AND c.status = :status AND (c.userId1 = :userId AND c.user1Pinned = false OR c.userId2 = :userId AND c.user2Pinned = false) ORDER BY c.lastMessageTime DESC")
    List<Conversation> findUnpinnedUserConversations(@Param("userId") Long userId, @Param("status") String status);

    @Modifying
    @Query("UPDATE Conversation c SET c.lastMessageId = :messageId, c.lastMessageContent = :content, c.lastMessageType = :messageType, c.lastMessageTime = :messageTime WHERE c.id = :conversationId")
    int updateLastMessage(@Param("conversationId") Long conversationId, @Param("messageId") Long messageId, @Param("content") String content, @Param("messageType") String messageType, @Param("messageTime") LocalDateTime messageTime);

    @Modifying
    @Query("UPDATE Conversation c SET c.user1UnreadCount = c.user1UnreadCount + 1 WHERE c.id = :conversationId AND c.userId2 = :senderId")
    int incrementUser1UnreadCount(@Param("conversationId") Long conversationId, @Param("senderId") Long senderId);

    @Modifying
    @Query("UPDATE Conversation c SET c.user2UnreadCount = c.user2UnreadCount + 1 WHERE c.id = :conversationId AND c.userId1 = :senderId")
    int incrementUser2UnreadCount(@Param("conversationId") Long conversationId, @Param("senderId") Long senderId);

    @Modifying
    @Query("UPDATE Conversation c SET c.user1UnreadCount = 0 WHERE c.id = :conversationId")
    int clearUser1UnreadCount(@Param("conversationId") Long conversationId);

    @Modifying
    @Query("UPDATE Conversation c SET c.user2UnreadCount = 0 WHERE c.id = :conversationId")
    int clearUser2UnreadCount(@Param("conversationId") Long conversationId);

    @Modifying
    @Query("UPDATE Conversation c SET c.user1Deleted = true WHERE c.id = :conversationId AND c.userId1 = :userId")
    int deleteUser1Conversation(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Conversation c SET c.user2Deleted = true WHERE c.id = :conversationId AND c.userId2 = :userId")
    int deleteUser2Conversation(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Conversation c SET c.user1Pinned = :pinned WHERE c.id = :conversationId AND c.userId1 = :userId")
    int updateUser1PinnedStatus(@Param("conversationId") Long conversationId, @Param("userId") Long userId, @Param("pinned") Boolean pinned);

    @Modifying
    @Query("UPDATE Conversation c SET c.user2Pinned = :pinned WHERE c.id = :conversationId AND c.userId2 = :userId")
    int updateUser2PinnedStatus(@Param("conversationId") Long conversationId, @Param("userId") Long userId, @Param("pinned") Boolean pinned);

    @Query("SELECT SUM(CASE WHEN c.userId1 = :userId THEN c.user1UnreadCount ELSE c.user2UnreadCount END) FROM Conversation c WHERE (c.userId1 = :userId OR c.userId2 = :userId) AND c.status = :status")
    Long countTotalUnreadMessages(@Param("userId") Long userId, @Param("status") String status);
}

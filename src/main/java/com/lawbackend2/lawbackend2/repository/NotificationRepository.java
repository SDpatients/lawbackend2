package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserIdAndCreateUserIdNot(Long userId, Long createUserId, Pageable pageable);

    Page<Notification> findByUserIdAndIsReadAndCreateUserIdNot(Long userId, Boolean isRead, Long createUserId, Pageable pageable);

    Page<Notification> findByUserIdAndTypeAndCreateUserIdNot(Long userId, String type, Long createUserId, Pageable pageable);

    Page<Notification> findByUserIdAndStatusAndCreateUserIdNot(Long userId, String status, Long createUserId, Pageable pageable);

    List<Notification> findByUserIdAndIsReadAndCreateUserIdNotOrderByCreateTimeDesc(Long userId, Boolean isRead, Long createUserId);

    @Query("SELECT n FROM Notification n WHERE " +
           "n.userId = :userId AND " +
           "n.createUserId <> :createUserId AND " +
           "(:type IS NULL OR n.type = :type) AND " +
           "(:isRead IS NULL OR n.isRead = :isRead) AND " +
           "(:status IS NULL OR n.status = :status)")
    Page<Notification> searchNotifications(@Param("userId") Long userId,
                                           @Param("createUserId") Long createUserId,
                                           @Param("type") String type,
                                           @Param("isRead") Boolean isRead,
                                           @Param("status") String status,
                                           Pageable pageable);

    Long countByUserIdAndIsReadAndCreateUserIdNot(Long userId, Boolean isRead, Long createUserId);

    Long countByUserIdAndStatusAndCreateUserIdNot(Long userId, String status, Long createUserId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.isRead = false AND n.createUserId <> :createUserId")
    Long countUnreadByUserIdAndCreateUserIdNot(@Param("userId") Long userId, @Param("createUserId") Long createUserId);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.expireTime IS NOT NULL AND n.expireTime < :expireTime")
    void deleteExpiredNotifications(@Param("expireTime") LocalDateTime expireTime);

    java.util.Optional<Notification> findByIdAndCreateUserIdNot(Long id, Long createUserId);
}

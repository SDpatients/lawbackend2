package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserId(Long userId, Pageable pageable);

    Page<Notification> findByUserIdAndIsRead(Long userId, Boolean isRead, Pageable pageable);

    Page<Notification> findByUserIdAndType(Long userId, String type, Pageable pageable);

    Page<Notification> findByUserIdAndStatus(Long userId, String status, Pageable pageable);

    List<Notification> findByUserIdAndIsReadOrderByCreateTimeDesc(Long userId, Boolean isRead);

    @Query("SELECT n FROM Notification n WHERE " +
           "n.userId = :userId AND " +
           "(:type IS NULL OR n.type = :type) AND " +
           "(:isRead IS NULL OR n.isRead = :isRead) AND " +
           "(:status IS NULL OR n.status = :status)")
    Page<Notification> searchNotifications(@Param("userId") Long userId,
                                           @Param("type") String type,
                                           @Param("isRead") Boolean isRead,
                                           @Param("status") String status,
                                           Pageable pageable);

    Long countByUserIdAndIsRead(Long userId, Boolean isRead);

    Long countByUserIdAndStatus(Long userId, String status);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.isRead = false")
    Long countUnreadByUserId(@Param("userId") Long userId);

    @Query("DELETE FROM Notification n WHERE n.expireTime IS NOT NULL AND n.expireTime < :expireTime")
    void deleteExpiredNotifications(@Param("expireTime") LocalDateTime expireTime);
}

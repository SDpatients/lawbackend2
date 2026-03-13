package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("SELECT a FROM Activity a WHERE a.isDeleted = false AND a.userId = :userId")
    Page<Activity> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE a.isDeleted = false AND a.type = :type AND a.createTime > :startTime")
    List<Activity> findRecentActivitiesByType(@Param("type") String type, @Param("startTime") LocalDateTime startTime);

    @Query("SELECT a FROM Activity a WHERE a.isDeleted = false AND (:userId IS NULL OR a.userId = :userId) AND (:type IS NULL OR a.type = :type) AND a.status = :status ORDER BY a.createTime DESC")
    Page<Activity> searchActivities(@Param("userId") Long userId, @Param("type") String type, @Param("status") String status, Pageable pageable);

    @Query("SELECT COUNT(a) FROM Activity a WHERE a.isDeleted = false AND a.userId = :userId AND a.createTime > :startTime")
    long countActivitiesByUserSince(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime);

    @Query("SELECT a FROM Activity a WHERE a.isDeleted = false AND a.relatedType = :relatedType AND a.relatedId = :relatedId ORDER BY a.createTime DESC")
    List<Activity> findByRelatedTypeAndRelatedId(@Param("relatedType") String relatedType, @Param("relatedId") Long relatedId);
}

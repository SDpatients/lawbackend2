package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.LoginRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginRecordRepository extends JpaRepository<LoginRecord, Long> {

    Page<LoginRecord> findByUserId(Long userId, Pageable pageable);

    Page<LoginRecord> findByUserAccount(String userAccount, Pageable pageable);

    Page<LoginRecord> findByLoginStatus(String loginStatus, Pageable pageable);

    @Query("SELECT lr FROM LoginRecord lr WHERE lr.userId = :userId AND lr.loginTime BETWEEN :startTime AND :endTime")
    List<LoginRecord> findByUserIdAndTimeRange(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT lr FROM LoginRecord lr WHERE lr.loginStatus = 'FAILED' AND lr.createTime > :startTime")
    List<LoginRecord> findRecentFailedLogins(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT COUNT(lr) FROM LoginRecord lr WHERE lr.userId = :userId AND lr.loginStatus = 'SUCCESS' AND lr.loginTime > :startTime")
    long countSuccessfulLoginsSince(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime);

    @Query("SELECT lr FROM LoginRecord lr WHERE (:userId IS NULL OR lr.userId = :userId) AND (:userAccount IS NULL OR lr.userAccount = :userAccount) AND (:loginStatus IS NULL OR lr.loginStatus = :loginStatus)")
    Page<LoginRecord> searchLoginRecords(@Param("userId") Long userId, @Param("userAccount") String userAccount, @Param("loginStatus") String loginStatus, Pageable pageable);
}

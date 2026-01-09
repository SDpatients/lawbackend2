package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.SmsCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SmsCodeRepository extends JpaRepository<SmsCode, Long> {

    @Query("SELECT s FROM SmsCode s WHERE s.mobile = :mobile AND s.smsType = :smsType AND s.expireTime > :now AND s.usedStatus = '0' ORDER BY s.createTime DESC")
    List<SmsCode> findValidCodes(@Param("mobile") String mobile, @Param("smsType") String smsType, @Param("now") LocalDateTime now);

    @Query("SELECT s FROM SmsCode s WHERE s.mobile = :mobile AND s.smsType = :smsType AND s.expireTime > :now AND s.usedStatus = '0' ORDER BY s.createTime DESC")
    Optional<SmsCode> findLatestValidCode(@Param("mobile") String mobile, @Param("smsType") String smsType, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(s) FROM SmsCode s WHERE s.mobile = :mobile AND s.createTime > :startTime")
    long countRecentCodes(@Param("mobile") String mobile, @Param("startTime") LocalDateTime startTime);

    @Modifying
    @Transactional
    @Query("UPDATE SmsCode s SET s.usedStatus = '1', s.usedTime = :usedTime WHERE s.id = :id")
    int markAsUsed(@Param("id") Long id, @Param("usedTime") LocalDateTime usedTime);

    @Modifying
    @Transactional
    @Query("DELETE FROM SmsCode s WHERE s.expireTime < :expireTime")
    int deleteExpiredCodes(@Param("expireTime") LocalDateTime expireTime);
}

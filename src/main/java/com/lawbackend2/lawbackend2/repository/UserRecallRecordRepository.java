package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.UserRecallRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRecallRecordRepository extends JpaRepository<UserRecallRecord, Long> {

    Optional<UserRecallRecord> findByUserIdAndRecallDate(Long userId, LocalDate recallDate);

    @Modifying
    @Query("UPDATE UserRecallRecord urr SET urr.recallCount = urr.recallCount + 1, urr.updateTime = CURRENT_TIMESTAMP " +
           "WHERE urr.userId = :userId AND urr.recallDate = :recallDate")
    int incrementRecallCount(@Param("userId") Long userId, @Param("recallDate") LocalDate recallDate);

    @Query("SELECT COALESCE(SUM(urr.recallCount), 0) FROM UserRecallRecord urr " +
           "WHERE urr.userId = :userId AND urr.recallDate BETWEEN :startDate AND :endDate")
    int getTotalRecallCountInRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.UserAgreementRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAgreementRecordRepository extends JpaRepository<UserAgreementRecord, Long> {

    List<UserAgreementRecord> findByUserIdAndIsDeletedFalseOrderByAgreeTimeDesc(Long userId);

    Optional<UserAgreementRecord> findTopByUserIdAndAgreementTypeAndIsDeletedFalseOrderByAgreeTimeDesc(
            Long userId, String agreementType);

    @Query("SELECT r FROM UserAgreementRecord r WHERE r.userId = ?1 AND r.agreementType = ?2 AND r.agreementVersion = ?3 AND r.isDeleted = false ORDER BY r.agreeTime DESC")
    Optional<UserAgreementRecord> findLatestByUserIdAndTypeAndVersion(Long userId, String agreementType, String agreementVersion);

    boolean existsByUserIdAndAgreementTypeAndIsDeletedFalse(Long userId, String agreementType);
}
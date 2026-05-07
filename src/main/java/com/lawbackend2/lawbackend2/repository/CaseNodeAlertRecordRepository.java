package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.CaseNodeAlertRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CaseNodeAlertRecordRepository extends JpaRepository<CaseNodeAlertRecord, Long> {

    @Query("SELECT ar FROM CaseNodeAlertRecord ar WHERE ar.nodeInstanceId = :nodeInstanceId AND ar.isDeleted = false ORDER BY ar.alertDate DESC")
    List<CaseNodeAlertRecord> findByNodeInstanceIdOrderByAlertDateDesc(@Param("nodeInstanceId") Long nodeInstanceId);

    @Query("SELECT ar FROM CaseNodeAlertRecord ar WHERE ar.caseId = :caseId AND ar.isDeleted = false ORDER BY ar.alertDate DESC")
    List<CaseNodeAlertRecord> findByCaseIdOrderByAlertDateDesc(@Param("caseId") Long caseId);

    @Query("SELECT ar FROM CaseNodeAlertRecord ar WHERE ar.alertDate = :alertDate AND ar.isDeleted = false")
    List<CaseNodeAlertRecord> findByAlertDate(@Param("alertDate") LocalDate alertDate);

    @Query("SELECT ar FROM CaseNodeAlertRecord ar WHERE ar.isNotified = false AND ar.isDeleted = false")
    List<CaseNodeAlertRecord> findUnnotifiedRecords();

    @Query("SELECT ar FROM CaseNodeAlertRecord ar WHERE ar.recipientId = :recipientId AND ar.isDeleted = false ORDER BY ar.alertDate DESC")
    List<CaseNodeAlertRecord> findByRecipientIdOrderByAlertDateDesc(@Param("recipientId") Long recipientId);

    @Query("SELECT ar FROM CaseNodeAlertRecord ar WHERE ar.nodeInstanceId = :nodeInstanceId AND ar.alertLevel = :alertLevel AND ar.alertDate = :alertDate AND ar.isDeleted = false")
    Optional<CaseNodeAlertRecord> findByNodeInstanceIdAndAlertLevelAndAlertDate(@Param("nodeInstanceId") Long nodeInstanceId, @Param("alertLevel") String alertLevel, @Param("alertDate") LocalDate alertDate);

    @Query("SELECT COUNT(ar) FROM CaseNodeAlertRecord ar WHERE ar.caseId = :caseId AND ar.alertLevel = :alertLevel AND ar.isDeleted = false")
    Long countByCaseIdAndAlertLevel(@Param("caseId") Long caseId, @Param("alertLevel") String alertLevel);
}

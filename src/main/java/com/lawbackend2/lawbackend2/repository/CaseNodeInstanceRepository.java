package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.CaseNodeInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CaseNodeInstanceRepository extends JpaRepository<CaseNodeInstance, Long> {

    @Query("SELECT ni FROM CaseNodeInstance ni WHERE ni.caseId = :caseId AND ni.isDeleted = false ORDER BY ni.sortOrder")
    List<CaseNodeInstance> findByCaseIdOrderBySortOrder(@Param("caseId") Long caseId);

    @Query("SELECT ni FROM CaseNodeInstance ni WHERE ni.caseId = :caseId AND ni.nodeStatus = :nodeStatus AND ni.isDeleted = false ORDER BY ni.sortOrder")
    List<CaseNodeInstance> findByCaseIdAndNodeStatusOrderBySortOrder(@Param("caseId") Long caseId, @Param("nodeStatus") String nodeStatus);

    @Query("SELECT ni FROM CaseNodeInstance ni WHERE ni.caseId = :caseId AND ni.nodeCode = :nodeCode AND ni.isDeleted = false")
    Optional<CaseNodeInstance> findByCaseIdAndNodeCode(@Param("caseId") Long caseId, @Param("nodeCode") String nodeCode);

    @Query("SELECT ni FROM CaseNodeInstance ni WHERE ni.caseId = :caseId AND ni.prevNodeInstanceId = :prevNodeInstanceId AND ni.isDeleted = false")
    List<CaseNodeInstance> findByCaseIdAndPrevNodeInstanceId(@Param("caseId") Long caseId, @Param("prevNodeInstanceId") Long prevNodeInstanceId);

    @Query("SELECT ni FROM CaseNodeInstance ni WHERE ni.nodeStatus = 'IN_PROGRESS' AND ni.isDeleted = false AND ni.deadlineDate <= :deadlineDate ORDER BY ni.deadlineDate")
    List<CaseNodeInstance> findInProgressByDeadlineBefore(@Param("deadlineDate") LocalDate deadlineDate);

    @Query("SELECT ni FROM CaseNodeInstance ni WHERE ni.nodeStatus = 'IN_PROGRESS' AND ni.isDeleted = false AND ni.deadlineDate = :deadlineDate")
    List<CaseNodeInstance> findInProgressByDeadlineDate(@Param("deadlineDate") LocalDate deadlineDate);

    @Query("SELECT ni FROM CaseNodeInstance ni WHERE ni.nodeStatus = 'IN_PROGRESS' AND ni.isDeleted = false AND ni.deadlineDate < :today")
    List<CaseNodeInstance> findOverdueNodes(@Param("today") LocalDate today);

    @Query("SELECT ni FROM CaseNodeInstance ni WHERE ni.responsiblePersonId = :responsiblePersonId AND ni.nodeStatus = 'IN_PROGRESS' AND ni.isDeleted = false ORDER BY ni.deadlineDate")
    List<CaseNodeInstance> findInProgressByResponsiblePersonId(@Param("responsiblePersonId") Long responsiblePersonId);

    @Query("SELECT COUNT(ni) FROM CaseNodeInstance ni WHERE ni.caseId = :caseId AND ni.nodeStatus = :nodeStatus AND ni.isDeleted = false")
    Long countByCaseIdAndNodeStatus(@Param("caseId") Long caseId, @Param("nodeStatus") String nodeStatus);

    @Query("SELECT COUNT(ni) FROM CaseNodeInstance ni WHERE ni.caseId = :caseId AND ni.isDeleted = false")
    Long countByCaseId(@Param("caseId") Long caseId);

    @Modifying
    @Query("DELETE FROM CaseNodeInstance ni WHERE ni.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT ni FROM CaseNodeInstance ni WHERE ni.caseId = :caseId AND ni.alertLevel IN (:alertLevels) AND ni.isDeleted = false ORDER BY ni.deadlineDate")
    List<CaseNodeInstance> findByCaseIdAndAlertLevelIn(@Param("caseId") Long caseId, @Param("alertLevels") List<String> alertLevels);
}

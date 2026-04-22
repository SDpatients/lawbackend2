package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.WorkLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {

    @Query("SELECT wl FROM WorkLog wl WHERE " +
           "(:caseId IS NULL OR wl.caseId = :caseId) " +
           "AND (:workType IS NULL OR wl.workType = :workType) " +
           "AND (:startDate IS NULL OR wl.workDate >= :startDate) " +
           "AND (:endDate IS NULL OR wl.workDate <= :endDate) " +
           "AND (:createUserId IS NULL OR wl.createUserId = :createUserId) " +
           "AND (:status IS NULL OR wl.status = :status)")
    Page<WorkLog> findByConditions(@Param("caseId") Long caseId,
                                    @Param("workType") String workType,
                                    @Param("startDate") java.time.LocalDate startDate,
                                    @Param("endDate") java.time.LocalDate endDate,
                                    @Param("createUserId") Long createUserId,
                                    @Param("status") String status,
                                    Pageable pageable);

    @Modifying
    @Query("DELETE FROM WorkLog wl WHERE wl.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
}

package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.FundOperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FundOperationLogRepository extends JpaRepository<FundOperationLog, Long> {

    @Query("SELECT fol FROM FundOperationLog fol WHERE fol.isDeleted = false " +
           "AND (:caseId IS NULL OR fol.caseId = :caseId) " +
           "AND (:operationType IS NULL OR fol.operationType = :operationType) " +
           "AND (:status IS NULL OR fol.status = :status)")
    Page<FundOperationLog> findByConditions(@Param("caseId") Long caseId,
                                             @Param("operationType") String operationType,
                                             @Param("status") String status,
                                             Pageable pageable);

    @Modifying
    @Query("UPDATE FundOperationLog fol SET fol.isDeleted = true WHERE fol.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
}

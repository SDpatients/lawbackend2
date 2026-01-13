package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.EscrowManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EscrowManagementRepository extends JpaRepository<EscrowManagement, Long> {

    Optional<EscrowManagement> findByEscrowNoAndIsDeleted(String escrowNo, Boolean isDeleted);

    @Query("SELECT em FROM EscrowManagement em WHERE em.isDeleted = false " +
           "AND (:caseId IS NULL OR em.caseId = :caseId) " +
           "AND (:escrowType IS NULL OR em.escrowType = :escrowType) " +
           "AND (:releaseStatus IS NULL OR em.releaseStatus = :releaseStatus)")
    Page<EscrowManagement> findByConditions(@Param("caseId") Long caseId,
                                            @Param("escrowType") String escrowType,
                                            @Param("releaseStatus") String releaseStatus,
                                            Pageable pageable);

    List<EscrowManagement> findByCaseIdAndIsDeleted(Long caseId, Boolean isDeleted);
}
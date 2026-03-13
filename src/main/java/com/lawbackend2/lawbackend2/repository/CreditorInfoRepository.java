package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.CreditorInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditorInfoRepository extends JpaRepository<CreditorInfo, Long>, JpaSpecificationExecutor<CreditorInfo> {
    @Query("SELECT c FROM CreditorInfo c WHERE c.isDeleted = false AND c.caseId = :caseId")
    Page<CreditorInfo> findByCaseId(@Param("caseId") Long caseId, Pageable pageable);

    @Query("SELECT c FROM CreditorInfo c WHERE c.isDeleted = false AND c.creditorType = :creditorType")
    Page<CreditorInfo> findByCreditorType(@Param("creditorType") String creditorType, Pageable pageable);

    @Query("SELECT c FROM CreditorInfo c WHERE c.isDeleted = false AND c.caseId = :caseId AND c.creditorType = :creditorType")
    Page<CreditorInfo> findByCaseIdAndCreditorType(@Param("caseId") Long caseId, @Param("creditorType") String creditorType, Pageable pageable);

    @Modifying
    @Query("UPDATE CreditorInfo c SET c.isDeleted = true WHERE c.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
}

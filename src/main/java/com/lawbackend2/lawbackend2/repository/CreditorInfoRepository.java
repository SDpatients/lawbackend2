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
    Page<CreditorInfo> findByCaseId(Long caseId, Pageable pageable);

    Page<CreditorInfo> findByCreditorType(String creditorType, Pageable pageable);

    Page<CreditorInfo> findByCaseIdAndCreditorType(Long caseId, String creditorType, Pageable pageable);

    @Modifying
    @Query("DELETE FROM CreditorInfo c WHERE c.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
}

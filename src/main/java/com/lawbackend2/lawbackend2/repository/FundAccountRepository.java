package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.FundAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FundAccountRepository extends JpaRepository<FundAccount, Long> {

    @Query("SELECT fa FROM FundAccount fa WHERE fa.isDeleted = false " +
           "AND (:caseId IS NULL OR fa.caseId = :caseId) " +
           "AND (:status IS NULL OR fa.status = :status)")
    Page<FundAccount> findByConditions(@Param("caseId") Long caseId,
                                        @Param("status") String status,
                                        Pageable pageable);

    List<FundAccount> findByCaseIdAndIsDeleted(Long caseId, Boolean isDeleted);
    FundAccount findByAccountName(String accountName);

    @Modifying
    @Query("UPDATE FundAccount fa SET fa.isDeleted = true WHERE fa.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
}

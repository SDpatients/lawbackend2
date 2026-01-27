package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.DebtorEnterprise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DebtorEnterpriseRepository extends JpaRepository<DebtorEnterprise, Long>, JpaSpecificationExecutor<DebtorEnterprise> {

    Optional<DebtorEnterprise> findByUnifiedSocialCreditCode(String unifiedSocialCreditCode);

    Page<DebtorEnterprise> findByCaseId(Long caseId, Pageable pageable);

    @Query("SELECT d FROM DebtorEnterprise d WHERE " +
           "(:caseId IS NULL OR d.caseId = :caseId) AND " +
           "(:enterpriseName IS NULL OR d.enterpriseName LIKE %:enterpriseName%) AND " +
           "(:unifiedSocialCreditCode IS NULL OR d.unifiedSocialCreditCode LIKE %:unifiedSocialCreditCode%) AND " +
           "(:legalRepresentative IS NULL OR d.legalRepresentative LIKE %:legalRepresentative%)")
    Page<DebtorEnterprise> findByConditions(@Param("caseId") Long caseId,
                                           @Param("enterpriseName") String enterpriseName,
                                           @Param("unifiedSocialCreditCode") String unifiedSocialCreditCode,
                                           @Param("legalRepresentative") String legalRepresentative,
                                           Pageable pageable);
    void deleteByCaseId(Long caseId);
}

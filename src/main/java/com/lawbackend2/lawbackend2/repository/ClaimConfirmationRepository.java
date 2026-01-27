package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.ClaimConfirmation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimConfirmationRepository extends JpaRepository<ClaimConfirmation, Long>, JpaSpecificationExecutor<ClaimConfirmation> {
    Optional<ClaimConfirmation> findByClaimRegistrationId(Long claimRegistrationId);

    Page<ClaimConfirmation> findByCaseId(Long caseId, Pageable pageable);

    Page<ClaimConfirmation> findByConfirmationStatus(String confirmationStatus, Pageable pageable);

    Page<ClaimConfirmation> findByHasObjection(Boolean hasObjection, Pageable pageable);

    Page<ClaimConfirmation> findByHasLawsuit(Boolean hasLawsuit, Pageable pageable);

    @Query("SELECT COUNT(c) FROM ClaimConfirmation c WHERE c.caseId = :caseId")
    Long countByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT COUNT(c) FROM ClaimConfirmation c WHERE c.confirmationStatus = :confirmationStatus")
    Long countByConfirmationStatus(@Param("confirmationStatus") String confirmationStatus);
    
    @Query("SELECT COUNT(c) FROM ClaimConfirmation c WHERE c.caseId = :caseId AND c.confirmationStatus = :confirmationStatus")
    Long countByCaseIdAndConfirmationStatus(@Param("caseId") Long caseId, @Param("confirmationStatus") String confirmationStatus);

    @Query("SELECT COUNT(c) FROM ClaimConfirmation c WHERE c.hasObjection = :hasObjection")
    Long countByHasObjection(@Param("hasObjection") Boolean hasObjection);

    @Query("SELECT COUNT(c) FROM ClaimConfirmation c WHERE c.hasLawsuit = :hasLawsuit")
    Long countByHasLawsuit(@Param("hasLawsuit") Boolean hasLawsuit);

    @Query("SELECT c.confirmationStatus, COUNT(c) FROM ClaimConfirmation c WHERE c.caseId = :caseId GROUP BY c.confirmationStatus")
    List<Object[]> countByConfirmationStatusGroupByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT c FROM ClaimConfirmation c WHERE c.caseId = :caseId AND c.hasObjection = true")
    List<ClaimConfirmation> findObjectionsByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT c FROM ClaimConfirmation c WHERE c.caseId = :caseId AND c.hasLawsuit = true")
    List<ClaimConfirmation> findLawsuitsByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT c FROM ClaimConfirmation c WHERE c.caseId = :caseId AND c.confirmationStatus = 'PENDING'")
    List<ClaimConfirmation> findPendingConfirmationsByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT c FROM ClaimConfirmation c WHERE c.caseId = :caseId AND c.confirmationStatus = 'OBJECTION'")
    List<ClaimConfirmation> findObjectionConfirmationsByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT c FROM ClaimConfirmation c WHERE c.caseId = :caseId AND c.lawsuitStatus = 'TRIALING'")
    List<ClaimConfirmation> findTrialingLawsuitsByCaseId(@Param("caseId") Long caseId);
    void deleteByCaseId(Long caseId);
}

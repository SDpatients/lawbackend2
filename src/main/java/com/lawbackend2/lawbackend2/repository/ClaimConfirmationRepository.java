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
    List<ClaimConfirmation> findByClaimRegistrationIdAndIsDeletedFalse(Long claimRegistrationId);

    Page<ClaimConfirmation> findByCaseIdAndIsDeletedFalse(Long caseId, Pageable pageable);

    Page<ClaimConfirmation> findByConfirmationStatusAndIsDeletedFalse(String confirmationStatus, Pageable pageable);

    Page<ClaimConfirmation> findByCaseIdAndConfirmationStatusAndIsDeletedFalse(Long caseId, String confirmationStatus, Pageable pageable);

    @Query("SELECT c FROM ClaimConfirmation c WHERE c.confirmationStatus IN ('IN_PROGRESS', 'CONFIRMED') AND c.isDeleted = false")
    Page<ClaimConfirmation> findInProgressAndConfirmed(Pageable pageable);

    @Query("SELECT c FROM ClaimConfirmation c WHERE c.caseId = :caseId AND c.confirmationStatus IN ('IN_PROGRESS', 'CONFIRMED') AND c.isDeleted = false")
    Page<ClaimConfirmation> findInProgressAndConfirmedByCaseId(@Param("caseId") Long caseId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM ClaimConfirmation c WHERE c.confirmationStatus IN ('IN_PROGRESS', 'CONFIRMED') AND c.isDeleted = false")
    Long countInProgressAndConfirmed();

    @Query("SELECT COUNT(c) FROM ClaimConfirmation c WHERE c.caseId = :caseId AND c.confirmationStatus IN ('IN_PROGRESS', 'CONFIRMED') AND c.isDeleted = false")
    Long countInProgressAndConfirmedByCaseId(@Param("caseId") Long caseId);

    Page<ClaimConfirmation> findByHasObjectionAndIsDeletedFalse(Boolean hasObjection, Pageable pageable);

    Page<ClaimConfirmation> findByHasLawsuitAndIsDeletedFalse(Boolean hasLawsuit, Pageable pageable);

    @Query("SELECT COUNT(c) FROM ClaimConfirmation c WHERE c.caseId = :caseId AND c.isDeleted = false")
    Long countByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT COUNT(c) FROM ClaimConfirmation c WHERE c.confirmationStatus = :confirmationStatus AND c.isDeleted = false")
    Long countByConfirmationStatus(@Param("confirmationStatus") String confirmationStatus);

    @Query("SELECT COUNT(c) FROM ClaimConfirmation c WHERE c.caseId = :caseId AND c.confirmationStatus = :confirmationStatus AND c.isDeleted = false")
    Long countByCaseIdAndConfirmationStatus(@Param("caseId") Long caseId, @Param("confirmationStatus") String confirmationStatus);

    @Query("SELECT COUNT(c) FROM ClaimConfirmation c WHERE c.hasObjection = :hasObjection AND c.isDeleted = false")
    Long countByHasObjection(@Param("hasObjection") Boolean hasObjection);

    @Query("SELECT COUNT(c) FROM ClaimConfirmation c WHERE c.hasLawsuit = :hasLawsuit AND c.isDeleted = false")
    Long countByHasLawsuit(@Param("hasLawsuit") Boolean hasLawsuit);

    @Query("SELECT c.confirmationStatus, COUNT(c) FROM ClaimConfirmation c WHERE c.caseId = :caseId AND c.isDeleted = false GROUP BY c.confirmationStatus")
    List<Object[]> countByConfirmationStatusGroupByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT c FROM ClaimConfirmation c WHERE c.caseId = :caseId AND c.hasObjection = true AND c.isDeleted = false")
    List<ClaimConfirmation> findObjectionsByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT c FROM ClaimConfirmation c WHERE c.caseId = :caseId AND c.hasLawsuit = true AND c.isDeleted = false")
    List<ClaimConfirmation> findLawsuitsByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT c FROM ClaimConfirmation c WHERE c.caseId = :caseId AND c.confirmationStatus = 'PENDING' AND c.isDeleted = false")
    List<ClaimConfirmation> findPendingConfirmationsByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT c FROM ClaimConfirmation c WHERE c.caseId = :caseId AND c.confirmationStatus = 'OBJECTION' AND c.isDeleted = false")
    List<ClaimConfirmation> findObjectionConfirmationsByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT c FROM ClaimConfirmation c WHERE c.caseId = :caseId AND c.lawsuitStatus = 'TRIALING' AND c.isDeleted = false")
    List<ClaimConfirmation> findTrialingLawsuitsByCaseId(@Param("caseId") Long caseId);

    void deleteByCaseId(Long caseId);

    List<ClaimConfirmation> findAllByCreditorNameAndIsDeletedFalse(String creditorName);
}

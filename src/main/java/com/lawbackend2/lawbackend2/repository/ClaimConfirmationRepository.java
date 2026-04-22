package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.ClaimConfirmation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimConfirmationRepository extends JpaRepository<ClaimConfirmation, Long>, JpaSpecificationExecutor<ClaimConfirmation> {
    List<ClaimConfirmation> findByClaimRegistrationId(Long claimRegistrationId);

    Page<ClaimConfirmation> findByCaseId(Long caseId, Pageable pageable);

    Page<ClaimConfirmation> findByConfirmationStatus(String confirmationStatus, Pageable pageable);

    Page<ClaimConfirmation> findByCaseIdAndConfirmationStatus(Long caseId, String confirmationStatus, Pageable pageable);

    @Query("SELECT c FROM ClaimConfirmation c WHERE c.confirmationStatus IN ('IN_PROGRESS', 'CONFIRMED')")
    Page<ClaimConfirmation> findInProgressAndConfirmed(Pageable pageable);

    @Query("SELECT c FROM ClaimConfirmation c WHERE c.caseId = :caseId AND c.confirmationStatus IN ('IN_PROGRESS', 'CONFIRMED')")
    Page<ClaimConfirmation> findInProgressAndConfirmedByCaseId(@Param("caseId") Long caseId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM ClaimConfirmation c WHERE c.confirmationStatus IN ('IN_PROGRESS', 'CONFIRMED')")
    Long countInProgressAndConfirmed();

    @Query("SELECT COUNT(c) FROM ClaimConfirmation c WHERE c.caseId = :caseId AND c.confirmationStatus IN ('IN_PROGRESS', 'CONFIRMED')")
    Long countInProgressAndConfirmedByCaseId(@Param("caseId") Long caseId);

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

    @Modifying
    @Query("DELETE FROM ClaimConfirmation c WHERE c.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);

    List<ClaimConfirmation> findAllByCreditorName(String creditorName);

    @Query(value = "SELECT c.case_id, SUM(COALESCE(NULLIF(c.final_confirmed_amount, 0), r.confirmed_total_amount, 0)) " +
            "FROM tb_claim_confirmation c " +
            "LEFT JOIN tb_claim_review r ON c.claim_registration_id = r.claim_registration_id " +
            "GROUP BY c.case_id " +
            "ORDER BY SUM(COALESCE(NULLIF(c.final_confirmed_amount, 0), r.confirmed_total_amount, 0)) DESC", nativeQuery = true)
    List<Object[]> sumFinalConfirmedAmountByCaseIdGroup();

    @Query(value = "SELECT c.creditor_name, SUM(COALESCE(NULLIF(c.final_confirmed_amount, 0), r.confirmed_total_amount, 0)) " +
            "FROM tb_claim_confirmation c " +
            "LEFT JOIN tb_claim_review r ON c.claim_registration_id = r.claim_registration_id " +
            "GROUP BY c.creditor_name " +
            "ORDER BY SUM(COALESCE(NULLIF(c.final_confirmed_amount, 0), r.confirmed_total_amount, 0)) DESC", nativeQuery = true)
    List<Object[]> findTopConfirmationsByAmount();

    @Query(value = "SELECT c.creditor_name, SUM(COALESCE(NULLIF(c.final_confirmed_amount, 0), r.confirmed_total_amount, 0)) " +
            "FROM tb_claim_confirmation c " +
            "LEFT JOIN tb_claim_review r ON c.claim_registration_id = r.claim_registration_id " +
            "GROUP BY c.creditor_name " +
            "ORDER BY SUM(COALESCE(NULLIF(c.final_confirmed_amount, 0), r.confirmed_total_amount, 0)) DESC", nativeQuery = true)
    List<Object[]> findTopConfirmationsByAmount(Pageable pageable);

    @Query(value = "SELECT c.case_id, SUM(COALESCE(NULLIF(c.final_confirmed_amount, 0), r.confirmed_total_amount, 0)) " +
            "FROM tb_claim_confirmation c " +
            "LEFT JOIN tb_claim_review r ON c.claim_registration_id = r.claim_registration_id " +
            "WHERE c.case_id IN (SELECT bc.id FROM tb_bankrupt_case bc WHERE bc.create_user_id = :userId) " +
            "GROUP BY c.case_id " +
            "ORDER BY SUM(COALESCE(NULLIF(c.final_confirmed_amount, 0), r.confirmed_total_amount, 0)) DESC", nativeQuery = true)
    List<Object[]> sumFinalConfirmedAmountByCaseIdGroupByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT c.creditor_name, SUM(COALESCE(NULLIF(c.final_confirmed_amount, 0), r.confirmed_total_amount, 0)) " +
            "FROM tb_claim_confirmation c " +
            "LEFT JOIN tb_claim_review r ON c.claim_registration_id = r.claim_registration_id " +
            "WHERE c.case_id IN (SELECT bc.id FROM tb_bankrupt_case bc WHERE bc.create_user_id = :userId) " +
            "GROUP BY c.creditor_name " +
            "ORDER BY SUM(COALESCE(NULLIF(c.final_confirmed_amount, 0), r.confirmed_total_amount, 0)) DESC", nativeQuery = true)
    List<Object[]> findTopConfirmationsByAmountByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT c.creditor_name, SUM(COALESCE(NULLIF(c.final_confirmed_amount, 0), r.confirmed_total_amount, 0)) " +
            "FROM tb_claim_confirmation c " +
            "LEFT JOIN tb_claim_review r ON c.claim_registration_id = r.claim_registration_id " +
            "WHERE c.case_id IN (SELECT bc.id FROM tb_bankrupt_case bc WHERE bc.create_user_id = :userId) " +
            "GROUP BY c.creditor_name " +
            "ORDER BY SUM(COALESCE(NULLIF(c.final_confirmed_amount, 0), r.confirmed_total_amount, 0)) DESC", nativeQuery = true)
    List<Object[]> findTopConfirmationsByAmountByUserId(@Param("userId") Long userId, Pageable pageable);

    Page<ClaimConfirmation> findAll(Pageable pageable);

    @Query("SELECT COUNT(c) FROM ClaimConfirmation c")
    Long countAllActive();

    // 兼容方法，等同于 findAll
    default Page<ClaimConfirmation> findAllActive(Pageable pageable) {
        return findAll(pageable);
    }
}

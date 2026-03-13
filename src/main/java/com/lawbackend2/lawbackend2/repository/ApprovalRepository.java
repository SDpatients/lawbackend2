package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    @Query("SELECT a FROM Approval a WHERE a.isDeleted = false AND a.caseId = :caseId")
    List<Approval> findByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT a FROM Approval a WHERE a.isDeleted = false AND a.lawyerId = :lawyerId")
    List<Approval> findByLawyerId(@Param("lawyerId") Long lawyerId);

    @Query("SELECT a FROM Approval a WHERE a.isDeleted = false AND a.approvalStatus = :approvalStatus")
    List<Approval> findByApprovalStatus(@Param("approvalStatus") String approvalStatus);

    @Query("SELECT a FROM Approval a WHERE a.isDeleted = false AND a.approverId = :approverId")
    List<Approval> findByApproverId(@Param("approverId") Long approverId);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Approval a WHERE a.isDeleted = false AND a.caseId = :caseId AND a.approvalType = :approvalType")
    boolean existsByCaseIdAndApprovalType(@Param("caseId") Long caseId, @Param("approvalType") String approvalType);

    @Query("SELECT a FROM Approval a WHERE a.isDeleted = false AND a.caseId = :caseId AND a.approvalType = :approvalType")
    Optional<Approval> findByCaseIdAndApprovalType(@Param("caseId") Long caseId, @Param("approvalType") String approvalType);

    @Modifying
    @Query("UPDATE Approval a SET a.isDeleted = true WHERE a.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT COUNT(a) FROM Approval a WHERE a.isDeleted = false AND a.approvalResult IS NULL AND a.approvalType = :approvalType")
    long countByApprovalResultIsNullAndApprovalType(@Param("approvalType") String approvalType);

    @Query("SELECT a FROM Approval a WHERE a.isDeleted = false AND a.approvalResult IS NULL AND a.approvalType = :approvalType")
    List<Approval> findByApprovalResultIsNullAndApprovalType(@Param("approvalType") String approvalType);

    @Query("SELECT a FROM Approval a WHERE a.isDeleted = false AND a.approvalResult IS NULL")
    List<Approval> findByApprovalResultIsNull();
}

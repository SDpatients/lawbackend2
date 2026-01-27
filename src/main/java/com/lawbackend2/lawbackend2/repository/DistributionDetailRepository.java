package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.DistributionDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistributionDetailRepository extends JpaRepository<DistributionDetail, Long> {

    @Query("SELECT dd FROM DistributionDetail dd WHERE dd.isDeleted = false " +
           "AND (:distributionExecutionId IS NULL OR dd.distributionExecutionId = :distributionExecutionId) " +
           "AND (:caseId IS NULL OR dd.caseId = :caseId) " +
           "AND (:creditorType IS NULL OR dd.creditorType = :creditorType) " +
           "AND (:paymentStatus IS NULL OR dd.paymentStatus = :paymentStatus)")
    Page<DistributionDetail> findByConditions(@Param("distributionExecutionId") Long distributionExecutionId,
                                            @Param("caseId") Long caseId,
                                            @Param("creditorType") String creditorType,
                                            @Param("paymentStatus") String paymentStatus,
                                            Pageable pageable);

    List<DistributionDetail> findByDistributionExecutionIdAndIsDeleted(Long distributionExecutionId, Boolean isDeleted);

    List<DistributionDetail> findByCaseIdAndIsDeleted(Long caseId, Boolean isDeleted);
    void deleteByCaseId(Long caseId);
}
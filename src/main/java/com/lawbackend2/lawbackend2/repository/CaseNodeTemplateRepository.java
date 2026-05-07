package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.CaseNodeTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseNodeTemplateRepository extends JpaRepository<CaseNodeTemplate, Long> {

    @Query("SELECT t FROM CaseNodeTemplate t WHERE t.caseType = :caseType AND t.status = 'ACTIVE' AND t.isDeleted = false ORDER BY t.sortOrder")
    List<CaseNodeTemplate> findActiveByCaseTypeOrderBySortOrder(@Param("caseType") String caseType);

    @Query("SELECT t FROM CaseNodeTemplate t WHERE t.caseType IN (:caseTypes) AND t.status = 'ACTIVE' AND t.isDeleted = false ORDER BY t.sortOrder")
    List<CaseNodeTemplate> findActiveByCaseTypesOrderBySortOrder(@Param("caseTypes") List<String> caseTypes);

    @Query("SELECT t FROM CaseNodeTemplate t WHERE t.nodeCode = :nodeCode AND t.caseType = :caseType AND t.status = 'ACTIVE' AND t.isDeleted = false")
    Optional<CaseNodeTemplate> findByNodeCodeAndCaseType(@Param("nodeCode") String nodeCode, @Param("caseType") String caseType);

    @Query("SELECT t FROM CaseNodeTemplate t WHERE t.status = 'ACTIVE' AND t.isDeleted = false ORDER BY t.caseType, t.sortOrder")
    List<CaseNodeTemplate> findAllActiveOrderByCaseTypeAndSortOrder();

    @Query("SELECT t FROM CaseNodeTemplate t WHERE t.caseStage = :caseStage AND t.status = 'ACTIVE' AND t.isDeleted = false ORDER BY t.sortOrder")
    List<CaseNodeTemplate> findActiveByCaseStageOrderBySortOrder(@Param("caseStage") String caseStage);
}

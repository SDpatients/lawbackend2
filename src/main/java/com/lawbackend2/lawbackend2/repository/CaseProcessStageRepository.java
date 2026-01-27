package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.CaseProcessStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseProcessStageRepository extends JpaRepository<CaseProcessStage, Long> {

    List<CaseProcessStage> findByCaseId(Long caseId);

    List<CaseProcessStage> findByCaseIdAndStageNum(Long caseId, Integer stageNum);

    List<CaseProcessStage> findByCaseIdAndModuleCode(Long caseId, String moduleCode);

    @Query("SELECT c FROM CaseProcessStage c WHERE c.caseId = :caseId AND c.isDeleted = false")
    List<CaseProcessStage> findByCaseIdAndNotDeleted(@Param("caseId") Long caseId);

    @Query("SELECT c FROM CaseProcessStage c WHERE c.caseId = :caseId AND c.stageNum = :stageNum AND c.isDeleted = false")
    List<CaseProcessStage> findByCaseIdAndStageNumAndNotDeleted(@Param("caseId") Long caseId, @Param("stageNum") Integer stageNum);

    @Query("SELECT c FROM CaseProcessStage c WHERE c.caseId = :caseId AND c.moduleCode = :moduleCode AND c.isDeleted = false")
    List<CaseProcessStage> findByCaseIdAndModuleCodeAndNotDeleted(@Param("caseId") Long caseId, @Param("moduleCode") String moduleCode);

    @Query("SELECT c FROM CaseProcessStage c WHERE c.caseId = :caseId AND c.stageNum = :stageNum AND c.moduleCode = :moduleCode AND c.isDeleted = false")
    List<CaseProcessStage> findByCaseIdAndStageNumAndModuleCodeAndNotDeleted(@Param("caseId") Long caseId, @Param("stageNum") Integer stageNum, @Param("moduleCode") String moduleCode);
    void deleteByCaseId(Long caseId);
}

package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.CaseProcessStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseProcessStageRepository extends JpaRepository<CaseProcessStage, Long> {

    @Query("SELECT c FROM CaseProcessStage c WHERE c.isDeleted = false AND c.caseId = :caseId")
    List<CaseProcessStage> findByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT c FROM CaseProcessStage c WHERE c.isDeleted = false AND c.caseId = :caseId AND c.stageNum = :stageNum")
    List<CaseProcessStage> findByCaseIdAndStageNum(@Param("caseId") Long caseId, @Param("stageNum") Integer stageNum);

    @Query("SELECT c FROM CaseProcessStage c WHERE c.isDeleted = false AND c.caseId = :caseId AND c.moduleCode = :moduleCode")
    List<CaseProcessStage> findByCaseIdAndModuleCode(@Param("caseId") Long caseId, @Param("moduleCode") String moduleCode);

    @Query("SELECT c FROM CaseProcessStage c WHERE c.isDeleted = false AND c.caseId = :caseId")
    List<CaseProcessStage> findByCaseIdAndNotDeleted(@Param("caseId") Long caseId);

    @Query("SELECT c FROM CaseProcessStage c WHERE c.isDeleted = false AND c.caseId = :caseId AND c.stageNum = :stageNum")
    List<CaseProcessStage> findByCaseIdAndStageNumAndNotDeleted(@Param("caseId") Long caseId, @Param("stageNum") Integer stageNum);

    @Query("SELECT c FROM CaseProcessStage c WHERE c.isDeleted = false AND c.caseId = :caseId AND c.moduleCode = :moduleCode")
    List<CaseProcessStage> findByCaseIdAndModuleCodeAndNotDeleted(@Param("caseId") Long caseId, @Param("moduleCode") String moduleCode);

    @Query("SELECT c FROM CaseProcessStage c WHERE c.isDeleted = false AND c.caseId = :caseId AND c.stageNum = :stageNum AND c.moduleCode = :moduleCode")
    List<CaseProcessStage> findByCaseIdAndStageNumAndModuleCodeAndNotDeleted(@Param("caseId") Long caseId, @Param("stageNum") Integer stageNum, @Param("moduleCode") String moduleCode);

    @Modifying
    @Query("UPDATE CaseProcessStage c SET c.isDeleted = true WHERE c.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
}

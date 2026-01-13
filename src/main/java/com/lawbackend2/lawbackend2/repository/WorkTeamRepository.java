package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.WorkTeam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkTeamRepository extends JpaRepository<WorkTeam, Long> {

    @Query("SELECT wt FROM WorkTeam wt WHERE wt.isDeleted = false " +
           "AND (:caseId IS NULL OR wt.caseId = :caseId) " +
           "AND (:status IS NULL OR wt.status = :status) " +
           "AND (:teamName IS NULL OR wt.teamName LIKE %:teamName%) " +
           "AND (:teamLeaderId IS NULL OR wt.teamLeaderId = :teamLeaderId)")
    Page<WorkTeam> findByConditions(@Param("caseId") Long caseId,
                                     @Param("status") String status,
                                     @Param("teamName") String teamName,
                                     @Param("teamLeaderId") Long teamLeaderId,
                                     Pageable pageable);

    @Query("SELECT wt FROM WorkTeam wt WHERE wt.isDeleted = false AND wt.id = :teamId")
    WorkTeam findWorkTeamById(@Param("teamId") Long teamId);
}

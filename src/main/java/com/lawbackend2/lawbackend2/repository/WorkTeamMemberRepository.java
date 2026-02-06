package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.WorkTeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkTeamMemberRepository extends JpaRepository<WorkTeamMember, Long> {

    @Query("SELECT wtm FROM WorkTeamMember wtm WHERE wtm.isDeleted = false " +
           "AND wtm.teamId = :teamId " +
           "AND wtm.isActive = 1")
    List<WorkTeamMember> findByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT wtm FROM WorkTeamMember wtm WHERE wtm.isDeleted = false " +
           "AND wtm.id = :memberId " +
           "AND wtm.isActive = 1")
    WorkTeamMember findMemberById(@Param("memberId") Long memberId);

    @Query("SELECT wtm FROM WorkTeamMember wtm WHERE wtm.isDeleted = false " +
           "AND wtm.userId = :userId " +
           "AND wtm.isActive = 1")
    List<WorkTeamMember> findByUserId(@Param("userId") Long userId);

    @Query("SELECT wtm FROM WorkTeamMember wtm WHERE wtm.isDeleted = false " +
           "AND wtm.caseId = :caseId " +
           "AND wtm.isActive = 1")
    List<WorkTeamMember> findByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT wtm FROM WorkTeamMember wtm WHERE wtm.isDeleted = false " +
           "AND wtm.caseId = :caseId " +
           "AND wtm.userId = :userId " +
           "AND wtm.isActive = 1")
    List<WorkTeamMember> findByCaseIdAndUserId(@Param("caseId") Long caseId, @Param("userId") Long userId);

    @Query("SELECT DISTINCT wtm.caseId FROM WorkTeamMember wtm WHERE wtm.isDeleted = false " +
           "AND wtm.userId = :userId " +
           "AND wtm.isActive = 1 " +
           "AND wtm.caseId IS NOT NULL")
    List<Long> findCaseIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT wtm.teamId FROM WorkTeamMember wtm WHERE wtm.isDeleted = false " +
           "AND wtm.userId = :userId " +
           "AND wtm.isActive = 1 " +
           "AND wtm.teamId IS NOT NULL")
    List<Long> findTeamIdsByUserId(@Param("userId") Long userId);
}

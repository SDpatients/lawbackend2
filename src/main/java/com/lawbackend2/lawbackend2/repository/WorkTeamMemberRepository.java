package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.WorkTeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkTeamMemberRepository extends JpaRepository<WorkTeamMember, Long> {

    @Query("SELECT wtm FROM WorkTeamMember wtm WHERE " +
           "wtm.teamId = :teamId " +
           "AND wtm.isActive = 1")
    List<WorkTeamMember> findByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT wtm FROM WorkTeamMember wtm WHERE " +
           "wtm.id = :memberId " +
           "AND wtm.isActive = 1")
    WorkTeamMember findMemberById(@Param("memberId") Long memberId);

    @Query("SELECT wtm FROM WorkTeamMember wtm WHERE " +
           "wtm.userId = :userId " +
           "AND wtm.isActive = 1")
    List<WorkTeamMember> findByUserId(@Param("userId") Long userId);

    @Query("SELECT wtm FROM WorkTeamMember wtm WHERE " +
           "wtm.caseId = :caseId " +
           "AND wtm.isActive = 1")
    List<WorkTeamMember> findByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT wtm FROM WorkTeamMember wtm WHERE " +
           "wtm.caseId = :caseId " +
           "AND wtm.userId = :userId " +
           "AND wtm.isActive = 1")
    List<WorkTeamMember> findByCaseIdAndUserId(@Param("caseId") Long caseId, @Param("userId") Long userId);

    @Query("SELECT DISTINCT wtm.caseId FROM WorkTeamMember wtm WHERE " +
           "wtm.userId = :userId " +
           "AND wtm.isActive = 1 " +
           "AND wtm.caseId IS NOT NULL")
    List<Long> findCaseIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT wtm.teamId FROM WorkTeamMember wtm WHERE " +
           "wtm.userId = :userId " +
           "AND wtm.isActive = 1 " +
           "AND wtm.teamId IS NOT NULL")
    List<Long> findTeamIdsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM WorkTeamMember wtm WHERE wtm.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT wtm.userId, wtm.teamRole, COUNT(DISTINCT wtm.caseId) " +
           "FROM WorkTeamMember wtm " +
           "JOIN WorkTeam wt ON wtm.teamId = wt.id " +
           "JOIN BankruptCase bc ON wtm.caseId = bc.id " +
           "WHERE wtm.isActive = 1 " +
           "AND wtm.teamRole IN ('LEADER', 'ADMIN') " +
           "AND YEAR(bc.acceptanceDate) = :year " +
           "GROUP BY wtm.userId, wtm.teamRole")
    List<Object[]> countCasesByUserAndRoleInYear(@Param("year") Integer year);

    @Query("SELECT wtm.userId, wtm.teamRole, COUNT(DISTINCT wtm.caseId) " +
           "FROM WorkTeamMember wtm " +
           "JOIN WorkTeam wt ON wtm.teamId = wt.id " +
           "JOIN BankruptCase bc ON wtm.caseId = bc.id " +
           "WHERE wtm.isActive = 1 " +
           "AND wtm.teamRole IN ('LEADER', 'ADMIN') " +
           "GROUP BY wtm.userId, wtm.teamRole")
    List<Object[]> countCasesByUserAndRole();
}

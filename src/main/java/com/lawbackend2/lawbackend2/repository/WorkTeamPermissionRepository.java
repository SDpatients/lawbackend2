package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.WorkTeamPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkTeamPermissionRepository extends JpaRepository<WorkTeamPermission, Long> {

    @Query("SELECT wtp FROM WorkTeamPermission wtp WHERE " +
           "wtp.teamMemberId = :teamMemberId " +
           "AND wtp.isAllowed = 1")
    List<WorkTeamPermission> findByTeamMemberId(@Param("teamMemberId") Long teamMemberId);

    @Query("SELECT wtp FROM WorkTeamPermission wtp WHERE " +
           "wtp.teamMemberId = :teamMemberId")
    List<WorkTeamPermission> findAllByTeamMemberId(@Param("teamMemberId") Long teamMemberId);

    @Query("SELECT wtp FROM WorkTeamPermission wtp WHERE " +
           "wtp.teamMemberId = :teamMemberId " +
           "AND wtp.moduleType = :moduleType " +
           "AND wtp.permissionType = :permissionType")
    WorkTeamPermission findByMemberAndModuleAndPermission(@Param("teamMemberId") Long teamMemberId,
                                                          @Param("moduleType") String moduleType,
                                                          @Param("permissionType") String permissionType);

    @Modifying
    @Query("DELETE FROM WorkTeamPermission wtp WHERE wtp.teamMemberId IN (SELECT wtm.id FROM WorkTeamMember wtm WHERE wtm.caseId = :caseId)")
    void deleteByCaseId(@Param("caseId") Long caseId);

    @Modifying
    @Query("DELETE FROM WorkTeamPermission wtp WHERE wtp.teamMemberId = :teamMemberId")
    void deleteByTeamMemberId(@Param("teamMemberId") Long teamMemberId);
}

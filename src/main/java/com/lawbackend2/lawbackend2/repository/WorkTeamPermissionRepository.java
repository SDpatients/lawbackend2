package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.WorkTeamPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkTeamPermissionRepository extends JpaRepository<WorkTeamPermission, Long> {

    @Query("SELECT wtp FROM WorkTeamPermission wtp WHERE wtp.isDeleted = false " +
           "AND wtp.teamMemberId = :teamMemberId " +
           "AND wtp.isAllowed = 1")
    List<WorkTeamPermission> findByTeamMemberId(@Param("teamMemberId") Long teamMemberId);
}

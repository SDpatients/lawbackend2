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
}

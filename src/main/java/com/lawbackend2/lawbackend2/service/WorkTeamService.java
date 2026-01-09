package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.AssignWorkTeamPermissionsRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamMemberCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamMemberPermissionRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamUpdateRequest;
import com.lawbackend2.lawbackend2.entity.WorkTeam;
import com.lawbackend2.lawbackend2.entity.WorkTeamMember;
import com.lawbackend2.lawbackend2.entity.WorkTeamPermission;

import java.util.List;

public interface WorkTeamService {

    Long createWorkTeam(WorkTeamCreateRequest request);

    PageResult<WorkTeam> getWorkTeamList(Integer pageNum, Integer pageSize, Long caseId, String status, String teamName, Long teamLeaderId);

    WorkTeam getWorkTeamDetail(Long teamId);

    void updateWorkTeam(Long teamId, WorkTeamUpdateRequest request);

    void deleteWorkTeam(Long teamId);

    Long addWorkTeamMember(Long teamId, WorkTeamMemberCreateRequest request);

    List<WorkTeamMember> getWorkTeamMembers(Long teamId);

    List<WorkTeamPermission> getWorkTeamMemberPermissions(Long memberId);

    void updateWorkTeamMemberPermission(Long memberId, WorkTeamMemberPermissionRequest request);

    void assignPermissionsToMember(Long memberId, AssignWorkTeamPermissionsRequest request);
}

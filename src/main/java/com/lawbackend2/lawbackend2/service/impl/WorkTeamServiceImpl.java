package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.AssignWorkTeamPermissionsRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamMemberCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamMemberPermissionRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamUpdateRequest;
import com.lawbackend2.lawbackend2.entity.WorkTeam;
import com.lawbackend2.lawbackend2.entity.WorkTeamMember;
import com.lawbackend2.lawbackend2.entity.WorkTeamPermission;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.WorkTeamMemberRepository;
import com.lawbackend2.lawbackend2.repository.WorkTeamPermissionRepository;
import com.lawbackend2.lawbackend2.repository.WorkTeamRepository;
import com.lawbackend2.lawbackend2.service.WorkTeamService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WorkTeamServiceImpl implements WorkTeamService {

    private final WorkTeamRepository workTeamRepository;
    private final WorkTeamMemberRepository workTeamMemberRepository;
    private final WorkTeamPermissionRepository workTeamPermissionRepository;

    public WorkTeamServiceImpl(WorkTeamRepository workTeamRepository,
                             WorkTeamMemberRepository workTeamMemberRepository,
                             WorkTeamPermissionRepository workTeamPermissionRepository) {
        this.workTeamRepository = workTeamRepository;
        this.workTeamMemberRepository = workTeamMemberRepository;
        this.workTeamPermissionRepository = workTeamPermissionRepository;
    }

    @Override
    public Long createWorkTeam(WorkTeamCreateRequest request) {
        WorkTeam workTeam = new WorkTeam();
        BeanUtils.copyProperties(request, workTeam);
        workTeam.setStatus("ACTIVE");

        WorkTeam saved = workTeamRepository.save(workTeam);
        return saved.getId();
    }

    @Override
    public PageResult<WorkTeam> getWorkTeamList(Integer pageNum, Integer pageSize, Long caseId, String status, String teamName, Long teamLeaderId) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<WorkTeam> page = workTeamRepository.findByConditions(caseId, status, teamName, teamLeaderId, pageable);

        PageResult<WorkTeam> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public WorkTeam getWorkTeamDetail(Long teamId) {
        return workTeamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException("工作团队不存在"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWorkTeam(Long teamId, WorkTeamUpdateRequest request) {
        WorkTeam workTeam = getWorkTeamDetail(teamId);

        if (request.getTeamName() != null) {
            workTeam.setTeamName(request.getTeamName());
        }
        if (request.getTeamDescription() != null) {
            workTeam.setTeamDescription(request.getTeamDescription());
        }
        if (request.getStatus() != null) {
            workTeam.setStatus(request.getStatus());
        }

        workTeamRepository.save(workTeam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorkTeam(Long teamId) {
        if (!workTeamRepository.existsById(teamId)) {
            throw new BusinessException("工作团队不存在");
        }
        workTeamRepository.deleteById(teamId);
    }

    @Override
    public Long addWorkTeamMember(Long teamId, WorkTeamMemberCreateRequest request) {
        WorkTeam workTeam = getWorkTeamDetail(teamId);

        WorkTeamMember member = new WorkTeamMember();
        BeanUtils.copyProperties(request, member);
        member.setTeamId(teamId);
        member.setIsActive(1);
        member.setStatus("ACTIVE");

        WorkTeamMember saved = workTeamMemberRepository.save(member);
        return saved.getId();
    }

    @Override
    public List<WorkTeamMember> getWorkTeamMembers(Long teamId) {
        return workTeamMemberRepository.findByTeamId(teamId);
    }

    @Override
    public List<WorkTeamPermission> getWorkTeamMemberPermissions(Long memberId) {
        return workTeamPermissionRepository.findByTeamMemberId(memberId);
    }

    @Override
    public void updateWorkTeamMemberPermission(Long memberId, WorkTeamMemberPermissionRequest request) {
        WorkTeamMember member = workTeamMemberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("团队成员不存在"));

        List<WorkTeamPermission> existingPermissions = workTeamPermissionRepository.findByTeamMemberId(memberId);

        for (WorkTeamPermission permission : existingPermissions) {
            permission.setIsAllowed("ADMIN".equals(request.getPermissionLevel()) ? 1 : 0);
            workTeamPermissionRepository.save(permission);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissionsToMember(Long memberId, AssignWorkTeamPermissionsRequest request) {
        WorkTeamMember member = workTeamMemberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("团队成员不存在"));

        for (AssignWorkTeamPermissionsRequest.WorkTeamPermissionItem item : request.getPermissions()) {
            WorkTeamPermission permission = new WorkTeamPermission();
            permission.setTeamMemberId(memberId);
            permission.setModuleType(item.getModuleType());
            permission.setPermissionType(item.getPermissionType());
            permission.setIsAllowed(item.getIsAllowed());
            permission.setStatus("ACTIVE");
            workTeamPermissionRepository.save(permission);
        }
    }
}

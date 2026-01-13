package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.AssignWorkTeamPermissionsRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamMemberCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamMemberPermissionRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.WorkTeamDetailResponse;
import com.lawbackend2.lawbackend2.dto.response.WorkTeamMemberDetailResponse;
import com.lawbackend2.lawbackend2.dto.response.WorkTeamMemberResponse;
import com.lawbackend2.lawbackend2.dto.response.WorkTeamPermissionResponse;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.entity.WorkTeam;
import com.lawbackend2.lawbackend2.entity.WorkTeamMember;
import com.lawbackend2.lawbackend2.entity.WorkTeamPermission;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkTeamServiceImpl implements WorkTeamService {

    private final WorkTeamRepository workTeamRepository;
    private final WorkTeamMemberRepository workTeamMemberRepository;
    private final WorkTeamPermissionRepository workTeamPermissionRepository;
    private final UserRepository userRepository;
    private final BankruptCaseRepository bankruptCaseRepository;

    public WorkTeamServiceImpl(WorkTeamRepository workTeamRepository,
                             WorkTeamMemberRepository workTeamMemberRepository,
                             WorkTeamPermissionRepository workTeamPermissionRepository,
                             UserRepository userRepository,
                             BankruptCaseRepository bankruptCaseRepository) {
        this.workTeamRepository = workTeamRepository;
        this.workTeamMemberRepository = workTeamMemberRepository;
        this.workTeamPermissionRepository = workTeamPermissionRepository;
        this.userRepository = userRepository;
        this.bankruptCaseRepository = bankruptCaseRepository;
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
    public WorkTeamDetailResponse getWorkTeamDetailWithMembers(Long teamId) {
        WorkTeam workTeam = workTeamRepository.findWorkTeamById(teamId);
        if (workTeam == null) {
            throw new BusinessException("工作团队不存在");
        }

        WorkTeamDetailResponse response = WorkTeamDetailResponse.builder()
                .id(workTeam.getId())
                .teamName(workTeam.getTeamName())
                .teamLeaderId(workTeam.getTeamLeaderId())
                .caseId(workTeam.getCaseId())
                .teamDescription(workTeam.getTeamDescription())
                .status(workTeam.getStatus())
                .createTime(workTeam.getCreateTime())
                .updateTime(workTeam.getUpdateTime())
                .createUserId(workTeam.getCreateUserId())
                .updateUserId(workTeam.getUpdateUserId())
                .build();

        if (workTeam.getTeamLeaderId() != null) {
            User leader = userRepository.findById(workTeam.getTeamLeaderId()).orElse(null);
            if (leader != null) {
                response.setTeamLeaderName(leader.getRealName());
            }
        }

        if (workTeam.getCaseId() != null) {
            BankruptCase bankruptCase = bankruptCaseRepository.findById(workTeam.getCaseId()).orElse(null);
            if (bankruptCase != null) {
                response.setCaseName(bankruptCase.getCaseName());
            }
        }

        List<WorkTeamMember> members = workTeamMemberRepository.findByTeamId(teamId);
        List<WorkTeamMemberResponse> memberResponses = members.stream()
                .map(this::convertToMemberResponse)
                .collect(Collectors.toList());
        response.setMembers(memberResponses);

        return response;
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
    public List<WorkTeamMemberResponse> getWorkTeamMembers(Long teamId) {
        List<WorkTeamMember> members = workTeamMemberRepository.findByTeamId(teamId);
        return members.stream()
                .map(this::convertToMemberResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WorkTeamMemberDetailResponse getWorkTeamMemberDetail(Long memberId) {
        WorkTeamMember member = workTeamMemberRepository.findMemberById(memberId);
        if (member == null) {
            throw new BusinessException("团队成员不存在");
        }

        WorkTeamMemberDetailResponse response = WorkTeamMemberDetailResponse.builder()
                .id(member.getId())
                .teamId(member.getTeamId())
                .caseId(member.getCaseId())
                .userId(member.getUserId())
                .teamRole(member.getTeamRole())
                .permissionLevel(member.getPermissionLevel())
                .isActive(member.getIsActive())
                .status(member.getStatus())
                .createTime(member.getCreateTime())
                .updateTime(member.getUpdateTime())
                .createUserId(member.getCreateUserId())
                .updateUserId(member.getUpdateUserId())
                .build();

        if (member.getTeamId() != null) {
            WorkTeam workTeam = workTeamRepository.findById(member.getTeamId()).orElse(null);
            if (workTeam != null) {
                response.setTeamName(workTeam.getTeamName());
            }
        }

        if (member.getCaseId() != null) {
            BankruptCase bankruptCase = bankruptCaseRepository.findById(member.getCaseId()).orElse(null);
            if (bankruptCase != null) {
                response.setCaseName(bankruptCase.getCaseName());
            }
        }

        if (member.getUserId() != null) {
            User user = userRepository.findById(member.getUserId()).orElse(null);
            if (user != null) {
                response.setUserName(user.getUsername());
                response.setUserRealName(user.getRealName());
            }
        }

        List<WorkTeamPermission> permissions = workTeamPermissionRepository.findAllByTeamMemberId(memberId);
        List<WorkTeamPermissionResponse> permissionResponses = permissions.stream()
                .map(this::convertToPermissionResponse)
                .collect(Collectors.toList());
        response.setPermissions(permissionResponses);

        return response;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeWorkTeamMember(Long memberId) {
        WorkTeamMember member = workTeamMemberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("团队成员不存在"));
        
        workTeamMemberRepository.delete(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeWorkTeamMemberPermission(Long permissionId) {
        if (!workTeamPermissionRepository.existsById(permissionId)) {
            throw new BusinessException("权限不存在");
        }
        workTeamPermissionRepository.deleteById(permissionId);
    }

    private WorkTeamMemberResponse convertToMemberResponse(WorkTeamMember member) {
        WorkTeamMemberResponse response = WorkTeamMemberResponse.builder()
                .id(member.getId())
                .teamId(member.getTeamId())
                .caseId(member.getCaseId())
                .userId(member.getUserId())
                .teamRole(member.getTeamRole())
                .permissionLevel(member.getPermissionLevel())
                .isActive(member.getIsActive())
                .status(member.getStatus())
                .createTime(member.getCreateTime())
                .updateTime(member.getUpdateTime())
                .createUserId(member.getCreateUserId())
                .updateUserId(member.getUpdateUserId())
                .build();

        if (member.getTeamId() != null) {
            WorkTeam workTeam = workTeamRepository.findById(member.getTeamId()).orElse(null);
            if (workTeam != null) {
                response.setTeamName(workTeam.getTeamName());
            }
        }

        if (member.getCaseId() != null) {
            BankruptCase bankruptCase = bankruptCaseRepository.findById(member.getCaseId()).orElse(null);
            if (bankruptCase != null) {
                response.setCaseName(bankruptCase.getCaseName());
            }
        }

        if (member.getUserId() != null) {
            User user = userRepository.findById(member.getUserId()).orElse(null);
            if (user != null) {
                response.setUserName(user.getUsername());
                response.setUserRealName(user.getRealName());
            }
        }

        return response;
    }

    private WorkTeamPermissionResponse convertToPermissionResponse(WorkTeamPermission permission) {
        return WorkTeamPermissionResponse.builder()
                .id(permission.getId())
                .teamMemberId(permission.getTeamMemberId())
                .moduleType(permission.getModuleType())
                .permissionType(permission.getPermissionType())
                .isAllowed(permission.getIsAllowed())
                .status(permission.getStatus())
                .createTime(permission.getCreateTime())
                .updateTime(permission.getUpdateTime())
                .createUserId(permission.getCreateUserId())
                .updateUserId(permission.getUpdateUserId())
                .build();
    }
}

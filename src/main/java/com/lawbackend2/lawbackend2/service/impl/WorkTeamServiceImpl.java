package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.AssignWorkTeamPermissionsRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamMemberCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamMemberPermissionRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamMemberUpdateRequest;
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
import com.lawbackend2.lawbackend2.service.NotificationService;
import com.lawbackend2.lawbackend2.service.UserRoleService;
import com.lawbackend2.lawbackend2.service.WorkTeamService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
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
    private final NotificationService notificationService;
    private final UserRoleService userRoleService;

    public WorkTeamServiceImpl(WorkTeamRepository workTeamRepository,
                             WorkTeamMemberRepository workTeamMemberRepository,
                             WorkTeamPermissionRepository workTeamPermissionRepository,
                             UserRepository userRepository,
                             BankruptCaseRepository bankruptCaseRepository,
                             NotificationService notificationService,
                             UserRoleService userRoleService) {
        this.workTeamRepository = workTeamRepository;
        this.workTeamMemberRepository = workTeamMemberRepository;
        this.workTeamPermissionRepository = workTeamPermissionRepository;
        this.userRepository = userRepository;
        this.bankruptCaseRepository = bankruptCaseRepository;
        this.notificationService = notificationService;
        this.userRoleService = userRoleService;
    }

    @Override
    public Long createWorkTeam(WorkTeamCreateRequest request) {
        WorkTeam workTeam = new WorkTeam();
        BeanUtils.copyProperties(request, workTeam);
        workTeam.setStatus("ACTIVE");
        
        Long currentUserId = SecurityUtil.getCurrentUserId();
        workTeam.setCreateUserId(currentUserId);

        WorkTeam saved = workTeamRepository.save(workTeam);

        User user = userRepository.findById(currentUserId).orElse(null);
        String realName = user != null ? user.getRealName() : "未知用户";
        String content = String.format("%s 创建了工作团队：%s", realName, request.getTeamName());
        notificationService.sendNotificationToAdminAndSuperAdmin(
                "工作团队创建通知",
                content,
                "WORK_TEAM",
                saved.getId(),
                "WorkTeam",
                currentUserId,
                realName
        );

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
        checkViewPermission(teamId);
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
    public PageResult<WorkTeamDetailResponse> getWorkTeamListWithDetails(Integer pageNum, Integer pageSize, Long caseId, String status, String teamName, Long teamLeaderId) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<WorkTeam> page = workTeamRepository.findByConditions(caseId, status, teamName, teamLeaderId, pageable);

        List<WorkTeamDetailResponse> detailResponses = new ArrayList<>();
        for (WorkTeam team : page.getContent()) {
            try {
                WorkTeamDetailResponse detailResponse = getWorkTeamDetailWithMembers(team.getId());
                detailResponses.add(detailResponse);
            } catch (BusinessException e) {
                // 跳过没有权限查看的团队
                continue;
            }
        }

        PageResult<WorkTeamDetailResponse> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(detailResponses);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWorkTeam(Long teamId, WorkTeamUpdateRequest request) {
        WorkTeam workTeam = getWorkTeamDetail(teamId);
        Long currentUserId = SecurityUtil.getCurrentUserId();
        workTeam.setUpdateUserId(currentUserId);

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
        checkAdminPermission(teamId);
        if (!workTeamRepository.existsById(teamId)) {
            throw new BusinessException("工作团队不存在");
        }
        workTeamRepository.deleteById(teamId);
    }

    @Override
    public Long addWorkTeamMember(Long teamId, WorkTeamMemberCreateRequest request) {
        checkEditPermission(teamId);
        WorkTeam workTeam = getWorkTeamDetail(teamId);
        Long currentUserId = SecurityUtil.getCurrentUserId();

        String permissionLevel = request.getPermissionLevel();
        if ("管理".equals(permissionLevel)) {
            permissionLevel = "ADMIN";
        }
        if ("查看".equals(permissionLevel)) {
            permissionLevel = "VIEW";
        }

        Long lastSavedId = null;
        for (Long userId : request.getUserId()) {
            WorkTeamMember member = new WorkTeamMember();
            member.setCaseId(request.getCaseId());
            member.setUserId(userId);
            member.setTeamRole(request.getTeamRole());
            member.setPermissionLevel(permissionLevel);
            member.setTeamId(teamId);
            member.setIsActive(1);
            member.setStatus("ACTIVE");
            member.setCreateUserId(currentUserId);

            WorkTeamMember saved = workTeamMemberRepository.save(member);
            lastSavedId = saved.getId();

            initializePermissionsForMember(saved.getId(), permissionLevel);
        }

        return lastSavedId;
    }

    @Override
    public List<WorkTeamMemberResponse> getWorkTeamMembers(Long teamId) {
        checkViewPermission(teamId);
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
        WorkTeamMember member = workTeamMemberRepository.findMemberById(memberId);
        if (member == null) {
            throw new BusinessException("团队成员不存在");
        }
        
        checkViewPermission(member.getTeamId());
        return workTeamPermissionRepository.findByTeamMemberId(memberId);
    }

    @Override
    public void updateWorkTeamMemberPermission(Long memberId, WorkTeamMemberPermissionRequest request) {
        WorkTeamMember member = workTeamMemberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("团队成员不存在"));
        Long currentUserId = SecurityUtil.getCurrentUserId();

        List<WorkTeamPermission> existingPermissions = workTeamPermissionRepository.findByTeamMemberId(memberId);

        String permissionLevel = request.getPermissionLevel();
        // 转换中文权限级别为英文
        if ("管理".equals(permissionLevel)) {
            permissionLevel = "ADMIN";
        }
        if ("查看".equals(permissionLevel)) {
            permissionLevel = "VIEW";
        }

        for (WorkTeamPermission permission : existingPermissions) {
            permission.setIsAllowed("ADMIN".equals(permissionLevel) ? 1 : 0);
            permission.setUpdateUserId(currentUserId);
            workTeamPermissionRepository.save(permission);
        }

        if (request.getTeamRole() != null) {
            member.setTeamRole(request.getTeamRole());
            member.setUpdateUserId(currentUserId);
            workTeamMemberRepository.save(member);
        }

        // 更新成员的权限级别字段
        member.setPermissionLevel(permissionLevel);
        member.setUpdateUserId(currentUserId);
        workTeamMemberRepository.save(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWorkTeamMember(Long memberId, WorkTeamMemberUpdateRequest request) {
        WorkTeamMember member = workTeamMemberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("团队成员不存在"));
        
        checkEditPermission(member.getTeamId());
        Long currentUserId = SecurityUtil.getCurrentUserId();
        member.setUpdateUserId(currentUserId);

        if (request.getTeamRole() != null) {
            member.setTeamRole(request.getTeamRole());
        }

        if (request.getPermissionLevel() != null) {
            String permissionLevel = request.getPermissionLevel();
            if ("管理".equals(permissionLevel)) {
                permissionLevel = "ADMIN";
            }
            if ("查看".equals(permissionLevel)) {
                permissionLevel = "VIEW";
            }

            member.setPermissionLevel(permissionLevel);

            List<WorkTeamPermission> existingPermissions = workTeamPermissionRepository.findByTeamMemberId(memberId);
            for (WorkTeamPermission permission : existingPermissions) {
                permission.setIsAllowed(calculateIsAllowedForUpdate(permissionLevel, permission.getModuleType(), permission.getPermissionType()));
                permission.setUpdateUserId(currentUserId);
                workTeamPermissionRepository.save(permission);
            }
        }

        workTeamMemberRepository.save(member);
    }

    private Integer calculateIsAllowedForUpdate(String permissionLevel, String moduleType, String permissionType) {
        if ("ADMIN".equals(permissionLevel)) {
            return 1;
        } else if ("EDIT".equals(permissionLevel)) {
            return "VIEW".equals(permissionType) || "EDIT".equals(permissionType) ? 1 : 0;
        } else if ("VIEW".equals(permissionLevel)) {
            return "VIEW".equals(permissionType) ? 1 : 0;
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissionsToMember(Long memberId, AssignWorkTeamPermissionsRequest request) {
        WorkTeamMember member = workTeamMemberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("团队成员不存在"));
        Long currentUserId = SecurityUtil.getCurrentUserId();

        for (AssignWorkTeamPermissionsRequest.WorkTeamPermissionItem item : request.getPermissions()) {
            WorkTeamPermission permission = new WorkTeamPermission();
            permission.setTeamMemberId(memberId);
            permission.setModuleType(item.getModuleType());
            permission.setPermissionType(item.getPermissionType());
            permission.setIsAllowed(item.getIsAllowed());
            permission.setStatus("ACTIVE");
            permission.setCreateUserId(currentUserId);
            workTeamPermissionRepository.save(permission);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeWorkTeamMember(Long memberId) {
        WorkTeamMember member = workTeamMemberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("团队成员不存在"));
        
        checkEditPermission(member.getTeamId());
        
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

    private void initializePermissionsForMember(Long memberId, String permissionLevel) {
        List<String> modules = Arrays.asList("CASE", "CREDITOR", "FUND");
        List<String> permissionTypes = Arrays.asList("VIEW", "EDIT", "DELETE", "APPROVE");

        for (String module : modules) {
            for (String permType : permissionTypes) {
                WorkTeamPermission permission = new WorkTeamPermission();
                permission.setTeamMemberId(memberId);
                permission.setModuleType(module);
                permission.setPermissionType(permType);
                permission.setIsAllowed(calculateIsAllowed(permissionLevel, permType));
                permission.setStatus("ACTIVE");
                workTeamPermissionRepository.save(permission);
            }
        }

        List<String> announcementPermTypes = Arrays.asList("VIEW", "EDIT", "DELETE", "PUBLISH");
        for (String permType : announcementPermTypes) {
            WorkTeamPermission permission = new WorkTeamPermission();
            permission.setTeamMemberId(memberId);
            permission.setModuleType("ANNOUNCEMENT");
            permission.setPermissionType(permType);
            permission.setIsAllowed(calculateIsAllowedForAnnouncement(permissionLevel, permType));
            permission.setStatus("ACTIVE");
            workTeamPermissionRepository.save(permission);
        }
    }

    private Integer calculateIsAllowed(String permissionLevel, String permissionType) {
        if ("ADMIN".equals(permissionLevel)) {
            return 1;
        } else if ("EDIT".equals(permissionLevel)) {
            return "VIEW".equals(permissionType) || "EDIT".equals(permissionType) ? 1 : 0;
        } else if ("VIEW".equals(permissionLevel)) {
            return "VIEW".equals(permissionType) ? 1 : 0;
        }
        return 0;
    }

    private Integer calculateIsAllowedForAnnouncement(String permissionLevel, String permissionType) {
        if ("ADMIN".equals(permissionLevel)) {
            return 1;
        } else if ("EDIT".equals(permissionLevel)) {
            return "VIEW".equals(permissionType) ? 1 : 0;
        } else if ("VIEW".equals(permissionLevel)) {
            return "VIEW".equals(permissionType) ? 1 : 0;
        }
        return 0;
    }

    @Override
    public List<WorkTeam> getWorkTeamsByUserId(Long userId) {
        List<Long> teamIds = workTeamMemberRepository.findTeamIdsByUserId(userId);
        
        if (teamIds.isEmpty()) {
            return List.of();
        }
        
        return workTeamRepository.findByIdIn(teamIds);
    }

    private void checkViewPermission(Long teamId) {
        // 检查用户是否已认证
        if (SecurityUtil.isAuthenticated()) {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            
            // 检查用户是否为SUPER_ADMIN或ADMIN角色，如果是则直接放行
            List<String> roleCodes = userRoleService.getUserRoleCodes(currentUserId);
            if (roleCodes.contains("SUPER_ADMIN") || roleCodes.contains("ADMIN")) {
                return;
            }
            
            WorkTeam workTeam = workTeamRepository.findById(teamId)
                    .orElseThrow(() -> new BusinessException("工作团队不存在"));

            // 团队负责人有查看权限
            if (workTeam.getTeamLeaderId() != null && workTeam.getTeamLeaderId().equals(currentUserId)) {
                return;
            }

            List<WorkTeamMember> members = workTeamMemberRepository.findByUserId(currentUserId);
            boolean hasPermission = members.stream()
                    .anyMatch(member -> member.getTeamId().equals(teamId));

            if (!hasPermission) {
                throw new BusinessException(403, "您没有权限查看该工作团队");
            }
        }
        // 未认证用户不进行权限检查，允许查看所有团队
    }

    private void checkEditPermission(Long teamId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        // 检查用户是否为SUPER_ADMIN或ADMIN角色，如果是则直接放行
        List<String> roleCodes = userRoleService.getUserRoleCodes(currentUserId);
        if (roleCodes.contains("SUPER_ADMIN") || roleCodes.contains("ADMIN")) {
            return;
        }
        
        WorkTeam workTeam = workTeamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException("工作团队不存在"));

        // 团队负责人有编辑权限
        if (workTeam.getTeamLeaderId() != null && workTeam.getTeamLeaderId().equals(currentUserId)) {
            return;
        }

        List<WorkTeamMember> members = workTeamMemberRepository.findByUserId(currentUserId);
        boolean hasPermission = members.stream()
                .anyMatch(member -> member.getTeamId().equals(teamId) 
                        && ("EDIT".equals(member.getPermissionLevel()) || "ADMIN".equals(member.getPermissionLevel())));

        if (!hasPermission) {
            throw new BusinessException(403, "您没有权限编辑该工作团队");
        }
    }

    private void checkAdminPermission(Long teamId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        // 检查用户是否为SUPER_ADMIN或ADMIN角色，如果是则直接放行
        List<String> roleCodes = userRoleService.getUserRoleCodes(currentUserId);
        if (roleCodes.contains("SUPER_ADMIN") || roleCodes.contains("ADMIN")) {
            return;
        }
        
        WorkTeam workTeam = workTeamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException("工作团队不存在"));

        // 团队负责人有管理权限
        if (workTeam.getTeamLeaderId() != null && workTeam.getTeamLeaderId().equals(currentUserId)) {
            return;
        }

        List<WorkTeamMember> members = workTeamMemberRepository.findByUserId(currentUserId);
        boolean hasPermission = members.stream()
                .anyMatch(member -> member.getTeamId().equals(teamId) 
                        && "ADMIN".equals(member.getPermissionLevel()));

        if (!hasPermission) {
            throw new BusinessException(403, "您没有权限管理该工作团队");
        }
    }
}
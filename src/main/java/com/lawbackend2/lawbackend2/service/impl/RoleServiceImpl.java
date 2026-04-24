package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.CreateRoleRequest;
import com.lawbackend2.lawbackend2.dto.request.UpdateRoleRequest;
import com.lawbackend2.lawbackend2.dto.response.RoleListResponse;
import com.lawbackend2.lawbackend2.dto.response.RoleResponse;
import com.lawbackend2.lawbackend2.entity.Role;
import com.lawbackend2.lawbackend2.entity.RolePermission;
import com.lawbackend2.lawbackend2.entity.Token;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.RolePermissionRepository;
import com.lawbackend2.lawbackend2.repository.RoleRepository;
import com.lawbackend2.lawbackend2.repository.TokenRepository;
import com.lawbackend2.lawbackend2.repository.UserRoleRepository;
import com.lawbackend2.lawbackend2.service.RoleService;
import com.lawbackend2.lawbackend2.service.TokenBlacklistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Override
    @Transactional
    public RoleResponse createRole(CreateRoleRequest request) {
        if (roleRepository.existsByRoleCode(request.getRoleCode())) {
            throw new BusinessException(400, "角色代码已存在");
        }

        Role role = Role.builder()
                .roleCode(request.getRoleCode())
                .roleName(request.getRoleName())
                .roleDesc(request.getRoleDesc())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .build();

        role = roleRepository.save(role);
        log.info("角色创建成功 - 角色ID: {}, 角色代码: {}", role.getId(), role.getRoleCode());

        return convertToRoleResponse(role);
    }

    @Override
    @Transactional
    public RoleResponse updateRole(Long roleId, UpdateRoleRequest request) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (!roleOpt.isPresent()) {
            throw new BusinessException(404, "角色不存在");
        }

        Role role = roleOpt.get();

        if (!role.getRoleCode().equals(request.getRoleCode()) && roleRepository.existsByRoleCode(request.getRoleCode())) {
            throw new BusinessException(400, "角色代码已存在");
        }

        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setRoleDesc(request.getRoleDesc());
        if (request.getStatus() != null) {
            role.setStatus(request.getStatus());
        }
        if (request.getSortOrder() != null) {
            role.setSortOrder(request.getSortOrder());
        }

        role = roleRepository.save(role);
        log.info("角色更新成功 - 角色ID: {}, 角色代码: {}", role.getId(), role.getRoleCode());

        return convertToRoleResponse(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (!roleOpt.isPresent()) {
            throw new BusinessException(404, "角色不存在");
        }

        Role role = roleOpt.get();

        if ("1".equals(role.getIsSystem())) {
            throw new BusinessException(400, "系统角色不能删除");
        }

        role.setIsDeleted(true);
        role.setStatus("DELETED");
        roleRepository.save(role);

        rolePermissionRepository.deleteByRoleId(roleId);

        invalidateUsersWithRole(roleId);

        log.info("角色删除成功 - 角色ID: {}", roleId);
    }

    @Override
    public RoleResponse getRoleById(Long roleId) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (!roleOpt.isPresent()) {
            throw new BusinessException(404, "角色不存在");
        }
        Role role = roleOpt.get();
        if (role.getIsDeleted()) {
            throw new BusinessException(404, "角色不存在");
        }
        RoleResponse response = convertToRoleResponse(role);
        List<Long> permissionIds = rolePermissionRepository.findByRoleId(roleId).stream()
                .map(RolePermission::getPermId)
                .collect(Collectors.toList());
        response.setPermissionIds(permissionIds);
        response.setPermissionCount(permissionIds.size());
        return response;
    }

    @Override
    public RoleListResponse getRoleList(Integer page, Integer size, String sortField, String sortOrder, String keyword, String status) {
        Sort sort = Sort.by(
                "ASC".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortField
        );

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Role> rolePage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            List<Role> roles = roleRepository.searchByKeyword(keyword);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), roles.size());
            List<Role> pageContent = roles.subList(start, end);
            rolePage = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, roles.size());
        } else if (status != null && !status.trim().isEmpty()) {
            List<Role> roles = roleRepository.findByStatus(status);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), roles.size());
            List<Role> pageContent = roles.subList(start, end);
            rolePage = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, roles.size());
        } else {
            rolePage = roleRepository.findAll(pageable);
        }

        List<RoleResponse> roleResponses = rolePage.getContent().stream()
                .filter(role -> !role.getIsDeleted())
                .map(role -> {
                    RoleResponse response = convertToRoleResponse(role);
                    List<Long> permissionIds = rolePermissionRepository.findByRoleId(role.getId()).stream()
                            .map(RolePermission::getPermId)
                            .collect(Collectors.toList());
                    response.setPermissionIds(permissionIds);
                    response.setPermissionCount(permissionIds.size());
                    return response;
                })
                .collect(Collectors.toList());

        return RoleListResponse.builder()
                .total(rolePage.getTotalElements())
                .page(page)
                .size(size)
                .totalPages(rolePage.getTotalPages())
                .roles(roleResponses)
                .build();
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAllActive();
    }

    @Override
    public List<Role> getRolesByStatus(String status) {
        return roleRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (!roleOpt.isPresent()) {
            throw new BusinessException(404, "角色不存在");
        }

        Role role = roleOpt.get();
        if (role.getIsDeleted()) {
            throw new BusinessException(404, "角色不存在");
        }

        List<Long> existingPermIds = rolePermissionRepository.findByRoleId(roleId).stream()
                .map(RolePermission::getPermId)
                .collect(Collectors.toList());

        for (Long permId : permissionIds) {
            if (!existingPermIds.contains(permId)) {
                RolePermission rolePermission = RolePermission.builder()
                        .roleId(roleId)
                        .permId(permId)
                        .build();
                rolePermissionRepository.save(rolePermission);
            }
        }

        log.info("为角色分配权限 - 角色ID: {}, 新增权限数量: {}", roleId, permissionIds.size());

        invalidateUsersWithRole(roleId);
    }

    @Override
    public List<Long> getRolePermissions(Long roleId) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (!roleOpt.isPresent()) {
            throw new BusinessException(404, "角色不存在");
        }

        Role role = roleOpt.get();
        if (role.getIsDeleted()) {
            throw new BusinessException(404, "角色不存在");
        }

        List<Long> permissionIds = rolePermissionRepository.findByRoleId(roleId).stream()
                .map(RolePermission::getPermId)
                .collect(Collectors.toList());

        log.info("查询角色权限 - 角色ID: {}, 权限数量: {}", roleId, permissionIds.size());
        return permissionIds;
    }

    @Override
    @Transactional
    public void removePermissionsFromRole(Long roleId, List<Long> permissionIds) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (!roleOpt.isPresent()) {
            throw new BusinessException(404, "角色不存在");
        }

        Role role = roleOpt.get();
        if (role.getIsDeleted()) {
            throw new BusinessException(404, "角色不存在");
        }

        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleId(roleId);
        List<RolePermission> toRemove = rolePermissions.stream()
                .filter(rp -> permissionIds.contains(rp.getPermId()))
                .collect(Collectors.toList());

        rolePermissionRepository.deleteAll(toRemove);

        log.info("移除角色权限 - 角色ID: {}, 移除权限数量: {}", roleId, toRemove.size());
    }

    @Override
    @Transactional
    public void updateRolePermissions(Long roleId, List<Long> permissionIds) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (!roleOpt.isPresent()) {
            throw new BusinessException(404, "角色不存在");
        }

        Role role = roleOpt.get();
        if (role.getIsDeleted()) {
            throw new BusinessException(404, "角色不存在");
        }

        rolePermissionRepository.deleteByRoleId(roleId);

        for (Long permId : permissionIds) {
            RolePermission rolePermission = RolePermission.builder()
                    .roleId(roleId)
                    .permId(permId)
                    .build();
            rolePermissionRepository.save(rolePermission);
        }

        log.info("更新角色权限 - 角色ID: {}, 权限数量: {}", roleId, permissionIds.size());

        invalidateUsersWithRole(roleId);
    }

    private void invalidateUsersWithRole(Long roleId) {
        try {
            List<Long> userIds = userRoleRepository.findUserIdsByRoleId(roleId);
            for (Long userId : userIds) {
                invalidateUserTokens(userId);
            }
            log.info("角色权限变更，已使关联用户的Token失效 - 角色ID: {}, 影响用户数量: {}", roleId, userIds.size());
        } catch (Exception e) {
            log.warn("使关联用户Token失效时出错 - 角色ID: {}, 错误: {}", roleId, e.getMessage());
        }
    }

    private void invalidateUserTokens(Long userId) {
        try {
            List<Token> activeTokens = tokenRepository.findByUserIdAndStatus(userId, "ACTIVE");
            for (Token token : activeTokens) {
                if ("A".equals(token.getTokenType())) {
                    tokenBlacklistService.addToBlacklist(token.getTokenValue());
                    token.setStatus("INACTIVE");
                    token.setRevokeTime(LocalDateTime.now());
                    tokenRepository.save(token);
                }
            }
        } catch (Exception e) {
            log.warn("使用户Token失效时出错 - 用户ID: {}, 错误: {}", userId, e.getMessage());
        }
    }

    private RoleResponse convertToRoleResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .roleCode(role.getRoleCode())
                .roleName(role.getRoleName())
                .roleDesc(role.getRoleDesc())
                .isSystem(role.getIsSystem())
                .status(role.getStatus())
                .sortOrder(role.getSortOrder())
                .createTime(role.getCreateTime())
                .updateTime(role.getUpdateTime())
                .createUserId(role.getCreateUserId())
                .updateUserId(role.getUpdateUserId())
                .build();
    }
}

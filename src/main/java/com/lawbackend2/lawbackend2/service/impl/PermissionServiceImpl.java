package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.response.PermissionTreeResponse;
import com.lawbackend2.lawbackend2.entity.Permission;
import com.lawbackend2.lawbackend2.repository.PermissionRepository;
import com.lawbackend2.lawbackend2.repository.RolePermissionRepository;
import com.lawbackend2.lawbackend2.repository.UserRoleRepository;
import com.lawbackend2.lawbackend2.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Override
    public List<PermissionTreeResponse> getPermissionTree() {
        return getPermissionTreeByStatus("ACTIVE");
    }

    @Override
    public List<PermissionTreeResponse> getPermissionTreeByStatus(String status) {
        List<Permission> allPermissions = permissionRepository.findByStatus(status);

        List<PermissionTreeResponse> permissionResponses = allPermissions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return buildPermissionTree(permissionResponses);
    }

    @Override
    public List<PermissionTreeResponse> buildPermissionTree(List<PermissionTreeResponse> permissions) {
        List<PermissionTreeResponse> roots = new ArrayList<>();

        for (PermissionTreeResponse permission : permissions) {
            if (permission.getParentId() == 0L) {
                roots.add(permission);
            }
        }

        for (PermissionTreeResponse root : roots) {
            buildChildren(root, permissions);
        }

        return roots;
    }

    @Override
    @Transactional
    public void assignPermissionsToUser(Long userId, List<Long> permissionIds) {
        log.info("为用户分配权限 - 用户ID: {}, 权限数量: {}", userId, permissionIds.size());
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        log.info("查询用户权限 - 用户ID: {}", userId);
        
        // 1. 根据用户ID查询角色ID列表
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return List.of();
        }
        
        // 2. 根据角色ID列表查询权限ID列表
        List<Long> permIds = rolePermissionRepository.findPermIdsByRoleIds(roleIds);
        if (permIds.isEmpty()) {
            return List.of();
        }
        
        // 3. 根据权限ID列表查询权限信息
        List<Permission> permissions = permissionRepository.findAllById(permIds);
        
        // 4. 提取权限编码
        return permissions.stream()
                .map(Permission::getPermCode)
                .distinct()
                .collect(Collectors.toList());
    }

    private void buildChildren(PermissionTreeResponse parent, List<PermissionTreeResponse> allPermissions) {
        List<PermissionTreeResponse> children = new ArrayList<>();

        for (PermissionTreeResponse permission : allPermissions) {
            if (permission.getParentId().equals(parent.getId())) {
                children.add(permission);
            }
        }

        for (PermissionTreeResponse child : children) {
            buildChildren(child, allPermissions);
        }

        parent.setChildren(children);
    }

    private PermissionTreeResponse convertToResponse(Permission permission) {
        return PermissionTreeResponse.builder()
                .id(permission.getId())
                .permCode(permission.getPermCode())
                .permName(permission.getPermName())
                .permType(permission.getPermType())
                .parentId(permission.getParentId())
                .path(permission.getPath())
                .component(permission.getComponent())
                .icon(permission.getIcon())
                .sortOrder(permission.getSortOrder())
                .status(permission.getStatus())
                .children(new ArrayList<>())
                .build();
    }
}

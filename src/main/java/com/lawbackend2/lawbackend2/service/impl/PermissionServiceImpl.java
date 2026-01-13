package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.CreatePermissionRequest;
import com.lawbackend2.lawbackend2.dto.request.UpdatePermissionRequest;
import com.lawbackend2.lawbackend2.dto.response.PermissionListResponse;
import com.lawbackend2.lawbackend2.dto.response.PermissionResponse;
import com.lawbackend2.lawbackend2.dto.response.PermissionTreeResponse;
import com.lawbackend2.lawbackend2.entity.Permission;
import com.lawbackend2.lawbackend2.entity.RolePermission;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.PermissionRepository;
import com.lawbackend2.lawbackend2.repository.RolePermissionRepository;
import com.lawbackend2.lawbackend2.repository.UserRoleRepository;
import com.lawbackend2.lawbackend2.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    @Transactional
    public PermissionResponse createPermission(CreatePermissionRequest request) {
        if (permissionRepository.existsByPermCode(request.getPermCode())) {
            throw new BusinessException(400, "权限代码已存在");
        }

        Permission permission = Permission.builder()
                .permCode(request.getPermCode())
                .permName(request.getPermName())
                .permType(request.getPermType())
                .parentId(request.getParentId() != null ? request.getParentId() : 0L)
                .path(request.getPath())
                .component(request.getComponent())
                .icon(request.getIcon())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .isExternal(request.getIsExternal() != null ? request.getIsExternal() : '0')
                .build();

        permission = permissionRepository.save(permission);
        log.info("权限创建成功 - 权限ID: {}, 权限代码: {}", permission.getId(), permission.getPermCode());

        return convertToPermissionResponse(permission);
    }

    @Override
    @Transactional
    public PermissionResponse updatePermission(Long permId, UpdatePermissionRequest request) {
        Permission permission = permissionRepository.findById(permId)
                .orElseThrow(() -> new BusinessException(404, "权限不存在"));

        if (permission.getIsDeleted()) {
            throw new BusinessException(404, "权限不存在");
        }

        if (!permission.getPermCode().equals(request.getPermCode()) && permissionRepository.existsByPermCode(request.getPermCode())) {
            throw new BusinessException(400, "权限代码已存在");
        }

        permission.setPermCode(request.getPermCode());
        permission.setPermName(request.getPermName());
        permission.setPermType(request.getPermType());
        permission.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        permission.setPath(request.getPath());
        permission.setComponent(request.getComponent());
        permission.setIcon(request.getIcon());
        if (request.getSortOrder() != null) {
            permission.setSortOrder(request.getSortOrder());
        }
        if (request.getStatus() != null) {
            permission.setStatus(request.getStatus());
        }
        if (request.getIsExternal() != null) {
            permission.setIsExternal(request.getIsExternal());
        }

        permission = permissionRepository.save(permission);
        log.info("权限更新成功 - 权限ID: {}, 权限代码: {}", permission.getId(), permission.getPermCode());

        return convertToPermissionResponse(permission);
    }

    @Override
    @Transactional
    public void deletePermission(Long permId) {
        Permission permission = permissionRepository.findById(permId)
                .orElseThrow(() -> new BusinessException(404, "权限不存在"));

        if (permission.getIsDeleted()) {
            throw new BusinessException(404, "权限不存在");
        }

        List<Permission> childPermissions = permissionRepository.findByParentIdAndStatus(permId, "ACTIVE");
        if (!childPermissions.isEmpty()) {
            throw new BusinessException(400, "该权限下存在子权限，不能删除");
        }

        permission.setIsDeleted(true);
        permission.setStatus("DELETED");
        permissionRepository.save(permission);

        rolePermissionRepository.deleteByPermId(permId);

        log.info("权限删除成功 - 权限ID: {}", permId);
    }

    @Override
    public PermissionResponse getPermissionById(Long permId) {
        Permission permission = permissionRepository.findById(permId)
                .orElseThrow(() -> new BusinessException(404, "权限不存在"));

        if (permission.getIsDeleted()) {
            throw new BusinessException(404, "权限不存在");
        }

        return convertToPermissionResponse(permission);
    }

    @Override
    public PermissionListResponse getPermissionList(Integer page, Integer size, String sortField, String sortOrder, String keyword, String status) {
        Sort sort = Sort.by(
                "ASC".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortField
        );

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Permission> permissionPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            List<Permission> permissions = permissionRepository.searchByKeyword(keyword);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), permissions.size());
            List<Permission> pageContent = permissions.subList(start, end);
            permissionPage = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, permissions.size());
        } else if (status != null && !status.trim().isEmpty()) {
            List<Permission> permissions = permissionRepository.findByStatus(status);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), permissions.size());
            List<Permission> pageContent = permissions.subList(start, end);
            permissionPage = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, permissions.size());
        } else {
            permissionPage = permissionRepository.findAll(pageable);
        }

        List<PermissionResponse> permissionResponses = permissionPage.getContent().stream()
                .filter(permission -> !permission.getIsDeleted())
                .map(this::convertToPermissionResponse)
                .collect(Collectors.toList());

        return PermissionListResponse.builder()
                .total(permissionPage.getTotalElements())
                .page(page)
                .size(size)
                .totalPages(permissionPage.getTotalPages())
                .permissions(permissionResponses)
                .build();
    }

    @Override
    public List<PermissionTreeResponse> getPermissionTree() {
        return getPermissionTreeByStatus("ACTIVE");
    }

    @Override
    public List<PermissionTreeResponse> getPermissionTreeByStatus(String status) {
        List<Permission> allPermissions = permissionRepository.findByStatus(status);

        List<PermissionTreeResponse> permissionResponses = allPermissions.stream()
                .map(this::convertToTreeResponse)
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
        
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return List.of();
        }
        
        List<Long> permIds = rolePermissionRepository.findPermIdsByRoleIds(roleIds);
        if (permIds.isEmpty()) {
            return List.of();
        }
        
        List<Permission> permissions = permissionRepository.findAllById(permIds);
        
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

    private PermissionTreeResponse convertToTreeResponse(Permission permission) {
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

    private PermissionResponse convertToPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
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
                .isExternal(permission.getIsExternal())
                .createTime(permission.getCreateTime())
                .updateTime(permission.getUpdateTime())
                .createUserId(permission.getCreateUserId())
                .updateUserId(permission.getUpdateUserId())
                .build();
    }
}

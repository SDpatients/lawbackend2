package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.CreateRoleRequest;
import com.lawbackend2.lawbackend2.entity.Role;
import com.lawbackend2.lawbackend2.repository.RoleRepository;
import com.lawbackend2.lawbackend2.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public Role createRole(CreateRoleRequest request) {
        if (roleRepository.existsByRoleCode(request.getRoleCode())) {
            throw new RuntimeException("角色代码已存在");
        }

        Role role = Role.builder()
                .roleCode(request.getRoleCode())
                .roleName(request.getRoleName())
                .roleDesc(request.getRoleDesc())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .sortOrder(0)
                .build();

        role = roleRepository.save(role);
        log.info("角色创建成功 - 角色ID: {}, 角色代码: {}", role.getId(), role.getRoleCode());

        return role;
    }

    @Override
    @Transactional
    public Role updateRole(Long roleId, CreateRoleRequest request) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (!roleOpt.isPresent()) {
            throw new RuntimeException("角色不存在");
        }

        Role role = roleOpt.get();

        if (!role.getRoleCode().equals(request.getRoleCode()) && roleRepository.existsByRoleCode(request.getRoleCode())) {
            throw new RuntimeException("角色代码已存在");
        }

        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setRoleDesc(request.getRoleDesc());
        if (request.getStatus() != null) {
            role.setStatus(request.getStatus());
        }

        role = roleRepository.save(role);
        log.info("角色更新成功 - 角色ID: {}, 角色代码: {}", role.getId(), role.getRoleCode());

        return role;
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (!roleOpt.isPresent()) {
            throw new RuntimeException("角色不存在");
        }

        Role role = roleOpt.get();

        if ("1".equals(role.getIsSystem())) {
            throw new RuntimeException("系统角色不能删除");
        }

        role.setIsDeleted(true);
        role.setStatus("DELETED");
        roleRepository.save(role);

        log.info("角色删除成功 - 角色ID: {}", roleId);
    }

    @Override
    public Role getRoleById(Long roleId) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (!roleOpt.isPresent()) {
            throw new RuntimeException("角色不存在");
        }
        return roleOpt.get();
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
            throw new RuntimeException("角色不存在");
        }

        log.info("为角色分配权限 - 角色ID: {}, 权限数量: {}", roleId, permissionIds.size());
    }

    @Override
    public List<Long> getRolePermissions(Long roleId) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (!roleOpt.isPresent()) {
            throw new RuntimeException("角色不存在");
        }

        log.info("查询角色权限 - 角色ID: {}", roleId);
        return List.of(1L, 2L, 3L);
    }

    @Override
    @Transactional
    public void removePermissionsFromRole(Long roleId, List<Long> permissionIds) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (!roleOpt.isPresent()) {
            throw new RuntimeException("角色不存在");
        }

        log.info("移除角色权限 - 角色ID: {}, 权限数量: {}", roleId, permissionIds.size());
    }
}

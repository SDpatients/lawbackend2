package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.CreateRoleRequest;
import com.lawbackend2.lawbackend2.entity.Role;

import java.util.List;

public interface RoleService {

    Role createRole(CreateRoleRequest request);

    Role updateRole(Long roleId, CreateRoleRequest request);

    void deleteRole(Long roleId);

    Role getRoleById(Long roleId);

    List<Role> getAllRoles();

    List<Role> getRolesByStatus(String status);

    void assignPermissionsToRole(Long roleId, List<Long> permissionIds);

    List<Long> getRolePermissions(Long roleId);

    void removePermissionsFromRole(Long roleId, List<Long> permissionIds);
}

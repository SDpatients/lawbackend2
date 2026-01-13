package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.CreateRoleRequest;
import com.lawbackend2.lawbackend2.dto.request.UpdateRoleRequest;
import com.lawbackend2.lawbackend2.dto.response.RoleListResponse;
import com.lawbackend2.lawbackend2.dto.response.RoleResponse;
import com.lawbackend2.lawbackend2.entity.Role;

import java.util.List;

public interface RoleService {

    RoleResponse createRole(CreateRoleRequest request);

    RoleResponse updateRole(Long roleId, UpdateRoleRequest request);

    void deleteRole(Long roleId);

    RoleResponse getRoleById(Long roleId);

    RoleListResponse getRoleList(Integer page, Integer size, String sortField, String sortOrder, String keyword, String status);

    List<Role> getAllRoles();

    List<Role> getRolesByStatus(String status);

    void assignPermissionsToRole(Long roleId, List<Long> permissionIds);

    List<Long> getRolePermissions(Long roleId);

    void removePermissionsFromRole(Long roleId, List<Long> permissionIds);

    void updateRolePermissions(Long roleId, List<Long> permissionIds);
}

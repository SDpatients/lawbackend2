package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.response.PermissionTreeResponse;

import java.util.List;

public interface PermissionService {

    List<PermissionTreeResponse> getPermissionTree();

    List<PermissionTreeResponse> getPermissionTreeByStatus(String status);

    List<PermissionTreeResponse> buildPermissionTree(List<PermissionTreeResponse> permissions);

    void assignPermissionsToUser(Long userId, List<Long> permissionIds);

    List<String> getUserPermissions(Long userId);
}

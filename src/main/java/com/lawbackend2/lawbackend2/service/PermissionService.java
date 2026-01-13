package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.CreatePermissionRequest;
import com.lawbackend2.lawbackend2.dto.request.UpdatePermissionRequest;
import com.lawbackend2.lawbackend2.dto.response.PermissionListResponse;
import com.lawbackend2.lawbackend2.dto.response.PermissionResponse;
import com.lawbackend2.lawbackend2.dto.response.PermissionTreeResponse;

import java.util.List;

public interface PermissionService {

    PermissionResponse createPermission(CreatePermissionRequest request);

    PermissionResponse updatePermission(Long permId, UpdatePermissionRequest request);

    void deletePermission(Long permId);

    PermissionResponse getPermissionById(Long permId);

    PermissionListResponse getPermissionList(Integer page, Integer size, String sortField, String sortOrder, String keyword, String status);

    List<PermissionTreeResponse> getPermissionTree();

    List<PermissionTreeResponse> getPermissionTreeByStatus(String status);

    List<PermissionTreeResponse> buildPermissionTree(List<PermissionTreeResponse> permissions);

    void assignPermissionsToUser(Long userId, List<Long> permissionIds);

    List<String> getUserPermissions(Long userId);
}

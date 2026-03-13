package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.LibPermissionGrantRequest;
import com.lawbackend2.lawbackend2.dto.response.LibPermissionResponse;

import java.util.List;
import java.util.Map;

public interface LibDocumentPermissionService {

    List<LibPermissionResponse> getAllPermissions();

    LibPermissionResponse getPermissionById(Long id);

    LibPermissionResponse getPermissionByCode(String code);

    void grantFolderPermission(Long folderId, LibPermissionGrantRequest request, Long userId);

    void revokeFolderPermission(Long folderId, Long permissionId, String targetType, Long targetId);

    void grantDocumentPermission(Long documentId, LibPermissionGrantRequest request, Long userId);

    void revokeDocumentPermission(Long documentId, Long permissionId, String targetType, Long targetId);

    List<Map<String, Object>> getFolderPermissions(Long folderId);

    List<Map<String, Object>> getDocumentPermissions(Long documentId);

    boolean checkPermission(Long userId, Long resourceId, String resourceType, String permissionType);

    List<Long> getAccessibleFolderIds(Long userId, String permissionType);

    List<Long> getAccessibleDocumentIds(Long userId, String permissionType);
}

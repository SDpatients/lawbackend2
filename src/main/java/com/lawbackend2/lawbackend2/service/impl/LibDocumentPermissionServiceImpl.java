package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.LibPermissionGrantRequest;
import com.lawbackend2.lawbackend2.dto.response.LibPermissionResponse;
import com.lawbackend2.lawbackend2.entity.LibDocumentPermission;
import com.lawbackend2.lawbackend2.entity.LibDocumentPermissionRel;
import com.lawbackend2.lawbackend2.entity.LibFolderPermission;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.LibDocumentPermissionRelRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentPermissionRepository;
import com.lawbackend2.lawbackend2.repository.LibFolderPermissionRepository;
import com.lawbackend2.lawbackend2.service.LibDocumentPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibDocumentPermissionServiceImpl implements LibDocumentPermissionService {

    private final LibDocumentPermissionRepository permissionRepository;
    private final LibFolderPermissionRepository folderPermissionRepository;
    private final LibDocumentPermissionRelRepository documentPermissionRelRepository;

    @Override
    public List<LibPermissionResponse> getAllPermissions() {
        List<LibDocumentPermission> permissions = permissionRepository.findAllActive();
        return permissions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LibPermissionResponse getPermissionById(Long id) {
        LibDocumentPermission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("权限不存在"));
        return convertToResponse(permission);
    }

    @Override
    public LibPermissionResponse getPermissionByCode(String code) {
        LibDocumentPermission permission = permissionRepository.findByPermissionCode(code)
                .orElseThrow(() -> new BusinessException("权限不存在"));
        return convertToResponse(permission);
    }

    @Override
    @Transactional
    public void grantFolderPermission(Long folderId, LibPermissionGrantRequest request, Long userId) {
        LibDocumentPermission permission = permissionRepository.findById(request.getPermissionId())
                .orElseThrow(() -> new BusinessException("权限不存在"));

        LibFolderPermission existing = folderPermissionRepository.findByFolderPermissionTarget(
                folderId, request.getPermissionId(), request.getTargetType(), request.getTargetId());

        if (existing != null) {
            throw new BusinessException("该权限已授予");
        }

        LibFolderPermission folderPermission = LibFolderPermission.builder()
                .folderId(folderId)
                .permissionId(request.getPermissionId())
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .isInherit(request.getIsInherit() != null ? request.getIsInherit() : true)
                .build();
        folderPermission.setCreateUserId(userId);

        folderPermissionRepository.save(folderPermission);
        log.info("授予文件夹权限成功 - 文件夹ID: {}, 权限ID: {}, 目标类型: {}, 目标ID: {}", 
                folderId, request.getPermissionId(), request.getTargetType(), request.getTargetId());
    }

    @Override
    @Transactional
    public void revokeFolderPermission(Long folderId, Long permissionId, String targetType, Long targetId) {
        LibFolderPermission permission = folderPermissionRepository.findByFolderPermissionTarget(
                folderId, permissionId, targetType, targetId);

        if (permission == null) {
            throw new BusinessException("权限不存在");
        }

        permission.setIsDeleted(true);
        folderPermissionRepository.save(permission);

        log.info("撤销文件夹权限成功 - 文件夹ID: {}, 权限ID: {}, 目标类型: {}, 目标ID: {}", 
                folderId, permissionId, targetType, targetId);
    }

    @Override
    @Transactional
    public void grantDocumentPermission(Long documentId, LibPermissionGrantRequest request, Long userId) {
        LibDocumentPermission permission = permissionRepository.findById(request.getPermissionId())
                .orElseThrow(() -> new BusinessException("权限不存在"));

        List<LibDocumentPermissionRel> existing = documentPermissionRelRepository.findByDocumentIdAndTarget(
                documentId, request.getTargetType(), request.getTargetId());

        boolean alreadyGranted = existing.stream()
                .anyMatch(p -> p.getPermissionId().equals(request.getPermissionId()));

        if (alreadyGranted) {
            throw new BusinessException("该权限已授予");
        }

        LibDocumentPermissionRel documentPermission = LibDocumentPermissionRel.builder()
                .documentId(documentId)
                .permissionId(request.getPermissionId())
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .build();
        documentPermission.setCreateUserId(userId);

        documentPermissionRelRepository.save(documentPermission);
        log.info("授予文档权限成功 - 文档ID: {}, 权限ID: {}, 目标类型: {}, 目标ID: {}", 
                documentId, request.getPermissionId(), request.getTargetType(), request.getTargetId());
    }

    @Override
    @Transactional
    public void revokeDocumentPermission(Long documentId, Long permissionId, String targetType, Long targetId) {
        List<LibDocumentPermissionRel> permissions = documentPermissionRelRepository.findByDocumentIdAndTarget(
                documentId, targetType, targetId);

        LibDocumentPermissionRel permission = permissions.stream()
                .filter(p -> p.getPermissionId().equals(permissionId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("权限不存在"));

        permission.setIsDeleted(true);
        documentPermissionRelRepository.save(permission);

        log.info("撤销文档权限成功 - 文档ID: {}, 权限ID: {}, 目标类型: {}, 目标ID: {}", 
                documentId, permissionId, targetType, targetId);
    }

    @Override
    public List<Map<String, Object>> getFolderPermissions(Long folderId) {
        List<LibFolderPermission> permissions = folderPermissionRepository.findByFolderIdAndIsDeletedFalse(folderId);

        return permissions.stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("folderId", p.getFolderId());
                    map.put("permissionId", p.getPermissionId());
                    map.put("targetType", p.getTargetType());
                    map.put("targetId", p.getTargetId());
                    map.put("isInherit", p.getIsInherit());
                    map.put("createTime", p.getCreateTime());

                    permissionRepository.findById(p.getPermissionId()).ifPresent(perm -> {
                        map.put("permissionName", perm.getPermissionName());
                        map.put("permissionCode", perm.getPermissionCode());
                        map.put("permissionType", perm.getPermissionType());
                    });

                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getDocumentPermissions(Long documentId) {
        List<LibDocumentPermissionRel> permissions = documentPermissionRelRepository.findByDocumentIdAndIsDeletedFalse(documentId);

        return permissions.stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("documentId", p.getDocumentId());
                    map.put("permissionId", p.getPermissionId());
                    map.put("targetType", p.getTargetType());
                    map.put("targetId", p.getTargetId());
                    map.put("createTime", p.getCreateTime());

                    permissionRepository.findById(p.getPermissionId()).ifPresent(perm -> {
                        map.put("permissionName", perm.getPermissionName());
                        map.put("permissionCode", perm.getPermissionCode());
                        map.put("permissionType", perm.getPermissionType());
                    });

                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkPermission(Long userId, Long resourceId, String resourceType, String permissionType) {
        LibDocumentPermission permission = permissionRepository.findByPermissionType(permissionType).stream()
                .findFirst()
                .orElse(null);

        if (permission == null) {
            return false;
        }

        if ("FOLDER".equals(resourceType)) {
            List<LibFolderPermission> folderPermissions = folderPermissionRepository.findByFolderIdAndIsDeletedFalse(resourceId);
            return folderPermissions.stream()
                    .anyMatch(p -> {
                        if ("USER".equals(p.getTargetType()) && p.getTargetId().equals(userId)) {
                            return true;
                        }
                        return false;
                    });
        } else if ("DOCUMENT".equals(resourceType)) {
            List<LibDocumentPermissionRel> docPermissions = documentPermissionRelRepository.findByDocumentIdAndIsDeletedFalse(resourceId);
            return docPermissions.stream()
                    .anyMatch(p -> {
                        if ("USER".equals(p.getTargetType()) && p.getTargetId().equals(userId)) {
                            return true;
                        }
                        return false;
                    });
        }

        return false;
    }

    @Override
    public List<Long> getAccessibleFolderIds(Long userId, String permissionType) {
        List<LibFolderPermission> permissions = folderPermissionRepository.findByTarget("USER", userId);

        return permissions.stream()
                .filter(p -> {
                    LibDocumentPermission permission = permissionRepository.findById(p.getPermissionId()).orElse(null);
                    return permission != null && permissionType.equals(permission.getPermissionType());
                })
                .map(LibFolderPermission::getFolderId)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getAccessibleDocumentIds(Long userId, String permissionType) {
        List<LibDocumentPermissionRel> permissions = documentPermissionRelRepository.findByTarget("USER", userId);

        return permissions.stream()
                .filter(p -> {
                    LibDocumentPermission permission = permissionRepository.findById(p.getPermissionId()).orElse(null);
                    return permission != null && permissionType.equals(permission.getPermissionType());
                })
                .map(LibDocumentPermissionRel::getDocumentId)
                .distinct()
                .collect(Collectors.toList());
    }

    private LibPermissionResponse convertToResponse(LibDocumentPermission permission) {
        return LibPermissionResponse.builder()
                .id(permission.getId())
                .permissionName(permission.getPermissionName())
                .permissionCode(permission.getPermissionCode())
                .permissionType(permission.getPermissionType())
                .description(permission.getDescription())
                .sortOrder(permission.getSortOrder())
                .status(permission.getStatus())
                .createTime(permission.getCreateTime())
                .build();
    }
}

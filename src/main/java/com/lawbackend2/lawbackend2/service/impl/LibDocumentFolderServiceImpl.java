package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.LibFolderCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.LibFolderUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.LibDocumentTreeResponse;
import com.lawbackend2.lawbackend2.dto.response.LibFolderListResponse;
import com.lawbackend2.lawbackend2.dto.response.LibFolderResponse;
import com.lawbackend2.lawbackend2.entity.LibDocumentFolder;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.LibDocumentFavoriteRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentFolderRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentPermissionRelRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentShareRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentVersionRepository;
import com.lawbackend2.lawbackend2.repository.LibFolderPermissionRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.LibDocumentFolderService;
import com.lawbackend2.lawbackend2.service.LibDocumentOperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibDocumentFolderServiceImpl implements LibDocumentFolderService {

    private final LibDocumentFolderRepository folderRepository;
    private final LibDocumentRepository documentRepository;
    private final LibDocumentVersionRepository versionRepository;
    private final LibDocumentShareRepository shareRepository;
    private final LibDocumentFavoriteRepository favoriteRepository;
    private final LibDocumentPermissionRelRepository permissionRelRepository;
    private final LibFolderPermissionRepository folderPermissionRepository;
    private final LibDocumentOperationLogService operationLogService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public LibFolderResponse createFolder(LibFolderCreateRequest request, Long userId) {
        if (request.getParentId() != null) {
            LibDocumentFolder parent = folderRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException("父文件夹不存在"));

            if (!canAccessFolder(request.getParentId(), userId)) {
                throw new BusinessException("没有权限在该文件夹下创建");
            }

            if (folderRepository.existsByParentIdAndFolderName(request.getParentId(), request.getFolderName())) {
                throw new BusinessException("该文件夹下已存在同名文件夹");
            }
        }

        String folderPath = buildFolderPath(request.getParentId(), request.getFolderName());
        if (folderRepository.existsByFolderPath(folderPath)) {
            throw new BusinessException("文件夹路径已存在");
        }

        Integer folderLevel = calculateFolderLevel(request.getParentId());

        LibDocumentFolder folder = LibDocumentFolder.builder()
                .folderName(request.getFolderName())
                .folderPath(folderPath)
                .parentId(request.getParentId())
                .folderLevel(folderLevel)
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .description(request.getDescription())
                .icon(request.getIcon())
                .color(request.getColor())
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : false)
                .build();
        folder.setCreateUserId(userId);

        folder = folderRepository.save(folder);
        log.info("创建文件夹成功 - 文件夹ID: {}, 名称: {}, 用户ID: {}", folder.getId(), folder.getFolderName(), userId);

        operationLogService.logOperation(null, folder.getId(), "CREATE", "创建文件夹: " + folder.getFolderName(), null, folder.getFolderPath(), userId, null, null);

        return convertToResponse(folder);
    }

    @Override
    public LibFolderResponse getFolderById(Long id, Long userId) {
        LibDocumentFolder folder = folderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文件夹不存在"));

        if (!canAccessFolder(id, userId)) {
            throw new BusinessException("没有权限访问该文件夹");
        }

        LibFolderResponse response = convertToResponse(folder);
        response.setDocumentCount(documentRepository.countByFolderId(id));
        response.setSubFolderCount(folderRepository.countByParentId(id));

        return response;
    }

    @Override
    @Transactional
    public LibFolderResponse updateFolder(Long id, LibFolderUpdateRequest request, Long userId) {
        LibDocumentFolder folder = folderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文件夹不存在"));

        if (!canAccessFolder(id, userId)) {
            throw new BusinessException("没有权限修改该文件夹");
        }

        String oldPath = folder.getFolderPath();

        if (request.getFolderName() != null && !request.getFolderName().equals(folder.getFolderName())) {
            if (folderRepository.existsByParentIdAndFolderName(folder.getParentId(), request.getFolderName())) {
                throw new BusinessException("该文件夹下已存在同名文件夹");
            }
            folder.setFolderName(request.getFolderName());
            folder.setFolderPath(buildFolderPath(folder.getParentId(), request.getFolderName()));
        }

        if (request.getDescription() != null) {
            folder.setDescription(request.getDescription());
        }
        if (request.getIcon() != null) {
            folder.setIcon(request.getIcon());
        }
        if (request.getColor() != null) {
            folder.setColor(request.getColor());
        }
        if (request.getIsPublic() != null) {
            if (folder.getIsPublic() && !request.getIsPublic()) {
                throw new BusinessException("公开的文件夹不能设置为私人");
            }
            folder.setIsPublic(request.getIsPublic());
        }
        if (request.getSortOrder() != null) {
            folder.setSortOrder(request.getSortOrder());
        }

        folder.setUpdateUserId(userId);
        folder = folderRepository.save(folder);

        log.info("更新文件夹成功 - 文件夹ID: {}, 用户ID: {}", id, userId);
        operationLogService.logOperation(null, id, "UPDATE", "更新文件夹信息", oldPath, folder.getFolderPath(), userId, null, null);

        return convertToResponse(folder);
    }

    @Override
    @Transactional
    public void deleteFolder(Long id, Long userId) {
        LibDocumentFolder folder = folderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文件夹不存在"));

        if (!canAccessFolder(id, userId)) {
            throw new BusinessException("没有权限删除该文件夹");
        }

        // 递归删除子文件夹及其内容
        deleteSubFolders(id);

        // 删除文件夹内的所有文档
        deleteDocumentsByFolderId(id);

        // 删除文件夹权限
        folderPermissionRepository.deleteByFolderId(id);

        // 硬删除文件夹
        folderRepository.deleteById(id);

        log.info("删除文件夹成功 - 文件夹ID: {}, 用户ID: {}", id, userId);
        operationLogService.logOperation(null, id, "DELETE", "删除文件夹", folder.getFolderName(), null, userId, null, null);
    }

    private void deleteSubFolders(Long parentId) {
        List<LibDocumentFolder> subFolders = folderRepository.findByParentIdOrderBySortOrderAsc(parentId);
        for (LibDocumentFolder folder : subFolders) {
            // 递归删除子文件夹
            deleteSubFolders(folder.getId());
            // 删除子文件夹内的文档
            deleteDocumentsByFolderId(folder.getId());
            // 删除子文件夹权限
            folderPermissionRepository.deleteByFolderId(folder.getId());
            // 删除子文件夹
            folderRepository.deleteById(folder.getId());
        }
    }

    private void deleteDocumentsByFolderId(Long folderId) {
        // 获取文件夹内所有文档
        List<com.lawbackend2.lawbackend2.entity.LibDocument> documents = documentRepository.findByFolderIdOrderByCreateTimeDesc(folderId);
        for (com.lawbackend2.lawbackend2.entity.LibDocument document : documents) {
            Long docId = document.getId();

            // 删除当前版本物理文件
            String filePath = document.getFilePath();
            if (filePath != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (!deleted) {
                        log.warn("删除文档物理文件失败，文件可能被占用或权限不足 - 文档ID: {}, 路径: {}", docId, filePath);
                    }
                }
            }

            // 删除该文档所有历史版本的物理文件
            List<com.lawbackend2.lawbackend2.entity.LibDocumentVersion> versions = versionRepository.findByDocumentIdOrderByVersionNumberDesc(docId);
            for (com.lawbackend2.lawbackend2.entity.LibDocumentVersion version : versions) {
                String versionFilePath = version.getFilePath();
                if (versionFilePath != null) {
                    File versionFile = new File(versionFilePath);
                    if (versionFile.exists()) {
                        boolean deleted = versionFile.delete();
                        if (!deleted) {
                            log.warn("删除版本物理文件失败，文件可能被占用或权限不足 - 版本ID: {}, 路径: {}", version.getId(), versionFilePath);
                        }
                    }
                }
            }

            // 级联删除关联数据
            versionRepository.deleteByDocumentId(docId);
            shareRepository.deleteByDocumentId(docId);
            favoriteRepository.deleteByDocumentId(docId);
            permissionRelRepository.deleteByDocumentId(docId);
        }
        // 删除文档记录
        documentRepository.deleteByFolderId(folderId);
    }

    @Override
    public LibFolderListResponse getFolderList(Long parentId, String keyword, Integer page, Integer size, Long userId) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<LibDocumentFolder> folderPage;

        List<LibDocumentFolder> allFolders;
        if (keyword != null && !keyword.isEmpty()) {
            allFolders = folderRepository.searchByKeyword(keyword);
        } else if (parentId != null) {
            allFolders = folderRepository.findByParentIdOrderBySortOrderAsc(parentId);
        } else {
            allFolders = folderRepository.findByParentIdIsNullOrderBySortOrderAsc();
        }

        final Long currentUserId = userId;
        List<LibDocumentFolder> filteredFolders = allFolders.stream()
                .filter(folder -> canAccessFolder(folder.getId(), currentUserId))
                .collect(Collectors.toList());

        int start = (page - 1) * size;
        int end = Math.min(start + size, filteredFolders.size());
        List<LibDocumentFolder> pagedFolders = start < filteredFolders.size() ? filteredFolders.subList(start, end) : new ArrayList<>();

        folderPage = new PageImpl<>(pagedFolders, pageable, filteredFolders.size());

        List<LibFolderResponse> folderResponses = folderPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return LibFolderListResponse.builder()
                .total(folderPage.getTotalElements())
                .page(page)
                .size(size)
                .totalPages(folderPage.getTotalPages())
                .folders(folderResponses)
                .build();
    }

    @Override
    public List<LibFolderResponse> getSubFolders(Long parentId, Long userId) {
        List<LibDocumentFolder> folders;
        if (parentId == null) {
            folders = folderRepository.findByParentIdIsNullOrderBySortOrderAsc();
        } else {
            folders = folderRepository.findByParentIdOrderBySortOrderAsc(parentId);
        }

        final Long currentUserId = userId;
        return folders.stream()
                .filter(folder -> canAccessFolder(folder.getId(), currentUserId))
                .map(f -> {
                    LibFolderResponse response = convertToResponse(f);
                    response.setDocumentCount(documentRepository.countByFolderId(f.getId()));
                    response.setSubFolderCount(folderRepository.countByParentId(f.getId()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public LibDocumentTreeResponse getFolderTree(Long userId) {
        List<LibDocumentFolder> allFolders = folderRepository.findAllOrderByLevelAndSort();
        
        final Long currentUserId = userId;
        List<LibDocumentFolder> accessibleFolders = allFolders.stream()
                .filter(folder -> canAccessFolder(folder.getId(), currentUserId))
                .collect(Collectors.toList());
        
        Map<Long, List<LibDocumentFolder>> folderMap = accessibleFolders.stream()
                .collect(Collectors.groupingBy(
                        f -> f.getParentId() != null ? f.getParentId() : 0L
                ));

        List<LibDocumentTreeResponse> children = buildTreeNodes(0L, folderMap);

        return LibDocumentTreeResponse.builder()
                .id(0L)
                .name("文档库")
                .type("root")
                .children(children)
                .build();
    }

    private List<LibDocumentTreeResponse> buildTreeNodes(Long parentId, Map<Long, List<LibDocumentFolder>> folderMap) {
        List<LibDocumentFolder> folders = folderMap.getOrDefault(parentId, new ArrayList<>());
        
        return folders.stream()
                .map(folder -> {
                    List<LibDocumentTreeResponse> children = buildTreeNodes(folder.getId(), folderMap);
                    String createUserName = null;
                    if (folder.getCreateUserId() != null) {
                        User user = userRepository.findById(folder.getCreateUserId()).orElse(null);
                        if (user != null) {
                            createUserName = user.getRealName() != null ? user.getRealName() : user.getUsername();
                        }
                    }
                    return LibDocumentTreeResponse.builder()
                            .id(folder.getId())
                            .name(folder.getFolderName())
                            .type("folder")
                            .parentId(folder.getParentId())
                            .path(folder.getFolderPath())
                            .folderLevel(folder.getFolderLevel())
                            .sortOrder(folder.getSortOrder())
                            .icon(folder.getIcon())
                            .color(folder.getColor())
                            .documentCount(documentRepository.countByFolderId(folder.getId()))
                            .createTime(folder.getCreateTime())
                            .createUserId(folder.getCreateUserId())
                            .createUserName(createUserName)
                            .children(children.isEmpty() ? null : children)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<LibDocumentTreeResponse> getBreadcrumbs(Long folderId, Long userId) {
        List<LibDocumentTreeResponse> breadcrumbs = new ArrayList<>();
        
        if (folderId == null) {
            return breadcrumbs;
        }

        LibDocumentFolder folder = folderRepository.findById(folderId).orElse(null);
        while (folder != null) {
            if (!canAccessFolder(folder.getId(), userId)) {
                break;
            }
            breadcrumbs.add(0, convertToTreeResponse(folder));
            if (folder.getParentId() == null) {
                break;
            }
            folder = folderRepository.findById(folder.getParentId()).orElse(null);
        }

        return breadcrumbs;
    }

    @Override
    @Transactional
    public void moveFolder(Long folderId, Long newParentId, Long userId) {
        LibDocumentFolder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new BusinessException("文件夹不存在"));

        if (!canAccessFolder(folderId, userId)) {
            throw new BusinessException("没有权限移动该文件夹");
        }

        if (newParentId != null) {
            LibDocumentFolder newParent = folderRepository.findById(newParentId)
                    .orElseThrow(() -> new BusinessException("目标文件夹不存在"));

            if (!canAccessFolder(newParentId, userId)) {
                throw new BusinessException("没有权限移动到目标文件夹");
            }

            if (newParentId.equals(folderId)) {
                throw new BusinessException("不能将文件夹移动到自身");
            }

            if (isDescendant(folderId, newParentId)) {
                throw new BusinessException("不能将文件夹移动到其子文件夹中");
            }

            if (folderRepository.existsByParentIdAndFolderName(newParentId, folder.getFolderName())) {
                throw new BusinessException("目标文件夹下已存在同名文件夹");
            }
        }

        String oldPath = folder.getFolderPath();
        folder.setParentId(newParentId);
        folder.setFolderPath(buildFolderPath(newParentId, folder.getFolderName()));
        folder.setFolderLevel(calculateFolderLevel(newParentId));
        folder.setUpdateUserId(userId);

        folderRepository.save(folder);
        log.info("移动文件夹成功 - 文件夹ID: {}, 新父文件夹ID: {}, 用户ID: {}", folderId, newParentId, userId);

        operationLogService.logOperation(null, folderId, "MOVE", "移动文件夹", oldPath, folder.getFolderPath(), userId, null, null);
    }

    @Override
    public Long getDocumentCount(Long folderId) {
        return documentRepository.countByFolderId(folderId);
    }

    @Override
    public boolean hasPermission(Long folderId, Long userId, String permissionType) {
        return true;
    }

    @Override
    public List<Long> getAllDescendantIds(Long folderId) {
        return folderRepository.findAllDescendantIds(folderId);
    }

    @Override
    public List<LibFolderResponse> getFoldersByLevel(Integer level, Long userId) {
        List<LibDocumentFolder> folders = folderRepository.findByFolderLevelOrderBySortOrder(level);
        
        final Long currentUserId = userId;
        return folders.stream()
                .filter(folder -> canAccessFolder(folder.getId(), currentUserId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateSortOrder(Long folderId, Integer sortOrder, Long userId) {
        LibDocumentFolder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new BusinessException("文件夹不存在"));

        if (!canAccessFolder(folderId, userId)) {
            throw new BusinessException("没有权限修改该文件夹");
        }

        folderRepository.updateSortOrder(folderId, sortOrder);
        log.info("更新文件夹排序成功 - 文件夹ID: {}, 排序: {}, 用户ID: {}", folderId, sortOrder, userId);
    }

    @Override
    public boolean canAccessFolder(Long folderId, Long userId) {
        if (folderId == null) {
            return true;
        }
        
        LibDocumentFolder folder = folderRepository.findById(folderId).orElse(null);
        if (folder == null) {
            return false;
        }
        
        if (folder.getIsPublic()) {
            return true;
        }
        if (userId == null) {
            return false;
        }
        if (folder.getCreateUserId() != null && folder.getCreateUserId().equals(userId)) {
            return true;
        }
        return false;
    }

    private String buildFolderPath(Long parentId, String folderName) {
        if (parentId == null) {
            return "/" + folderName;
        }

        LibDocumentFolder parent = folderRepository.findById(parentId).orElse(null);
        if (parent == null) {
            return "/" + folderName;
        }

        return parent.getFolderPath() + "/" + folderName;
    }

    private Integer calculateFolderLevel(Long parentId) {
        if (parentId == null) {
            return 1;
        }

        LibDocumentFolder parent = folderRepository.findById(parentId).orElse(null);
        if (parent == null) {
            return 1;
        }

        return parent.getFolderLevel() + 1;
    }

    private boolean isDescendant(Long ancestorId, Long descendantId) {
        LibDocumentFolder folder = folderRepository.findById(descendantId).orElse(null);
        while (folder != null && folder.getParentId() != null) {
            if (folder.getParentId().equals(ancestorId)) {
                return true;
            }
            folder = folderRepository.findById(folder.getParentId()).orElse(null);
        }
        return false;
    }

    private LibFolderResponse convertToResponse(LibDocumentFolder folder) {
        String createUserName = null;
        if (folder.getCreateUserId() != null) {
            User user = userRepository.findById(folder.getCreateUserId()).orElse(null);
            if (user != null) {
                createUserName = user.getRealName() != null ? user.getRealName() : user.getUsername();
            }
        }

        return LibFolderResponse.builder()
                .id(folder.getId())
                .folderName(folder.getFolderName())
                .folderPath(folder.getFolderPath())
                .parentId(folder.getParentId())
                .folderLevel(folder.getFolderLevel())
                .sortOrder(folder.getSortOrder())
                .description(folder.getDescription())
                .icon(folder.getIcon())
                .color(folder.getColor())
                .isPublic(folder.getIsPublic())
                .status(folder.getStatus())
                .createTime(folder.getCreateTime())
                .updateTime(folder.getUpdateTime())
                .createUserId(folder.getCreateUserId())
                .createUserName(createUserName)
                .build();
    }

    private LibDocumentTreeResponse convertToTreeResponse(LibDocumentFolder folder) {
        return LibDocumentTreeResponse.builder()
                .id(folder.getId())
                .name(folder.getFolderName())
                .type("folder")
                .parentId(folder.getParentId())
                .path(folder.getFolderPath())
                .folderLevel(folder.getFolderLevel())
                .sortOrder(folder.getSortOrder())
                .icon(folder.getIcon())
                .color(folder.getColor())
                .createTime(folder.getCreateTime())
                .build();
    }
}

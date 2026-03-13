package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.LibFolderCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.LibFolderUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.LibDocumentTreeResponse;
import com.lawbackend2.lawbackend2.dto.response.LibFolderListResponse;
import com.lawbackend2.lawbackend2.dto.response.LibFolderResponse;

import java.util.List;

public interface LibDocumentFolderService {

    LibFolderResponse createFolder(LibFolderCreateRequest request, Long userId);

    LibFolderResponse getFolderById(Long id, Long userId);

    LibFolderResponse updateFolder(Long id, LibFolderUpdateRequest request, Long userId);

    void deleteFolder(Long id, Long userId);

    LibFolderListResponse getFolderList(Long parentId, String keyword, Integer page, Integer size, Long userId);

    List<LibFolderResponse> getSubFolders(Long parentId, Long userId);

    LibDocumentTreeResponse getFolderTree(Long userId);

    List<LibDocumentTreeResponse> getBreadcrumbs(Long folderId, Long userId);

    void moveFolder(Long folderId, Long newParentId, Long userId);

    Long getDocumentCount(Long folderId);

    boolean hasPermission(Long folderId, Long userId, String permissionType);

    List<Long> getAllDescendantIds(Long folderId);

    List<LibFolderResponse> getFoldersByLevel(Integer level, Long userId);

    void updateSortOrder(Long folderId, Integer sortOrder, Long userId);

    boolean canAccessFolder(Long folderId, Long userId);
}

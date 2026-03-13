package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.LibVersionCreateRequest;
import com.lawbackend2.lawbackend2.dto.response.LibVersionListResponse;
import com.lawbackend2.lawbackend2.dto.response.LibVersionResponse;
import org.springframework.web.multipart.MultipartFile;

public interface LibDocumentVersionService {

    LibVersionResponse createVersion(LibVersionCreateRequest request, Long userId);

    LibVersionResponse uploadNewVersion(Long documentId, MultipartFile file, String changeSummary, Boolean isMajor, Long userId);

    LibVersionListResponse getVersionList(Long documentId);

    LibVersionResponse getVersion(Long documentId, Integer versionNumber);

    LibVersionResponse getLatestVersion(Long documentId);

    void deleteVersion(Long versionId, Long userId);

    void restoreVersion(Long documentId, Integer versionNumber, Long userId);

    Integer getNextVersionNumber(Long documentId);

    Long getTotalVersions(Long documentId);
}

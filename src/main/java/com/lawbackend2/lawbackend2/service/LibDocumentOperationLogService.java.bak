package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.response.LibOperationLogListResponse;

public interface LibDocumentOperationLogService {

    void logOperation(Long documentId, Long folderId, String operationType, String operationDetail, String oldValue, String newValue, Long userId, String ipAddress, String userAgent);

    LibOperationLogListResponse getDocumentLogs(Long documentId, Integer page, Integer size);

    LibOperationLogListResponse getFolderLogs(Long folderId, Integer page, Integer size);

    LibOperationLogListResponse getMyLogs(Long userId, Integer page, Integer size);

    LibOperationLogListResponse getLogsByType(String operationType, Integer page, Integer size);
}

package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.response.LibOperationLogListResponse;
import com.lawbackend2.lawbackend2.dto.response.LibOperationLogResponse;
import com.lawbackend2.lawbackend2.entity.LibDocumentFolder;
import com.lawbackend2.lawbackend2.entity.LibDocumentOperationLog;
import com.lawbackend2.lawbackend2.repository.LibDocumentFolderRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentOperationLogRepository;
import com.lawbackend2.lawbackend2.service.LibDocumentOperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibDocumentOperationLogServiceImpl implements LibDocumentOperationLogService {

    private final LibDocumentOperationLogRepository logRepository;
    private final LibDocumentFolderRepository folderRepository;

    @Override
    @Transactional
    public void logOperation(Long documentId, Long folderId, String operationType, String operationDetail, 
                            String oldValue, String newValue, Long userId, String ipAddress, String userAgent) {
        LibDocumentOperationLog operationLog = LibDocumentOperationLog.builder()
                .documentId(documentId)
                .folderId(folderId)
                .operationType(operationType)
                .operationDetail(operationDetail)
                .oldValue(oldValue)
                .newValue(newValue)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
        operationLog.setCreateUserId(userId);

        logRepository.save(operationLog);
    }

    @Override
    public LibOperationLogListResponse getDocumentLogs(Long documentId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<LibDocumentOperationLog> logPage = logRepository.findByDocumentIdOrderByCreateTimeDesc(documentId, pageable);

        List<LibOperationLogResponse> logResponses = logPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return LibOperationLogListResponse.builder()
                .total(logPage.getTotalElements())
                .page(page)
                .size(size)
                .totalPages(logPage.getTotalPages())
                .logs(logResponses)
                .build();
    }

    @Override
    public LibOperationLogListResponse getFolderLogs(Long folderId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<LibDocumentOperationLog> logPage = logRepository.findByFolderIdOrderByCreateTimeDesc(folderId, pageable);

        List<LibOperationLogResponse> logResponses = logPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return LibOperationLogListResponse.builder()
                .total(logPage.getTotalElements())
                .page(page)
                .size(size)
                .totalPages(logPage.getTotalPages())
                .logs(logResponses)
                .build();
    }

    @Override
    public LibOperationLogListResponse getMyLogs(Long userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<LibDocumentOperationLog> logPage = logRepository.findByCreateUserId(userId, pageable);

        List<LibOperationLogResponse> logResponses = logPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return LibOperationLogListResponse.builder()
                .total(logPage.getTotalElements())
                .page(page)
                .size(size)
                .totalPages(logPage.getTotalPages())
                .logs(logResponses)
                .build();
    }

    @Override
    public LibOperationLogListResponse getLogsByType(String operationType, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<LibDocumentOperationLog> logPage = logRepository.findByOperationType(operationType, pageable);

        List<LibOperationLogResponse> logResponses = logPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return LibOperationLogListResponse.builder()
                .total(logPage.getTotalElements())
                .page(page)
                .size(size)
                .totalPages(logPage.getTotalPages())
                .logs(logResponses)
                .build();
    }

    private LibOperationLogResponse convertToResponse(LibDocumentOperationLog operationLog) {
        String folderName = null;
        if (operationLog.getFolderId() != null) {
            LibDocumentFolder folder = folderRepository.findById(operationLog.getFolderId()).orElse(null);
            if (folder != null) {
                folderName = folder.getFolderName();
            }
        }

        return LibOperationLogResponse.builder()
                .id(operationLog.getId())
                .documentId(operationLog.getDocumentId())
                .documentName(operationLog.getDocumentName())
                .folderId(operationLog.getFolderId())
                .folderName(folderName)
                .operationType(operationLog.getOperationType())
                .operationDetail(operationLog.getOperationDetail())
                .oldValue(operationLog.getOldValue())
                .newValue(operationLog.getNewValue())
                .ipAddress(operationLog.getIpAddress())
                .userAgent(operationLog.getUserAgent())
                .createTime(operationLog.getCreateTime())
                .createUserId(operationLog.getCreateUserId())
                .build();
    }
}

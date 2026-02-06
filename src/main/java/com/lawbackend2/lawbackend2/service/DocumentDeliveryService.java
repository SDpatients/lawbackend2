package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.DocumentDeliveryApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.DocumentDeliveryApprovalWithFilesRequest;
import com.lawbackend2.lawbackend2.dto.request.DocumentDeliveryApproveRequest;
import com.lawbackend2.lawbackend2.dto.request.DocumentDeliveryCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.DocumentDeliveryDirectWithFilesRequest;
import com.lawbackend2.lawbackend2.dto.request.DocumentDeliveryUpdateRequest;
import com.lawbackend2.lawbackend2.dto.request.DocumentDeliveryWithFilesCreateRequest;
import com.lawbackend2.lawbackend2.dto.response.DocumentDeliveryResponse;
import com.lawbackend2.lawbackend2.dto.response.DocumentDeliveryWithFilesResponse;
import com.lawbackend2.lawbackend2.entity.DocumentDelivery;
import com.lawbackend2.lawbackend2.entity.FileRecord;

import java.util.List;

public interface DocumentDeliveryService {

    Long createDocumentDelivery(DocumentDeliveryCreateRequest request);

    Long createDocumentDeliveryDirect(DocumentDeliveryCreateRequest request);

    Long createDocumentDeliveryForApproval(DocumentDeliveryApprovalRequest request);

    DocumentDeliveryWithFilesResponse createDocumentDeliveryWithFiles(DocumentDeliveryWithFilesCreateRequest request);

    DocumentDeliveryWithFilesResponse createDocumentDeliveryDirectWithFiles(DocumentDeliveryDirectWithFilesRequest request);

    DocumentDeliveryWithFilesResponse createDocumentDeliveryApprovalWithFiles(DocumentDeliveryApprovalWithFilesRequest request);

    void approveDocumentDelivery(DocumentDeliveryApproveRequest request);

    PageResult<DocumentDelivery> getDocumentDeliveryList(Integer pageNum, Integer pageSize, Long caseId, String caseNumber, String documentType, String recipientType, String deliveryMethod, String sendStatus, String status);

    DocumentDelivery getDocumentDeliveryDetail(Long deliveryId);

    DocumentDeliveryResponse getDocumentDeliveryDetailWithCase(Long deliveryId);

    PageResult<DocumentDeliveryResponse> getDocumentDeliveryListWithDetails(Integer pageNum, Integer pageSize, Long caseId, String caseNumber, String documentType, String recipientType, String deliveryMethod, String sendStatus, String status);

    void updateDocumentDelivery(Long deliveryId, DocumentDeliveryUpdateRequest request);

    void deleteDocumentDelivery(Long deliveryId);

    void updateSendStatus(Long deliveryId, String sendStatus, String failureReason);

    void updateDeliveryStatus(Long deliveryId, String sendStatus);

    PageResult<DocumentDelivery> getAllDocumentDeliveryList(Integer pageNum, Integer pageSize, String documentType, String status, String caseNumber, String sendStatus);

    PageResult<DocumentDelivery> getAllDocumentDeliveryWithApprovalIdList(Integer pageNum, Integer pageSize, String documentType, String status, String caseNumber, String sendStatus);

    void updateStatusAndRemark(Long deliveryId, String status, String remark);

    DocumentDeliveryWithFilesResponse updateDocumentDeliveryWithFiles(Long deliveryId, DocumentDeliveryWithFilesCreateRequest request);

    void deleteDocumentDeliveryAttachment(Long deliveryId, Long fileId);

    List<FileRecord> getDocumentDeliveryAttachments(Long deliveryId);

    String getLatestAbbreviation();

    String getAbbreviationByCaseId(Long caseId);
}

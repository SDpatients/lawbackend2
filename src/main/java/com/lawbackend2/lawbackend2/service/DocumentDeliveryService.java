package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.DocumentDeliveryCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.DocumentDeliveryUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.DocumentDeliveryResponse;
import com.lawbackend2.lawbackend2.entity.DocumentDelivery;

import java.util.List;

public interface DocumentDeliveryService {

    Long createDocumentDelivery(DocumentDeliveryCreateRequest request);

    PageResult<DocumentDelivery> getDocumentDeliveryList(Integer pageNum, Integer pageSize, Long caseId, String caseNumber, String documentType, String recipientType, String deliveryMethod, String sendStatus, String status);

    DocumentDelivery getDocumentDeliveryDetail(Long deliveryId);

    DocumentDeliveryResponse getDocumentDeliveryDetailWithCase(Long deliveryId);

    PageResult<DocumentDeliveryResponse> getDocumentDeliveryListWithDetails(Integer pageNum, Integer pageSize, Long caseId, String caseNumber, String documentType, String recipientType, String deliveryMethod, String sendStatus, String status);

    void updateDocumentDelivery(Long deliveryId, DocumentDeliveryUpdateRequest request);

    void deleteDocumentDelivery(Long deliveryId);

    void updateSendStatus(Long deliveryId, String sendStatus, String failureReason);

    void updateDeliveryStatus(Long deliveryId, String sendStatus);

    PageResult<DocumentDelivery> getAllDocumentDeliveryList(Integer pageNum, Integer pageSize, String documentType, String status, String caseNumber);

    void updateStatusAndRemark(Long deliveryId, String status, String remark);
}

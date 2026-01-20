package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.DocumentDeliveryCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.DocumentDeliveryUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.DocumentDeliveryResponse;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.DocumentDelivery;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.DocumentDeliveryRepository;
import com.lawbackend2.lawbackend2.service.DocumentDeliveryService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentDeliveryServiceImpl implements DocumentDeliveryService {

    private final DocumentDeliveryRepository documentDeliveryRepository;
    private final BankruptCaseRepository bankruptCaseRepository;

    public DocumentDeliveryServiceImpl(DocumentDeliveryRepository documentDeliveryRepository,
                                   BankruptCaseRepository bankruptCaseRepository) {
        this.documentDeliveryRepository = documentDeliveryRepository;
        this.bankruptCaseRepository = bankruptCaseRepository;
    }

    @Override
    public Long createDocumentDelivery(DocumentDeliveryCreateRequest request) {
        DocumentDelivery documentDelivery = new DocumentDelivery();
        BeanUtils.copyProperties(request, documentDelivery);

        if (request.getCaseId() != null) {
            BankruptCase bankruptCase = bankruptCaseRepository.findById(request.getCaseId()).orElse(null);
            if (bankruptCase != null) {
                documentDelivery.setCaseNumber(bankruptCase.getCaseNumber());
                documentDelivery.setCaseName(bankruptCase.getCaseName());
            }
        }

        documentDelivery.setSendStatus("PENDING");

        if (request.getStatus() == null) {
            documentDelivery.setStatus("PENDING");
        }

        DocumentDelivery saved = documentDeliveryRepository.save(documentDelivery);
        return saved.getId();
    }

    @Override
    public PageResult<DocumentDelivery> getDocumentDeliveryList(Integer pageNum, Integer pageSize, Long caseId, String caseNumber, String documentType, String recipientType, String deliveryMethod, String sendStatus, String status) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<DocumentDelivery> page = documentDeliveryRepository.findByConditions(caseId, caseNumber, documentType, recipientType, deliveryMethod, sendStatus, status, pageable);

        PageResult<DocumentDelivery> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public DocumentDelivery getDocumentDeliveryDetail(Long deliveryId) {
        return documentDeliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new BusinessException("文书送达记录不存在"));
    }

    @Override
    public DocumentDeliveryResponse getDocumentDeliveryDetailWithCase(Long deliveryId) {
        DocumentDelivery documentDelivery = getDocumentDeliveryDetail(deliveryId);

        DocumentDeliveryResponse response = DocumentDeliveryResponse.builder()
                .id(documentDelivery.getId())
                .caseId(documentDelivery.getCaseId())
                .caseNumber(documentDelivery.getCaseNumber())
                .caseName(documentDelivery.getCaseName())
                .documentName(documentDelivery.getDocumentName())
                .documentType(documentDelivery.getDocumentType())
                .recipientName(documentDelivery.getRecipientName())
                .recipientType(documentDelivery.getRecipientType())
                .contactPhone(documentDelivery.getContactPhone())
                .deliveryAddress(documentDelivery.getDeliveryAddress())
                .deliveryMethod(documentDelivery.getDeliveryMethod())
                .sendStatus(documentDelivery.getSendStatus())
                .deliveryContent(documentDelivery.getDeliveryContent())
                .documentAttachment(documentDelivery.getDocumentAttachment())
                .sendTime(documentDelivery.getSendTime())
                .deliveryTime(documentDelivery.getDeliveryTime())
                .failureReason(documentDelivery.getFailureReason())
                .remark(documentDelivery.getRemark())
                .status(documentDelivery.getStatus())
                .createTime(documentDelivery.getCreateTime())
                .updateTime(documentDelivery.getUpdateTime())
                .createUserId(documentDelivery.getCreateUserId())
                .updateUserId(documentDelivery.getUpdateUserId())
                .build();

        return response;
    }

    @Override
    public PageResult<DocumentDeliveryResponse> getDocumentDeliveryListWithDetails(Integer pageNum, Integer pageSize, Long caseId, String caseNumber, String documentType, String recipientType, String deliveryMethod, String sendStatus, String status) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<DocumentDelivery> page = documentDeliveryRepository.findByConditions(caseId, caseNumber, documentType, recipientType, deliveryMethod, sendStatus, status, pageable);

        List<DocumentDeliveryResponse> responseList = page.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        PageResult<DocumentDeliveryResponse> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(responseList);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDocumentDelivery(Long deliveryId, DocumentDeliveryUpdateRequest request) {
        DocumentDelivery documentDelivery = getDocumentDeliveryDetail(deliveryId);

        if (request.getCaseId() != null) {
            BankruptCase bankruptCase = bankruptCaseRepository.findById(request.getCaseId()).orElse(null);
            if (bankruptCase != null) {
                documentDelivery.setCaseId(request.getCaseId());
                documentDelivery.setCaseNumber(bankruptCase.getCaseNumber());
                documentDelivery.setCaseName(bankruptCase.getCaseName());
            }
        }
        if (request.getDocumentName() != null) {
            documentDelivery.setDocumentName(request.getDocumentName());
        }
        if (request.getDocumentType() != null) {
            documentDelivery.setDocumentType(request.getDocumentType());
        }
        if (request.getRecipientName() != null) {
            documentDelivery.setRecipientName(request.getRecipientName());
        }
        if (request.getRecipientType() != null) {
            documentDelivery.setRecipientType(request.getRecipientType());
        }
        if (request.getContactPhone() != null) {
            documentDelivery.setContactPhone(request.getContactPhone());
        }
        if (request.getDeliveryAddress() != null) {
            documentDelivery.setDeliveryAddress(request.getDeliveryAddress());
        }
        if (request.getDeliveryMethod() != null) {
            documentDelivery.setDeliveryMethod(request.getDeliveryMethod());
        }
        if (request.getSendStatus() != null) {
            documentDelivery.setSendStatus(request.getSendStatus());
            if ("SENT".equals(request.getSendStatus())) {
                documentDelivery.setSendTime(LocalDateTime.now());
            }
        }
        if (request.getDeliveryContent() != null) {
            documentDelivery.setDeliveryContent(request.getDeliveryContent());
        }
        if (request.getDocumentAttachment() != null) {
            documentDelivery.setDocumentAttachment(request.getDocumentAttachment());
        }
        if (request.getFailureReason() != null) {
            documentDelivery.setFailureReason(request.getFailureReason());
        }
        if (request.getRemark() != null) {
            documentDelivery.setRemark(request.getRemark());
        }

        documentDeliveryRepository.save(documentDelivery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDocumentDelivery(Long deliveryId) {
        if (!documentDeliveryRepository.existsById(deliveryId)) {
            throw new BusinessException("文书送达记录不存在");
        }
        documentDeliveryRepository.deleteById(deliveryId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSendStatus(Long deliveryId, String sendStatus, String failureReason) {
        DocumentDelivery documentDelivery = getDocumentDeliveryDetail(deliveryId);
        documentDelivery.setSendStatus(sendStatus);

        if ("SENT".equals(sendStatus)) {
            documentDelivery.setSendTime(LocalDateTime.now());
        }
        if ("FAILED".equals(sendStatus)) {
            documentDelivery.setFailureReason(failureReason);
        }

        documentDeliveryRepository.save(documentDelivery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDeliveryStatus(Long deliveryId, String sendStatus) {
        DocumentDelivery documentDelivery = getDocumentDeliveryDetail(deliveryId);
        documentDelivery.setSendStatus(sendStatus);

        if ("DELIVERED".equals(sendStatus)) {
            documentDelivery.setDeliveryTime(LocalDateTime.now());
        }

        documentDeliveryRepository.save(documentDelivery);
    }

    @Override
    public PageResult<DocumentDelivery> getAllDocumentDeliveryList(Integer pageNum, Integer pageSize, String documentType, String status, String caseNumber) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<DocumentDelivery> page = documentDeliveryRepository.findByFuzzyConditions(documentType, status, caseNumber, pageable);

        PageResult<DocumentDelivery> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatusAndRemark(Long deliveryId, String status, String remark) {
        DocumentDelivery documentDelivery = getDocumentDeliveryDetail(deliveryId);
        
        if (status != null) {
            documentDelivery.setStatus(status);
        }
        if (remark != null) {
            documentDelivery.setRemark(remark);
        }
        
        documentDeliveryRepository.save(documentDelivery);
    }

    private DocumentDeliveryResponse convertToResponse(DocumentDelivery documentDelivery) {
        return DocumentDeliveryResponse.builder()
                .id(documentDelivery.getId())
                .caseId(documentDelivery.getCaseId())
                .caseNumber(documentDelivery.getCaseNumber())
                .caseName(documentDelivery.getCaseName())
                .documentName(documentDelivery.getDocumentName())
                .documentType(documentDelivery.getDocumentType())
                .recipientName(documentDelivery.getRecipientName())
                .recipientType(documentDelivery.getRecipientType())
                .contactPhone(documentDelivery.getContactPhone())
                .deliveryAddress(documentDelivery.getDeliveryAddress())
                .deliveryMethod(documentDelivery.getDeliveryMethod())
                .sendStatus(documentDelivery.getSendStatus())
                .deliveryContent(documentDelivery.getDeliveryContent())
                .documentAttachment(documentDelivery.getDocumentAttachment())
                .sendTime(documentDelivery.getSendTime())
                .deliveryTime(documentDelivery.getDeliveryTime())
                .failureReason(documentDelivery.getFailureReason())
                .remark(documentDelivery.getRemark())
                .status(documentDelivery.getStatus())
                .createTime(documentDelivery.getCreateTime())
                .updateTime(documentDelivery.getUpdateTime())
                .createUserId(documentDelivery.getCreateUserId())
                .updateUserId(documentDelivery.getUpdateUserId())
                .build();
    }
}

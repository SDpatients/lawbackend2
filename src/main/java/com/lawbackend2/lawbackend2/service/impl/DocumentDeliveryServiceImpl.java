package com.lawbackend2.lawbackend2.service.impl;

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
import com.lawbackend2.lawbackend2.entity.Approval;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.DocumentDelivery;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.ApprovalRepository;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.DocumentDeliveryRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.DocumentDeliveryService;
import com.lawbackend2.lawbackend2.service.FileService;
import com.lawbackend2.lawbackend2.service.NotificationService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class DocumentDeliveryServiceImpl implements DocumentDeliveryService {

    private final DocumentDeliveryRepository documentDeliveryRepository;
    private final BankruptCaseRepository bankruptCaseRepository;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ApprovalRepository approvalRepository;

    public DocumentDeliveryServiceImpl(DocumentDeliveryRepository documentDeliveryRepository,
                                   BankruptCaseRepository bankruptCaseRepository,
                                   FileService fileService,
                                   UserRepository userRepository,
                                   NotificationService notificationService,
                                   ApprovalRepository approvalRepository) {
        this.documentDeliveryRepository = documentDeliveryRepository;
        this.bankruptCaseRepository = bankruptCaseRepository;
        this.fileService = fileService;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.approvalRepository = approvalRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDocumentDelivery(DocumentDeliveryCreateRequest request) {
        log.info("开始创建文书送达记录, 案件ID: {}, 文书名称: {}", request.getCaseId(), request.getDocumentName());

        DocumentDelivery documentDelivery = new DocumentDelivery();
        BeanUtils.copyProperties(request, documentDelivery);

        if (request.getCaseId() != null) {
            BankruptCase bankruptCase = bankruptCaseRepository.findById(request.getCaseId()).orElse(null);
            if (bankruptCase != null) {
                documentDelivery.setCaseNumber(bankruptCase.getCaseNumber());
                documentDelivery.setCaseName(bankruptCase.getCaseName());
            }
        }

        String abbreviation = request.getAbbreviation();
        if (abbreviation == null || abbreviation.trim().isEmpty()) {
            abbreviation = documentDeliveryRepository.findLatestAbbreviation().orElse(null);
        }
        documentDelivery.setAbbreviation(abbreviation);

        String documentNumber = generateDocumentNumber(abbreviation);
        documentDelivery.setDocumentNumber(documentNumber);

        Long currentUserId = SecurityUtil.getCurrentUserId();
        documentDelivery.setCreateUserId(currentUserId);

        if (request.getSendStatus() != null) {
            documentDelivery.setSendStatus(request.getSendStatus());
            if ("SENT".equals(request.getSendStatus())) {
                documentDelivery.setSendTime(LocalDateTime.now());
            }
        }

        if (request.getStatus() == null) {
            documentDelivery.setStatus("PENDING");
        }

        DocumentDelivery saved = documentDeliveryRepository.save(documentDelivery);

        User user = userRepository.findById(currentUserId).orElse(null);
        String realName = user != null ? user.getRealName() : "未知用户";
        String content = String.format("%s 完成了文书送达：%s", realName, request.getDocumentName());
        notificationService.sendNotificationToAdminAndSuperAdmin(
                "文书送达通知",
                content,
                "DOCUMENT_DELIVERY",
                saved.getId(),
                "DocumentDelivery",
                currentUserId,
                realName
        );

        log.info("文书送达记录创建成功, ID: {}", saved.getId());
        return saved.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDocumentDeliveryDirect(DocumentDeliveryCreateRequest request) {
        log.info("开始创建文书送达记录（直接上传，无需审批）, 案件ID: {}, 文书名称: {}", request.getCaseId(), request.getDocumentName());

        DocumentDelivery documentDelivery = new DocumentDelivery();
        BeanUtils.copyProperties(request, documentDelivery);

        if (request.getCaseId() != null) {
            BankruptCase bankruptCase = bankruptCaseRepository.findById(request.getCaseId()).orElse(null);
            if (bankruptCase != null) {
                documentDelivery.setCaseNumber(bankruptCase.getCaseNumber());
                documentDelivery.setCaseName(bankruptCase.getCaseName());
            }
        }

        String abbreviation = request.getAbbreviation();
        if (abbreviation == null || abbreviation.trim().isEmpty()) {
            abbreviation = documentDeliveryRepository.findLatestAbbreviation().orElse(null);
        }
        documentDelivery.setAbbreviation(abbreviation);

        String documentNumber = generateDocumentNumber(abbreviation);
        documentDelivery.setDocumentNumber(documentNumber);

        Long currentUserId = SecurityUtil.getCurrentUserId();
        documentDelivery.setCreateUserId(currentUserId);
        documentDelivery.setStatus("APPROVED");
        documentDelivery.setSendStatus("PENDING");

        if (request.getSendStatus() != null) {
            documentDelivery.setSendStatus(request.getSendStatus());
            if ("SENT".equals(request.getSendStatus())) {
                documentDelivery.setSendTime(LocalDateTime.now());
            }
        }

        DocumentDelivery saved = documentDeliveryRepository.save(documentDelivery);

        User user = userRepository.findById(currentUserId).orElse(null);
        String realName = user != null ? user.getRealName() : "未知用户";
        String content = String.format("%s 完成了文书送达（直接上传）：%s", realName, request.getDocumentName());
        notificationService.sendNotificationToAdminAndSuperAdmin(
                "文书送达通知",
                content,
                "DOCUMENT_DELIVERY",
                saved.getId(),
                "DocumentDelivery",
                currentUserId,
                realName
        );

        log.info("文书送达记录创建成功（直接上传）, ID: {}", saved.getId());
        return saved.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDocumentDeliveryForApproval(DocumentDeliveryApprovalRequest request) {
        log.info("开始创建文书送达记录（需要审批）, 案件ID: {}, 文书名称: {}", request.getCaseId(), request.getDocumentName());

        DocumentDelivery documentDelivery = new DocumentDelivery();
        BeanUtils.copyProperties(request, documentDelivery);

        if (request.getCaseId() != null) {
            BankruptCase bankruptCase = bankruptCaseRepository.findById(request.getCaseId()).orElse(null);
            if (bankruptCase != null) {
                documentDelivery.setCaseNumber(bankruptCase.getCaseNumber());
                documentDelivery.setCaseName(bankruptCase.getCaseName());
            }
        }

        String abbreviation = request.getAbbreviation();
        if (abbreviation == null || abbreviation.trim().isEmpty()) {
            abbreviation = documentDeliveryRepository.findLatestAbbreviation().orElse(null);
        }
        documentDelivery.setAbbreviation(abbreviation);

        String documentNumber = generateDocumentNumber(abbreviation);
        documentDelivery.setDocumentNumber(documentNumber);

        Long currentUserId = SecurityUtil.getCurrentUserId();
        documentDelivery.setCreateUserId(currentUserId);
        documentDelivery.setStatus("PENDING");
        documentDelivery.setSendStatus("PENDING");

        DocumentDelivery saved = documentDeliveryRepository.save(documentDelivery);
        Long deliveryId = saved.getId();

        Approval approval = new Approval();
        approval.setCaseId(request.getCaseId());
        approval.setLawyerId(currentUserId);
        approval.setApprovalType("DOCUMENT_DELIVERY");
        approval.setApprovalTitle(request.getApprovalTitle());
        approval.setApprovalContent(request.getApprovalContent());
        approval.setApprovalStatus("PENDING");
        approval.setApprovalCount(0);
        approval.setStatus("ACTIVE");
        approval.setCreateUserId(currentUserId);

        approvalRepository.save(approval);

        User user = userRepository.findById(currentUserId).orElse(null);
        String realName = user != null ? user.getRealName() : "未知用户";
        String content = String.format("%s 提交了文书送达审批：%s", realName, request.getDocumentName());
        notificationService.sendNotificationToAdminAndSuperAdmin(
                "文书送达审批通知",
                content,
                "DOCUMENT_DELIVERY_APPROVAL",
                deliveryId,
                "DocumentDelivery",
                currentUserId,
                realName
        );

        log.info("文书送达记录创建成功（需要审批）, ID: {}", deliveryId);
        return deliveryId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveDocumentDelivery(DocumentDeliveryApproveRequest request) {
        log.info("开始审批文书送达, 送达记录ID: {}, 审批结果: {}", request.getDeliveryId(), request.getApprovalResult());

        DocumentDelivery documentDelivery = getDocumentDeliveryDetail(request.getDeliveryId());

        if (!"PENDING".equals(documentDelivery.getStatus())) {
            throw new BusinessException("该文书送达已处理，无法重复审批");
        }

        Long currentUserId = SecurityUtil.getCurrentUserId();

        Approval approval = approvalRepository.findByCaseIdAndApprovalType(documentDelivery.getCaseId(), "DOCUMENT_DELIVERY")
                .orElseThrow(() -> new BusinessException("未找到对应的审批记录"));

        if (!"PENDING".equals(approval.getApprovalStatus())) {
            throw new BusinessException("该审批已处理，无法重复审批");
        }

        LocalDateTime now = LocalDateTime.now();

        if ("PASS".equals(request.getApprovalResult())) {
            documentDelivery.setStatus("APPROVED");
            approval.setApprovalStatus("APPROVED");
            approval.setApprovalResult("PASS");
        } else {
            documentDelivery.setStatus("REJECTED");
            approval.setApprovalStatus("REJECTED");
            approval.setApprovalResult("FAIL");
        }

        approval.setApproverId(currentUserId);
        approval.setApprovalDate(now);
        approval.setApprovalCount(approval.getApprovalCount() + 1);
        approval.setRemark(request.getRemark());

        approvalRepository.save(approval);
        documentDeliveryRepository.save(documentDelivery);

        User approver = userRepository.findById(currentUserId).orElse(null);
        String approverName = approver != null ? approver.getRealName() : "未知用户";
        String content = String.format("%s %s了文书送达：%s", approverName, "PASS".equals(request.getApprovalResult()) ? "通过" : "驳回", documentDelivery.getDocumentName());
        notificationService.sendNotificationToUser(
                documentDelivery.getCreateUserId(),
                "文书送达审批结果",
                content,
                "DOCUMENT_DELIVERY",
                documentDelivery.getId(),
                "DocumentDelivery",
                currentUserId,
                approverName
        );

        log.info("文书送达审批完成, ID: {}, 结果: {}", request.getDeliveryId(), request.getApprovalResult());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentDeliveryWithFilesResponse createDocumentDeliveryWithFiles(DocumentDeliveryWithFilesCreateRequest request) {
        log.info("开始创建文书送达记录并上传文件, 案件ID: {}, 文书名称: {}, 文件数量: {}",
                request.getCaseId(), request.getDocumentName(), request.getFiles().size());

        DocumentDelivery documentDelivery = new DocumentDelivery();
        BeanUtils.copyProperties(request, documentDelivery);

        if (request.getCaseId() != null) {
            BankruptCase bankruptCase = bankruptCaseRepository.findById(request.getCaseId()).orElse(null);
            if (bankruptCase != null) {
                documentDelivery.setCaseNumber(bankruptCase.getCaseNumber());
                documentDelivery.setCaseName(bankruptCase.getCaseName());
            }
        }

        String abbreviation = request.getAbbreviation();
        if (abbreviation == null || abbreviation.trim().isEmpty()) {
            abbreviation = documentDeliveryRepository.findLatestAbbreviation().orElse(null);
        }
        documentDelivery.setAbbreviation(abbreviation);

        String documentNumber = generateDocumentNumber(abbreviation);
        documentDelivery.setDocumentNumber(documentNumber);

        Long currentUserId = SecurityUtil.getCurrentUserId();
        documentDelivery.setCreateUserId(currentUserId);

        if (request.getSendStatus() != null) {
            documentDelivery.setSendStatus(request.getSendStatus());
            if ("SENT".equals(request.getSendStatus())) {
                documentDelivery.setSendTime(LocalDateTime.now());
            }
        }

        if (request.getStatus() == null) {
            documentDelivery.setStatus("PENDING");
        }

        DocumentDelivery saved = documentDeliveryRepository.save(documentDelivery);
        Long deliveryId = saved.getId();

        log.info("文书送达记录创建成功, ID: {}", deliveryId);

        List<FileRecord> uploadedFiles = new ArrayList<>();
        List<String> fileDescriptions = request.getFileDescriptions();

        for (int i = 0; i < request.getFiles().size(); i++) {
            MultipartFile file = request.getFiles().get(i);
            String description = (fileDescriptions != null && i < fileDescriptions.size()) ? fileDescriptions.get(i) : null;

            log.info("开始上传文件: {}, 文件大小: {} bytes", file.getOriginalFilename(), file.getSize());

            FileRecord fileRecord = fileService.uploadFile(file, "DOCUMENT_DELIVERY", String.valueOf(deliveryId));
            uploadedFiles.add(fileRecord);

            log.info("文件上传成功, 文件ID: {}", fileRecord.getId());
        }

        DocumentDeliveryWithFilesResponse response = new DocumentDeliveryWithFilesResponse();
        response.setDeliveryId(deliveryId);

        List<DocumentDeliveryWithFilesResponse.FileRecordInfo> fileInfos = uploadedFiles.stream()
                .map(fileRecord -> {
                    DocumentDeliveryWithFilesResponse.FileRecordInfo fileInfo = new DocumentDeliveryWithFilesResponse.FileRecordInfo();
                    fileInfo.setFileId(fileRecord.getId());
                    fileInfo.setOriginalFileName(fileRecord.getOriginalFileName());
                    fileInfo.setStoredFileName(fileRecord.getStoredFileName());
                    fileInfo.setFileSize(fileRecord.getFileSize());
                    fileInfo.setFileExtension(fileRecord.getFileExtension());
                    fileInfo.setMimeType(fileRecord.getMimeType());
                    return fileInfo;
                })
                .collect(Collectors.toList());

        response.setFiles(fileInfos);

        User user = userRepository.findById(currentUserId).orElse(null);
        String realName = user != null ? user.getRealName() : "未知用户";
        String content = String.format("%s 完成了文书送达：%s", realName, request.getDocumentName());
        notificationService.sendNotificationToAdminAndSuperAdmin(
                "文书送达通知",
                content,
                "DOCUMENT_DELIVERY",
                deliveryId,
                "DocumentDelivery",
                currentUserId,
                realName
        );

        log.info("文书送达记录及文件创建完成, 送达ID: {}, 文件数量: {}", deliveryId, uploadedFiles.size());

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentDeliveryWithFilesResponse createDocumentDeliveryDirectWithFiles(DocumentDeliveryDirectWithFilesRequest request) {
        log.info("开始创建文书送达记录并上传文件（直接上传，无需审批）, 案件ID: {}, 文书名称: {}, 文件数量: {}",
                request.getCaseId(), request.getDocumentName(), request.getFiles().size());

        DocumentDelivery documentDelivery = new DocumentDelivery();
        BeanUtils.copyProperties(request, documentDelivery);

        if (request.getCaseId() != null) {
            BankruptCase bankruptCase = bankruptCaseRepository.findById(request.getCaseId()).orElse(null);
            if (bankruptCase != null) {
                documentDelivery.setCaseNumber(bankruptCase.getCaseNumber());
                documentDelivery.setCaseName(bankruptCase.getCaseName());
            }
        }

        String abbreviation = request.getAbbreviation();
        if (abbreviation == null || abbreviation.trim().isEmpty()) {
            abbreviation = documentDeliveryRepository.findLatestAbbreviation().orElse(null);
        }
        documentDelivery.setAbbreviation(abbreviation);

        String documentNumber = generateDocumentNumber(abbreviation);
        documentDelivery.setDocumentNumber(documentNumber);

        Long currentUserId = SecurityUtil.getCurrentUserId();
        documentDelivery.setCreateUserId(currentUserId);
        documentDelivery.setStatus("APPROVED");
        documentDelivery.setSendStatus("PENDING");

        if (request.getSendStatus() != null) {
            documentDelivery.setSendStatus(request.getSendStatus());
            if ("SENT".equals(request.getSendStatus())) {
                documentDelivery.setSendTime(LocalDateTime.now());
            }
        }

        DocumentDelivery saved = documentDeliveryRepository.save(documentDelivery);
        Long deliveryId = saved.getId();

        log.info("文书送达记录创建成功（直接上传）, ID: {}", deliveryId);

        List<FileRecord> uploadedFiles = new ArrayList<>();
        List<String> fileDescriptions = request.getFileDescriptions();

        for (int i = 0; i < request.getFiles().size(); i++) {
            MultipartFile file = request.getFiles().get(i);
            String description = (fileDescriptions != null && i < fileDescriptions.size()) ? fileDescriptions.get(i) : null;

            log.info("开始上传文件: {}, 文件大小: {} bytes", file.getOriginalFilename(), file.getSize());

            FileRecord fileRecord = fileService.uploadFile(file, "DOCUMENT_DELIVERY", String.valueOf(deliveryId));
            uploadedFiles.add(fileRecord);

            log.info("文件上传成功, 文件ID: {}", fileRecord.getId());
        }

        DocumentDeliveryWithFilesResponse response = new DocumentDeliveryWithFilesResponse();
        response.setDeliveryId(deliveryId);

        List<DocumentDeliveryWithFilesResponse.FileRecordInfo> fileInfos = uploadedFiles.stream()
                .map(fileRecord -> {
                    DocumentDeliveryWithFilesResponse.FileRecordInfo fileInfo = new DocumentDeliveryWithFilesResponse.FileRecordInfo();
                    fileInfo.setFileId(fileRecord.getId());
                    fileInfo.setOriginalFileName(fileRecord.getOriginalFileName());
                    fileInfo.setStoredFileName(fileRecord.getStoredFileName());
                    fileInfo.setFileSize(fileRecord.getFileSize());
                    fileInfo.setFileExtension(fileRecord.getFileExtension());
                    fileInfo.setMimeType(fileRecord.getMimeType());
                    return fileInfo;
                })
                .collect(Collectors.toList());

        response.setFiles(fileInfos);

        User user = userRepository.findById(currentUserId).orElse(null);
        String realName = user != null ? user.getRealName() : "未知用户";
        String content = String.format("%s 完成了文书送达（直接上传）：%s", realName, request.getDocumentName());
        notificationService.sendNotificationToAdminAndSuperAdmin(
                "文书送达通知",
                content,
                "DOCUMENT_DELIVERY",
                deliveryId,
                "DocumentDelivery",
                currentUserId,
                realName
        );

        log.info("文书送达记录及文件创建完成（直接上传）, 送达ID: {}, 文件数量: {}", deliveryId, uploadedFiles.size());

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentDeliveryWithFilesResponse createDocumentDeliveryApprovalWithFiles(DocumentDeliveryApprovalWithFilesRequest request) {
        log.info("开始创建文书送达记录并上传文件（需要审批）, 案件ID: {}, 文书名称: {}, 文件数量: {}",
                request.getCaseId(), request.getDocumentName(), request.getFiles().size());

        DocumentDelivery documentDelivery = new DocumentDelivery();
        BeanUtils.copyProperties(request, documentDelivery);

        if (request.getCaseId() != null) {
            BankruptCase bankruptCase = bankruptCaseRepository.findById(request.getCaseId()).orElse(null);
            if (bankruptCase != null) {
                documentDelivery.setCaseNumber(bankruptCase.getCaseNumber());
                documentDelivery.setCaseName(bankruptCase.getCaseName());
            }
        }

        String abbreviation = request.getAbbreviation();
        if (abbreviation == null || abbreviation.trim().isEmpty()) {
            abbreviation = documentDeliveryRepository.findLatestAbbreviation().orElse(null);
        }
        documentDelivery.setAbbreviation(abbreviation);

        String documentNumber = generateDocumentNumber(abbreviation);
        documentDelivery.setDocumentNumber(documentNumber);

        Long currentUserId = SecurityUtil.getCurrentUserId();
        documentDelivery.setCreateUserId(currentUserId);
        documentDelivery.setStatus("PENDING");
        documentDelivery.setSendStatus("PENDING");

        DocumentDelivery saved = documentDeliveryRepository.save(documentDelivery);
        Long deliveryId = saved.getId();

        log.info("文书送达记录创建成功（需要审批）, ID: {}", deliveryId);

        List<FileRecord> uploadedFiles = new ArrayList<>();
        List<String> fileDescriptions = request.getFileDescriptions();

        for (int i = 0; i < request.getFiles().size(); i++) {
            MultipartFile file = request.getFiles().get(i);
            String description = (fileDescriptions != null && i < fileDescriptions.size()) ? fileDescriptions.get(i) : null;

            log.info("开始上传文件: {}, 文件大小: {} bytes", file.getOriginalFilename(), file.getSize());

            FileRecord fileRecord = fileService.uploadFile(file, "DOCUMENT_DELIVERY", String.valueOf(deliveryId));
            uploadedFiles.add(fileRecord);

            log.info("文件上传成功, 文件ID: {}", fileRecord.getId());
        }

        Approval approval = new Approval();
        approval.setCaseId(request.getCaseId());
        approval.setLawyerId(currentUserId);
        approval.setApprovalType("DOCUMENT_DELIVERY");
        approval.setApprovalTitle(request.getApprovalTitle());
        approval.setApprovalContent(request.getApprovalContent());
        approval.setApprovalStatus("PENDING");
        approval.setApprovalCount(0);
        approval.setStatus("ACTIVE");
        approval.setCreateUserId(currentUserId);

        approvalRepository.save(approval);

        DocumentDeliveryWithFilesResponse response = new DocumentDeliveryWithFilesResponse();
        response.setDeliveryId(deliveryId);

        List<DocumentDeliveryWithFilesResponse.FileRecordInfo> fileInfos = uploadedFiles.stream()
                .map(fileRecord -> {
                    DocumentDeliveryWithFilesResponse.FileRecordInfo fileInfo = new DocumentDeliveryWithFilesResponse.FileRecordInfo();
                    fileInfo.setFileId(fileRecord.getId());
                    fileInfo.setOriginalFileName(fileRecord.getOriginalFileName());
                    fileInfo.setStoredFileName(fileRecord.getStoredFileName());
                    fileInfo.setFileSize(fileRecord.getFileSize());
                    fileInfo.setFileExtension(fileRecord.getFileExtension());
                    fileInfo.setMimeType(fileRecord.getMimeType());
                    return fileInfo;
                })
                .collect(Collectors.toList());

        response.setFiles(fileInfos);

        User user = userRepository.findById(currentUserId).orElse(null);
        String realName = user != null ? user.getRealName() : "未知用户";
        String content = String.format("%s 提交了文书送达审批：%s", realName, request.getDocumentName());
        notificationService.sendNotificationToAdminAndSuperAdmin(
                "文书送达审批通知",
                content,
                "DOCUMENT_DELIVERY_APPROVAL",
                deliveryId,
                "DocumentDelivery",
                currentUserId,
                realName
        );

        log.info("文书送达记录及文件创建完成（需要审批）, 送达ID: {}, 文件数量: {}", deliveryId, uploadedFiles.size());

        return response;
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
                .documentNumber(documentDelivery.getDocumentNumber())
                .abbreviation(documentDelivery.getAbbreviation())
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
        if (request.getAbbreviation() != null) {
            documentDelivery.setAbbreviation(request.getAbbreviation());
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
    public PageResult<DocumentDelivery> getAllDocumentDeliveryList(Integer pageNum, Integer pageSize, String documentType, String status, String caseNumber, String sendStatus) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<DocumentDelivery> page = documentDeliveryRepository.findByFuzzyConditions(documentType, status, caseNumber, sendStatus, pageable);

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
                .documentNumber(documentDelivery.getDocumentNumber())
                .abbreviation(documentDelivery.getAbbreviation())
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

    private String generateDocumentNumber(String abbreviation) {
        if (abbreviation == null || abbreviation.trim().isEmpty()) {
            return null;
        }

        int currentYear = Year.now().getValue();
        String prefix = currentYear + abbreviation + "破管字第";
        
        Long maxNumber = documentDeliveryRepository.findMaxDocumentNumberByPrefix(prefix + "%");
        int nextNumber = (maxNumber != null) ? maxNumber.intValue() + 1 : 1;
        
        return prefix + nextNumber + "号";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentDeliveryWithFilesResponse updateDocumentDeliveryWithFiles(Long deliveryId, DocumentDeliveryWithFilesCreateRequest request) {
        log.info("开始更新文书送达记录并上传文件, 送达记录ID: {}, 文件数量: {}", deliveryId, request.getFiles().size());

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
        if (request.getAbbreviation() != null) {
            documentDelivery.setAbbreviation(request.getAbbreviation());
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

        documentDeliveryRepository.save(documentDelivery);

        List<FileRecord> uploadedFiles = new ArrayList<>();
        List<String> fileDescriptions = request.getFileDescriptions();

        for (int i = 0; i < request.getFiles().size(); i++) {
            MultipartFile file = request.getFiles().get(i);
            String description = (fileDescriptions != null && i < fileDescriptions.size()) ? fileDescriptions.get(i) : null;

            log.info("开始上传文件: {}, 文件大小: {} bytes", file.getOriginalFilename(), file.getSize());

            FileRecord fileRecord = fileService.uploadFile(file, "DOCUMENT_DELIVERY", String.valueOf(deliveryId));
            uploadedFiles.add(fileRecord);

            log.info("文件上传成功, 文件ID: {}", fileRecord.getId());
        }

        DocumentDeliveryWithFilesResponse response = new DocumentDeliveryWithFilesResponse();
        response.setDeliveryId(deliveryId);

        List<DocumentDeliveryWithFilesResponse.FileRecordInfo> fileInfos = uploadedFiles.stream()
                .map(fileRecord -> {
                    DocumentDeliveryWithFilesResponse.FileRecordInfo fileInfo = new DocumentDeliveryWithFilesResponse.FileRecordInfo();
                    fileInfo.setFileId(fileRecord.getId());
                    fileInfo.setOriginalFileName(fileRecord.getOriginalFileName());
                    fileInfo.setStoredFileName(fileRecord.getStoredFileName());
                    fileInfo.setFileSize(fileRecord.getFileSize());
                    fileInfo.setFileExtension(fileRecord.getFileExtension());
                    fileInfo.setMimeType(fileRecord.getMimeType());
                    return fileInfo;
                })
                .collect(Collectors.toList());

        response.setFiles(fileInfos);

        Long currentUserId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(currentUserId).orElse(null);
        String realName = user != null ? user.getRealName() : "未知用户";
        String content = String.format("%s 更新了文书送达：%s", realName, request.getDocumentName());
        notificationService.sendNotificationToAdminAndSuperAdmin(
                "文书送达更新通知",
                content,
                "DOCUMENT_DELIVERY",
                deliveryId,
                "DocumentDelivery",
                currentUserId,
                realName
        );

        log.info("文书送达记录更新完成, 送达ID: {}, 新增文件数量: {}", deliveryId, uploadedFiles.size());

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDocumentDeliveryAttachment(Long deliveryId, Long fileId) {
        log.info("删除文书送达附件, 送达记录ID: {}, 文件ID: {}", deliveryId, fileId);

        getDocumentDeliveryDetail(deliveryId);

        fileService.deleteFile(fileId);

        log.info("文书送达附件删除成功, 文件ID: {}", fileId);
    }

    @Override
    public List<FileRecord> getDocumentDeliveryAttachments(Long deliveryId) {
        log.debug("查询文书送达附件列表, 送达记录ID: {}", deliveryId);

        getDocumentDeliveryDetail(deliveryId);

        return fileService.getFileList(1, 100, "DOCUMENT_DELIVERY", String.valueOf(deliveryId), null).getList();
    }

    @Override
    public String getLatestAbbreviation() {
        return documentDeliveryRepository.findLatestAbbreviation().orElse(null);
    }
}

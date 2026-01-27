package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
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
import com.lawbackend2.lawbackend2.service.DocumentDeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "文书送达管理")
@RestController
@RequestMapping("/document-delivery")
@Validated
public class DocumentDeliveryController {

    private final DocumentDeliveryService documentDeliveryService;

    public DocumentDeliveryController(DocumentDeliveryService documentDeliveryService) {
        this.documentDeliveryService = documentDeliveryService;
    }

    @Operation(summary = "创建文书送达")
    @PostMapping
    public Result<Map<String, Object>> createDocumentDelivery(@Valid @RequestBody DocumentDeliveryCreateRequest request) {
        Long deliveryId = documentDeliveryService.createDocumentDelivery(request);

        Map<String, Object> data = new HashMap<>();
        data.put("deliveryId", deliveryId);

        return Result.success(data);
    }

    @Operation(summary = "文书上传（直接上传，无需审批）")
    @PostMapping("/direct-upload")
    public Result<Map<String, Object>> createDocumentDeliveryDirect(@Valid @RequestBody DocumentDeliveryCreateRequest request) {
        Long deliveryId = documentDeliveryService.createDocumentDeliveryDirect(request);

        Map<String, Object> data = new HashMap<>();
        data.put("deliveryId", deliveryId);

        return Result.success("文书上传成功", data);
    }

    @Operation(summary = "文书审批提交（需要审批流程）")
    @PostMapping("/submit-for-approval")
    public Result<Map<String, Object>> createDocumentDeliveryForApproval(@Valid @RequestBody DocumentDeliveryApprovalRequest request) {
        Long deliveryId = documentDeliveryService.createDocumentDeliveryForApproval(request);

        Map<String, Object> data = new HashMap<>();
        data.put("deliveryId", deliveryId);

        return Result.success("文书审批提交成功，等待管理员审批", data);
    }

    @Operation(summary = "管理员审批文书送达")
    @PostMapping("/approve")
    public Result<Void> approveDocumentDelivery(@Valid @RequestBody DocumentDeliveryApproveRequest request) {
        documentDeliveryService.approveDocumentDelivery(request);
        return Result.success();
    }

    @Operation(summary = "文书上传（直接上传，无需审批，带文件）")
    @PostMapping("/direct-upload-with-files")
    public Result<DocumentDeliveryWithFilesResponse> createDocumentDeliveryDirectWithFiles(
            @Parameter(description = "案件ID") @RequestParam Long caseId,
            @Parameter(description = "文书名称") @RequestParam String documentName,
            @Parameter(description = "缩写") @RequestParam(required = false) String abbreviation,
            @Parameter(description = "文书类型") @RequestParam(required = false) String documentType,
            @Parameter(description = "受送达人") @RequestParam String recipientName,
            @Parameter(description = "受送达人类型") @RequestParam(required = false) String recipientType,
            @Parameter(description = "联系电话") @RequestParam(required = false) String contactPhone,
            @Parameter(description = "送达地址") @RequestParam(required = false) String deliveryAddress,
            @Parameter(description = "送达方式") @RequestParam(required = false) String deliveryMethod,
            @Parameter(description = "送达内容") @RequestParam(required = false) String deliveryContent,
            @Parameter(description = "发送状态") @RequestParam(required = false) String sendStatus,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "文件列表") @RequestParam("files") List<MultipartFile> files,
            @Parameter(description = "文件描述列表") @RequestParam(value = "fileDescriptions", required = false) List<String> fileDescriptions) {

        DocumentDeliveryDirectWithFilesRequest request = new DocumentDeliveryDirectWithFilesRequest();
        request.setCaseId(caseId);
        request.setDocumentName(documentName);
        request.setAbbreviation(abbreviation);
        request.setDocumentType(documentType);
        request.setRecipientName(recipientName);
        request.setRecipientType(recipientType);
        request.setContactPhone(contactPhone);
        request.setDeliveryAddress(deliveryAddress);
        request.setDeliveryMethod(deliveryMethod);
        request.setDeliveryContent(deliveryContent);
        request.setSendStatus(sendStatus);
        request.setStatus(status);
        request.setFiles(files);
        request.setFileDescriptions(fileDescriptions);

        DocumentDeliveryWithFilesResponse response = documentDeliveryService.createDocumentDeliveryDirectWithFiles(request);
        return Result.success("文书上传成功", response);
    }

    @Operation(summary = "文书审批提交（需要审批流程，带文件）")
    @PostMapping("/submit-for-approval-with-files")
    public Result<DocumentDeliveryWithFilesResponse> createDocumentDeliveryApprovalWithFiles(
            @Parameter(description = "案件ID") @RequestParam Long caseId,
            @Parameter(description = "文书名称") @RequestParam String documentName,
            @Parameter(description = "缩写") @RequestParam(required = false) String abbreviation,
            @Parameter(description = "文书类型") @RequestParam(required = false) String documentType,
            @Parameter(description = "受送达人") @RequestParam String recipientName,
            @Parameter(description = "受送达人类型") @RequestParam(required = false) String recipientType,
            @Parameter(description = "联系电话") @RequestParam(required = false) String contactPhone,
            @Parameter(description = "送达地址") @RequestParam(required = false) String deliveryAddress,
            @Parameter(description = "送达方式") @RequestParam(required = false) String deliveryMethod,
            @Parameter(description = "送达内容") @RequestParam(required = false) String deliveryContent,
            @Parameter(description = "审批标题") @RequestParam String approvalTitle,
            @Parameter(description = "审批内容") @RequestParam(required = false) String approvalContent,
            @Parameter(description = "备注") @RequestParam(required = false) String remark,
            @Parameter(description = "文件列表") @RequestParam("files") List<MultipartFile> files,
            @Parameter(description = "文件描述列表") @RequestParam(value = "fileDescriptions", required = false) List<String> fileDescriptions) {

        DocumentDeliveryApprovalWithFilesRequest request = new DocumentDeliveryApprovalWithFilesRequest();
        request.setCaseId(caseId);
        request.setDocumentName(documentName);
        request.setAbbreviation(abbreviation);
        request.setDocumentType(documentType);
        request.setRecipientName(recipientName);
        request.setRecipientType(recipientType);
        request.setContactPhone(contactPhone);
        request.setDeliveryAddress(deliveryAddress);
        request.setDeliveryMethod(deliveryMethod);
        request.setDeliveryContent(deliveryContent);
        request.setApprovalTitle(approvalTitle);
        request.setApprovalContent(approvalContent);
        request.setRemark(remark);
        request.setFiles(files);
        request.setFileDescriptions(fileDescriptions);

        DocumentDeliveryWithFilesResponse response = documentDeliveryService.createDocumentDeliveryApprovalWithFiles(request);
        return Result.success("文书审批提交成功，等待管理员审批", response);
    }

    @Operation(summary = "创建文书送达(带文件)")
    @PostMapping("/with-files")
    public Result<DocumentDeliveryWithFilesResponse> createDocumentDeliveryWithFiles(
            @Parameter(description = "案件ID") @RequestParam Long caseId,
            @Parameter(description = "文书名称") @RequestParam String documentName,
            @Parameter(description = "缩写") @RequestParam(required = false) String abbreviation,
            @Parameter(description = "文书类型") @RequestParam(required = false) String documentType,
            @Parameter(description = "受送达人") @RequestParam String recipientName,
            @Parameter(description = "受送达人类型") @RequestParam(required = false) String recipientType,
            @Parameter(description = "联系电话") @RequestParam(required = false) String contactPhone,
            @Parameter(description = "送达地址") @RequestParam(required = false) String deliveryAddress,
            @Parameter(description = "送达方式") @RequestParam(required = false) String deliveryMethod,
            @Parameter(description = "送达内容") @RequestParam(required = false) String deliveryContent,
            @Parameter(description = "发送状态") @RequestParam(required = false) String sendStatus,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "文件列表") @RequestParam("files") List<org.springframework.web.multipart.MultipartFile> files,
            @Parameter(description = "文件描述列表") @RequestParam(value = "fileDescriptions", required = false) List<String> fileDescriptions) {

        DocumentDeliveryWithFilesCreateRequest request = new DocumentDeliveryWithFilesCreateRequest();
        request.setCaseId(caseId);
        request.setDocumentName(documentName);
        request.setAbbreviation(abbreviation);
        request.setDocumentType(documentType);
        request.setRecipientName(recipientName);
        request.setRecipientType(recipientType);
        request.setContactPhone(contactPhone);
        request.setDeliveryAddress(deliveryAddress);
        request.setDeliveryMethod(deliveryMethod);
        request.setDeliveryContent(deliveryContent);
        request.setSendStatus(sendStatus);
        request.setStatus(status);
        request.setFiles(files);
        request.setFileDescriptions(fileDescriptions);

        DocumentDeliveryWithFilesResponse response = documentDeliveryService.createDocumentDeliveryWithFiles(request);
        return Result.success("文书送达及文件创建成功", response);
    }

    @Operation(summary = "文书送达列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<DocumentDelivery>> getDocumentDeliveryList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "案号") @RequestParam(required = false) String caseNumber,
            @Parameter(description = "文书类型") @RequestParam(required = false) String documentType,
            @Parameter(description = "受送达人类型") @RequestParam(required = false) String recipientType,
            @Parameter(description = "送达方式") @RequestParam(required = false) String deliveryMethod,
            @Parameter(description = "发送状态") @RequestParam(required = false) String sendStatus,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {

        PageResult<DocumentDelivery> result = documentDeliveryService.getDocumentDeliveryList(pageNum, pageSize, caseId, caseNumber, documentType, recipientType, deliveryMethod, sendStatus, status);
        return Result.success(result);
    }

    @Operation(summary = "文书送达列表详情(分页)")
    @GetMapping("/list/details")
    public Result<PageResult<DocumentDeliveryResponse>> getDocumentDeliveryListWithDetails(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "案号") @RequestParam(required = false) String caseNumber,
            @Parameter(description = "文书类型") @RequestParam(required = false) String documentType,
            @Parameter(description = "受送达人类型") @RequestParam(required = false) String recipientType,
            @Parameter(description = "送达方式") @RequestParam(required = false) String deliveryMethod,
            @Parameter(description = "发送状态") @RequestParam(required = false) String sendStatus,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {

        PageResult<DocumentDeliveryResponse> result = documentDeliveryService.getDocumentDeliveryListWithDetails(pageNum, pageSize, caseId, caseNumber, documentType, recipientType, deliveryMethod, sendStatus, status);
        return Result.success(result);
    }

    @Operation(summary = "获取文书送达详情")
    @GetMapping("/{deliveryId}")
    public Result<DocumentDelivery> getDocumentDeliveryDetail(@Parameter(description = "送达记录ID") @PathVariable Long deliveryId) {
        DocumentDelivery documentDelivery = documentDeliveryService.getDocumentDeliveryDetail(deliveryId);
        return Result.success(documentDelivery);
    }

    @Operation(summary = "获取文书送达详情(含案件信息)")
    @GetMapping("/{deliveryId}/detail")
    public Result<DocumentDeliveryResponse> getDocumentDeliveryDetailWithCase(@Parameter(description = "送达记录ID") @PathVariable Long deliveryId) {
        DocumentDeliveryResponse response = documentDeliveryService.getDocumentDeliveryDetailWithCase(deliveryId);
        return Result.success(response);
    }

    @Operation(summary = "更新文书送达信息")
    @PutMapping("/{deliveryId}")
    public Result<Void> updateDocumentDelivery(
            @Parameter(description = "送达记录ID") @PathVariable Long deliveryId,
            @Valid @RequestBody DocumentDeliveryUpdateRequest request) {

        documentDeliveryService.updateDocumentDelivery(deliveryId, request);
        return Result.success();
    }

    @Operation(summary = "删除文书送达记录")
    @DeleteMapping("/{deliveryId}")
    public Result<Void> deleteDocumentDelivery(@Parameter(description = "送达记录ID") @PathVariable Long deliveryId) {
        documentDeliveryService.deleteDocumentDelivery(deliveryId);
        return Result.success();
    }

    @Operation(summary = "更新发送状态")
    @PutMapping("/{deliveryId}/send-status")
    public Result<Void> updateSendStatus(
            @Parameter(description = "送达记录ID") @PathVariable Long deliveryId,
            @Parameter(description = "发送状态") @RequestParam String sendStatus,
            @Parameter(description = "失败原因") @RequestParam(required = false) String failureReason) {

        documentDeliveryService.updateSendStatus(deliveryId, sendStatus, failureReason);
        return Result.success();
    }

    @Operation(summary = "更新送达状态")
    @PutMapping("/{deliveryId}/delivery-status")
    public Result<Void> updateDeliveryStatus(
            @Parameter(description = "送达记录ID") @PathVariable Long deliveryId,
            @Parameter(description = "送达状态") @RequestParam String sendStatus) {

        documentDeliveryService.updateDeliveryStatus(deliveryId, sendStatus);
        return Result.success();
    }

    @Operation(summary = "获取所有文书送达列表(支持模糊查询)")
    @GetMapping("/all")
    public Result<PageResult<DocumentDelivery>> getAllDocumentDeliveryList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "文书类型(模糊查询)") @RequestParam(required = false) String documentType,
            @Parameter(description = "状态(模糊查询)") @RequestParam(required = false) String status,
            @Parameter(description = "案号(模糊查询)") @RequestParam(required = false) String caseNumber) {

        PageResult<DocumentDelivery> result = documentDeliveryService.getAllDocumentDeliveryList(pageNum, pageSize, documentType, status, caseNumber, "SENT");
        return Result.success(result);
    }

    @Operation(summary = "更新状态和备注")
    @PutMapping("/{deliveryId}/status-remark")
    public Result<Void> updateStatusAndRemark(
            @Parameter(description = "送达记录ID") @PathVariable Long deliveryId,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "备注") @RequestParam(required = false) String remark) {

        documentDeliveryService.updateStatusAndRemark(deliveryId, status, remark);
        return Result.success();
    }

    @Operation(summary = "获取文书送达附件列表")
    @GetMapping("/{deliveryId}/attachments")
    public Result<List<FileRecord>> getDocumentDeliveryAttachments(
            @Parameter(description = "送达记录ID") @PathVariable Long deliveryId) {

        List<FileRecord> attachments = documentDeliveryService.getDocumentDeliveryAttachments(deliveryId);
        return Result.success(attachments);
    }

    @Operation(summary = "更新文书送达信息（带文件上传）")
    @PutMapping("/{deliveryId}/with-files")
    public Result<DocumentDeliveryWithFilesResponse> updateDocumentDeliveryWithFiles(
            @Parameter(description = "送达记录ID") @PathVariable Long deliveryId,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "文书名称") @RequestParam(required = false) String documentName,
            @Parameter(description = "文书类型") @RequestParam(required = false) String documentType,
            @Parameter(description = "受送达人") @RequestParam(required = false) String recipientName,
            @Parameter(description = "受送达人类型") @RequestParam(required = false) String recipientType,
            @Parameter(description = "联系电话") @RequestParam(required = false) String contactPhone,
            @Parameter(description = "送达地址") @RequestParam(required = false) String deliveryAddress,
            @Parameter(description = "送达方式") @RequestParam(required = false) String deliveryMethod,
            @Parameter(description = "送达内容") @RequestParam(required = false) String deliveryContent,
            @Parameter(description = "发送状态") @RequestParam(required = false) String sendStatus,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "文书附件路径") @RequestParam(required = false) String documentAttachment,
            @Parameter(description = "新增文件列表") @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @Parameter(description = "文件描述列表") @RequestParam(value = "fileDescriptions", required = false) List<String> fileDescriptions) {

        DocumentDeliveryWithFilesCreateRequest request = new DocumentDeliveryWithFilesCreateRequest();
        request.setCaseId(caseId);
        request.setDocumentName(documentName);
        request.setDocumentType(documentType);
        request.setRecipientName(recipientName);
        request.setRecipientType(recipientType);
        request.setContactPhone(contactPhone);
        request.setDeliveryAddress(deliveryAddress);
        request.setDeliveryMethod(deliveryMethod);
        request.setDeliveryContent(deliveryContent);
        request.setSendStatus(sendStatus);
        request.setStatus(status);
        request.setDocumentAttachment(documentAttachment);
        request.setFiles(files != null ? files : new java.util.ArrayList<>());
        request.setFileDescriptions(fileDescriptions);

        DocumentDeliveryWithFilesResponse response = documentDeliveryService.updateDocumentDeliveryWithFiles(deliveryId, request);
        return Result.success("文书送达更新成功", response);
    }

    @Operation(summary = "删除文书送达附件")
    @DeleteMapping("/{deliveryId}/attachments/{fileId}")
    public Result<Void> deleteDocumentDeliveryAttachment(
            @Parameter(description = "送达记录ID") @PathVariable Long deliveryId,
            @Parameter(description = "文件ID") @PathVariable Long fileId) {

        documentDeliveryService.deleteDocumentDeliveryAttachment(deliveryId, fileId);
        return Result.success();
    }

    @Operation(summary = "获取最新文书编号缩写")
    @GetMapping("/latest-abbreviation")
    public Result<String> getLatestAbbreviation() {
        String abbreviation = documentDeliveryService.getLatestAbbreviation();
        return Result.success(abbreviation);
    }
}

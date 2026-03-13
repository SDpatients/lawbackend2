package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.ExpenseReimbursementApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.ExpenseReimbursementCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.ExpenseReimbursementItemCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.ExpenseReimbursementUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.ExpenseReimbursementResponse;
import com.lawbackend2.lawbackend2.service.ExpenseReimbursementService;
import com.lawbackend2.lawbackend2.service.UserRoleService;
import com.lawbackend2.lawbackend2.util.PermissionChecker;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "费用报销管理")
@RestController
@RequestMapping("/expense-reimbursement")
@Validated
public class ExpenseReimbursementController {

    private final ExpenseReimbursementService expenseReimbursementService;
    private final UserRoleService userRoleService;
    private final PermissionChecker permissionChecker;
    
    private static final String UPLOAD_ROOT_PATH = "C:\\law-upload";

    public ExpenseReimbursementController(ExpenseReimbursementService expenseReimbursementService, UserRoleService userRoleService, PermissionChecker permissionChecker) {
        this.expenseReimbursementService = expenseReimbursementService;
        this.userRoleService = userRoleService;
        this.permissionChecker = permissionChecker;
    }

    @Operation(summary = "创建报销单")
    @PostMapping
    public Result<Map<String, Object>> createExpenseReimbursement(@Valid @RequestBody ExpenseReimbursementCreateRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        Long reimbursementId = expenseReimbursementService.createExpenseReimbursement(request, currentUserId);

        Map<String, Object> data = new HashMap<>();
        data.put("reimbursementId", reimbursementId);

        return Result.success(data);
    }

    @Operation(summary = "查询报销单详情")
    @GetMapping("/{id}")
    public Result<ExpenseReimbursementResponse> getExpenseReimbursementDetail(
            @Parameter(description = "报销单ID") @PathVariable Long id) {
        permissionChecker.checkReimbursementAccessPermission(id);
        ExpenseReimbursementResponse response = expenseReimbursementService.getExpenseReimbursementDetail(id);
        return Result.success(response);
    }

    @Operation(summary = "查询报销单列表")
    @GetMapping
    public Result<PageResult<ExpenseReimbursementResponse>> getExpenseReimbursementList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "申请人ID") @RequestParam(required = false) Long applicantId,
            @Parameter(description = "审批状态") @RequestParam(required = false) String approvalStatus,
            @Parameter(description = "报销日期") @RequestParam(required = false) java.time.LocalDate reimbursementDate) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        List<String> roleCodes = userRoleService.getUserRoleCodes(currentUserId);

        // 权限校验：如果不是ADMIN或SUPER_ADMIN，只能看到自己申请的报销单
        if (!roleCodes.contains("ADMIN") && !roleCodes.contains("SUPER_ADMIN")) {
            applicantId = currentUserId;
        }

        PageResult<ExpenseReimbursementResponse> result = expenseReimbursementService.getExpenseReimbursementList(
                page, size, caseId, applicantId, approvalStatus, reimbursementDate);
        return Result.success(result);
    }

    @Operation(summary = "更新报销单")
    @PutMapping("/{id}")
    public Result<Void> updateExpenseReimbursement(
            @Parameter(description = "报销单ID") @PathVariable Long id,
            @Valid @RequestBody ExpenseReimbursementUpdateRequest request) {
        permissionChecker.checkReimbursementEditPermission(id);
        request.setId(id);
        expenseReimbursementService.updateExpenseReimbursement(request);
        return Result.success();
    }

    @Operation(summary = "删除报销单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteExpenseReimbursement(
            @Parameter(description = "报销单ID") @PathVariable Long id) {
        permissionChecker.checkReimbursementDeletePermission(id);
        expenseReimbursementService.deleteExpenseReimbursement(id);
        return Result.success();
    }

    @Operation(summary = "审批报销单")
    @PostMapping("/{id}/approve")
    public Result<Void> approveExpenseReimbursement(
            @Parameter(description = "报销单ID") @PathVariable Long id,
            @Valid @RequestBody ExpenseReimbursementApprovalRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        permissionChecker.checkReimbursementApprovePermission(id);
        expenseReimbursementService.approveExpenseReimbursement(id, request, currentUserId);
        return Result.success();
    }

    @Operation(summary = "添加报销明细")
    @PostMapping("/{id}/items")
    public Result<Map<String, Object>> addExpenseReimbursementItem(
            @Parameter(description = "报销单ID") @PathVariable Long id,
            @Valid @RequestBody ExpenseReimbursementItemCreateRequest request) {
        permissionChecker.checkReimbursementEditPermission(id);
        request.setReimbursementId(id);
        Long itemId = expenseReimbursementService.addExpenseReimbursementItem(request);

        Map<String, Object> data = new HashMap<>();
        data.put("itemId", itemId);

        return Result.success(data);
    }

    @Operation(summary = "删除报销明细")
    @DeleteMapping("/{id}/items/{itemId}")
    public Result<Void> deleteExpenseReimbursementItem(
            @Parameter(description = "报销单ID") @PathVariable Long id,
            @Parameter(description = "明细ID") @PathVariable Long itemId) {
        permissionChecker.checkReimbursementEditPermission(id);
        expenseReimbursementService.deleteExpenseReimbursementItem(itemId);
        return Result.success();
    }

    @Operation(summary = "上传报销附件")
    @PostMapping("/{id}/attachments")
    public Result<Map<String, Object>> uploadExpenseReimbursementAttachment(
            @Parameter(description = "报销单ID") @PathVariable Long id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) throws IOException {
        permissionChecker.checkReimbursementEditPermission(id);
        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();
        Long fileSize = file.getSize();

        String relativePath = "/uploads/expense/" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/" + java.util.UUID.randomUUID().toString() + "_" + fileName;
        String fullPath = UPLOAD_ROOT_PATH + relativePath;

        java.io.File destFile = new java.io.File(fullPath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }

        file.transferTo(destFile);

        Long attachmentId = expenseReimbursementService.uploadExpenseReimbursementAttachment(id, fileName, relativePath, fileSize, fileType);

        Map<String, Object> data = new HashMap<>();
        data.put("attachmentId", attachmentId);
        data.put("filePath", relativePath);

        return Result.success(data);
    }

    @Operation(summary = "删除报销附件")
    @DeleteMapping("/{id}/attachments/{attachmentId}")
    public Result<Void> deleteExpenseReimbursementAttachment(
            @Parameter(description = "报销单ID") @PathVariable Long id,
            @Parameter(description = "附件ID") @PathVariable Long attachmentId) {
        permissionChecker.checkReimbursementEditPermission(id);
        expenseReimbursementService.deleteExpenseReimbursementAttachment(attachmentId);
        return Result.success();
    }

    @Operation(summary = "关联已存在的文件到报销单")
    @PostMapping("/{id}/attachments/{fileId}")
    public Result<Map<String, Object>> linkAttachment(
            @Parameter(description = "报销单ID") @PathVariable Long id,
            @Parameter(description = "文件ID") @PathVariable Long fileId) {
        Long attachmentId = expenseReimbursementService.linkAttachment(id, fileId);
        Map<String, Object> data = new HashMap<>();
        data.put("attachmentId", attachmentId);
        return Result.success(data);
    }
    
    @Operation(summary = "文件预览")
    @GetMapping("/attachments/{attachmentId}/preview")
    public ResponseEntity<Resource> previewAttachment(
            @Parameter(description = "附件 ID") @PathVariable Long attachmentId) throws IOException {
        com.lawbackend2.lawbackend2.entity.ExpenseReimbursementAttachment attachment = expenseReimbursementService.getAttachmentById(attachmentId);
        
        String filePath = attachment.getFilePath();
        String fullPath = buildFullPath(filePath);
        
        log.info("预览文件，attachmentId: {}, filePath: {}, fullPath: {}", attachmentId, filePath, fullPath);
        
        java.io.File file = new java.io.File(fullPath);
        if (!file.exists()) {
            log.error("文件不存在，attachmentId: {}, fullPath: {}, UPLOAD_ROOT_PATH: {}", attachmentId, fullPath, UPLOAD_ROOT_PATH);
            throw new RuntimeException("文件不存在，文件路径：" + fullPath);
        }
        
        Resource resource = new org.springframework.core.io.FileSystemResource(file);
        
        String contentType = attachment.getFileType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        
        log.info("文件预览成功，contentType: {}, fileName: {}", contentType, attachment.getFileName());
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + attachment.getFileName() + "\"")
                .body(resource);
    }

    @Operation(summary = "文件下载")
    @GetMapping("/attachments/{attachmentId}/download")
    public void downloadAttachment(
            @Parameter(description = "附件ID") @PathVariable Long attachmentId,
            HttpServletResponse response) throws IOException {
        com.lawbackend2.lawbackend2.entity.ExpenseReimbursementAttachment attachment = expenseReimbursementService.getAttachmentById(attachmentId);
        
        String filePath = attachment.getFilePath();
        String fullPath = buildFullPath(filePath);
        
        java.io.File file = new java.io.File(fullPath);
        if (!file.exists()) {
            log.error("文件不存在，attachmentId: {}, fullPath: {}", attachmentId, fullPath);
            throw new RuntimeException("文件不存在，文件路径：" + fullPath);
        }
        
        String contentType = attachment.getFileType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        
        response.setContentType(contentType);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + attachment.getFileName() + "\"");
        response.setContentLengthLong(file.length());
        
        Files.copy(file.toPath(), response.getOutputStream());
    }
    
    private String buildFullPath(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new RuntimeException("文件路径为空");
        }
        if (filePath.contains(":\\") || filePath.contains(":/")) {
            return filePath;
        }
        String normalizedPath = filePath.startsWith("/") || filePath.startsWith("\\") 
            ? filePath.substring(1) 
            : filePath;
        return UPLOAD_ROOT_PATH + "\\" + normalizedPath.replace("/", "\\");
    }
}

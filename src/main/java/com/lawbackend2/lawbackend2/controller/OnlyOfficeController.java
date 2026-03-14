package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.OnlyOfficeCallbackRequest;
import com.lawbackend2.lawbackend2.dto.OnlyOfficeConfigDTO;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.service.FileService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

/**
 * OnlyOffice 集成控制器
 */
@Slf4j
@Tag(name = "OnlyOffice 集成接口", description = "OnlyOffice 文档编辑相关接口")
@RestController
@RequestMapping("/api/v1/onlyoffice")
public class OnlyOfficeController {

    @Autowired
    private FileService fileService;

    @Value("${onlyoffice.server-url:http://localhost:8081}")
    private String onlyOfficeServerUrl;

    @Value("${onlyoffice.jwt.enabled:false}")
    private Boolean jwtEnabled;

    @Value("${onlyoffice.jwt.secret:onlyoffice-secret}")
    private String jwtSecret;

    @Value("${onlyoffice.callback.url:http://localhost:8080/api/v1/onlyoffice/callback}")
    private String callbackUrl;

    @Value("${app.file.download-url:http://localhost:8080/api/v1/file/download/}")
    private String fileDownloadUrl;

    /**
     * 获取 OnlyOffice 配置
     */
    @GetMapping("/config/{fileId}")
    @Operation(summary = "获取 OnlyOffice 编辑配置")
    public Result<OnlyOfficeConfigDTO> getOnlyOfficeConfig(@PathVariable Long fileId) {
        try {
            FileRecord fileRecord = fileService.getFileInfo(fileId);
            
            String fileType = getFileType(fileRecord.getOriginalFileName());
            String documentType = getDocumentType(fileRecord.getOriginalFileName());
            
            OnlyOfficeConfigDTO config = OnlyOfficeConfigDTO.builder()
                .document(OnlyOfficeConfigDTO.DocumentConfig.builder()
                    .fileType(fileType)
                    .key(fileRecord.getStoredFileName())
                    .title(fileRecord.getOriginalFileName())
                    .url(getFileDownloadUrl(fileId))
                    .permissions(OnlyOfficeConfigDTO.Permissions.builder()
                        .edit(true)
                        .download(true)
                        .print(true)
                        .copy(true)
                        .build())
                    .build())
                .documentType(documentType)
                .editorConfig(OnlyOfficeConfigDTO.EditorConfig.builder()
                    .callbackUrl(callbackUrl + "?fileId=" + fileId)
                    .user(OnlyOfficeConfigDTO.UserInfo.builder()
                        .id(SecurityUtil.getCurrentUserId().toString())
                        .name("用户" + SecurityUtil.getCurrentUserId())
                        .build())
                    .customization(OnlyOfficeConfigDTO.Customization.builder()
                        .autosave(true)
                        .forcesave(true)
                        .trackChanges(true)
                        .build())
                    .build())
                .build();
            
            return Result.success(config);
        } catch (Exception e) {
            log.error("获取 OnlyOffice 配置失败", e);
            return Result.error("获取配置失败：" + e.getMessage());
        }
    }

    /**
     * OnlyOffice 回调接口
     */
    @PostMapping("/callback")
    @Operation(summary = "OnlyOffice 编辑回调接口")
    public Result<Map<String, Object>> handleOnlyOfficeCallback(
            @RequestParam Long fileId,
            @RequestBody OnlyOfficeCallbackRequest request) {
        
        log.info("OnlyOffice 回调 - fileId: {}, action: {}", fileId, request.getAction());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证 JWT 签名（如果启用）
            if (jwtEnabled && request.getToken() != null) {
                // TODO: 实现 JWT 验证
                // validateJwtToken(request.getToken());
            }
            
            // 根据 action 处理不同逻辑
            Integer action = request.getAction();
            if (action == null) {
                action = 0;
            }
            
            switch (action) {
                case 0:
                    // 无操作
                    log.info("无操作，忽略回调");
                    break;
                    
                case 1:
                    // 下载文档
                    log.info("用户下载文档");
                    break;
                    
                case 2:
                    // 编辑文档
                    log.info("用户编辑文档");
                    break;
                    
                case 3:
                    // 保存文档（最重要）
                    handleSaveDocument(fileId, request);
                    break;
                    
                default:
                    log.warn("未知的 action 类型：{}", action);
            }
            
            response.put("error", 0);
            response.put("fileType", "docx");
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("处理 OnlyOffice 回调失败", e);
            response.put("error", 1);
            response.put("message", e.getMessage());
            return Result.error("回调处理失败：" + e.getMessage());
        }
    }

    /**
     * 处理文档保存
     */
    private void handleSaveDocument(Long fileId, OnlyOfficeCallbackRequest request) throws IOException {
        log.info("开始保存文档 - fileId: {}, version: {}", fileId, request.getData().getVersion());
        
        // 1. 从 OnlyOffice 下载编辑后的文档
        String downloadUrl = request.getData().getUrl();
        if (downloadUrl == null || downloadUrl.isEmpty()) {
            throw new RuntimeException("文档下载地址为空");
        }
        
        // 2. 下载文件
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> fileResponse = restTemplate.exchange(
            downloadUrl,
            HttpMethod.GET,
            null,
            byte[].class
        );
        
        byte[] fileContent = fileResponse.getBody();
        if (fileContent == null || fileContent.length == 0) {
            throw new RuntimeException("下载的文件内容为空");
        }
        
        // 3. 获取原文件信息
        FileRecord fileRecord = fileService.getFileInfo(fileId);
        
        // 4. 保存文件（覆盖原文件或创建新版本）
        String filePath = fileRecord.getFilePath();
        
        // 备份原文件
        String backupPath = filePath + ".bak." + System.currentTimeMillis();
        Files.copy(Paths.get(filePath), Paths.get(backupPath), StandardCopyOption.REPLACE_EXISTING);
        
        // 保存新文件
        Files.write(Paths.get(filePath), fileContent);
        
        // 5. 更新文件记录
        fileRecord.setFileSize((long) fileContent.length);
        // TODO: 实现文件更新方法
        // fileService.updateFile(fileRecord);
        
        log.info("文档保存成功 - fileId: {}, 大小：{} bytes", fileId, fileContent.length);
    }

    /**
     * 获取文件类型
     */
    private String getFileType(String fileName) {
        if (fileName == null) return "docx";
        
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "doc":
            case "docx":
            case "odt":
            case "rtf":
            case "txt":
                return "docx";
            case "xls":
            case "xlsx":
            case "ods":
            case "csv":
                return "xlsx";
            case "ppt":
            case "pptx":
            case "odp":
                return "pptx";
            default:
                return "docx";
        }
    }

    /**
     * 获取文档类型
     */
    private String getDocumentType(String fileName) {
        if (fileName == null) return "word";
        
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "doc":
            case "docx":
            case "odt":
            case "rtf":
            case "txt":
                return "word";
            case "xls":
            case "xlsx":
            case "ods":
            case "csv":
                return "cell";
            case "ppt":
            case "pptx":
            case "odp":
                return "slide";
            default:
                return "word";
        }
    }

    /**
     * 获取文件下载地址
     */
    private String getFileDownloadUrl(Long fileId) {
        return fileDownloadUrl + fileId;
    }

    /**
     * 锁定文件
     */
    @PostMapping("/lock/{fileId}")
    @Operation(summary = "锁定文件，防止多人同时编辑")
    public Result<Void> lockFile(@PathVariable Long fileId) {
        log.info("锁定文件 - fileId: {}", fileId);
        // TODO: 实现文件锁定逻辑
        return Result.success();
    }

    /**
     * 解锁文件
     */
    @PostMapping("/unlock/{fileId}")
    @Operation(summary = "解锁文件")
    public Result<Void> unlockFile(@PathVariable Long fileId) {
        log.info("解锁文件 - fileId: {}", fileId);
        // TODO: 实现文件解锁逻辑
        return Result.success();
    }

    /**
     * 获取编辑历史记录
     */
    @GetMapping("/history/{fileId}")
    @Operation(summary = "获取文件编辑历史")
    public Result<Map<String, Object>> getEditHistory(@PathVariable Long fileId) {
        log.info("获取编辑历史 - fileId: {}", fileId);
        
        Map<String, Object> history = new HashMap<>();
        // TODO: 实现编辑历史查询
        
        return Result.success(history);
    }

    /**
     * 获取协作者信息
     */
    @GetMapping("/collaborators/{fileId}")
    @Operation(summary = "获取当前协作者信息")
    public Result<Map<String, Object>> getCollaborators(@PathVariable Long fileId) {
        log.info("获取协作者 - fileId: {}", fileId);
        
        Map<String, Object> collaborators = new HashMap<>();
        // TODO: 实现协作者查询
        
        return Result.success(collaborators);
    }
}

package com.lawbackend2.lawbackend2.license.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.license.LicenseInfo;
import com.lawbackend2.lawbackend2.license.LicenseProperties;
import com.lawbackend2.lawbackend2.license.LicenseService;
import com.lawbackend2.lawbackend2.license.MachineCodeGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/system/license")
@RequiredArgsConstructor
@Tag(name = "许可证管理", description = "软件许可证管理相关接口")
public class LicenseController {

    private final LicenseService licenseService;
    private final LicenseProperties licenseProperties;

    @GetMapping("/status")
    @Operation(summary = "获取许可证状态", description = "获取当前许可证状态信息")
    public Result<Map<String, Object>> getStatus() {
        Map<String, Object> status = licenseService.getValidationInfo();
        status.put("machineCode", licenseService.getMachineCode());

        LicenseInfo license = licenseService.getCurrentLicense();
        if (license != null) {
            Map<String, Object> info = new HashMap<>();
            info.put("licenseId", license.getLicenseId());
            info.put("customerName", license.getCustomerName());
            info.put("modules", license.getModules());
            info.put("maxUsers", license.getMaxUsers());
            info.put("expireDate", license.getExpireDate());
            info.put("createTime", license.getCreateTime());
            status.put("licenseInfo", info);
        }

        return Result.success(status);
    }

    @GetMapping("/machine-code")
    @Operation(summary = "获取机器码", description = "获取当前服务器的机器码，用于生成许可证")
    public Result<Map<String, Object>> getMachineCode() {
        Map<String, Object> result = new HashMap<>();
        
        String machineCode = MachineCodeGenerator.generate();
        String error = MachineCodeGenerator.getLastError();
        Map<String, String> components = MachineCodeGenerator.getLastComponents();
        
        result.put("machineCode", machineCode);
        result.put("message", "请将此机器码发送给软件供应商以获取许可证");
        result.put("tip", "机器码是服务器的唯一标识，用于绑定许可证");
        
        if (error != null) {
            result.put("warning", error);
        }
        
        result.put("components", components);
        
        return Result.success(result);
    }

    @PostMapping("/validate")
    @Operation(summary = "验证许可证", description = "手动触发许可证验证")
    @PreAuthorize("hasAuthority('system:license:manage')")
    public Result<Map<String, Object>> validateLicense() {
        try {
            licenseService.validateLicense();
            Map<String, Object> result = licenseService.getValidationInfo();
            
            if (licenseService.isValid()) {
                result.put("message", "许可证验证成功");
            } else {
                result.put("message", "许可证验证失败");
            }
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("许可证验证失败", e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("valid", false);
            result.put("message", "许可证验证失败");
            result.put("error", e.getMessage());
            result.put("help", "请联系软件供应商获取技术支持");
            
            return Result.error(500, "许可证验证失败: " + e.getMessage(), result);
        }
    }

    @GetMapping("/modules/{module}")
    @Operation(summary = "检查模块授权", description = "检查指定功能模块是否已授权")
    @PreAuthorize("hasAuthority('system:license:view')")
    public Result<Map<String, Object>> checkModule(@PathVariable String module) {
        Map<String, Object> result = new HashMap<>();
        result.put("module", module);
        result.put("enabled", licenseService.isModuleEnabled(module));
        result.put("licenseValid", licenseService.isValid());
        
        if (!licenseService.isValid()) {
            result.put("reason", licenseService.getValidationErrorMessage());
            result.put("help", licenseService.getLastValidationDetails().get("help"));
        }
        
        return Result.success(result);
    }

    @PostMapping("/upload")
    @Operation(summary = "上传许可证文件", description = "上传许可证文件并验证")
    public Result<Map<String, Object>> uploadLicense(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                result.put("valid", false);
                result.put("errorCode", "FILE_EMPTY");
                result.put("message", "请选择许可证文件");
                result.put("help", "许可证文件通常是 .lic 或 .json 格式，请联系软件供应商获取许可证文件");
                return Result.error(400, "请选择许可证文件", result);
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                result.put("valid", false);
                result.put("errorCode", "INVALID_FILENAME");
                result.put("message", "无法识别文件名");
                result.put("help", "请确保上传的文件有正确的文件名");
                return Result.error(400, "无法识别文件名", result);
            }

            int dotIndex = originalFilename.lastIndexOf(".");
            String extension = dotIndex > 0 ? originalFilename.substring(dotIndex + 1).toLowerCase() : "";
            
            if (!extension.equals("lic") && !extension.equals("json") && !extension.isEmpty()) {
                result.put("valid", false);
                result.put("errorCode", "INVALID_EXTENSION");
                result.put("message", "不支持的文件格式: " + extension);
                result.put("help", "请上传 .lic 或 .json 格式的许可证文件");
                return Result.error(400, "请上传 .lic 或 .json 格式的许可证文件，当前文件格式: " + extension, result);
            }

            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            log.info("上传的许可证文件内容预览: {}", 
                    content.length() > 200 ? content.substring(0, 200) + "..." : content);

            if (!content.trim().startsWith("{")) {
                result.put("valid", false);
                result.put("errorCode", "INVALID_FORMAT");
                result.put("message", "许可证文件格式错误");
                result.put("contentPreview", content.substring(0, Math.min(100, content.length())));
                result.put("help", "许可证文件应该是JSON格式，请确认上传的是正确的许可证文件而非其他文件");
                return Result.error(400, "许可证文件格式错误，请确认上传的是许可证文件而非其他文件", result);
            }

            Path targetPath = Paths.get(licenseProperties.getFilePath());
            try {
                Files.createDirectories(targetPath.getParent());
            } catch (Exception e) {
                log.error("无法创建目录: {}", targetPath.getParent(), e);
                result.put("valid", false);
                result.put("errorCode", "DIR_CREATE_ERROR");
                result.put("message", "无法创建许可证存储目录");
                result.put("help", "请检查服务器是否有足够的文件系统权限");
                return Result.error(500, "无法创建许可证存储目录，请检查服务器权限", result);
            }
            
            Files.write(targetPath, file.getBytes());

            log.info("许可证文件已保存到: {}", targetPath);

            licenseService.validateLicense();

            if (licenseService.isValid()) {
                result.put("valid", true);
                result.put("message", "许可证验证成功，系统已激活");
                LicenseInfo license = licenseService.getCurrentLicense();
                if (license != null) {
                    Map<String, Object> info = new HashMap<>();
                    info.put("licenseId", license.getLicenseId());
                    info.put("customerName", license.getCustomerName());
                    info.put("modules", license.getModules());
                    info.put("maxUsers", license.getMaxUsers());
                    info.put("expireDate", license.getExpireDate());
                    info.put("remainingDays", license.getRemainingDays());
                    result.put("licenseInfo", info);
                }
                return Result.success(result);
            } else {
                result.put("valid", false);
                result.put("errorCode", licenseService.getLastValidationErrorCode());
                result.put("message", "许可证验证失败");
                result.put("error", licenseService.getValidationErrorMessage());
                result.put("details", licenseService.getLastValidationDetails());
                return Result.error(400, "许可证验证失败: " + licenseService.getValidationErrorMessage(), result);
            }
            
        } catch (JsonParseException e) {
            log.error("许可证文件JSON解析失败", e);
            result.put("valid", false);
            result.put("errorCode", "JSON_PARSE_ERROR");
            result.put("message", "许可证文件格式错误");
            result.put("error", "JSON解析失败: " + e.getOriginalMessage());
            result.put("lineNumber", e.getLocation() != null ? e.getLocation().getLineNr() : -1);
            result.put("help", "许可证文件不是有效的JSON格式，请检查文件内容是否完整");
            return Result.error(400, "许可证文件格式错误，不是有效的JSON格式", result);
        } catch (JsonMappingException e) {
            log.error("许可证数据映射失败", e);
            result.put("valid", false);
            result.put("errorCode", "JSON_MAPPING_ERROR");
            result.put("message", "许可证数据格式错误");
            result.put("error", e.getMessage());
            result.put("help", "许可证文件缺少必要字段或字段类型错误，请确认文件内容正确");
            return Result.error(400, "许可证数据格式错误，请确认文件内容正确", result);
        } catch (Exception e) {
            log.error("许可证上传失败", e);
            result.put("valid", false);
            result.put("errorCode", "UPLOAD_ERROR");
            result.put("message", "许可证上传失败");
            result.put("error", e.getMessage());
            result.put("help", "请检查网络连接或联系软件供应商获取技术支持");
            return Result.error(500, "许可证上传失败: " + e.getMessage(), result);
        }
    }
    
    @GetMapping("/config")
    @Operation(summary = "获取许可证配置信息", description = "获取当前许可证系统的配置状态")
    @PreAuthorize("hasAuthority('system:license:view')")
    public Result<Map<String, Object>> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("enabled", licenseProperties.isEnabled());
        config.put("filePath", licenseProperties.getFilePath());
        config.put("publicKeyConfigured", licenseProperties.isPublicKeyConfigured());
        config.put("strictMode", licenseProperties.isStrictMode());
        config.put("warningDays", licenseProperties.getWarningDays());
        return Result.success(config);
    }
}

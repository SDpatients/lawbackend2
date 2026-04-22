package com.lawbackend2.lawbackend2.license;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class LicenseService {

    private final LicenseProperties licenseProperties;
    private LicenseInfo currentLicense;
    private final ObjectMapper objectMapper;
    
    private String lastValidationError;
    private String lastValidationErrorCode;
    private Map<String, Object> lastValidationDetails;

    public LicenseService(LicenseProperties licenseProperties) {
        this.licenseProperties = licenseProperties;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @PostConstruct
    public void init() {
        if (!licenseProperties.isEnabled()) {
            log.info("许可证验证已禁用");
            return;
        }

        log.info("正在初始化许可证验证...");
        log.info(licenseProperties.getConfigurationStatus());
        validateLicense();
    }

    public void validateLicense() {
        this.lastValidationError = null;
        this.lastValidationErrorCode = null;
        this.lastValidationDetails = new HashMap<>();
        this.currentLicense = null;
        
        try {
            Path licensePath = Paths.get(licenseProperties.getFilePath());
            
            if (!Files.exists(licensePath)) {
                String error = "许可证文件不存在";
                String path = licenseProperties.getFilePath();
                log.error("{}: {}", error, path);
                this.lastValidationError = error;
                this.lastValidationErrorCode = "FILE_NOT_FOUND";
                this.lastValidationDetails.put("path", path);
                this.lastValidationDetails.put("help", "请上传许可证文件到指定路径，或联系软件供应商获取许可证");
                // 注意：不再在严格模式下抛出异常阻止启动
                // 拦截器会阻止API访问，但系统可以启动以便上传许可证
                return;
            }

            if (Files.size(licensePath) == 0) {
                String error = "许可证文件为空";
                log.error(error);
                this.lastValidationError = error;
                this.lastValidationErrorCode = "FILE_EMPTY";
                this.lastValidationDetails.put("help", "许可证文件内容为空，请重新上传有效的许可证文件");
                return;
            }

            String licenseContent;
            try {
                licenseContent = new String(Files.readAllBytes(licensePath), StandardCharsets.UTF_8);
            } catch (IOException e) {
                String error = "无法读取许可证文件";
                log.error("{}: {}", error, e.getMessage());
                this.lastValidationError = error;
                this.lastValidationErrorCode = "FILE_READ_ERROR";
                this.lastValidationDetails.put("reason", e.getMessage());
                this.lastValidationDetails.put("help", "请检查文件权限或文件是否损坏");
                return;
            }
            
            if (licenseContent.trim().isEmpty()) {
                String error = "许可证文件内容为空";
                log.error(error);
                this.lastValidationError = error;
                this.lastValidationErrorCode = "CONTENT_EMPTY";
                this.lastValidationDetails.put("help", "许可证文件内容为空，请重新上传有效的许可证文件");
                return;
            }

            if (!licenseContent.trim().startsWith("{")) {
                String error = "许可证文件格式错误，不是有效的JSON格式";
                log.error(error);
                this.lastValidationError = error;
                this.lastValidationErrorCode = "INVALID_FORMAT";
                this.lastValidationDetails.put("contentPreview", licenseContent.substring(0, Math.min(100, licenseContent.length())));
                this.lastValidationDetails.put("help", "许可证文件应该是JSON格式，请确认上传的是正确的许可证文件");
                return;
            }

            log.debug("许可证文件内容: {}", licenseContent.length() > 200 ? 
                    licenseContent.substring(0, 200) + "..." : licenseContent);

            LicenseInfo license;
            try {
                license = objectMapper.readValue(licenseContent, LicenseInfo.class);
            } catch (JsonParseException e) {
                String error = "许可证文件JSON解析失败";
                log.error("{}: {}", error, e.getOriginalMessage());
                this.lastValidationError = error;
                this.lastValidationErrorCode = "JSON_PARSE_ERROR";
                this.lastValidationDetails.put("reason", e.getOriginalMessage());
                this.lastValidationDetails.put("lineNumber", e.getLocation() != null ? e.getLocation().getLineNr() : -1);
                this.lastValidationDetails.put("help", "许可证文件JSON格式错误，请检查文件内容是否完整");
                return;
            } catch (JsonMappingException e) {
                String error = "许可证数据映射失败";
                log.error("{}: {}", error, e.getMessage());
                this.lastValidationError = error;
                this.lastValidationErrorCode = "JSON_MAPPING_ERROR";
                this.lastValidationDetails.put("reason", e.getMessage());
                this.lastValidationDetails.put("help", "许可证文件缺少必要字段或字段类型错误");
                return;
            }

            try {
                license.validate();
            } catch (LicenseInfo.LicenseValidationException e) {
                String errorMsg = e.getMessage();
                // 检查是否是过期异常
                if (errorMsg != null && errorMsg.contains("过期")) {
                    String error = "许可证已过期";
                    log.error("{}: {}", error, license.getExpireDate());
                    this.currentLicense = license;
                    this.lastValidationError = error;
                    this.lastValidationErrorCode = "LICENSE_EXPIRED";
                    this.lastValidationDetails.put("expireDate", license.getExpireDate().toString());
                    this.lastValidationDetails.put("help", "许可证已过期，请联系软件供应商续期");
                    return;
                }
                String error = "许可证信息不完整";
                log.error("{}: {}", error, errorMsg);
                this.lastValidationError = error;
                this.lastValidationErrorCode = "VALIDATION_ERROR";
                this.lastValidationDetails.put("reason", errorMsg);
                this.lastValidationDetails.put("help", "许可证缺少必要信息，请联系软件供应商重新生成许可证");
                return;
            }

            if (!verifySignature(license)) {
                String error = "许可证签名验证失败";
                log.error(error);
                this.lastValidationError = error;
                this.lastValidationErrorCode = "SIGNATURE_INVALID";
                this.lastValidationDetails.put("help", "许可证可能被篡改或损坏，请重新获取许可证文件");
                return;
            }

            String currentMachineCode = MachineCodeGenerator.generate();
            if (!currentMachineCode.equals(license.getMachineCode())) {
                String error = "许可证与当前服务器不匹配";
                log.error("{}: 许可证绑定={}, 当前服务器={}", error, license.getMachineCode(), currentMachineCode);
                this.currentLicense = license;
                this.lastValidationError = error;
                this.lastValidationErrorCode = "MACHINE_CODE_MISMATCH";
                this.lastValidationDetails.put("expectedMachineCode", license.getMachineCode());
                this.lastValidationDetails.put("actualMachineCode", currentMachineCode);
                this.lastValidationDetails.put("help", "该许可证绑定于其他服务器，如需迁移请联系软件供应商重新生成许可证");
                return;
            }

            long remainingDays = license.getRemainingDays();
            if (remainingDays <= licenseProperties.getWarningDays()) {
                log.warn("许可证将在 {} 天后过期，请及时续期", remainingDays);
            }

            this.currentLicense = license;
            this.lastValidationDetails.put("licenseId", license.getLicenseId());
            this.lastValidationDetails.put("customerName", license.getCustomerName());
            this.lastValidationDetails.put("remainingDays", remainingDays);
            
            log.info("许可证验证成功！客户: {}, 授权模块: {}, 到期时间: {}, 剩余天数: {}",
                    license.getCustomerName(), license.getModules(), 
                    license.getExpireDate(), remainingDays);

        } catch (Exception e) {
            String error = "许可证验证失败";
            log.error("{}: {}", error, e.getMessage(), e);
            this.lastValidationError = error;
            this.lastValidationErrorCode = "UNKNOWN_ERROR";
            this.lastValidationDetails.put("reason", e.getMessage());
            this.lastValidationDetails.put("help", "请联系软件供应商获取技术支持");
        }
    }

    public boolean verifySignature(LicenseInfo license) {
        try {
            if (license.getSignature() == null || license.getSignature().isEmpty()) {
                log.warn("许可证签名为空");
                return false;
            }

            String publicKeyStr = licenseProperties.getProcessedPublicKey();
            if (publicKeyStr == null || publicKeyStr.isEmpty()) {
                log.warn("未配置公钥，跳过签名验证");
                return true;
            }

            String dataToVerify = buildDataToVerify(license);
            byte[] signatureBytes;
            try {
                signatureBytes = Base64.getDecoder().decode(license.getSignature());
            } catch (IllegalArgumentException e) {
                log.error("签名格式错误，不是有效的Base64编码");
                return false;
            }

            PublicKey publicKey = loadPublicKey(publicKeyStr);
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(dataToVerify.getBytes(StandardCharsets.UTF_8));

            return signature.verify(signatureBytes);
        } catch (Exception e) {
            log.error("签名验证异常: {}", e.getMessage());
            return false;
        }
    }

    private String buildDataToVerify(LicenseInfo license) {
        return license.getLicenseId() + "|" +
                license.getCustomerName() + "|" +
                license.getMachineCode() + "|" +
                String.join(",", license.getModules()) + "|" +
                license.getMaxUsers() + "|" +
                license.getExpireDate();
    }

    private PublicKey loadPublicKey(String publicKeyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    public boolean isModuleEnabled(String module) {
        if (!licenseProperties.isEnabled()) {
            return true;
        }
        // 有过期或机器码不匹配等错误时，模块不可用
        if (lastValidationError != null) {
            return false;
        }
        if (currentLicense == null) {
            return false;
        }
        return currentLicense.hasModule(module);
    }

    public boolean isExpired() {
        if (!licenseProperties.isEnabled() || currentLicense == null) {
            return false;
        }
        return currentLicense.isExpired();
    }

    public long getRemainingDays() {
        if (!licenseProperties.isEnabled() || currentLicense == null) {
            return Long.MAX_VALUE;
        }
        return currentLicense.getRemainingDays();
    }

    public LicenseInfo getCurrentLicense() {
        return currentLicense;
    }

    public String getMachineCode() {
        return MachineCodeGenerator.generate();
    }

    /**
     * 检查许可证验证是否启用
     */
    public boolean isLicenseEnabled() {
        return licenseProperties.isEnabled();
    }

    public boolean isValid() {
        if (!licenseProperties.isEnabled()) {
            return lastValidationError == null;
        }
        // 有验证错误时返回无效
        if (lastValidationError != null) {
            return false;
        }
        return currentLicense != null && !currentLicense.isExpired();
    }
    
    public String getLastValidationError() {
        return lastValidationError;
    }
    
    public String getLastValidationErrorCode() {
        return lastValidationErrorCode;
    }
    
    public Map<String, Object> getLastValidationDetails() {
        return lastValidationDetails != null ? new HashMap<>(lastValidationDetails) : new HashMap<>();
    }
    
    public String getValidationErrorMessage() {
        if (lastValidationError != null) {
            return lastValidationError;
        }
        if (!licenseProperties.isEnabled()) {
            return "许可证验证已禁用";
        }
        if (currentLicense == null) {
            return "未找到有效的许可证文件";
        }
        if (currentLicense.isExpired()) {
            return "许可证已过期";
        }
        return null;
    }
    
    public Map<String, Object> getValidationInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("valid", isValid());
        info.put("enabled", licenseProperties.isEnabled());
        info.put("strictMode", licenseProperties.isStrictMode());
        
        if (lastValidationError != null) {
            info.put("error", lastValidationError);
            info.put("errorCode", lastValidationErrorCode);
            info.put("details", lastValidationDetails);
        }
        
        if (currentLicense != null) {
            info.put("licenseId", currentLicense.getLicenseId());
            info.put("customerName", currentLicense.getCustomerName());
            info.put("remainingDays", getRemainingDays());
            info.put("expired", isExpired());
        }
        
        return info;
    }
}

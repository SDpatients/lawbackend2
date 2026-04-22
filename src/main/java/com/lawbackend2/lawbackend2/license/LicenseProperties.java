package com.lawbackend2.lawbackend2.license;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.regex.Pattern;

@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "license")
public class LicenseProperties {

    private boolean enabled = true;

    private String filePath = "D:\\law-backup\\license.lic";

    private String publicKey;

    private int warningDays = 30;

    private boolean strictMode = true;

    private static final Pattern BASE64_PATTERN = Pattern.compile("^[A-Za-z0-9+/]+=*$");

    @PostConstruct
    public void validate() {
        if (enabled) {
            if (filePath == null || filePath.trim().isEmpty()) {
                log.warn("许可证文件路径未配置，将使用默认路径: D:\\law-backup\\license.lic");
                filePath = "D:\\law-backup\\license.lic";
            }

            if (publicKey == null || publicKey.trim().isEmpty()) {
                log.warn("公钥未配置，许可证签名验证将被跳过。建议在 application.yml 中配置 license.public-key");
            } else {
                String processedKey = getProcessedPublicKey();
                if (!isValidBase64(processedKey)) {
                    log.error("公钥格式错误，不是有效的Base64编码。请检查 application.yml 中的 license.public-key 配置");
                }
            }

            if (warningDays < 0) {
                log.warn("警告天数(warningDays)不能为负数，已重置为默认值 30 天");
                warningDays = 30;
            } else if (warningDays > 365) {
                log.warn("警告天数(warningDays)设置过大({}天)，建议设置为30-90天", warningDays);
            }
        }
    }

    public String getProcessedPublicKey() {
        if (publicKey == null) {
            return null;
        }
        return publicKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
    }

    public boolean isPublicKeyConfigured() {
        return publicKey != null && !publicKey.trim().isEmpty();
    }

    public String getConfigurationStatus() {
        StringBuilder status = new StringBuilder();
        status.append("许可证配置状态:\n");
        status.append("  - 启用状态: ").append(enabled ? "已启用" : "已禁用").append("\n");
        status.append("  - 文件路径: ").append(filePath != null ? filePath : "未配置").append("\n");
        status.append("  - 公钥配置: ").append(isPublicKeyConfigured() ? "已配置" : "未配置").append("\n");
        status.append("  - 严格模式: ").append(strictMode ? "开启" : "关闭").append("\n");
        status.append("  - 过期警告: ").append(warningDays).append("天前");
        return status.toString();
    }

    private boolean isValidBase64(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            return BASE64_PATTERN.matcher(str).matches() && str.length() % 4 == 0;
        } catch (Exception e) {
            return false;
        }
    }
}

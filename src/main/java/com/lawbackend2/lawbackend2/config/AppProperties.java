package com.lawbackend2.lawbackend2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String name = "法律破产管理系统";
    private String company = "";
    private String logoUrl = "";
    private String footerText = "";
    private String defaultLanguage = "zh-CN";
    private String timezone = "Asia/Shanghai";

    private ServerConfig server = new ServerConfig();
    private FileConfig file = new FileConfig();
    private CacheConfig cache = new CacheConfig();
    private SecurityConfig security = new SecurityConfig();
    private FeatureConfig features = new FeatureConfig();
    private SwaggerConfig swagger = new SwaggerConfig();
    private CorsConfig cors = new CorsConfig();

    @Data
    public static class ServerConfig {
        private String url = "http://localhost:8080";
    }

    @Data
    public static class FileConfig {
        private String uploadPath = "D:\\law-upload";
        private String downloadUrl = "http://localhost:8080/api/v1/file/download/";
        private long maxSize = 50 * 1024 * 1024;
        private String allowedTypes = "jpg,jpeg,png,gif,bmp,pdf,doc,docx,xls,xlsx,ppt,pptx,txt,zip,rar";
        private VideoConfig video = new VideoConfig();

        public Set<String> getAllowedTypesSet() {
            return new HashSet<>(Arrays.asList(allowedTypes.toLowerCase().split(",")));
        }

        public boolean isAllowedType(String extension) {
            if (extension == null) {
                return false;
            }
            return getAllowedTypesSet().contains(extension.toLowerCase().trim());
        }
    }

    @Data
    public static class VideoConfig {
        private long maxSize = 500 * 1024 * 1024;
        private String allowedTypes = "mp4,avi,mov,wmv,flv,mkv,webm,mpeg,mpg,3gp";
        private String allowedMimeTypes = "video/mp4,video/avi,video/x-msvideo,video/quicktime,video/x-ms-wmv,video/x-flv,video/x-matroska,video/webm,video/mpeg,video/3gpp";

        public Set<String> getAllowedTypesSet() {
            return new HashSet<>(Arrays.asList(allowedTypes.toLowerCase().split(",")));
        }

        public Set<String> getAllowedMimeTypesSet() {
            return new HashSet<>(Arrays.asList(allowedMimeTypes.toLowerCase().split(",")));
        }

        public boolean isVideoFile(String contentType, String fileExtension) {
            if (contentType != null && getAllowedMimeTypesSet().contains(contentType.toLowerCase())) {
                return true;
            }
            if (fileExtension != null) {
                return getAllowedTypesSet().contains(fileExtension.toLowerCase());
            }
            return false;
        }
    }

    @Data
    public static class CacheConfig {
        private int defaultTtlMinutes = 10;
        private RecentCaseConfig recentCase = new RecentCaseConfig();

        @Data
        public static class RecentCaseConfig {
            private int maxRecords = 50;
            private int expireDays = 30;
        }
    }

    @Data
    public static class SecurityConfig {
        private int passwordMinLength = 6;
        private boolean passwordRequireSpecialChar = false;
        private boolean passwordRequireDigit = false;
        private boolean passwordRequireUppercase = false;
    }

    @Data
    public static class FeatureConfig {
        private boolean aiChat = true;
        private boolean onlyOffice = true;
        private boolean videoUpload = true;
        private boolean dataExport = true;
        private boolean batchImport = true;
    }

    @Data
    public static class SwaggerConfig {
        private boolean enabled = true;
        private String title = "法律破产管理系统API";
        private String description = "法律破产管理系统后端API文档";
        private String version = "1.0.0";
        private String contactName = "技术支持";
        private String contactEmail = "support@example.com";
    }

    @Data
    public static class CorsConfig {
        private String allowedOrigins = "http://localhost:3000,http://localhost:5173,http://localhost:8080,http://localhost:5779,http://localhost:5780,http://192.168.0.151:5779,http://192.168.0.151:5780,http://192.168.1.92:5779,http://192.168.1.92:5780";
        private String allowedMethods = "GET,POST,PUT,DELETE,OPTIONS";
        private String allowedHeaders = "*";
        private boolean allowCredentials = true;
        private long maxAge = 3600;
    }
}

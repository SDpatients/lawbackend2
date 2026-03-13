package com.lawbackend2.lawbackend2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OnlyOffice 配置响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnlyOfficeConfigDTO {

    /**
     * 文档配置
     */
    private DocumentConfig document;

    /**
     * 文档类型：word, cell, slide
     */
    private String documentType;

    /**
     * 编辑器配置
     */
    private EditorConfig editorConfig;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentConfig {
        /**
         * 文件类型
         */
        private String fileType;

        /**
         * 文档唯一标识
         */
        private String key;

        /**
         * 文档标题
         */
        private String title;

        /**
         * 文档 URL（OnlyOffice 服务访问）
         */
        private String url;

        /**
         * 权限配置
         */
        private Permissions permissions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Permissions {
        /**
         * 是否可编辑
         */
        private Boolean edit;

        /**
         * 是否可下载
         */
        private Boolean download;

        /**
         * 是否可打印
         */
        private Boolean print;

        /**
         * 是否可复制
         */
        private Boolean copy;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditorConfig {
        /**
         * 回调 URL
         */
        private String callbackUrl;

        /**
         * 用户信息
         */
        private UserInfo user;

        /**
         * 自定义配置
         */
        private Customization customization;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        /**
         * 用户 ID
         */
        private String id;

        /**
         * 用户名称
         */
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Customization {
        /**
         * 是否启用自动保存
         */
        private Boolean autosave;

        /**
         * 是否强制保存
         */
        private Boolean forcesave;

        /**
         * 是否显示跟踪更改
         */
        private Boolean trackChanges;
    }
}

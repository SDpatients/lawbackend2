package com.lawbackend2.lawbackend2.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibOfficePreviewResponse {

    private String documentType;
    private DocumentInfo document;
    private EditorConfig editorConfig;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DocumentInfo {
        private String fileType;
        private String key;
        private String title;
        private String url;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EditorConfig {
        private String mode;
        private String lang;
        private UserInfo user;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private String id;
        private String name;
    }
}

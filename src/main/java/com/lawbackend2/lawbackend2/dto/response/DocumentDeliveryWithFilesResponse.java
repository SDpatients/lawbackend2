package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class DocumentDeliveryWithFilesResponse {

    private Long deliveryId;

    private List<FileRecordInfo> files;

    @Data
    public static class FileRecordInfo {
        private Long fileId;
        private String originalFileName;
        private String storedFileName;
        private Long fileSize;
        private String fileExtension;
        private String mimeType;
    }
}

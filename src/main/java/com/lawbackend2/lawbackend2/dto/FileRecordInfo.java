package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

@Data
public class FileRecordInfo {
    private Long id;

    private String originalFileName;

    private Long fileSize;

    private String fileExtension;

    private String mimeType;
}

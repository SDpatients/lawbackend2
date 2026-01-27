package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileRecordInfo {
    private Long id;

    private String originalFileName;

    private String filePath;

    private Long fileSize;

    private String fileExtension;

    private String mimeType;

    private LocalDateTime uploadTime;

    private String uploadUserName;

    private Integer sortOrder;
}

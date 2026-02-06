package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentExportHistoryResponse {

    private Long id;

    private Long templateId;

    private String templateName;

    private String exportType;

    private String fileName;

    private Long fileSize;

    private String exportStatus;

    private String errorMessage;

    private Long exportedBy;

    private String exportedByName;

    private LocalDateTime exportedTime;

    private Integer processingTime;

    private LocalDateTime createTime;
}

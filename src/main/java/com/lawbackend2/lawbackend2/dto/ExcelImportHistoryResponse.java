package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

@Data
public class ExcelImportHistoryResponse {
    private Long id;
    private Long templateId;
    private String templateName;
    private String fileName;
    private Long fileSize;
    private Integer sheetIndex;
    private Integer totalRows;
    private Integer successRows;
    private Integer failRows;
    private String importStatus;
    private String errorMessage;
    private Long importedBy;
    private String importedByName;
    private String importedTime;
    private Integer processingTime;
}

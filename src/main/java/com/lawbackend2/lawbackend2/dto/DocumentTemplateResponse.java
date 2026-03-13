package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DocumentTemplateResponse {

    private Long id;

    private String templateName;

    private String templateCode;

    private String templateType;

    private String description;

    private String filePath;

    private String configJson;

    private Boolean isDefault;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUserId;

    private Long updateUserId;

    private List<DocumentTemplateFieldDTO> fields;

    private List<DocumentTemplateMappingDTO> mappings;
}

package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

@Data
public class DocumentTemplateFieldDTO {

    private Long id;

    private String fieldName;

    private String fieldLabel;

    private String fieldType;

    private String sourceField;

    private String defaultValue;

    private Integer sortOrder;

    private Boolean isRequired;

    private String formatPattern;
}

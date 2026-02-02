package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

@Data
public class ExcelFieldValidationRuleResponse {
    private Long id;
    private String fieldName;
    private String ruleType;
    private String ruleValue;
    private String errorMessage;
    private Boolean isActive;
    private Integer priority;
    private Long createdBy;
    private String createdByName;
    private String createdTime;
}

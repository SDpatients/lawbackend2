package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

@Data
public class CreditorSimpleResponse {
    private Long id;
    private Long caseId;
    private String creditorName;
    private String idNumber;
    private String creditorType;
}

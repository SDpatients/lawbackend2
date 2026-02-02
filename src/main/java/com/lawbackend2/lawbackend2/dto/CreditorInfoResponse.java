package com.lawbackend2.lawbackend2.dto;

import com.lawbackend2.lawbackend2.enums.CreditorStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreditorInfoResponse {
    private Long id;
    private Long caseId;
    private String creditorName;
    private String creditorType;
    private String contactPhone;
    private String contactEmail;
    private String address;
    private String idNumber;
    private String legalRepresentative;
    private BigDecimal registeredCapital;
    private CreditorStatus creditorStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUserId;
    private Long updateUserId;
    private Boolean isDeleted;
    
    private String caseNumber;
    private String caseName;
}
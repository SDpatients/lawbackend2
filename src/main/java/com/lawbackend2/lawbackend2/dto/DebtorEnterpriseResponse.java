package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DebtorEnterpriseResponse {
    private Long id;
    private Long caseId;
    private String enterpriseName;
    private String unifiedSocialCreditCode;
    private String legalRepresentative;
    private String registrationAuthority;
    private LocalDate establishmentDate;
    private BigDecimal registeredCapital;
    private String businessScope;
    private String enterpriseType;
    private String industry;
    private String registeredAddress;
    private String contactPhone;
    private String contactPerson;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUserId;
    private Long updateUserId;
    private Boolean isDeleted;
    
    private String caseNumber;
    private String caseName;
}
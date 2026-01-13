package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class IntellectualPropertyCreateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "案件名称不能为空")
    private String caseName;

    @NotNull(message = "知识产权类型不能为空")
    private String ipType;

    @NotBlank(message = "知识产权名称不能为空")
    private String ipName;

    private String ipDescription;

    private String registrationNo;

    private LocalDateTime registrationDate;

    private String registrationAuthority;

    private LocalDateTime expiryDate;

    private BigDecimal registrationCost;

    private BigDecimal currentValue;

    private LocalDateTime valuationDate;

    private String valuationMethod;

    private String valuationInstitution;

    private String valuationReport;

    private Boolean isEncumbered;

    private String encumbranceType;

    private BigDecimal encumbranceAmount;

    private LocalDateTime encumbranceDate;

    private String custodian;

    private String custodianContact;

    private String insuranceStatus;

    private String insuranceCompany;

    private String insurancePolicyNo;

    private BigDecimal insuranceAmount;

    private LocalDateTime insuranceStartDate;

    private LocalDateTime insuranceEndDate;

    private String attachments;

    private String remarks;
}
package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PropertyCreateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "案件名称不能为空")
    private String caseName;

    @NotBlank(message = "财产类型不能为空")
    private String propertyType;

    @NotBlank(message = "财产名称不能为空")
    private String propertyName;

    private String propertyDescription;

    private String propertyLocation;

    private String propertyAddress;

    @NotBlank(message = "所有权人名称不能为空")
    private String ownerName;

    private String ownerType;

    private String ownershipCertificate;

    private String certificateNo;

    private LocalDateTime registrationDate;

    private LocalDateTime acquisitionDate;

    private String acquisitionMethod;

    private BigDecimal acquisitionCost;

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

    private Long relatedDebtId;

    private Long relatedClaimId;

    private String attachments;

    private String remarks;
}
package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RealEstateCreateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "案件名称不能为空")
    private String caseName;

    @NotNull(message = "房产类型不能为空")
    private String estateType;

    @NotBlank(message = "房产名称不能为空")
    private String estateName;

    @NotBlank(message = "房产地址不能为空")
    private String estateAddress;

    private String province;

    private String city;

    private String district;

    private String street;

    private String buildingNo;

    private String unitNo;

    private String floor;

    private String roomNo;

    private String landUseType;

    private BigDecimal landArea;

    private BigDecimal buildingArea;

    private BigDecimal usableArea;

    private BigDecimal constructionArea;

    private String buildingStructure;

    private Integer buildingYear;

    private Integer floorsAboveGround;

    private Integer floorsBelowGround;

    private String ownershipType;

    private String landCertificateNo;

    private String propertyCertificateNo;

    private LocalDateTime certificateDate;

    private LocalDateTime registrationDate;

    private LocalDateTime acquisitionDate;

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

    private String attachments;

    private String remarks;
}
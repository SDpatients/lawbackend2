package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EquipmentCreateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "案件名称不能为空")
    private String caseName;

    @NotNull(message = "设备类型不能为空")
    private String equipmentType;

    @NotBlank(message = "设备名称不能为空")
    private String equipmentName;

    private String equipmentDescription;

    private String equipmentModel;

    private String equipmentSpec;

    private String brand;

    private String manufacturer;

    private LocalDateTime manufactureDate;

    private LocalDateTime purchaseDate;

    private BigDecimal purchaseCost;

    private BigDecimal currentValue;

    private LocalDateTime valuationDate;

    private String valuationMethod;

    private String valuationInstitution;

    private String valuationReport;

    private Integer quantity;

    private String unit;

    private String condition;

    private String location;

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
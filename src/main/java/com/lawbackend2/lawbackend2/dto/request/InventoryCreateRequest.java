package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryCreateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "案件名称不能为空")
    private String caseName;

    @NotNull(message = "存货类型不能为空")
    private String inventoryType;

    @NotBlank(message = "存货名称不能为空")
    private String inventoryName;

    private String inventoryDescription;

    private String category;

    private String specification;

    private String unit;

    @NotNull(message = "数量不能为空")
    private BigDecimal quantity;

    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;

    @NotNull(message = "总价值不能为空")
    private BigDecimal totalValue;

    private BigDecimal currentQuantity;

    private BigDecimal currentValue;

    private LocalDateTime valuationDate;

    private String valuationMethod;

    private String valuationInstitution;

    private String valuationReport;

    private String storageLocation;

    private String condition;

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
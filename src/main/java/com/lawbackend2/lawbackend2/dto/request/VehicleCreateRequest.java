package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VehicleCreateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "案件名称不能为空")
    private String caseName;

    @NotNull(message = "车辆类型不能为空")
    private String vehicleType;

    @NotBlank(message = "车辆名称不能为空")
    private String vehicleName;

    private String vehicleDescription;

    @NotBlank(message = "车牌号不能为空")
    private String licensePlate;

    private String vin;

    private String engineNo;

    private String brand;

    private String model;

    private String color;

    private LocalDateTime manufactureDate;

    private LocalDateTime registrationDate;

    private LocalDateTime acquisitionDate;

    private BigDecimal acquisitionCost;

    private BigDecimal currentValue;

    private LocalDateTime valuationDate;

    private String valuationMethod;

    private String valuationInstitution;

    private String valuationReport;

    private BigDecimal mileage;

    private String fuelType;

    private BigDecimal displacement;

    private Integer seatingCapacity;

    private BigDecimal loadCapacity;

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
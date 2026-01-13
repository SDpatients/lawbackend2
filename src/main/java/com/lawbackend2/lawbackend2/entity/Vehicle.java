package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_vehicle", indexes = {
    @Index(name = "uk_vehicle_no", columnList = "vehicle_no", unique = true),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_vehicle_type", columnList = "vehicle_type"),
    @Index(name = "idx_vehicle_status", columnList = "vehicle_status"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class Vehicle extends BaseEntity {

    @Column(name = "vehicle_no", length = 50, unique = true)
    private String vehicleNo;

    @Column(name = "property_id")
    private Long propertyId;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_name", length = 200)
    private String caseName;

    @Column(name = "vehicle_type", length = 50)
    private String vehicleType;

    @Column(name = "vehicle_name", length = 200)
    private String vehicleName;

    @Column(name = "vehicle_description", columnDefinition = "TEXT")
    private String vehicleDescription;

    @Column(name = "license_plate", length = 50)
    private String licensePlate;

    @Column(name = "vin", length = 50)
    private String vin;

    @Column(name = "engine_no", length = 50)
    private String engineNo;

    @Column(name = "brand", length = 100)
    private String brand;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "manufacture_date")
    private LocalDateTime manufactureDate;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "acquisition_date")
    private LocalDateTime acquisitionDate;

    @Column(name = "acquisition_cost", precision = 18, scale = 2)
    private BigDecimal acquisitionCost;

    @Column(name = "current_value", precision = 18, scale = 2)
    private BigDecimal currentValue;

    @Column(name = "valuation_date")
    private LocalDateTime valuationDate;

    @Column(name = "valuation_method", length = 50)
    private String valuationMethod;

    @Column(name = "valuation_institution", length = 200)
    private String valuationInstitution;

    @Column(name = "valuation_report", length = 200)
    private String valuationReport;

    @Column(name = "mileage", precision = 18, scale = 2)
    private BigDecimal mileage;

    @Column(name = "fuel_type", length = 50)
    private String fuelType;

    @Column(name = "displacement", precision = 10, scale = 2)
    private BigDecimal displacement;

    @Column(name = "seating_capacity")
    private Integer seatingCapacity;

    @Column(name = "load_capacity", precision = 18, scale = 2)
    private BigDecimal loadCapacity;

    @Column(name = "is_encumbered")
    private Boolean isEncumbered = false;

    @Column(name = "encumbrance_type", length = 50)
    private String encumbranceType;

    @Column(name = "encumbrance_amount", precision = 18, scale = 2)
    private BigDecimal encumbranceAmount;

    @Column(name = "encumbrance_date")
    private LocalDateTime encumbranceDate;

    @Column(name = "vehicle_status", length = 20)
    private String vehicleStatus = "NORMAL";

    @Column(name = "management_status", length = 20)
    private String managementStatus = "PENDING";

    @Column(name = "custodian", length = 100)
    private String custodian;

    @Column(name = "custodian_contact", length = 100)
    private String custodianContact;

    @Column(name = "insurance_status", length = 20)
    private String insuranceStatus = "UNINSURED";

    @Column(name = "insurance_company", length = 200)
    private String insuranceCompany;

    @Column(name = "insurance_policy_no", length = 100)
    private String insurancePolicyNo;

    @Column(name = "insurance_amount", precision = 18, scale = 2)
    private BigDecimal insuranceAmount;

    @Column(name = "insurance_start_date")
    private LocalDateTime insuranceStartDate;

    @Column(name = "insurance_end_date")
    private LocalDateTime insuranceEndDate;

    @Column(name = "attachments", columnDefinition = "TEXT")
    private String attachments;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
}
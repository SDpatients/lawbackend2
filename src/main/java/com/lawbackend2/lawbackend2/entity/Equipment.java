package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_equipment", indexes = {
    @Index(name = "uk_equipment_no", columnList = "equipment_no", unique = true),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_equipment_type", columnList = "equipment_type"),
    @Index(name = "idx_equipment_status", columnList = "equipment_status"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class Equipment extends BaseEntity {

    @Column(name = "equipment_no", length = 50, unique = true)
    private String equipmentNo;

    @Column(name = "property_id")
    private Long propertyId;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_name", length = 200)
    private String caseName;

    @Column(name = "equipment_type", length = 50)
    private String equipmentType;

    @Column(name = "equipment_name", length = 200)
    private String equipmentName;

    @Column(name = "equipment_description", columnDefinition = "TEXT")
    private String equipmentDescription;

    @Column(name = "equipment_model", length = 100)
    private String equipmentModel;

    @Column(name = "equipment_spec", length = 200)
    private String equipmentSpec;

    @Column(name = "brand", length = 100)
    private String brand;

    @Column(name = "manufacturer", length = 200)
    private String manufacturer;

    @Column(name = "manufacture_date")
    private LocalDateTime manufactureDate;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Column(name = "purchase_cost", precision = 18, scale = 2)
    private BigDecimal purchaseCost;

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

    @Column(name = "quantity")
    private Integer quantity = 1;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "condition", length = 20)
    private String condition;

    @Column(name = "location", length = 500)
    private String location;

    @Column(name = "is_encumbered")
    private Boolean isEncumbered = false;

    @Column(name = "encumbrance_type", length = 50)
    private String encumbranceType;

    @Column(name = "encumbrance_amount", precision = 18, scale = 2)
    private BigDecimal encumbranceAmount;

    @Column(name = "encumbrance_date")
    private LocalDateTime encumbranceDate;

    @Column(name = "equipment_status", length = 20)
    private String equipmentStatus = "NORMAL";

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
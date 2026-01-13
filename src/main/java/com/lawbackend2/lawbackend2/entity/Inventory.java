package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_inventory", indexes = {
    @Index(name = "uk_inventory_no", columnList = "inventory_no", unique = true),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_inventory_type", columnList = "inventory_type"),
    @Index(name = "idx_inventory_status", columnList = "inventory_status"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class Inventory extends BaseEntity {

    @Column(name = "inventory_no", length = 50, unique = true)
    private String inventoryNo;

    @Column(name = "property_id")
    private Long propertyId;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_name", length = 200)
    private String caseName;

    @Column(name = "inventory_type", length = 50)
    private String inventoryType;

    @Column(name = "inventory_name", length = 200)
    private String inventoryName;

    @Column(name = "inventory_description", columnDefinition = "TEXT")
    private String inventoryDescription;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "specification", length = 200)
    private String specification;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "unit_price", precision = 18, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_value", precision = 18, scale = 2)
    private BigDecimal totalValue;

    @Column(name = "current_quantity")
    private BigDecimal currentQuantity;

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

    @Column(name = "storage_location", length = 500)
    private String storageLocation;

    @Column(name = "condition", length = 20)
    private String condition;

    @Column(name = "is_encumbered")
    private Boolean isEncumbered = false;

    @Column(name = "encumbrance_type", length = 50)
    private String encumbranceType;

    @Column(name = "encumbrance_amount", precision = 18, scale = 2)
    private BigDecimal encumbranceAmount;

    @Column(name = "encumbrance_date")
    private LocalDateTime encumbranceDate;

    @Column(name = "inventory_status", length = 20)
    private String inventoryStatus = "NORMAL";

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
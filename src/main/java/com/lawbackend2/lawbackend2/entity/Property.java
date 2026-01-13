package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_property", indexes = {
    @Index(name = "uk_property_no", columnList = "property_no", unique = true),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_property_type", columnList = "property_type"),
    @Index(name = "idx_property_status", columnList = "property_status"),
    @Index(name = "idx_registration_date", columnList = "registration_date"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class Property extends BaseEntity {

    @Column(name = "property_no", length = 50, unique = true)
    private String propertyNo;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_name", length = 200)
    private String caseName;

    @Column(name = "property_type", length = 50)
    private String propertyType;

    @Column(name = "property_name", length = 200)
    private String propertyName;

    @Column(name = "property_description", columnDefinition = "TEXT")
    private String propertyDescription;

    @Column(name = "property_location", length = 500)
    private String propertyLocation;

    @Column(name = "property_address", length = 500)
    private String propertyAddress;

    @Column(name = "owner_name", length = 200)
    private String ownerName;

    @Column(name = "owner_type", length = 50)
    private String ownerType;

    @Column(name = "ownership_certificate", length = 200)
    private String ownershipCertificate;

    @Column(name = "certificate_no", length = 100)
    private String certificateNo;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "acquisition_date")
    private LocalDateTime acquisitionDate;

    @Column(name = "acquisition_method", length = 50)
    private String acquisitionMethod;

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

    @Column(name = "is_encumbered")
    private Boolean isEncumbered = false;

    @Column(name = "encumbrance_type", length = 50)
    private String encumbranceType;

    @Column(name = "encumbrance_amount", precision = 18, scale = 2)
    private BigDecimal encumbranceAmount;

    @Column(name = "encumbrance_date")
    private LocalDateTime encumbranceDate;

    @Column(name = "property_status", length = 20)
    private String propertyStatus = "NORMAL";

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

    @Column(name = "related_debt_id")
    private Long relatedDebtId;

    @Column(name = "related_claim_id")
    private Long relatedClaimId;

    @Column(name = "attachments", columnDefinition = "TEXT")
    private String attachments;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
}
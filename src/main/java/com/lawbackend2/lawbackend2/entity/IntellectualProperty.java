package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_intellectual_property", indexes = {
    @Index(name = "uk_ip_no", columnList = "ip_no", unique = true),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_ip_type", columnList = "ip_type"),
    @Index(name = "idx_ip_status", columnList = "ip_status"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class IntellectualProperty extends BaseEntity {

    @Column(name = "ip_no", length = 50, unique = true)
    private String ipNo;

    @Column(name = "property_id")
    private Long propertyId;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_name", length = 200)
    private String caseName;

    @Column(name = "ip_type", length = 50)
    private String ipType;

    @Column(name = "ip_name", length = 200)
    private String ipName;

    @Column(name = "ip_description", columnDefinition = "TEXT")
    private String ipDescription;

    @Column(name = "registration_no", length = 100)
    private String registrationNo;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "registration_authority", length = 200)
    private String registrationAuthority;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "registration_cost", precision = 18, scale = 2)
    private BigDecimal registrationCost;

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

    @Column(name = "ip_status", length = 20)
    private String ipStatus = "NORMAL";

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
package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_real_estate", indexes = {
    @Index(name = "uk_estate_no", columnList = "estate_no", unique = true),
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_estate_type", columnList = "estate_type"),
    @Index(name = "idx_estate_status", columnList = "estate_status"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class RealEstate extends BaseEntity {

    @Column(name = "estate_no", length = 50, unique = true)
    private String estateNo;

    @Column(name = "property_id")
    private Long propertyId;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_name", length = 200)
    private String caseName;

    @Column(name = "estate_type", length = 50)
    private String estateType;

    @Column(name = "estate_name", length = 200)
    private String estateName;

    @Column(name = "estate_address", length = 500)
    private String estateAddress;

    @Column(name = "province", length = 50)
    private String province;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "district", length = 50)
    private String district;

    @Column(name = "street", length = 200)
    private String street;

    @Column(name = "building_no", length = 50)
    private String buildingNo;

    @Column(name = "unit_no", length = 50)
    private String unitNo;

    @Column(name = "floor", length = 20)
    private String floor;

    @Column(name = "room_no", length = 50)
    private String roomNo;

    @Column(name = "land_use_type", length = 50)
    private String landUseType;

    @Column(name = "land_area", precision = 18, scale = 2)
    private BigDecimal landArea;

    @Column(name = "building_area", precision = 18, scale = 2)
    private BigDecimal buildingArea;

    @Column(name = "usable_area", precision = 18, scale = 2)
    private BigDecimal usableArea;

    @Column(name = "construction_area", precision = 18, scale = 2)
    private BigDecimal constructionArea;

    @Column(name = "building_structure", length = 50)
    private String buildingStructure;

    @Column(name = "building_year")
    private Integer buildingYear;

    @Column(name = "floors_above_ground")
    private Integer floorsAboveGround;

    @Column(name = "floors_below_ground")
    private Integer floorsBelowGround;

    @Column(name = "ownership_type", length = 50)
    private String ownershipType;

    @Column(name = "land_certificate_no", length = 100)
    private String landCertificateNo;

    @Column(name = "property_certificate_no", length = 100)
    private String propertyCertificateNo;

    @Column(name = "certificate_date")
    private LocalDateTime certificateDate;

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

    @Column(name = "is_encumbered")
    private Boolean isEncumbered = false;

    @Column(name = "encumbrance_type", length = 50)
    private String encumbranceType;

    @Column(name = "encumbrance_amount", precision = 18, scale = 2)
    private BigDecimal encumbranceAmount;

    @Column(name = "encumbrance_date")
    private LocalDateTime encumbranceDate;

    @Column(name = "estate_status", length = 20)
    private String estateStatus = "NORMAL";

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
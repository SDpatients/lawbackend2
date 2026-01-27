package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_debtor_enterprise")
public class DebtorEnterprise extends BaseEntity {

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "enterprise_name", length = 255)
    private String enterpriseName;

    @Column(name = "unified_social_credit_code", unique = true, length = 50)
    private String unifiedSocialCreditCode;

    @Column(name = "legal_representative", length = 100)
    private String legalRepresentative;

    @Column(name = "registration_authority", length = 255)
    private String registrationAuthority;

    @Column(name = "establishment_date")
    private LocalDate establishmentDate;

    @Column(name = "registered_capital", precision = 18, scale = 4)
    private BigDecimal registeredCapital;

    @Column(name = "business_scope", columnDefinition = "TEXT")
    private String businessScope;

    @Column(name = "enterprise_type", length = 100)
    private String enterpriseType;

    @Column(name = "industry", length = 100)
    private String industry;

    @Column(name = "registered_address", length = 500)
    private String registeredAddress;

    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Column(name = "status", length = 20)
    private String status;
}

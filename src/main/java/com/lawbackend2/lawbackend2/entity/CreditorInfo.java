package com.lawbackend2.lawbackend2.entity;

import com.lawbackend2.lawbackend2.enums.CreditorStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_creditor_info")
public class CreditorInfo extends BaseEntity {
    @Column(name = "case_id")
    private Long caseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", insertable = false, updatable = false)
    private BankruptCase bankruptCase;

    @Column(name = "creditor_name", length = 255)
    private String creditorName;

    @Column(name = "creditor_type", length = 50)
    private String creditorType;

    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "id_number", length = 50)
    private String idNumber;

    @Column(name = "legal_representative", length = 100)
    private String legalRepresentative;

    @Column(name = "registered_capital", precision = 22, scale = 4)
    private BigDecimal registeredCapital;

    @Enumerated(EnumType.STRING)
    @Column(name = "creditor_status", length = 255)
    private CreditorStatus creditorStatus;
}

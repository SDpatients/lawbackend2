package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_creditor_claim")
public class CreditorClaim extends BaseEntity {
    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_name", length = 255)
    private String caseName;

    @Column(name = "debtor", length = 255)
    private String debtor;

    @Convert(converter = com.lawbackend2.lawbackend2.util.EncryptedStringConverter.class)
    @Column(name = "bank_account", length = 500)
    private String bankAccount;

    @Column(name = "creditor_name", length = 255)
    private String creditorName;

    @Column(name = "creditor_type", length = 50)
    private String creditorType;

    @Column(name = "credit_code", length = 100)
    private String creditCode;

    @Column(name = "legal_representative", length = 100)
    private String legalRepresentative;

    @Column(name = "service_address", length = 500)
    private String serviceAddress;

    @Column(name = "agent_name", length = 100)
    private String agentName;

    @Convert(converter = com.lawbackend2.lawbackend2.util.EncryptedStringConverter.class)
    @Column(name = "agent_phone", length = 255)
    private String agentPhone;

    @Convert(converter = com.lawbackend2.lawbackend2.util.EncryptedStringConverter.class)
    @Column(name = "agent_id_card", length = 255)
    private String agentIdCard;

    @Column(name = "agent_address", length = 500)
    private String agentAddress;

    @Column(name = "account_name", length = 255)
    private String accountName;

    @Convert(converter = com.lawbackend2.lawbackend2.util.EncryptedStringConverter.class)
    @Column(name = "creditor_bank_account", length = 500)
    private String creditorBankAccount;

    @Column(name = "bank_name", length = 255)
    private String bankName;

    @Column(name = "principal", precision = 18, scale = 2)
    private BigDecimal principal;

    @Column(name = "interest", precision = 18, scale = 2)
    private BigDecimal interest;

    @Column(name = "penalty", precision = 18, scale = 2)
    private BigDecimal penalty;

    @Column(name = "other_losses", precision = 18, scale = 2)
    private BigDecimal otherLosses;

    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "has_court_judgment")
    private Boolean hasCourtJudgment = false;

    @Column(name = "has_execution")
    private Boolean hasExecution = false;

    @Column(name = "has_collateral")
    private Boolean hasCollateral = false;

    @Column(name = "claim_nature", length = 50)
    private String claimNature;

    @Column(name = "claim_type", length = 50)
    private String claimType;

    @Column(name = "claim_facts", columnDefinition = "TEXT")
    private String claimFacts;

    @Column(name = "claim_nature_manager", length = 50)
    private String claimNatureManager;

    @Column(name = "claim_identifier", length = 100)
    private String claimIdentifier;

    @Column(name = "evidence_list", columnDefinition = "TEXT")
    private String evidenceList;

    @Column(name = "evidence_materials", columnDefinition = "TEXT")
    private String evidenceMaterials;

    @Column(name = "evidence_attachments", columnDefinition = "TEXT")
    private String evidenceAttachments;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "registration_status", length = 50)
    private String registrationStatus = "PENDING";
}

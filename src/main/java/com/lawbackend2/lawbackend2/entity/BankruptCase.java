package com.lawbackend2.lawbackend2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_bankrupt_case")
public class BankruptCase extends BaseEntity {
    @Column(name = "case_number", unique = true, length = 100)
    private String caseNumber;

    @Column(name = "case_name", length = 255)
    private String caseName;

    @Column(name = "acceptance_date")
    private LocalDate acceptanceDate;

    @Column(name = "case_source", length = 100)
    private String caseSource;

    @Column(name = "acceptance_court", length = 255)
    private String acceptanceCourt;

    @Column(name = "designated_institution", length = 255)
    private String designatedInstitution;

    @Column(name = "main_responsible_person", length = 100)
    private String mainResponsiblePerson;

    @Column(name = "is_simplified_trial")
    private Boolean isSimplifiedTrial = false;

    @Column(name = "case_reason", length = 255)
    private String caseReason;

    @Column(name = "case_progress", length = 50)
    private String caseProgress;

    @Column(name = "debt_claim_deadline")
    private LocalDateTime debtClaimDeadline;

    @Column(name = "filing_date")
    private LocalDate filingDate;

    @Column(name = "closing_date")
    private LocalDate closingDate;

    @Column(name = "bankruptcy_date")
    private LocalDate bankruptcyDate;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Column(name = "cancellation_date")
    private LocalDate cancellationDate;

    @Column(name = "archiving_date")
    private LocalDate archivingDate;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "file_upload_path", length = 1000)
    private String fileUploadPath;

    @Column(name = "undertaking_personnel", length = 255)
    private String undertakingPersonnel;

    @JsonIgnore
    @Column(name = "creator_name", length = 100)
    private String creatorName;

    @JsonIgnore
    @Column(name = "reviewer_id")
    private Long reviewerId;

    @JsonIgnore
    @Column(name = "review_status", length = 20)
    private String reviewStatus = "PENDING";

    @JsonIgnore
    @Column(name = "review_time")
    private LocalDateTime reviewTime;

    @JsonIgnore
    @Column(name = "review_opinion", columnDefinition = "TEXT")
    private String reviewOpinion;

    @JsonIgnore
    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Column(name = "case_status", length = 50)
    private String caseStatus = "ONGOING";

    @Column(name = "designated_judge", length = 100)
    private String designatedJudge;
}

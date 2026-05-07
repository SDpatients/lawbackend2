package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CaseUpdateRequest {
    private String caseName;

    private String caseReason;

    private String remarks;

    private LocalDate filingDate;

    private String caseProgress;

    private String caseType;

    private String mainResponsiblePerson;

    private String designatedInstitution;

    private String acceptanceCourt;

    private LocalDate debtClaimDeadline;
}

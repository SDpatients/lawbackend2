package com.lawbackend2.lawbackend2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentCaseSearchRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long caseId;
    private String caseNumber;
    private String caseName;
    private String caseStatus;
    private String caseProgress;
    private LocalDateTime searchTime;

    public RecentCaseSearchRecord(Long caseId, String caseNumber, String caseName, String caseStatus, String caseProgress) {
        this.caseId = caseId;
        this.caseNumber = caseNumber;
        this.caseName = caseName;
        this.caseStatus = caseStatus;
        this.caseProgress = caseProgress;
        this.searchTime = LocalDateTime.now();
    }
}
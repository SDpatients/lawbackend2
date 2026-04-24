package com.lawbackend2.lawbackend2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

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

    public RecentCaseSearchRecord(Long caseId, String caseNumber, String caseName, String caseStatus, String caseProgress, Long timestamp) {
        this.caseId = caseId;
        this.caseNumber = caseNumber;
        this.caseName = caseName;
        this.caseStatus = caseStatus;
        this.caseProgress = caseProgress;
        this.searchTime = LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(timestamp),
            ZoneId.systemDefault()
        );
    }
}
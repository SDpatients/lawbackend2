package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

@Data
public class CaseSimpleInfo {
    private Long id;
    private String caseNumber;
    private String caseName;
    private String reviewStatus;
    private String reviewOpinion;

    public CaseSimpleInfo() {
    }

    public CaseSimpleInfo(Long id, String caseNumber, String caseName) {
        this.id = id;
        this.caseNumber = caseNumber;
        this.caseName = caseName;
    }

    public CaseSimpleInfo(Long id, String caseNumber, String caseName, String reviewStatus, String reviewOpinion) {
        this.id = id;
        this.caseNumber = caseNumber;
        this.caseName = caseName;
        this.reviewStatus = reviewStatus;
        this.reviewOpinion = reviewOpinion;
    }

    public static CaseSimpleInfo of(Long id, String caseNumber, String caseName) {
        return new CaseSimpleInfo(id, caseNumber, caseName);
    }

    public static CaseSimpleInfo of(Long id, String caseNumber, String caseName, String reviewStatus, String reviewOpinion) {
        return new CaseSimpleInfo(id, caseNumber, caseName, reviewStatus, reviewOpinion);
    }
}

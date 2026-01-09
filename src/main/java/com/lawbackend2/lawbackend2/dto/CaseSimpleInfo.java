package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

@Data
public class CaseSimpleInfo {
    private Long id;
    private String caseNumber;
    private String caseName;

    public CaseSimpleInfo() {
    }

    public CaseSimpleInfo(Long id, String caseNumber, String caseName) {
        this.id = id;
        this.caseNumber = caseNumber;
        this.caseName = caseName;
    }

    public static CaseSimpleInfo of(Long id, String caseNumber, String caseName) {
        return new CaseSimpleInfo(id, caseNumber, caseName);
    }
}

package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserCaseListResponse {
    private Long id;
    private String caseNumber;
    private String acceptanceCourt;
    private String designatedJudge;
    private LocalDate filingDate;
    private String caseProgress;
    private String caseStatus;
    private Long creatorId;

    public UserCaseListResponse(Long id, String caseNumber, String acceptanceCourt, String designatedJudge, LocalDate filingDate, String caseProgress, String caseStatus, Long creatorId) {
        this.id = id;
        this.caseNumber = caseNumber;
        this.acceptanceCourt = acceptanceCourt;
        this.designatedJudge = designatedJudge;
        this.filingDate = filingDate;
        this.caseProgress = caseProgress;
        this.caseStatus = caseStatus;
        this.creatorId = creatorId;
    }

    public static UserCaseListResponse of(Long id, String caseNumber, String acceptanceCourt, String designatedJudge, LocalDate filingDate, String caseProgress, String caseStatus, Long creatorId) {
        return new UserCaseListResponse(id, caseNumber, acceptanceCourt, designatedJudge, filingDate, caseProgress, caseStatus, creatorId);
    }
}
package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CaseTaskSubmissionResponse {
    private Long id;
    private Long caseTaskId;
    private String submissionTitle;
    private String submissionContent;
    private String submissionType;
    private Integer submissionNumber;
    private String status;
    private String creatorName;
    private Long reviewerId;
    private String reviewOpinion;
    private LocalDateTime reviewTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer fileCount;
}

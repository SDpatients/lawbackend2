package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class WorkPlanResponse {

    private Long id;
    private String status;
    private Boolean isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUserId;
    private String createUserName;
    private Long updateUserId;
    private String planNumber;
    private String planType;
    private String planContent;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long responsibleUserId;
    private String responsibleUserName;
    private String executionStatus;
    private Long caseId;
    private String caseNumber;
    private String caseName;
}

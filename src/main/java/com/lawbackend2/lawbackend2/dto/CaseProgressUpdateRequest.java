package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;

@Data
public class CaseProgressUpdateRequest {
    private String stageName;

    private String stageDescription;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate expectedEndDate;

    private String progressStatus;

    @Min(value = 0, message = "完成百分比不能小于0")
    @Max(value = 100, message = "完成百分比不能大于100")
    private Integer completionPercentage;

    private String keyTasks;

    private String completedTasks;

    private String pendingTasks;

    private String issues;

    private String solutions;

    private String attachments;

    private String responsiblePerson;

    private Long responsiblePersonId;

    private String remarks;

    private Boolean isCompleted;
}

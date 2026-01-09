package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class WorkPlanUpdateRequest {

    @NotBlank(message = "计划编号不能为空")
    private String planNumber;

    @NotBlank(message = "计划类型不能为空")
    private String planType;

    @NotBlank(message = "计划内容不能为空")
    private String planContent;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long responsibleUserId;

    private Long caseId;

    private String executionStatus;
}

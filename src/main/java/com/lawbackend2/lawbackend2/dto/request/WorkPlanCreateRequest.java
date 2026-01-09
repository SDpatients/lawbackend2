package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class WorkPlanCreateRequest {

    @NotBlank(message = "计划编号不能为空")
    private String planNumber;

    @NotBlank(message = "计划类型不能为空")
    private String planType;

    @NotBlank(message = "计划内容不能为空")
    private String planContent;

    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    @NotNull(message = "负责人ID不能为空")
    private Long responsibleUserId;

    @NotNull(message = "案件ID不能为空")
    private Long caseId;
}

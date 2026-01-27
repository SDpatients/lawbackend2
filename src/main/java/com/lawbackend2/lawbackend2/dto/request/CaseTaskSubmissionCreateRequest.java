package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CaseTaskSubmissionCreateRequest {
    @NotNull(message = "任务ID不能为空")
    private Long caseTaskId;

    @NotBlank(message = "提交标题不能为空")
    private String submissionTitle;

    private String submissionContent;

    private String submissionType;
}

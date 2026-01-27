package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CaseTaskSubmissionUpdateRequest {
    @NotNull(message = "提交ID不能为空")
    private Long id;

    private String submissionTitle;

    private String submissionContent;

    private String submissionType;
}

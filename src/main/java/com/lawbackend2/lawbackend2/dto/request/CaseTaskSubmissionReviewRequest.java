package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CaseTaskSubmissionReviewRequest {
    @NotBlank(message = "审核意见不能为空")
    private String reviewOpinion;

    @NotBlank(message = "审核状态不能为空")
    private String status;
}

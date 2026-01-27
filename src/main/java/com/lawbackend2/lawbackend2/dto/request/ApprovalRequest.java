package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ApprovalRequest {
    @NotBlank(message = "审核结果不能为空")
    private String approvalResult;

    private String approvalOpinion;

    @NotNull(message = "审核人ID不能为空")
    private Long approverId;
}
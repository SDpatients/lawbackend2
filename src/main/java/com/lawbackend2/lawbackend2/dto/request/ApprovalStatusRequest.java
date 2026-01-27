package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ApprovalStatusRequest {
    @NotBlank(message = "审批状态不能为空")
    private String approvalStatus;
}
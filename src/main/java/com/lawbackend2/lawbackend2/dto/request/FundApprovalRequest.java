package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class FundApprovalRequest {

    @NotBlank(message = "审批状态不能为空")
    @Pattern(regexp = "^(APPROVED|REJECTED)$", message = "审批状态不正确")
    private String approvalStatus;

    private String approvalOpinion;
}

package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ExpenseReimbursementApprovalRequest {

    @NotNull(message = "审批状态不能为空")
    @NotBlank(message = "审批状态不能为空")
    private String approvalStatus;

    @Size(max = 500, message = "审批意见不能超过500个字符")
    private String approvalOpinion;
}

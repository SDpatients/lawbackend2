package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ApprovalCreateRequest {
    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    private Long lawyerId;

    @NotBlank(message = "审核类型不能为空")
    private String approvalType;

    @NotBlank(message = "审核标题不能为空")
    private String approvalTitle;

    private String approvalContent;

    private String approvalAttachment;

    private String remark;
}
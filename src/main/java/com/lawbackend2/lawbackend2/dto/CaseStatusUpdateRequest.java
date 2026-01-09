package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CaseStatusUpdateRequest {
    @NotBlank(message = "案件状态不能为空")
    private String caseStatus;

    private String remark;
}

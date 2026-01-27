package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CaseProcessStageStatusUpdateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "模块编码不能为空")
    private String moduleCode;

    @NotBlank(message = "状态不能为空")
    private String status;
}

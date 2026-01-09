package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class WorkPlanStatusRequest {

    @NotBlank(message = "执行状态不能为空")
    @Pattern(regexp = "^(NOT_STARTED|IN_PROGRESS|COMPLETED|DELAYED|CANCELLED)$", message = "执行状态不正确")
    private String executionStatus;
}

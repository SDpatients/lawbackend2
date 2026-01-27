package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class WorkLogStatusRequest {

    @NotBlank(message = "状态不能为空")
    private String status;
}

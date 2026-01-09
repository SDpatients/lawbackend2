package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FundApprovalStatusRequest {

    @NotNull(message = "状态不能为空")
    private String status;
}

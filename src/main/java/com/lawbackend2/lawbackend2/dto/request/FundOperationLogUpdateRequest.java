package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FundOperationLogUpdateRequest {

    private Long caseId;

    @NotBlank(message = "操作类型不能为空")
    private String operationType;

    @NotBlank(message = "操作内容不能为空")
    private String operationContent;

    @NotBlank(message = "操作人ID不能为空")
    private Long operatorId;

    private String ipAddress;

    private String browserInfo;
}

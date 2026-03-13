package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreditorClaimQueryRequest {
    
    @NotNull(message = "案件 ID 不能为空")
    private Long caseId;
    
    private String creditorName;
    
    private String creditorType;
    
    private String claimType;
    
    private String creditorStatus;
    
    @NotNull(message = "页码不能为空")
    private Integer pageNum = 1;
    
    @NotNull(message = "每页大小不能为空")
    private Integer pageSize = 10;
}

package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AdministratorCreateRequest {

    @NotBlank(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "管理人类型不能为空")
    private String administratorType;

    private Long responsiblePersonId;

    private String contactPhone;

    private String contactEmail;

    private String officeAddress;
}

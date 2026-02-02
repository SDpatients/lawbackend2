package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AdministratorCreateRequest {

    @NotBlank(message = "管理人名称不能为空")
    private String administratorName;

    private Long caseId;

    @NotBlank(message = "管理人类型不能为空")
    private String administratorType;

    private Long responsiblePersonId;

    private String responsiblePerson;

    private String contactPhone;

    private String contactEmail;

    private String officeAddress;
}

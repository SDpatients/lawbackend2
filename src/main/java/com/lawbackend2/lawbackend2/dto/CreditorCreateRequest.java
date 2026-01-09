package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreditorCreateRequest {
    private Long caseId;

    @NotBlank(message = "债权人名称不能为空")
    private String creditorName;

    @NotBlank(message = "债权人类型不能为空")
    private String creditorType;

    private String contactPhone;

    private String contactEmail;

    private String address;

    private String idNumber;

    private String legalRepresentative;

    private java.math.BigDecimal registeredCapital;
}

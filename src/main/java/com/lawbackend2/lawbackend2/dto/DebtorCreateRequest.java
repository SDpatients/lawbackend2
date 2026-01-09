package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DebtorCreateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "企业名称不能为空")
    private String enterpriseName;

    private String unifiedSocialCreditCode;

    @NotBlank(message = "法定代表人不能为空")
    private String legalRepresentative;

    private String registrationAuthority;

    private LocalDate establishmentDate;

    private BigDecimal registeredCapital;

    private String businessScope;

    private String enterpriseType;

    private String industry;

    private String registeredAddress;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    private String contactPhone;

    private String contactPerson;
}

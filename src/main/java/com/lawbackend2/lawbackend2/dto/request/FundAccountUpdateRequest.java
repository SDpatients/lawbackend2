package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class FundAccountUpdateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "案件名称不能为空")
    private String caseName;

    @NotBlank(message = "账户名称不能为空")
    private String accountName;

    @NotBlank(message = "账户类型不能为空")
    private String accountType;

    @NotBlank(message = "银行名称不能为空")
    private String bankName;

    @NotBlank(message = "银行账号不能为空")
    private String bankAccount;
}

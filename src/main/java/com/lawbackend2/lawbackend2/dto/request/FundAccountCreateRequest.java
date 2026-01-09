package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class FundAccountCreateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "案件名称不能为空")
    private String caseName;

    @NotBlank(message = "账户名称不能为空")
    private String accountName;

    @NotBlank(message = "账户类型不能为空")
    private String accountType;

    @NotNull(message = "初始余额不能为空")
    @Positive(message = "初始余额必须大于0")
    private BigDecimal initialBalance;

    @NotBlank(message = "银行名称不能为空")
    private String bankName;

    @NotBlank(message = "银行账号不能为空")
    private String bankAccount;
}

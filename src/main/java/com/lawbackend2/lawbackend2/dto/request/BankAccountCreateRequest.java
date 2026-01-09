package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BankAccountCreateRequest {

    @NotBlank(message = "账户名称不能为空")
    private String accountName;

    @NotBlank(message = "银行名称不能为空")
    private String bankName;

    @NotBlank(message = "银行账号不能为空")
    @Pattern(regexp = "^[0-9]{10,30}$", message = "银行账号格式不正确")
    private String accountNumber;

    @NotBlank(message = "账户类型不能为空")
    private String accountType;

    @Pattern(regexp = "^[A-Z]{3}$", message = "币种格式不正确")
    private String currency = "CNY";

    @NotNull(message = "当前余额不能为空")
    @Positive(message = "当前余额必须大于0")
    private BigDecimal currentBalance;

    private LocalDate openingDate;

    @NotBlank(message = "密码不能为空")
    private String password;
}

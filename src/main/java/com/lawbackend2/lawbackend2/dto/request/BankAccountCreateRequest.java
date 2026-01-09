package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
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

    private String currency = "CNY";

    private BigDecimal currentBalance;

    private LocalDate openingDate;

    @NotBlank(message = "密码不能为空")
    private String password;
}

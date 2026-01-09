package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class BankAccountUpdateRequest {

    @NotBlank(message = "账户名称不能为空")
    private String accountName;

    @NotNull(message = "当前余额不能为空")
    @Positive(message = "当前余额必须大于0")
    private BigDecimal currentBalance;
}

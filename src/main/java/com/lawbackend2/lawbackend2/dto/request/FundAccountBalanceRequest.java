package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class FundAccountBalanceRequest {

    @NotNull(message = "当前余额不能为空")
    @DecimalMin(value = "0", inclusive = true, message = "当前余额不能为负数")
    private BigDecimal currentBalance;
}

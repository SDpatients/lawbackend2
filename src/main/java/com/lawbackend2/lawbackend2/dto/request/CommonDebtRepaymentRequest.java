package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CommonDebtRepaymentRequest {

    @NotBlank(message = "清偿状态不能为空")
    private String repaymentStatus;

    @NotNull(message = "清偿金额不能为空")
    private BigDecimal repaidAmount;

    private String repaymentMethod;

    private Long repaymentAccountId;

    private String repaymentVoucher;

    private LocalDateTime repaymentDate;
}
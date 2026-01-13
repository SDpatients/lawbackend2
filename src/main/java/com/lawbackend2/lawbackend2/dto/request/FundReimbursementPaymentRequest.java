package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FundReimbursementPaymentRequest {

    @NotBlank(message = "支付状态不能为空")
    private String paymentStatus;

    @NotNull(message = "报销金额不能为空")
    private BigDecimal reimbursedAmount;

    private String paymentMethod;

    private Long paymentAccountId;

    private String paymentVoucher;

    private LocalDateTime paymentDate;
}
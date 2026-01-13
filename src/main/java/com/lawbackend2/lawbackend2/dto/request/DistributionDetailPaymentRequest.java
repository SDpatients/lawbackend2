package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DistributionDetailPaymentRequest {

    @NotBlank(message = "支付状态不能为空")
    private String paymentStatus;

    private String paymentMethod;

    private Long paymentAccountId;

    private String paymentVoucher;

    private LocalDateTime paymentDate;
}
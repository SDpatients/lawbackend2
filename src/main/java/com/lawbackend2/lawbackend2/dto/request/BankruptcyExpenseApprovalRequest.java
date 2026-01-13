package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BankruptcyExpenseApprovalRequest {

    @NotBlank(message = "审批状态不能为空")
    private String approvalStatus;

    private String approvalOpinion;

    private BigDecimal approvedAmount;

    private LocalDateTime approvalDate;
}
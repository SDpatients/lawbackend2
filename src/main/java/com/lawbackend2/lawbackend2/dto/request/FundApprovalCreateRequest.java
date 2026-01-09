package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class FundApprovalCreateRequest {

    @NotNull(message = "资金流水ID不能为空")
    private Long flowId;

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotNull(message = "金额不能为空")
    @Positive(message = "金额必须大于0")
    private BigDecimal amount;

    @NotBlank(message = "审批内容不能为空")
    private String approvalContent;
}

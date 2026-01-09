package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class FundApprovalUpdateRequest {

    @NotNull(message = "流程ID不能为空")
    private Long flowId;

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    private String approvalContent;
}

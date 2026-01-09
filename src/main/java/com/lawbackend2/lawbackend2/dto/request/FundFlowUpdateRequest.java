package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FundFlowUpdateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "案件名称不能为空")
    private String caseName;

    @NotNull(message = "资金账户ID不能为空")
    private Long fundAccountId;

    @NotBlank(message = "流水类型不能为空")
    private String flowType;

    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    @NotNull(message = "变动前余额不能为空")
    private BigDecimal balanceBefore;

    @NotNull(message = "变动后余额不能为空")
    private BigDecimal balanceAfter;

    @NotNull(message = "交易日期不能为空")
    private LocalDateTime transactionDate;

    private String description;

    private String relatedDocument;

    @NotNull(message = "操作人ID不能为空")
    private Long operatorId;

    @NotNull(message = "操作时间不能为空")
    private LocalDateTime operationTime;

    private String remark;
}

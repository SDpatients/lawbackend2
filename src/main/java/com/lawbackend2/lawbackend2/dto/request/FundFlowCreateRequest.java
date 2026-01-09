package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FundFlowCreateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "案件名称不能为空")
    private String caseName;

    @NotNull(message = "资金账户ID不能为空")
    private Long fundAccountId;

    @NotBlank(message = "流水类型不能为空")
    @Pattern(regexp = "^(INCOME|EXPENSE)$", message = "流水类型不正确")
    private String flowType;

    @NotNull(message = "金额不能为空")
    @Positive(message = "金额必须大于0")
    private BigDecimal amount;

    @NotNull(message = "操作前余额不能为空")
    private BigDecimal balanceBefore;

    @NotNull(message = "操作后余额不能为空")
    private BigDecimal balanceAfter;

    @NotNull(message = "交易日期不能为空")
    private LocalDateTime transactionDate;

    @NotBlank(message = "交易描述不能为空")
    private String description;

    private String relatedDocument;

    private String remark;
}

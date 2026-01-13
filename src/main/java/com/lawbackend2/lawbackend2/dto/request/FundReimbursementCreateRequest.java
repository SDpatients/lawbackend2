package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FundReimbursementCreateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "案件名称不能为空")
    private String caseName;

    @NotBlank(message = "报销类型不能为空")
    private String reimbursementType;

    @NotBlank(message = "报销名称不能为空")
    private String reimbursementName;

    private String reimbursementDescription;

    @NotNull(message = "申请人ID不能为空")
    private Long applicantId;

    @NotBlank(message = "申请人姓名不能为空")
    private String applicantName;

    private String department;

    @NotNull(message = "申请金额不能为空")
    private BigDecimal appliedAmount;

    private LocalDateTime expenseDate;

    private String expenseLocation;

    private String expensePurpose;

    private Long budgetId;

    private String budgetItem;

    private String attachments;

    private LocalDateTime applyDate;

    private String remarks;
}
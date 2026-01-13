package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FundBudgetCreateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "案件名称不能为空")
    private String caseName;

    @NotBlank(message = "预算类型不能为空")
    private String budgetType;

    @NotBlank(message = "预算名称不能为空")
    private String budgetName;

    private String budgetDescription;

    private LocalDateTime budgetStartDate;

    private LocalDateTime budgetEndDate;

    @NotNull(message = "预算总额不能为空")
    private BigDecimal totalBudgetAmount;

    private BigDecimal usedAmount;

    private BigDecimal remainingAmount;

    private BigDecimal litigationBudget;

    private BigDecimal arbitrationBudget;

    private BigDecimal managerFeeBudget;

    private BigDecimal executionBudget;

    private BigDecimal evaluationBudget;

    private BigDecimal auctionBudget;

    private BigDecimal announcementBudget;

    private BigDecimal storageBudget;

    private BigDecimal insuranceBudget;

    private BigDecimal meetingBudget;

    private BigDecimal otherBudget;

    private Long relatedBudgetId;

    private String attachments;

    private String remarks;
}
package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DistributionExecutionCreateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "案件名称不能为空")
    private String caseName;

    private Long distributionPlanId;

    private String distributionPlanName;

    @NotBlank(message = "分配批次不能为空")
    private String distributionBatch;

    private LocalDateTime distributionDate;

    private String distributionMethod;

    @NotNull(message = "可分配财产总额不能为空")
    private BigDecimal totalDistributableAmount;

    @NotNull(message = "本次分配总额不能为空")
    private BigDecimal totalDistributionAmount;

    private BigDecimal accumulatedDistributionAmount;

    private BigDecimal expenseAmount;

    private BigDecimal expenseRatio;

    private Integer expenseCount;

    private BigDecimal commonDebtAmount;

    private BigDecimal commonDebtRatio;

    private Integer commonDebtCount;

    private BigDecimal employeeClaimAmount;

    private BigDecimal employeeClaimRatio;

    private Integer employeeClaimCount;

    private BigDecimal taxClaimAmount;

    private BigDecimal taxClaimRatio;

    private Integer taxClaimCount;

    private BigDecimal securedClaimAmount;

    private BigDecimal securedClaimRatio;

    private Integer securedClaimCount;

    private BigDecimal unsecuredClaimAmount;

    private BigDecimal unsecuredClaimRatio;

    private Integer unsecuredClaimCount;

    private String attachments;

    private String remarks;
}
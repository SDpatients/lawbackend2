package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EscrowManagementCreateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "案件名称不能为空")
    private String caseName;

    @NotBlank(message = "提存类型不能为空")
    private String escrowType;

    @NotBlank(message = "提存名称不能为空")
    private String escrowName;

    private String escrowDescription;

    private String creditorName;

    private Long creditorClaimId;

    @NotNull(message = "提存金额不能为空")
    private BigDecimal escrowAmount;

    private BigDecimal releasedAmount;

    private BigDecimal unreleasedAmount;

    private String escrowReason;

    private String escrowInstitution;

    private String escrowAccount;

    private LocalDateTime escrowDate;

    private String releaseCondition;

    private Boolean isConditionMet;

    private LocalDateTime conditionMetDate;

    private Long releaseAccountId;

    private String releaseVoucher;

    private Long relatedDistributionId;

    private Long relatedFlowId;

    private String attachments;

    private String remarks;
}
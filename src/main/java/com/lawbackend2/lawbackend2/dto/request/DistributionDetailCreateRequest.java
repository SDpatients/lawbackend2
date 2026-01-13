package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DistributionDetailCreateRequest {

    @NotNull(message = "分配执行ID不能为空")
    private Long distributionExecutionId;

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "案件名称不能为空")
    private String caseName;

    private Long creditorClaimId;

    @NotBlank(message = "债权人名称不能为空")
    private String creditorName;

    private String creditorType;

    @NotNull(message = "债权金额不能为空")
    private BigDecimal claimAmount;

    @NotNull(message = "确认债权金额不能为空")
    private BigDecimal confirmedAmount;

    @NotNull(message = "本次分配金额不能为空")
    private BigDecimal currentDistributionAmount;

    private BigDecimal accumulatedDistributionAmount;

    private BigDecimal distributionRatio;

    private String paymentMethod;

    private Long paymentAccountId;

    private String payeeAccountName;

    private String payeeAccountNumber;

    private String payeeBankName;

    private Boolean isEscrowed;

    private Long escrowId;

    private String escrowReason;

    private String remarks;
}
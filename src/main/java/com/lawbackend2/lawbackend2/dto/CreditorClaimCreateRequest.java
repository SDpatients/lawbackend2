package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreditorClaimCreateRequest {
    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    private String caseName;

    private String debtor;

    private String bankAccount;

    @NotBlank(message = "债权人名称不能为空")
    private String creditorName;

    @NotBlank(message = "债权人类型不能为空")
    private String creditorType;

    private String creditCode;

    private String legalRepresentative;

    private String serviceAddress;

    private String agentName;

    private String agentPhone;

    private String agentIdCard;

    private String agentAddress;

    private String accountName;

    private String creditorBankAccount;

    private String bankName;

    private BigDecimal principal;

    private BigDecimal interest;

    private BigDecimal penalty;

    private BigDecimal otherLosses;

    @NotNull(message = "总金额不能为空")
    private BigDecimal totalAmount;

    private Integer hasCourtJudgment = 0;

    private Integer hasExecution = 0;

    private Integer hasCollateral = 0;

    private String claimNature;

    @NotBlank(message = "债权类型不能为空")
    private String claimType;

    private String claimFacts;

    private String claimNatureManager;

    private String claimIdentifier;

    private String evidenceList;

    private String evidenceMaterials;

    private String evidenceAttachments;

    private String remarks;
}

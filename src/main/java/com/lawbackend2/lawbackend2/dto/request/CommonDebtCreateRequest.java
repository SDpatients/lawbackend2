package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CommonDebtCreateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "案件名称不能为空")
    private String caseName;

    @NotBlank(message = "债务类型不能为空")
    private String debtType;

    @NotBlank(message = "债务名称不能为空")
    private String debtName;

    private String debtDescription;

    @NotBlank(message = "债权人名称不能为空")
    private String creditorName;

    private String creditorType;

    private String creditorContact;

    @NotNull(message = "债务金额不能为空")
    private BigDecimal debtAmount;

    private BigDecimal repaidAmount;

    private BigDecimal unrepaidAmount;

    private String debtBasis;

    private String basisDocument;

    private String basisDescription;

    private LocalDateTime debtStartDate;

    private LocalDateTime debtDueDate;

    private Boolean isOverdue;

    private String repaymentMethod;

    private Long repaymentAccountId;

    private String repaymentVoucher;

    private String relatedBusinessType;

    private Long relatedBusinessId;

    private String attachments;

    private LocalDateTime debtDate;

    private LocalDateTime applyDate;

    private String remarks;
}
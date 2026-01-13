package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CommonDebtUpdateRequest {

    private String debtName;

    private String debtDescription;

    private String creditorName;

    private String creditorType;

    private String creditorContact;

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
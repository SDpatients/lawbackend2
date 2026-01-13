package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BankruptcyExpenseUpdateRequest {

    private String expenseCategory;

    private String expenseName;

    private String expenseDescription;

    private BigDecimal appliedAmount;

    private BigDecimal approvedAmount;

    private BigDecimal paidAmount;

    private BigDecimal unpaidAmount;

    private String expenseBasis;

    private String basisDocument;

    private String basisDescription;

    private String expensePurpose;

    private String paymentMethod;

    private Long paymentAccountId;

    private String payeeName;

    private String payeeAccount;

    private String payeeBank;

    private String paymentVoucher;

    private String relatedBusinessType;

    private Long relatedBusinessId;

    private String attachments;

    private LocalDateTime expenseDate;

    private LocalDateTime applyDate;

    private String remarks;
}
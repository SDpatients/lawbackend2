package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExpenseReimbursementResponse {

    private Long id;
    private String reimbursementNumber;
    private Long caseId;
    private String caseName;
    private Long applicantId;
    private String applicantName;
    private Long fundAccountId;
    private String fundAccountName;
    private String bankName;
    private String bankAccount;
    private BigDecimal totalAmount;
    private LocalDate reimbursementDate;
    private String description;
    private String approvalStatus;
    private Long approverId;
    private String approverName;
    private LocalDateTime approvalTime;
    private String approvalOpinion;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<ExpenseReimbursementItemResponse> items;
    private List<ExpenseReimbursementAttachmentResponse> attachments;
}

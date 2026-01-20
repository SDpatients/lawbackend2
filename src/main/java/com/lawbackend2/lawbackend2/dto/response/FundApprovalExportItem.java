package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FundApprovalExportItem {
    private Long id;
    private String approvalNumber;
    private String approvalType;
    private BigDecimal amount;
    private String approvalStatus;
    private String approverName;
    private LocalDateTime approvalTime;
    private String approvalOpinion;
    private String rejectionReason;
    private String status;
}

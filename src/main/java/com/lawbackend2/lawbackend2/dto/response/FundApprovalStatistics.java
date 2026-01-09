package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.util.Map;

@Data
public class FundApprovalStatistics {

    private Long totalApprovals;
    private Long pendingApprovals;
    private Long approvedApprovals;
    private Long rejectedApprovals;
    private Double totalApprovedAmount;
    private Double totalRejectedAmount;
    private Map<String, Long> byApprovalType;
}

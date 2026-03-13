package com.lawbackend2.lawbackend2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundApprovalStatistics {

    private Long totalApprovals;
    private Long pendingApprovals;
    private Long approvedApprovals;
    private Long rejectedApprovals;
    private Double totalApprovedAmount;
    private Double totalRejectedAmount;
    private Map<String, Long> byApprovalType;
}

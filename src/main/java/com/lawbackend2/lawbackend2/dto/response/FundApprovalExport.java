package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class FundApprovalExport {
    private List<FundApprovalExportItem> approvals;
    private String fileName;
    private Long totalCount;
}

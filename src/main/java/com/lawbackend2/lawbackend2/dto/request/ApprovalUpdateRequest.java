package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

@Data
public class ApprovalUpdateRequest {
    private String approvalTitle;
    private String approvalContent;
    private String approvalAttachment;
    private String remark;
}
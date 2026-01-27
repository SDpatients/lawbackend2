package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApprovalHistoryResponse {
    private Long id;
    private Long approvalId;
    private Long caseId;
    private Long approverId;
    private String approvalType;
    private String approvalTitle;
    private String approvalAttachment;
    private String approvalStatus;
    private String approvalOpinion;
    private LocalDateTime approvalDate;
    private String status;
    private Boolean isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUserId;
    private Long updateUserId;

    // 从ApprovalHistory实体转换
    public static ApprovalHistoryResponse fromEntity(com.lawbackend2.lawbackend2.entity.ApprovalHistory history) {
        ApprovalHistoryResponse response = new ApprovalHistoryResponse();
        response.setId(history.getId());
        response.setApprovalId(history.getApprovalId());
        response.setCaseId(history.getCaseId());
        response.setApproverId(history.getApproverId());
        response.setApprovalType(history.getApprovalType());
        response.setApprovalTitle(history.getApprovalTitle());
        response.setApprovalAttachment(history.getApprovalAttachment());
        response.setApprovalStatus(history.getApprovalStatus());
        response.setApprovalOpinion(history.getApprovalOpinion());
        response.setApprovalDate(history.getApprovalDate());
        response.setStatus(history.getStatus());
        response.setIsDeleted(history.getIsDeleted());
        response.setCreateTime(history.getCreateTime());
        response.setUpdateTime(history.getUpdateTime());
        response.setCreateUserId(history.getCreateUserId());
        response.setUpdateUserId(history.getUpdateUserId());
        return response;
    }
}
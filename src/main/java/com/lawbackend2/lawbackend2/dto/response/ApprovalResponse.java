package com.lawbackend2.lawbackend2.dto.response;

import com.lawbackend2.lawbackend2.entity.Approval;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApprovalResponse {
    private Long id;
    private Long caseId;
    private String caseNumber;  // 案件编号
    private Long lawyerId;
    private String approvalType;
    private String approvalStatus;
    private String approvalTitle;
    private String approvalContent;
    private String approvalAttachment;
    private String approvalResult;
    private Integer approvalCount;
    private Long approverId;
    private LocalDateTime approvalDate;
    private String remark;
    private String status;
    private Boolean isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUserId;
    private Long updateUserId;
    private String realName;

    // 从Approval实体转换，不包含案件编号
    public static ApprovalResponse fromEntity(Approval approval) {
        ApprovalResponse response = new ApprovalResponse();
        response.setId(approval.getId());
        response.setCaseId(approval.getCaseId());
        response.setLawyerId(approval.getLawyerId());
        response.setApprovalType(approval.getApprovalType());
        response.setApprovalStatus(approval.getApprovalStatus());
        response.setApprovalTitle(approval.getApprovalTitle());
        response.setApprovalContent(approval.getApprovalContent());
        response.setApprovalAttachment(approval.getApprovalAttachment());
        response.setApprovalResult(approval.getApprovalResult());
        response.setApprovalCount(approval.getApprovalCount());
        response.setApproverId(approval.getApproverId());
        response.setApprovalDate(approval.getApprovalDate());
        response.setRemark(approval.getRemark());
        response.setStatus(approval.getStatus());
        response.setIsDeleted(approval.getIsDeleted());
        response.setCreateTime(approval.getCreateTime());
        response.setUpdateTime(approval.getUpdateTime());
        response.setCreateUserId(approval.getCreateUserId());
        response.setUpdateUserId(approval.getUpdateUserId());
        return response;
    }

    // 从Approval实体转换，包含案件编号
    public static ApprovalResponse fromEntity(Approval approval, String caseNumber) {
        ApprovalResponse response = fromEntity(approval);
        response.setCaseNumber(caseNumber);
        return response;
    }

    // 从Approval实体转换，包含案件编号和创建人姓名
    public static ApprovalResponse fromEntity(Approval approval, String caseNumber, String realName) {
        ApprovalResponse response = fromEntity(approval, caseNumber);
        response.setRealName(realName);
        return response;
    }
}
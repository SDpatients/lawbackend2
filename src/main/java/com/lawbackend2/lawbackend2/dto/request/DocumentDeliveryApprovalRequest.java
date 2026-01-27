package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DocumentDeliveryApprovalRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "文书名称不能为空")
    private String documentName;

    private String abbreviation;

    private String documentType;

    @NotBlank(message = "受送达人不能为空")
    private String recipientName;

    private String recipientType;

    private String contactPhone;

    private String deliveryAddress;

    private String deliveryMethod;

    private String deliveryContent;

    @NotBlank(message = "审批标题不能为空")
    private String approvalTitle;

    private String approvalContent;

    private String remark;
}

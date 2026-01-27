package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DocumentDeliveryUpdateRequest {

    private Long caseId;

    private String caseNumber;

    private String caseName;

    private String documentName;

    private String abbreviation;

    private String documentType;

    private String recipientName;

    private String recipientType;

    private String contactPhone;

    private String deliveryAddress;

    private String deliveryMethod;

    private String sendStatus;

    private String deliveryContent;

    private String documentAttachment;

    private String failureReason;

    private String remark;

    @NotNull(message = "ID不能为空")
    private Long id;
}

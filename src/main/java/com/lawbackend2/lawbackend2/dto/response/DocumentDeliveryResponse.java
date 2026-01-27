package com.lawbackend2.lawbackend2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDeliveryResponse {

    private Long id;

    private Long caseId;

    private String caseNumber;

    private String caseName;

    private String documentName;

    private String documentNumber;

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

    private LocalDateTime sendTime;

    private LocalDateTime deliveryTime;

    private String failureReason;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUserId;

    private Long updateUserId;

    private String status;
}

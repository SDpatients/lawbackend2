package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_document_delivery", indexes = {
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_case_number", columnList = "case_number"),
    @Index(name = "idx_send_status", columnList = "send_status"),
    @Index(name = "idx_document_type", columnList = "document_type"),
    @Index(name = "idx_recipient_type", columnList = "recipient_type"),
    @Index(name = "idx_delivery_method", columnList = "delivery_method"),
    @Index(name = "idx_create_time", columnList = "create_time"),
    @Index(name = "idx_status", columnList = "status")
})
public class DocumentDelivery extends BaseEntity {

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_number", length = 100)
    private String caseNumber;

    @Column(name = "case_name", length = 200)
    private String caseName;

    @Column(name = "document_name", length = 200, nullable = false)
    private String documentName;

    @Column(name = "document_number", length = 100)
    private String documentNumber;

    @Column(name = "abbreviation", length = 50)
    private String abbreviation;

    @Column(name = "document_type", length = 50)
    private String documentType;

    @Column(name = "recipient_name", length = 100, nullable = false)
    private String recipientName;

    @Column(name = "recipient_type", length = 50)
    private String recipientType;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "delivery_address", length = 500)
    private String deliveryAddress;

    @Column(name = "delivery_method", length = 50)
    private String deliveryMethod;

    @Column(name = "send_status", length = 20)
    private String sendStatus = "PENDING";

    @Column(name = "delivery_content", columnDefinition = "TEXT")
    private String deliveryContent;

    @Column(name = "document_attachment", length = 500)
    private String documentAttachment;

    @Column(name = "send_time")
    private LocalDateTime sendTime;

    @Column(name = "delivery_time")
    private LocalDateTime deliveryTime;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "remark", length = 500)
    private String remark;

    @Column(name = "create_user_id")
    private Long createUserId;

    @Column(name = "update_user_id")
    private Long updateUserId;
    
    @Column(name = "approval_id")
    private Long approvalId;
}

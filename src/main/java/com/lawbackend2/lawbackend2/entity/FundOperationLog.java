package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_fund_operation_log", indexes = {
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_operation_type", columnList = "operation_type"),
    @Index(name = "idx_operator_id", columnList = "operator_id"),
    @Index(name = "idx_operation_time", columnList = "operation_time"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class FundOperationLog extends BaseEntity {

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "operation_type", length = 50)
    private String operationType;

    @Column(name = "operation_content", length = 1000)
    private String operationContent;

    @Column(name = "operation_result", length = 20)
    private String operationResult;

    @Column(name = "data_before", columnDefinition = "TEXT")
    private String dataBefore;

    @Column(name = "data_after", columnDefinition = "TEXT")
    private String dataAfter;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "related_business_type", length = 50)
    private String relatedBusinessType;

    @Column(name = "related_business_id")
    private Long relatedBusinessId;

    @Column(name = "is_audited")
    private Boolean isAudited = false;

    @Column(name = "audit_date")
    private LocalDateTime auditDate;

    @Column(name = "audit_user_id")
    private Long auditUserId;

    @Column(name = "operator_id")
    private Long operatorId;

    @Column(name = "operation_time")
    private LocalDateTime operationTime;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "browser_info", length = 200)
    private String browserInfo;
}

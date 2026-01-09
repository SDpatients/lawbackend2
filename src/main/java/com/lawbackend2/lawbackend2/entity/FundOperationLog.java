package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 资金操作日志实体类
 */
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

    /** 案件ID */
    @Column(name = "case_id")
    private Long caseId;

    /** 操作类型 */
    @Column(name = "operation_type", length = 50)
    private String operationType;

    /** 操作内容 */
    @Column(name = "operation_content", columnDefinition = "TEXT")
    private String operationContent;

    /** 操作人ID */
    @Column(name = "operator_id")
    private Long operatorId;

    /** 操作时间 */
    @Column(name = "operation_time")
    private LocalDateTime operationTime;

    /** IP地址 */
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    /** 浏览器信息 */
    @Column(name = "browser_info", length = 500)
    private String browserInfo;
}

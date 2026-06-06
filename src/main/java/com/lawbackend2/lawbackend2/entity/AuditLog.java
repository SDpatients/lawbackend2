package com.lawbackend2.lawbackend2.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_audit_log", indexes = {
    @Index(name = "idx_audit_user_id", columnList = "user_id"),
    @Index(name = "idx_audit_module", columnList = "module"),
    @Index(name = "idx_audit_operation", columnList = "operation_type"),
    @Index(name = "idx_audit_business", columnList = "business_type,business_id"),
    @Index(name = "idx_audit_status", columnList = "status"),
    @Index(name = "idx_audit_create_time", columnList = "create_time")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_account", length = 100)
    private String userAccount;

    @Column(name = "user_name", length = 100)
    private String userName;

    @Column(name = "module", length = 100)
    private String module;

    @Column(name = "module_name", length = 200)
    private String moduleName;

    @Column(name = "operation_type", length = 50)
    private String operationType;

    @Column(name = "operation_name", length = 200)
    private String operationName;

    @Column(name = "business_type", length = 100)
    private String businessType;

    @Column(name = "business_id")
    private Long businessId;

    @Column(name = "business_name", length = 500)
    private String businessName;

    @Column(name = "request_method", length = 10)
    private String requestMethod;

    @Column(name = "request_url", length = 500)
    private String requestUrl;

    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams;

    @Column(name = "data_before", columnDefinition = "LONGTEXT")
    private String dataBefore;

    @Column(name = "data_after", columnDefinition = "LONGTEXT")
    private String dataAfter;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "SUCCESS";

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "browser", length = 200)
    private String browser;

    @Column(name = "os", length = 100)
    private String os;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "create_time", updatable = false)
    @Builder.Default
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(name = "hash_value", length = 128)
    private String hashValue;

    @Column(name = "previous_hash", length = 128)
    private String previousHash;

    @Column(name = "digital_signature", length = 256)
    private String digitalSignature;

    @Column(name = "integrity_status", length = 20)
    @Builder.Default
    private String integrityStatus = INTEGRITY_VERIFIED;

    @Column(name = "signed_by", length = 100)
    private String signedBy;

    @Column(name = "signed_time")
    private LocalDateTime signedTime;

    @Column(name = "chain_sequence")
    private Long chainSequence;

    public static final String TYPE_CREATE = "CREATE";
    public static final String TYPE_UPDATE = "UPDATE";
    public static final String TYPE_DELETE = "DELETE";
    public static final String TYPE_QUERY = "QUERY";
    public static final String TYPE_EXPORT = "EXPORT";
    public static final String TYPE_IMPORT = "IMPORT";
    public static final String TYPE_LOGIN = "LOGIN";
    public static final String TYPE_LOGOUT = "LOGOUT";
    public static final String TYPE_APPROVE = "APPROVE";
    public static final String TYPE_REJECT = "REJECT";

    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAIL = "FAIL";

    public static final String INTEGRITY_VERIFIED = "VERIFIED";
    public static final String INTEGRITY_TAMPERED = "TAMPERED";
    public static final String INTEGRITY_PENDING = "PENDING";
}

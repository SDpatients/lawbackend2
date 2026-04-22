package com.lawbackend2.lawbackend2.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_backup_record", indexes = {
    @Index(name = "idx_backup_status", columnList = "status"),
    @Index(name = "idx_backup_type", columnList = "backup_type"),
    @Index(name = "idx_backup_start_time", columnList = "start_time"),
    @Index(name = "idx_backup_create_time", columnList = "create_time")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BackupRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", length = 255, nullable = false)
    private String fileName;

    @Column(name = "file_path", length = 500, nullable = false)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "backup_type", length = 20)
    @Builder.Default
    private String backupType = "FULL";

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "database_name", length = 100)
    private String databaseName;

    @Column(name = "table_count")
    private Integer tableCount;

    @Column(name = "record_count")
    private Long recordCount;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "create_time", updatable = false)
    @Builder.Default
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(name = "update_time")
    @Builder.Default
    private LocalDateTime updateTime = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    public static final String TYPE_FULL = "FULL";
    public static final String TYPE_MANUAL = "MANUAL";

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_RUNNING = "RUNNING";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";
}

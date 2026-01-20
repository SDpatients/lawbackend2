package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_archive_record", indexes = {
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_category_code", columnList = "category_code"),
    @Index(name = "idx_file_id", columnList = "file_id"),
    @Index(name = "idx_archive_no", columnList = "archive_no"),
    @Index(name = "idx_upload_user_id", columnList = "upload_user_id"),
    @Index(name = "idx_upload_time", columnList = "upload_time"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_access_level", columnList = "access_level"),
    @Index(name = "idx_version", columnList = "version")
})
public class ArchiveRecord extends BaseEntity {

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "category_code", length = 100, nullable = false)
    private String categoryCode;

    @Column(name = "file_id", nullable = false)
    private Long fileId;

    @Column(name = "archive_no", length = 50)
    private String archiveNo;

    @Column(name = "file_title", length = 500)
    private String fileTitle;

    @Column(name = "file_description", columnDefinition = "TEXT")
    private String fileDescription;

    @Column(name = "upload_user_id", nullable = false)
    private Long uploadUserId;

    @Column(name = "upload_time", nullable = false)
    private LocalDateTime uploadTime;

    @Column(name = "is_confidential", nullable = false)
    private Boolean isConfidential = false;

    @Column(name = "access_level", length = 20)
    private String accessLevel = "INTERNAL";

    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @Column(name = "parent_version_id")
    private Long parentVersionId;

    @Transient
    private FileRecord file;
}

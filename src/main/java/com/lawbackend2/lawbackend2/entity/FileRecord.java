package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_file_record", indexes = {
    @Index(name = "idx_biz_type", columnList = "biz_type"),
    @Index(name = "idx_biz_id", columnList = "biz_id"),
    @Index(name = "idx_biz_type_biz_id", columnList = "biz_type,biz_id"),
    @Index(name = "idx_file_status", columnList = "file_status"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_upload_time", columnList = "upload_time"),
    @Index(name = "idx_upload_user_id", columnList = "upload_user_id"),
    @Index(name = "idx_create_time", columnList = "create_time")
})
public class FileRecord extends BaseEntity {

    @Column(name = "original_file_name", length = 255, nullable = false)
    private String originalFileName;

    @Column(name = "stored_file_name", length = 255, nullable = false)
    private String storedFileName;

    @Column(name = "file_path", length = 500, nullable = false)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_extension", length = 50)
    private String fileExtension;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "file_hash", length = 64)
    private String fileHash;

    @Column(name = "biz_type", length = 50)
    private String bizType;

    @Column(name = "biz_id")
    private String bizId;

    @Column(name = "upload_time")
    private LocalDateTime uploadTime;

    @Column(name = "upload_user_id")
    private Long uploadUserId;

    @Column(name = "file_status")
    private Integer fileStatus = 1;

    @Column(name = "delete_time")
    private LocalDateTime deleteTime;

    @Column(name = "delete_user_id")
    private Long deleteUserId;
}

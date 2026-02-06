package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_document_export_history")
public class DocumentExportHistory extends BaseEntity {

    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(name = "export_type", length = 20, nullable = false)
    private String exportType;

    @Column(name = "file_name", length = 255, nullable = false)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "export_params", columnDefinition = "JSON")
    private String exportParams;

    @Column(name = "export_status", length = 20)
    private String exportStatus = "SUCCESS";

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "exported_by", nullable = false)
    private Long exportedBy;

    @Column(name = "exported_time")
    private LocalDateTime exportedTime;

    @Column(name = "processing_time")
    private Integer processingTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private DocumentExportTemplate template;
}

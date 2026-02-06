package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_document_export_template")
public class DocumentExportTemplate extends BaseEntity {

    @Column(name = "template_name", length = 100, nullable = false)
    private String templateName;

    @Column(name = "template_code", length = 50, unique = true, nullable = false)
    private String templateCode;

    @Column(name = "template_type", length = 20, nullable = false)
    private String templateType;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "config_json", columnDefinition = "JSON")
    private String configJson;

    @Column(name = "is_default")
    private Boolean isDefault = false;
}

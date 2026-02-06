package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_document_template_field")
public class DocumentTemplateField extends BaseEntity {

    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(name = "field_name", length = 50, nullable = false)
    private String fieldName;

    @Column(name = "field_label", length = 100, nullable = false)
    private String fieldLabel;

    @Column(name = "field_type", length = 20, nullable = false)
    private String fieldType;

    @Column(name = "source_field", length = 100)
    private String sourceField;

    @Column(name = "default_value", length = 255)
    private String defaultValue;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "is_required")
    private Boolean isRequired = false;

    @Column(name = "format_pattern", length = 50)
    private String formatPattern;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", insertable = false, updatable = false)
    private DocumentExportTemplate template;
}

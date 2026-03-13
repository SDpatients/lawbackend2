package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 文档模板字段配置实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_document_template_field")
public class DocumentTemplateField extends BaseEntity {

    /**
     * 模板 ID
     */
    @Column(name = "template_id", nullable = false)
    private Long templateId;

    /**
     * 字段标签（显示名称）
     */
    @Column(name = "field_label", length = 200, nullable = false)
    private String fieldLabel;

    /**
     * 字段名称（占位符名称）
     */
    @Column(name = "field_name", length = 100, nullable = false)
    private String fieldName;

    /**
     * 字段类型：TEXT, NUMBER, DATE, TABLE, IMAGE
     */
    @Column(name = "field_type", length = 20)
    private String fieldType = "TEXT";

    /**
     * 是否必填
     */
    @Column(name = "is_required")
    private Boolean isRequired = false;

    /**
     * 排序顺序
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /**
     * 默认值
     */
    @Column(name = "default_value", length = 500)
    private String defaultValue;

    /**
     * 字段描述
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 验证规则（JSON 格式）
     */
    @Column(name = "validation_rule", columnDefinition = "TEXT")
    private String validationRule;

    /**
     * 源字段名
     */
    @Column(name = "source_field", length = 100)
    private String sourceField;

    /**
     * 格式化模式
     */
    @Column(name = "format_pattern", length = 50)
    private String formatPattern;
}

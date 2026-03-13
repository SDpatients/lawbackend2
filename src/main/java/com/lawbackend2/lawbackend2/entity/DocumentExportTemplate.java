package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * Word 文档导出模板实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_document_export_template")
public class DocumentExportTemplate extends BaseEntity {

    /**
     * 模板名称
     */
    @Column(name = "template_name", length = 200, nullable = false)
    private String templateName;

    /**
     * 模板编码（唯一标识）
     */
    @Column(name = "template_code", length = 100, unique = true)
    private String templateCode;

    /**
     * 模板类型：WORD, EXCEL
     */
    @Column(name = "template_type", length = 20, nullable = false)
    private String templateType = "WORD";

    /**
     * 模板文件路径
     */
    @Column(name = "file_path", length = 500)
    private String filePath;

    /**
     * 模板描述
     */
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * 是否为默认模板
     */
    @Column(name = "is_default")
    private Boolean isDefault = false;

    /**
     * 模板分类
     */
    @Column(name = "category", length = 50)
    private String category;

    /**
     * 模板版本
     */
    @Column(name = "version", length = 20)
    private String version = "1.0";

    /**
     * 配置信息（JSON 格式）
     */
    @Column(name = "config_json", columnDefinition = "TEXT")
    private String configJson;
}

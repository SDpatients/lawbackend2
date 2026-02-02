package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_excel_import_template")
public class ExcelImportTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "template_name", length = 100, nullable = false)
    private String templateName;
    
    @Column(name = "template_code", length = 50, unique = true, nullable = false)
    private String templateCode;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "field_mappings", columnDefinition = "JSON")
    private String fieldMappings;
    
    @Column(name = "is_default")
    private Boolean isDefault;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}

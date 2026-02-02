package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_excel_import_history")
public class ExcelImportHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "template_id")
    private Long templateId;
    
    @Column(name = "file_name", length = 255, nullable = false)
    private String fileName;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "sheet_index")
    private Integer sheetIndex;
    
    @Column(name = "total_rows")
    private Integer totalRows;
    
    @Column(name = "success_rows")
    private Integer successRows;
    
    @Column(name = "fail_rows")
    private Integer failRows;
    
    @Column(name = "import_status", length = 20)
    private String importStatus;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "imported_by", nullable = false)
    private Long importedBy;
    
    @Column(name = "imported_time")
    private LocalDateTime importedTime;
    
    @Column(name = "processing_time")
    private Integer processingTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", insertable = false, updatable = false)
    private ExcelImportTemplate template;
}

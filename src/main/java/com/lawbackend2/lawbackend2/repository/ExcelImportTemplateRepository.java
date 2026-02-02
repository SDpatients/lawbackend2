package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.ExcelImportTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExcelImportTemplateRepository extends JpaRepository<ExcelImportTemplate, Long> {
    
    ExcelImportTemplate findByTemplateCode(String templateCode);
    
    List<ExcelImportTemplate> findByIsDefaultTrue();
    
    List<ExcelImportTemplate> findByIsActiveTrueOrderByIsDefaultDesc();
    
    List<ExcelImportTemplate> findByCreatedByOrderByCreatedTimeDesc(Long createdBy);
}

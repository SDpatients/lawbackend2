package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.ExcelImportHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ExcelImportHistoryService {
    
    ExcelImportHistoryResponse createHistory(Long templateId, String fileName, Long fileSize, 
                                             Integer sheetIndex, Long importedBy);
    
    ExcelImportHistoryResponse updateHistory(Long id, Integer totalRows, Integer successRows, 
                                             Integer failRows, String importStatus, 
                                             String errorMessage, Integer processingTime);
    
    ExcelImportHistoryResponse getHistoryById(Long id);
    
    List<ExcelImportHistoryResponse> getHistoriesByTemplateId(Long templateId);
    
    List<ExcelImportHistoryResponse> getHistoriesByImportedBy(Long importedBy);
    
    List<ExcelImportHistoryResponse> getHistoriesByImportStatus(String importStatus);
    
    List<ExcelImportHistoryResponse> getHistoriesByImportedByAndTimeRange(Long importedBy, 
                                                                            LocalDateTime startTime, 
                                                                            LocalDateTime endTime);
    
    Page<ExcelImportHistoryResponse> getAllHistories(Pageable pageable);
    
    void deleteHistory(Long id);
}

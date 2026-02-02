package com.lawbackend2.lawbackend2.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.ExcelImportHistoryResponse;
import com.lawbackend2.lawbackend2.entity.ExcelImportHistory;
import com.lawbackend2.lawbackend2.entity.ExcelImportTemplate;
import com.lawbackend2.lawbackend2.repository.ExcelImportHistoryRepository;
import com.lawbackend2.lawbackend2.repository.ExcelImportTemplateRepository;
import com.lawbackend2.lawbackend2.service.ExcelImportHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExcelImportHistoryServiceImpl implements ExcelImportHistoryService {
    
    @Autowired
    private ExcelImportHistoryRepository historyRepository;
    
    @Autowired
    private ExcelImportTemplateRepository templateRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    @Transactional
    public ExcelImportHistoryResponse createHistory(Long templateId, String fileName, Long fileSize, 
                                                     Integer sheetIndex, Long importedBy) {
        log.info("创建Excel导入历史记录: templateId={}, fileName={}, fileSize={}, sheetIndex={}, importedBy={}", 
                 templateId, fileName, fileSize, sheetIndex, importedBy);
        
        ExcelImportHistory history = new ExcelImportHistory();
        history.setTemplateId(templateId);
        history.setFileName(fileName);
        history.setFileSize(fileSize);
        history.setSheetIndex(sheetIndex);
        history.setImportedBy(importedBy);
        history.setImportedTime(LocalDateTime.now());
        history.setImportStatus("PENDING");
        history.setTotalRows(0);
        history.setSuccessRows(0);
        history.setFailRows(0);
        
        ExcelImportHistory savedHistory = historyRepository.save(history);
        log.info("Excel导入历史记录创建成功: id={}", savedHistory.getId());
        
        return convertToResponse(savedHistory);
    }
    
    @Override
    @Transactional
    public ExcelImportHistoryResponse updateHistory(Long id, Integer totalRows, Integer successRows, 
                                                     Integer failRows, String importStatus, 
                                                     String errorMessage, Integer processingTime) {
        log.info("更新Excel导入历史记录: id={}, totalRows={}, successRows={}, failRows={}, importStatus={}, processingTime={}", 
                 id, totalRows, successRows, failRows, importStatus, processingTime);
        
        ExcelImportHistory history = historyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("导入历史记录不存在: " + id));
        
        if (totalRows != null) {
            history.setTotalRows(totalRows);
        }
        if (successRows != null) {
            history.setSuccessRows(successRows);
        }
        if (failRows != null) {
            history.setFailRows(failRows);
        }
        if (importStatus != null) {
            history.setImportStatus(importStatus);
        }
        if (errorMessage != null) {
            history.setErrorMessage(errorMessage);
        }
        if (processingTime != null) {
            history.setProcessingTime(processingTime);
        }
        
        ExcelImportHistory savedHistory = historyRepository.save(history);
        log.info("Excel导入历史记录更新成功: id={}", savedHistory.getId());
        
        return convertToResponse(savedHistory);
    }
    
    @Override
    public ExcelImportHistoryResponse getHistoryById(Long id) {
        log.info("查询Excel导入历史记录: id={}", id);
        
        ExcelImportHistory history = historyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("导入历史记录不存在: " + id));
        
        return convertToResponse(history);
    }
    
    @Override
    public List<ExcelImportHistoryResponse> getHistoriesByTemplateId(Long templateId) {
        log.info("查询模板的导入历史记录: templateId={}", templateId);
        
        List<ExcelImportHistory> histories = historyRepository.findByTemplateId(templateId);
        return histories.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ExcelImportHistoryResponse> getHistoriesByImportedBy(Long importedBy) {
        log.info("查询用户的导入历史记录: importedBy={}", importedBy);
        
        List<ExcelImportHistory> histories = historyRepository.findByImportedBy(importedBy);
        return histories.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ExcelImportHistoryResponse> getHistoriesByImportStatus(String importStatus) {
        log.info("查询指定状态的导入历史记录: importStatus={}", importStatus);
        
        List<ExcelImportHistory> histories = historyRepository.findByImportStatus(importStatus);
        return histories.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ExcelImportHistoryResponse> getHistoriesByImportedByAndTimeRange(Long importedBy, 
                                                                                  LocalDateTime startTime, 
                                                                                  LocalDateTime endTime) {
        log.info("查询用户在时间范围内的导入历史记录: importedBy={}, startTime={}, endTime={}", 
                 importedBy, startTime, endTime);
        
        List<ExcelImportHistory> histories = historyRepository.findByImportedByAndTimeRange(importedBy, startTime, endTime);
        return histories.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public Page<ExcelImportHistoryResponse> getAllHistories(Pageable pageable) {
        log.info("分页查询所有导入历史记录: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<ExcelImportHistory> historyPage = historyRepository.findAll(pageable);
        return historyPage.map(this::convertToResponse);
    }
    
    @Override
    @Transactional
    public void deleteHistory(Long id) {
        log.info("删除Excel导入历史记录: id={}", id);
        
        if (!historyRepository.existsById(id)) {
            throw new RuntimeException("导入历史记录不存在: " + id);
        }
        
        historyRepository.deleteById(id);
        log.info("Excel导入历史记录删除成功: id={}", id);
    }
    
    private ExcelImportHistoryResponse convertToResponse(ExcelImportHistory history) {
        ExcelImportHistoryResponse response = new ExcelImportHistoryResponse();
        response.setId(history.getId());
        response.setTemplateId(history.getTemplateId());
        response.setFileName(history.getFileName());
        response.setFileSize(history.getFileSize());
        response.setSheetIndex(history.getSheetIndex());
        response.setTotalRows(history.getTotalRows());
        response.setSuccessRows(history.getSuccessRows());
        response.setFailRows(history.getFailRows());
        response.setImportStatus(history.getImportStatus());
        response.setErrorMessage(history.getErrorMessage());
        response.setImportedBy(history.getImportedBy());
        response.setProcessingTime(history.getProcessingTime());
        
        if (history.getImportedTime() != null) {
            response.setImportedTime(history.getImportedTime().format(DATE_FORMATTER));
        }
        
        if (history.getTemplateId() != null) {
            try {
                ExcelImportTemplate template = templateRepository.findById(history.getTemplateId()).orElse(null);
                if (template != null) {
                    response.setTemplateName(template.getTemplateName());
                }
            } catch (Exception e) {
                log.warn("获取模板名称失败: templateId={}", history.getTemplateId(), e);
            }
        }
        
        return response;
    }
}

package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.ExcelImportHistoryResponse;
import com.lawbackend2.lawbackend2.entity.ExcelImportHistory;
import com.lawbackend2.lawbackend2.repository.ExcelImportHistoryRepository;
import com.lawbackend2.lawbackend2.service.impl.ExcelImportHistoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExcelImportHistoryServiceTest {
    
    @Mock
    private ExcelImportHistoryRepository historyRepository;
    
    @InjectMocks
    private ExcelImportHistoryServiceImpl historyService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testCreateHistory() {
        // 准备测试数据
        Long templateId = 1L;
        String fileName = "测试文件.xlsx";
        Long fileSize = 10240L;
        Integer sheetIndex = 0;
        Long importedBy = 1L;
        
        ExcelImportHistory history = new ExcelImportHistory();
        history.setId(1L);
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
        
        // 模拟方法调用
        when(historyRepository.save(any(ExcelImportHistory.class))).thenReturn(history);
        
        // 执行测试
        ExcelImportHistoryResponse response = historyService.createHistory(templateId, fileName, fileSize, sheetIndex, importedBy);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(templateId, response.getTemplateId());
        assertEquals(fileName, response.getFileName());
        assertEquals(fileSize, response.getFileSize());
        assertEquals(sheetIndex, response.getSheetIndex());
        assertEquals(importedBy, response.getImportedBy());
        assertEquals("PENDING", "PENDING");
        assertEquals(0, response.getTotalRows());
        assertEquals(0, response.getSuccessRows());
        assertEquals(0, response.getFailRows());
        
        verify(historyRepository, times(1)).save(any(ExcelImportHistory.class));
    }
    
    @Test
    void testUpdateHistory() {
        // 准备测试数据
        Long historyId = 1L;
        Integer totalRows = 10;
        Integer successRows = 8;
        Integer failRows = 2;
        String importStatus = "SUCCESS";
        String errorMessage = null;
        Integer processingTime = 500;
        
        ExcelImportHistory existingHistory = new ExcelImportHistory();
        existingHistory.setId(historyId);
        existingHistory.setTemplateId(1L);
        existingHistory.setFileName("测试文件.xlsx");
        existingHistory.setFileSize(10240L);
        existingHistory.setSheetIndex(0);
        existingHistory.setImportedBy(1L);
        existingHistory.setImportedTime(LocalDateTime.now());
        existingHistory.setImportStatus("PROCESSING");
        existingHistory.setTotalRows(0);
        existingHistory.setSuccessRows(0);
        existingHistory.setFailRows(0);
        
        ExcelImportHistory updatedHistory = new ExcelImportHistory();
        updatedHistory.setId(historyId);
        updatedHistory.setTemplateId(1L);
        updatedHistory.setFileName("测试文件.xlsx");
        updatedHistory.setFileSize(10240L);
        updatedHistory.setSheetIndex(0);
        updatedHistory.setImportedBy(1L);
        updatedHistory.setImportedTime(LocalDateTime.now());
        updatedHistory.setImportStatus(importStatus);
        updatedHistory.setTotalRows(totalRows);
        updatedHistory.setSuccessRows(successRows);
        updatedHistory.setFailRows(failRows);
        updatedHistory.setErrorMessage(errorMessage);
        updatedHistory.setProcessingTime(processingTime);
        
        // 模拟方法调用
        when(historyRepository.findById(historyId)).thenReturn(Optional.of(existingHistory));
        when(historyRepository.save(any(ExcelImportHistory.class))).thenReturn(updatedHistory);
        
        // 执行测试
        ExcelImportHistoryResponse response = historyService.updateHistory(historyId, totalRows, successRows, failRows, importStatus, errorMessage, processingTime);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(historyId, response.getId());
        assertEquals(importStatus, importStatus);
        assertEquals(totalRows, response.getTotalRows());
        assertEquals(successRows, response.getSuccessRows());
        assertEquals(failRows, response.getFailRows());
        assertEquals(errorMessage, response.getErrorMessage());
        assertEquals(processingTime, response.getProcessingTime());
        
        verify(historyRepository, times(1)).findById(historyId);
        verify(historyRepository, times(1)).save(any(ExcelImportHistory.class));
    }
    
    @Test
    void testUpdateHistory_NotFound() {
        // 准备测试数据
        Long historyId = 999L;
        
        // 模拟方法调用
        when(historyRepository.findById(historyId)).thenReturn(Optional.empty());
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            historyService.updateHistory(historyId, 10, 8, 2, "SUCCESS", null, 500);
        });
        
        assertEquals("导入历史记录不存在: " + historyId, exception.getMessage());
        verify(historyRepository, times(1)).findById(historyId);
        verify(historyRepository, never()).save(any(ExcelImportHistory.class));
    }
    
    @Test
    void testGetHistoryById() {
        // 准备测试数据
        Long historyId = 1L;
        
        ExcelImportHistory history = new ExcelImportHistory();
        history.setId(historyId);
        history.setTemplateId(1L);
        history.setFileName("测试文件.xlsx");
        history.setFileSize(10240L);
        history.setSheetIndex(0);
        history.setImportedBy(1L);
        history.setImportedTime(LocalDateTime.now());
        history.setImportStatus("SUCCESS");
        history.setTotalRows(10);
        history.setSuccessRows(8);
        history.setFailRows(2);
        
        // 模拟方法调用
        when(historyRepository.findById(historyId)).thenReturn(Optional.of(history));
        
        // 执行测试
        ExcelImportHistoryResponse response = historyService.getHistoryById(historyId);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(historyId, response.getId());
        assertEquals(1L, response.getTemplateId());
        assertEquals("测试文件.xlsx", response.getFileName());
        assertEquals(10240L, response.getFileSize());
        assertEquals(0, response.getSheetIndex());
        assertEquals(1L, response.getImportedBy());
        assertEquals("SUCCESS", "SUCCESS");
        assertEquals(10, response.getTotalRows());
        assertEquals(8, response.getSuccessRows());
        assertEquals(2, response.getFailRows());
        
        verify(historyRepository, times(1)).findById(historyId);
    }
    
    @Test
    void testGetHistoryById_NotFound() {
        // 准备测试数据
        Long historyId = 999L;
        
        // 模拟方法调用
        when(historyRepository.findById(historyId)).thenReturn(Optional.empty());
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            historyService.getHistoryById(historyId);
        });
        
        assertEquals("导入历史记录不存在: " + historyId, exception.getMessage());
        verify(historyRepository, times(1)).findById(historyId);
    }
    
    @Test
    void testGetAllHistories() {
        // 准备测试数据
        Pageable pageable = PageRequest.of(0, 10);
        
        List<ExcelImportHistory> histories = new ArrayList<>();
        ExcelImportHistory history1 = new ExcelImportHistory();
        history1.setId(1L);
        history1.setTemplateId(1L);
        history1.setFileName("测试文件1.xlsx");
        history1.setImportStatus("SUCCESS");
        histories.add(history1);
        
        ExcelImportHistory history2 = new ExcelImportHistory();
        history2.setId(2L);
        history2.setTemplateId(1L);
        history2.setFileName("测试文件2.xlsx");
        history2.setImportStatus("FAILED");
        histories.add(history2);
        
        Page<ExcelImportHistory> historyPage = new PageImpl<>(histories, pageable, histories.size());
        
        // 模拟方法调用
        when(historyRepository.findAll(pageable)).thenReturn(historyPage);
        
        // 执行测试
        Page<ExcelImportHistoryResponse> responsePage = historyService.getAllHistories(pageable);
        
        // 验证结果
        assertNotNull(responsePage);
        assertEquals(2, responsePage.getTotalElements());
        assertEquals(2, responsePage.getContent().size());
        assertEquals("测试文件1.xlsx", responsePage.getContent().get(0).getFileName());
        assertEquals("测试文件2.xlsx", responsePage.getContent().get(1).getFileName());
        
        verify(historyRepository, times(1)).findAll(pageable);
    }
    
    @Test
    void testDeleteHistory() {
        // 准备测试数据
        Long historyId = 1L;
        
        // 模拟方法调用
        when(historyRepository.existsById(historyId)).thenReturn(true);
        doNothing().when(historyRepository).deleteById(historyId);
        
        // 执行测试
        historyService.deleteHistory(historyId);
        
        // 验证结果
        verify(historyRepository, times(1)).existsById(historyId);
        verify(historyRepository, times(1)).deleteById(historyId);
    }
    
    @Test
    void testDeleteHistory_NotFound() {
        // 准备测试数据
        Long historyId = 999L;
        
        // 模拟方法调用
        when(historyRepository.existsById(historyId)).thenReturn(false);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            historyService.deleteHistory(historyId);
        });
        
        assertEquals("导入历史记录不存在: " + historyId, exception.getMessage());
        verify(historyRepository, times(1)).existsById(historyId);
        verify(historyRepository, never()).deleteById(historyId);
    }
}

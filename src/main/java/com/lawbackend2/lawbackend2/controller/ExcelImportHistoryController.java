package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.dto.ExcelImportHistoryResponse;
import com.lawbackend2.lawbackend2.service.ExcelImportHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Tag(name = "Excel导入历史管理")
@RestController
@RequestMapping("/api/excel-import-history")
public class ExcelImportHistoryController {
    
    @Autowired
    private ExcelImportHistoryService historyService;
    
    @Operation(summary = "分页查询所有导入历史记录")
    @GetMapping
    public ResponseEntity<Page<ExcelImportHistoryResponse>> getAllHistories(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "importedTime") String sort,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String direction) {
        
        log.info("分页查询所有导入历史记录: page={}, size={}, sort={}, direction={}", page, size, sort, direction);
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<ExcelImportHistoryResponse> result = historyService.getAllHistories(pageable);
        return ResponseEntity.ok(result);
    }
    
    @Operation(summary = "根据ID查询导入历史记录")
    @GetMapping("/{id}")
    public ResponseEntity<ExcelImportHistoryResponse> getHistoryById(
            @Parameter(description = "历史记录ID") @PathVariable Long id) {
        
        log.info("查询导入历史记录: id={}", id);
        
        ExcelImportHistoryResponse response = historyService.getHistoryById(id);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "根据模板ID查询导入历史记录")
    @GetMapping("/template/{templateId}")
    public ResponseEntity<List<ExcelImportHistoryResponse>> getHistoriesByTemplateId(
            @Parameter(description = "模板ID") @PathVariable Long templateId) {
        
        log.info("查询模板的导入历史记录: templateId={}", templateId);
        
        List<ExcelImportHistoryResponse> response = historyService.getHistoriesByTemplateId(templateId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "根据导入用户ID查询导入历史记录")
    @GetMapping("/user/{importedBy}")
    public ResponseEntity<List<ExcelImportHistoryResponse>> getHistoriesByImportedBy(
            @Parameter(description = "导入用户ID") @PathVariable Long importedBy) {
        
        log.info("查询用户的导入历史记录: importedBy={}", importedBy);
        
        List<ExcelImportHistoryResponse> response = historyService.getHistoriesByImportedBy(importedBy);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "根据导入状态查询导入历史记录")
    @GetMapping("/status/{importStatus}")
    public ResponseEntity<List<ExcelImportHistoryResponse>> getHistoriesByImportStatus(
            @Parameter(description = "导入状态") @PathVariable String importStatus) {
        
        log.info("查询指定状态的导入历史记录: importStatus={}", importStatus);
        
        List<ExcelImportHistoryResponse> response = historyService.getHistoriesByImportStatus(importStatus);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "根据用户ID和时间范围查询导入历史记录")
    @GetMapping("/user/{importedBy}/range")
    public ResponseEntity<List<ExcelImportHistoryResponse>> getHistoriesByImportedByAndTimeRange(
            @Parameter(description = "导入用户ID") @PathVariable Long importedBy,
            @Parameter(description = "开始时间") @RequestParam String startTime,
            @Parameter(description = "结束时间") @RequestParam String endTime) {
        
        log.info("查询用户在时间范围内的导入历史记录: importedBy={}, startTime={}, endTime={}", 
                 importedBy, startTime, endTime);
        
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);
        
        List<ExcelImportHistoryResponse> response = historyService.getHistoriesByImportedByAndTimeRange(importedBy, start, end);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "删除导入历史记录")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistory(
            @Parameter(description = "历史记录ID") @PathVariable Long id) {
        
        log.info("删除导入历史记录: id={}", id);
        
        historyService.deleteHistory(id);
        return ResponseEntity.ok().build();
    }
}

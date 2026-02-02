package com.lawbackend2.lawbackend2.service.impl;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.lawbackend2.lawbackend2.config.FieldMappingConfig;
import com.lawbackend2.lawbackend2.service.ExcelParseService;
import com.lawbackend2.lawbackend2.service.ExcelTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ExcelParseServiceImpl implements ExcelParseService {
    
    private final FieldMappingConfig fieldMappingConfig;
    private final ExcelTemplateService excelTemplateService;
    
    public ExcelParseServiceImpl(FieldMappingConfig fieldMappingConfig, ExcelTemplateService excelTemplateService) {
        this.fieldMappingConfig = fieldMappingConfig;
        this.excelTemplateService = excelTemplateService;
    }
    
    @Override
    public Map<String, Object> parseExcel(MultipartFile file) {
        return parseExcelWithSheet(file, 0);
    }
    
    @Override
    public Map<String, Object> parseExcelWithSheet(MultipartFile file, Integer sheetIndex) {
        return parseExcelWithSheet(file, sheetIndex, null);
    }
    
    @Override
    public Map<String, Object> parseExcelWithSheet(MultipartFile file, Integer sheetIndex, String templateCode) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            log.info("开始解析Excel文件 - 文件名: {}, 大小: {}KB, Sheet索引: {}, 模板编码: {}", 
                    file.getOriginalFilename(), file.getSize() / 1024, sheetIndex, templateCode);
            
            List<Map<Integer, String>> potentialHeaders = new ArrayList<>();
            List<Map<Integer, String>> dataRows = new ArrayList<>();
            
            com.alibaba.excel.EasyExcel.read(file.getInputStream())
                    .sheet(sheetIndex)
                    .headRowNumber(0)
                    .registerReadListener(new AnalysisEventListener<Map<Integer, String>>() {
                        @Override
                        public void invoke(Map<Integer, String> data, AnalysisContext context) {
                            int rowIndex = context.readRowHolder().getRowIndex();
                            
                            // 检查前10行，寻找可能的表头
                            if (rowIndex < 10) {
                                int nonEmptyCells = countNonEmptyCells(data);
                                if (nonEmptyCells > 0) {
                                    potentialHeaders.add(data);
                                    log.debug("发现潜在表头行 {} ({}个非空单元格): {}", 
                                            rowIndex + 1, nonEmptyCells, data);
                                }
                            }
                            
                            // 从第1行开始，寻找数据行
                            if (rowIndex >= 1) {
                                int nonEmptyCells = countNonEmptyCells(data);
                                if (nonEmptyCells > 0) {
                                    dataRows.add(data);
                                    log.debug("发现数据行 {} ({}个非空单元格): {}", 
                                            rowIndex + 1, nonEmptyCells, data);
                                }
                            }
                        }
                        
                        @Override
                        public void doAfterAllAnalysed(AnalysisContext context) {
                            log.info("Excel解析完成 - 总行数: {}", context.readRowHolder().getRowIndex() + 1);
                            log.info("发现 {} 个潜在表头行, {} 个数据行", 
                                    potentialHeaders.size(), dataRows.size());
                        }
                    })
                    .doRead();
            
            // 加载模板配置
            Map<String, String> templateMappings = null;
            if (templateCode != null && !templateCode.trim().isEmpty()) {
                templateMappings = excelTemplateService.getFieldMappings(templateCode);
                log.info("加载模板配置 - 模板编码: {}, 映射数量: {}", 
                        templateCode, templateMappings != null ? templateMappings.size() : 0);
            }
            
            // 寻找最佳表头行
            Map<Integer, String> bestHeader = findBestHeaderRow(potentialHeaders);
            if (bestHeader != null) {
                log.info("选择最佳表头行: {}", bestHeader);
                
                // 寻找最佳数据行
                Map<Integer, String> bestDataRow = findBestDataRow(dataRows);
                if (bestDataRow != null) {
                    log.info("选择最佳数据行: {}", bestDataRow);
                    
                    // 进行字段匹配 - 优先使用模板映射
                    int matchedCount = matchFields(bestHeader, bestDataRow, result, templateMappings);
                    log.info("字段匹配完成 - 匹配字段数量: {}", matchedCount);
                } else {
                    log.warn("未找到数据行");
                    result.put("warning", "未找到数据行");
                }
            } else {
                log.warn("未找到表头行");
                result.put("warning", "未找到表头行");
            }
            
            // 如果没有匹配到任何字段，添加一些调试信息
            if (result.isEmpty() || result.size() == 1 && result.containsKey("warning")) {
                Map<String, Object> debugInfo = new HashMap<>();
                debugInfo.put("fileName", file.getOriginalFilename());
                debugInfo.put("fileSize", file.getSize());
                debugInfo.put("potentialHeadersCount", potentialHeaders.size());
                debugInfo.put("dataRowsCount", dataRows.size());
                debugInfo.put("templateCode", templateCode != null ? templateCode : "未指定");
                debugInfo.put("templateMappings", templateMappings != null ? templateMappings.size() : 0);
                if (!potentialHeaders.isEmpty()) {
                    debugInfo.put("firstHeader", potentialHeaders.get(0));
                }
                if (!dataRows.isEmpty()) {
                    debugInfo.put("firstDataRow", dataRows.get(0));
                }
                result.put("debug", debugInfo);
                log.info("添加调试信息到响应: {}", debugInfo);
            }
            
            log.info("Excel解析完成 - 最终返回字段数量: {}", result.size());
            
        } catch (IOException e) {
            log.error("Excel文件解析失败", e);
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("error", "Excel文件解析失败: " + e.getMessage());
            errorInfo.put("errorType", "IO_ERROR");
            return errorInfo;
        } catch (Exception e) {
            log.error("解析过程中发生异常", e);
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("error", "解析过程中发生异常: " + e.getMessage());
            errorInfo.put("errorType", "UNKNOWN_ERROR");
            return errorInfo;
        }
        
        return result;
    }
    
    private int countNonEmptyCells(Map<Integer, String> row) {
        if (row == null) return 0;
        return (int) row.values().stream()
                .filter(cell -> cell != null && !cell.trim().isEmpty())
                .count();
    }
    
    private Map<Integer, String> findBestHeaderRow(List<Map<Integer, String>> potentialHeaders) {
        if (potentialHeaders.isEmpty()) return null;
        
        Map<Integer, String> bestHeader = null;
        int maxNonEmptyCells = 0;
        
        for (Map<Integer, String> header : potentialHeaders) {
            int nonEmptyCells = countNonEmptyCells(header);
            if (nonEmptyCells > maxNonEmptyCells) {
                maxNonEmptyCells = nonEmptyCells;
                bestHeader = header;
            }
        }
        
        return bestHeader;
    }
    
    private Map<Integer, String> findBestDataRow(List<Map<Integer, String>> dataRows) {
        if (dataRows.isEmpty()) return null;
        
        Map<Integer, String> bestDataRow = null;
        int maxNonEmptyCells = 0;
        
        for (Map<Integer, String> dataRow : dataRows) {
            int nonEmptyCells = countNonEmptyCells(dataRow);
            
            // 跳过可能是表头的数据行
            if (isPotentialHeader(dataRow)) {
                log.debug("跳过潜在表头行: {}", dataRow);
                continue;
            }
            
            if (nonEmptyCells > maxNonEmptyCells) {
                maxNonEmptyCells = nonEmptyCells;
                bestDataRow = dataRow;
            }
        }
        
        return bestDataRow;
    }
    
    private boolean isPotentialHeader(Map<Integer, String> row) {
        if (row == null || row.isEmpty()) return false;
        
        // 检查是否包含常见的表头关键词
        String[] headerKeywords = {
            "收件", "编号", "债权人", "申报时间", "住所", "邮编", "联系电话", 
            "申报金额", "性质", "法定代表人", "代理人", "债权性质", "债权种类", 
            "开户名", "开户行", "账号", "涉讼", "备注"
        };
        
        int keywordCount = 0;
        for (String value : row.values()) {
            if (value != null) {
                String trimmedValue = value.trim();
                for (String keyword : headerKeywords) {
                    if (trimmedValue.contains(keyword)) {
                        keywordCount++;
                        break;
                    }
                }
            }
        }
        
        // 如果包含3个或更多表头关键词，认为这是一个表头行
        return keywordCount >= 3;
    }
    
    private int matchFields(Map<Integer, String> headerRow, Map<Integer, String> dataRow, Map<String, Object> result, Map<String, String> templateMappings) {
        int matchedCount = 0;
        
        for (Map.Entry<Integer, String> headerEntry : headerRow.entrySet()) {
            Integer columnIndex = headerEntry.getKey();
            String headerValue = headerEntry.getValue();
            
            if (headerValue != null && !headerValue.trim().isEmpty()) {
                String fieldName = matchField(headerValue, templateMappings);
                if (fieldName != null) {
                    String dataValue = dataRow.getOrDefault(columnIndex, "");
                    if (dataValue != null && !dataValue.trim().isEmpty()) {
                        result.put(fieldName, dataValue.trim());
                        log.debug("字段匹配成功 - 表头: '{}', 字段名: '{}', 值: '{}', 列索引: {}", 
                                headerValue, fieldName, dataValue, columnIndex);
                        matchedCount++;
                    } else {
                        log.debug("字段匹配但数据为空 - 表头: '{}', 字段名: '{}', 列索引: {}", 
                                headerValue, fieldName, columnIndex);
                    }
                } else {
                    log.debug("字段未匹配 - 表头: '{}', 列索引: {}", headerValue, columnIndex);
                }
            }
        }
        
        return matchedCount;
    }
    
    private String matchField(String header, Map<String, String> templateMappings) {
        if (header == null || header.trim().isEmpty()) {
            return null;
        }
        
        header = header.trim();
        
        String fieldName = templateMappings.get(header);
        if (fieldName != null) {
            return fieldName;
        }
        
        String bestMatch = null;
        int minDistance = Integer.MAX_VALUE;
        
        for (String key : templateMappings.keySet()) {
            int distance = calculateLevenshteinDistance(header, key);
            if (distance < minDistance && distance <= 2) {
                minDistance = distance;
                bestMatch = templateMappings.get(key);
            }
        }
        
        if (bestMatch != null) {
            log.debug("模板匹配成功 - 表头: {}, 最佳匹配: {}, 距离: {}", 
                    header, bestMatch, minDistance);
        }
        
        return bestMatch;
    }
    
    private int calculateLevenshteinDistance(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return Math.max(s1 == null ? 0 : s1.length(), s2 == null ? 0 : s2.length());
        }
        
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        
        return dp[s1.length()][s2.length()];
    }
}

package com.lawbackend2.lawbackend2.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import org.apache.poi.ss.usermodel.*;
import com.lawbackend2.lawbackend2.dto.ExcelTemplateImportResult;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.service.ExcelTemplateImportExportService;
import com.lawbackend2.lawbackend2.service.ExcelTemplateService;
import com.lawbackend2.lawbackend2.service.excel.TemplateHandler;
import com.lawbackend2.lawbackend2.service.excel.TemplateHandlerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Excel模板导入导出服务实现（V2 - 使用策略模式）
 * 支持自动识别模板类型并调用对应的处理器
 */
@Slf4j
@Primary
@Service
public class ExcelTemplateImportExportServiceImplV2 implements ExcelTemplateImportExportService {

    @Autowired
    private TemplateHandlerRegistry handlerRegistry;

    @Autowired
    private ExcelTemplateService excelTemplateService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExcelTemplateImportResult importFromExcel(MultipartFile file, String templateCode, Long caseId, Integer sheetIndex, Long userId) {
        log.info("开始基于模板导入Excel - 模板编码: {}, 案件ID: {}, Sheet索引: {}, 文件名: {}",
                templateCode, caseId, sheetIndex, file.getOriginalFilename());

        ExcelTemplateImportResult result = new ExcelTemplateImportResult();
        result.setSuccessCount(0);
        result.setFailCount(0);
        result.setTotalCount(0);
        result.setErrors(new ArrayList<>());
        result.setTemplateCode(templateCode);
        result.setCaseId(caseId);

        // 1. 获取处理器
        TemplateHandler handler = handlerRegistry.getHandler(templateCode);
        if (handler == null) {
            result.setMessage("未找到对应的模板处理器，模板编码: " + templateCode);
            log.error("未找到对应的模板处理器，模板编码: {}", templateCode);
            return result;
        }

        // 2. 解析Excel文件
        try {
            List<Map<Integer, String>> headers = new ArrayList<>();
            List<Map<Integer, String>> dataRows = new ArrayList<>();

            int finalSheetIndex = sheetIndex != null ? sheetIndex : 0;

            EasyExcel.read(file.getInputStream())
                    .sheet(finalSheetIndex)
                    .headRowNumber(0)
                    .registerReadListener(new AnalysisEventListener<Map<Integer, String>>() {
                        @Override
                        public void invoke(Map<Integer, String> data, AnalysisContext context) {
                            int rowIndex = context.readRowHolder().getRowIndex();
                            if (rowIndex < 10) {
                                headers.add(data);
                            }
                            if (rowIndex >= 1) {
                                int nonEmptyCells = countNonEmptyCells(data);
                                if (nonEmptyCells > 0) {
                                    dataRows.add(data);
                                }
                            }
                        }

                        @Override
                        public void doAfterAllAnalysed(AnalysisContext context) {
                            log.info("Excel解析完成，共{}行数据", dataRows.size());
                        }
                    })
                    .doRead();

            if (dataRows.isEmpty()) {
                result.setMessage("Excel文件中没有数据行");
                return result;
            }

            // 3. 找到最佳表头行
            Map<Integer, String> bestHeader = findBestHeaderRow(headers);
            if (bestHeader == null) {
                result.setMessage("未找到表头行");
                return result;
            }

            // 4. 获取默认字段映射
            Map<String, String> fieldMappings = handler.getDefaultFieldMappings();

            // 5. 构建列索引到字段名的映射
            Map<Integer, String> columnToFieldMap = new HashMap<>();
            for (Map.Entry<Integer, String> entry : bestHeader.entrySet()) {
                Integer colIndex = entry.getKey();
                String headerValue = entry.getValue();
                if (headerValue != null && !headerValue.trim().isEmpty()) {
                    String fieldName = matchField(headerValue, fieldMappings);
                    if (fieldName != null) {
                        columnToFieldMap.put(colIndex, fieldName);
                    }
                }
            }

            if (columnToFieldMap.isEmpty()) {
                result.setMessage("未能匹配任何字段，请检查Excel表头与模板配置是否匹配");
                return result;
            }
            log.info("成功匹配{}个字段", columnToFieldMap.size());

            // 6. 转换数据格式并导入
            List<Map<String, String>> mappedDataRows = new ArrayList<>();
            for (Map<Integer, String> dataRow : dataRows) {
                Map<String, String> mappedRow = new HashMap<>();
                for (Map.Entry<Integer, String> entry : columnToFieldMap.entrySet()) {
                    String fieldName = entry.getValue();
                    String value = dataRow.get(entry.getKey());
                    if (value != null) {
                        mappedRow.put(fieldName, value.trim());
                    }
                }
                if (!mappedRow.isEmpty()) {
                    mappedDataRows.add(mappedRow);
                }
            }

            // 7. 使用处理器导入数据
            TemplateHandler.ImportResult importResult = handler.importData(mappedDataRows, caseId, userId);

            result.setSuccessCount(importResult.getSuccessCount());
            result.setFailCount(importResult.getFailCount());
            result.setTotalCount(mappedDataRows.size());
            
            // 转换错误信息
            if (importResult.getErrors() != null) {
                for (TemplateHandler.ImportError error : importResult.getErrors()) {
                    result.getErrors().add(new ExcelTemplateImportResult.ImportError(
                        error.getRowNum(), error.getMessage(), error.getData()
                    ));
                }
            }

            result.setMessage(String.format("%s导入完成，成功%d条，失败%d条，总计%d条",
                    handler.getTemplateName(), result.getSuccessCount(), result.getFailCount(), result.getTotalCount()));
            log.info("模板导入完成 - {}", result.getMessage());

        } catch (IOException e) {
            result.setMessage("Excel文件读取失败: " + e.getMessage());
            log.error("Excel文件读取失败", e);
        } catch (Exception e) {
            result.setMessage("导入过程中发生错误: " + e.getMessage());
            log.error("导入过程中发生错误", e);
        }

        return result;
    }

    @Override
    public void exportToExcel(HttpServletResponse response, String templateCode, Long caseId, String registrationStatus) {
        log.info("开始基于模板导出Excel - 模板编码: {}, 案件ID: {}, 状态: {}",
                templateCode, caseId, registrationStatus);

        try {
            // 1. 获取处理器
            TemplateHandler handler = handlerRegistry.getHandler(templateCode);
            if (handler == null) {
                throw new BusinessException("未找到对应的模板处理器，模板编码: " + templateCode);
            }

            // 2. 获取模板配置（如果数据库中有配置则使用，否则使用默认配置）
            Map<String, String> fieldMappings = excelTemplateService.getFieldMappings(templateCode);
            if (fieldMappings == null || fieldMappings.isEmpty()) {
                fieldMappings = handler.getDefaultFieldMappings();
            }

            // 3. 导出数据
            List<Map<String, Object>> exportData = handler.exportData(caseId, registrationStatus);
            log.info("查询到{}条记录", exportData.size());

            // 4. 设置响应头
            String fileName = handler.getExportFileName() + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");

            // 5. 写入Excel - 使用动态表头
            List<List<String>> headers = new ArrayList<>();
            for (String headerName : fieldMappings.keySet()) {
                List<String> head = new ArrayList<>();
                head.add(headerName);
                headers.add(head);
            }

            List<List<Object>> data = new ArrayList<>();
            for (Map<String, Object> row : exportData) {
                List<Object> rowData = new ArrayList<>();
                for (String headerName : fieldMappings.keySet()) {
                    String fieldName = fieldMappings.get(headerName);
                    Object value = row.get(fieldName);
                    rowData.add(value != null ? value : "");
                }
                data.add(rowData);
            }

            // 设置列头样式（无背景色，增大宽度）
            WriteCellStyle headWriteCellStyle = new WriteCellStyle();
            // 设置背景色为无填充
            headWriteCellStyle.setFillPatternType(FillPatternType.NO_FILL);
            headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            headWriteCellStyle.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
            // 设置字体
            WriteFont headWriteFont = new WriteFont();
            headWriteFont.setFontHeightInPoints((short)12); // 增大字体大小
            headWriteFont.setBold(true); // 增粗字体
            headWriteCellStyle.setWriteFont(headWriteFont);
            // 设置边框
            headWriteCellStyle.setBorderBottom(BorderStyle.THIN);
            headWriteCellStyle.setBorderLeft(BorderStyle.THIN);
            headWriteCellStyle.setBorderRight(BorderStyle.THIN);
            headWriteCellStyle.setBorderTop(BorderStyle.THIN);
            // 设置水平对齐
            headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);

            // 设置内容样式（保持默认）
            WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
            // 设置边框
            contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
            contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
            contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
            contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
            // 设置水平对齐
            contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);

            // 创建样式策略
            HorizontalCellStyleStrategy styleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);

            EasyExcel.write(response.getOutputStream())
                    .head(headers)
                    .sheet(handler.getExportSheetName())
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .registerWriteHandler(styleStrategy)
                    .doWrite(data);

            log.info("导出完成，共{}条数据", data.size());

        } catch (IOException e) {
            log.error("导出Excel失败", e);
            throw new BusinessException("导出Excel失败: " + e.getMessage());
        }
    }

    private int countNonEmptyCells(Map<Integer, String> row) {
        if (row == null) return 0;
        return (int) row.values().stream()
                .filter(cell -> cell != null && !cell.trim().isEmpty())
                .count();
    }

    private Map<Integer, String> findBestHeaderRow(List<Map<Integer, String>> headers) {
        if (headers.isEmpty()) return null;

        Map<Integer, String> bestHeader = null;
        int maxNonEmptyCells = 0;

        for (Map<Integer, String> header : headers) {
            int nonEmptyCells = countNonEmptyCells(header);
            if (nonEmptyCells > maxNonEmptyCells) {
                maxNonEmptyCells = nonEmptyCells;
                bestHeader = header;
            }
        }

        return bestHeader;
    }

    private String matchField(String header, Map<String, String> fieldMappings) {
        if (header == null || header.trim().isEmpty()) {
            return null;
        }

        header = header.trim();

        // 直接匹配
        String fieldName = fieldMappings.get(header);
        if (fieldName != null) {
            return fieldName;
        }

        // 模糊匹配 - 计算编辑距离
        String bestMatch = null;
        int minDistance = Integer.MAX_VALUE;

        for (String key : fieldMappings.keySet()) {
            int distance = calculateLevenshteinDistance(header, key);
            if (distance < minDistance && distance <= 2) {
                minDistance = distance;
                bestMatch = fieldMappings.get(key);
            }
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

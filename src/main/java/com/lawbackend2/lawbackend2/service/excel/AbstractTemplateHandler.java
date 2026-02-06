package com.lawbackend2.lawbackend2.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 模板处理器抽象基类
 * 提供通用的导入导出逻辑，子类只需实现特定的方法
 */
@Slf4j
public abstract class AbstractTemplateHandler implements TemplateHandler {

    @Autowired
    private TemplateHandlerRegistry registry;

    /**
     * 注册处理器到注册中心
     */
    @PostConstruct
    public void init() {
        registry.register(this);
        log.info("模板处理器已注册: {} - {}", getTemplateCode(), getTemplateName());
    }

    @Override
    public ImportResult importData(List<Map<String, String>> dataRows, Long caseId, Long userId) {
        ImportResult result = new ImportResult();
        result.setSuccessCount(0);
        result.setFailCount(0);
        result.setErrors(new ArrayList<>());

        int rowNum = 2; // 从第2行开始

        for (Map<String, String> row : dataRows) {
            try {
                // 验证数据行
                String error = validateRow(row);
                if (error != null) {
                    result.setFailCount(result.getFailCount() + 1);
                    result.getErrors().add(new ImportError(rowNum, error, row.toString()));
                    rowNum++;
                    continue;
                }

                // 保存数据
                saveRow(row, caseId, userId);
                result.setSuccessCount(result.getSuccessCount() + 1);

            } catch (Exception e) {
                result.setFailCount(result.getFailCount() + 1);
                result.getErrors().add(new ImportError(rowNum, e.getMessage(), row.toString()));
                log.error("第{}行导入失败: {}", rowNum, e.getMessage());
            }
            rowNum++;
        }

        return result;
    }

    /**
     * 保存单行数据（子类必须实现）
     */
    protected abstract void saveRow(Map<String, String> row, Long caseId, Long userId);

    /**
     * 获取字段值（支持多种字段名变体）
     */
    protected String getStringValue(Map<String, String> row, String... fieldNames) {
        for (String fieldName : fieldNames) {
            String value = row.get(fieldName);
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return null;
    }

    /**
     * 获取BigDecimal值
     */
    protected BigDecimal getBigDecimalValue(Map<String, String> row, String... fieldNames) {
        String value = getStringValue(row, fieldNames);
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            // 移除千分位分隔符和货币符号
            String cleanValue = value.replaceAll(",", "")
                    .replaceAll("￥", "")
                    .replaceAll("\\$", "")
                    .trim();
            return new BigDecimal(cleanValue);
        } catch (NumberFormatException e) {
            log.warn("无法解析金额: {}", value);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 获取布尔值
     */
    protected Boolean getBooleanValue(Map<String, String> row, String... fieldNames) {
        String value = getStringValue(row, fieldNames);
        if (value == null) return false;
        String lower = value.trim().toLowerCase();
        return lower.equals("是") || lower.equals("yes") || lower.equals("true") 
                || lower.equals("1") || lower.equals("有");
    }

    /**
     * 获取Long值
     */
    protected Long getLongValue(Map<String, String> row, String... fieldNames) {
        String value = getStringValue(row, fieldNames);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            log.warn("无法解析Long: {}", value);
            return null;
        }
    }
}

package com.lawbackend2.lawbackend2.service.excel;

import java.util.List;
import java.util.Map;

/**
 * Excel模板处理器接口
 * 每个业务实体需要实现此接口来支持Excel导入导出
 */
public interface TemplateHandler {

    /**
     * 获取处理器支持的模板编码
     */
    String getTemplateCode();

    /**
     * 获取模板名称
     */
    String getTemplateName();

    /**
     * 获取默认字段映射（表头 -> 字段名）
     */
    Map<String, String> getDefaultFieldMappings();

    /**
     * 导入数据
     * @param dataRows 数据行（表头 -> 值）
     * @param caseId 案件ID
     * @param userId 用户ID
     * @return 导入结果
     */
    ImportResult importData(List<Map<String, String>> dataRows, Long caseId, Long userId);

    /**
     * 导出数据
     * @param caseId 案件ID（可选）
     * @param status 状态（可选）
     * @return 导出数据（每行是一个Map，key为字段名，value为值）
     */
    List<Map<String, Object>> exportData(Long caseId, String status);

    /**
     * 获取导出文件名
     */
    String getExportFileName();

    /**
     * 获取导出Sheet名称
     */
    String getExportSheetName();

    /**
     * 验证数据行
     * @param row 数据行
     * @return 错误信息，null表示验证通过
     */
    String validateRow(Map<String, String> row);

    /**
     * 导入结果
     */
    class ImportResult {
        private int successCount;
        private int failCount;
        private List<ImportError> errors;

        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getFailCount() { return failCount; }
        public void setFailCount(int failCount) { this.failCount = failCount; }
        public List<ImportError> getErrors() { return errors; }
        public void setErrors(List<ImportError> errors) { this.errors = errors; }
    }

    /**
     * 导入错误
     */
    class ImportError {
        private int rowNum;
        private String message;
        private String data;

        public ImportError(int rowNum, String message, String data) {
            this.rowNum = rowNum;
            this.message = message;
            this.data = data;
        }

        public int getRowNum() { return rowNum; }
        public void setRowNum(int rowNum) { this.rowNum = rowNum; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
    }
}

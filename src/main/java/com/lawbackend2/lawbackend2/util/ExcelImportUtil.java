package com.lawbackend2.lawbackend2.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelImportUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER_SIMPLE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static List<Map<String, Object>> parseExcel(MultipartFile file) throws IOException {
        List<Map<String, Object>> dataList = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        System.out.println("Sheet name: " + sheet.getSheetName());
        System.out.println("Total rows: " + sheet.getLastRowNum() + 1);

        // 查找真正的表头行
        int headerRowIndex = findHeaderRowIndex(sheet);
        Row headerRow = sheet.getRow(headerRowIndex);
        if (headerRow == null) {
            throw new IOException("Excel文件为空或没有表头");
        }

        System.out.println("Header row index: " + headerRowIndex);
        System.out.println("Header row cells count: " + headerRow.getLastCellNum());

        List<String> headers = new ArrayList<>();
        for (int j = 0; j < headerRow.getLastCellNum(); j++) {
            Cell cell = headerRow.getCell(j);
            String headerValue = getCellValueAsString(cell);
            headers.add(headerValue);
            System.out.println("Header cell " + j + ": " + headerValue);
        }

        int consecutiveEmptyRows = 0;
        int maxConsecutiveEmptyRows = 10;

        for (int i = headerRowIndex + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                consecutiveEmptyRows++;
                if (consecutiveEmptyRows <= 5) {
                    System.out.println("Row " + i + " is null, skipping");
                } else if (consecutiveEmptyRows == 6) {
                    System.out.println("... more empty rows, skipping log output");
                }
                if (consecutiveEmptyRows >= maxConsecutiveEmptyRows) {
                    System.out.println("Found " + consecutiveEmptyRows + " consecutive empty rows, stopping parsing");
                    break;
                }
                continue;
            }

            // 重置连续空行计数
            consecutiveEmptyRows = 0;

            // 检查行是否为空（所有单元格都为空）
            boolean isEmptyRow = true;
            for (int j = 0; j < headers.size(); j++) {
                Cell cell = row.getCell(j);
                if (cell != null && getCellValueAsString(cell) != null && !getCellValueAsString(cell).trim().isEmpty()) {
                    isEmptyRow = false;
                    break;
                }
            }

            if (isEmptyRow) {
                consecutiveEmptyRows++;
                if (consecutiveEmptyRows <= 5) {
                    System.out.println("Row " + i + " is empty, skipping");
                }
                if (consecutiveEmptyRows >= maxConsecutiveEmptyRows) {
                    System.out.println("Found " + consecutiveEmptyRows + " consecutive empty rows, stopping parsing");
                    break;
                }
                continue;
            }

            // 只打印前10行和最后10行的详细信息
            if (i <= headerRowIndex + 10 || i > sheet.getLastRowNum() - 10) {
                System.out.println("Row " + i + " cells count: " + row.getLastCellNum());
            }

            Map<String, Object> rowData = new HashMap<>();
            for (int j = 0; j < headers.size(); j++) {
                Cell cell = row.getCell(j);
                String header = headers.get(j);
                Object value = getCellValue(cell);
                rowData.put(header, value);
                if (i <= headerRowIndex + 10 || i > sheet.getLastRowNum() - 10) {
                    System.out.println("Row " + i + " cell " + j + " (" + header + "): " + value);
                }
            }
            dataList.add(rowData);
            if (i <= headerRowIndex + 10 || i > sheet.getLastRowNum() - 10) {
                System.out.println("Row " + i + " data: " + rowData);
            } else if (i == headerRowIndex + 11) {
                System.out.println("... more data rows, skipping log output");
            }
        }

        workbook.close();
        System.out.println("Total data rows parsed: " + dataList.size());
        return dataList;
    }

    /**
     * 查找真正的表头行
     * @param sheet Excel工作表
     * @return 表头行索引
     */
    private static int findHeaderRowIndex(Sheet sheet) {
        int maxNonEmptyCells = 0;
        int headerRowIndex = 0;
        
        // 检查前20行，找到非空单元格最多的行作为表头行
        for (int i = 0; i <= Math.min(20, sheet.getLastRowNum()); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                int nonEmptyCells = 0;
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null && getCellValueAsString(cell) != null && !getCellValueAsString(cell).trim().isEmpty()) {
                        nonEmptyCells++;
                    }
                }
                if (nonEmptyCells > maxNonEmptyCells) {
                    maxNonEmptyCells = nonEmptyCells;
                    headerRowIndex = i;
                }
            }
        }
        
        return headerRowIndex;
    }

    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue();
                } else {
                    double numValue = cell.getNumericCellValue();
                    if (numValue == (long) numValue) {
                        return (long) numValue;
                    }
                    return new BigDecimal(String.valueOf(numValue));
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return null;
            default:
                return null;
        }
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        Object value = getCellValue(cell);
        return value != null ? value.toString() : "";
    }

    public static String getStringValue(Map<String, Object> row, String key) {
        // 直接查找指定的key
        Object value = row.get(key);
        if (value != null) {
            return value.toString().trim();
        }
        
        // 如果找不到，尝试查找包含指定key的表头
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String header = entry.getKey();
            if (header != null && header.contains(key)) {
                return entry.getValue() != null ? entry.getValue().toString().trim() : "空";
            }
        }
        
        // 特殊处理债权人名称字段
        if ("债权人名称".equals(key)) {
            // 尝试查找常见的债权人名称字段变体
            String[] creditorNameVariants = {"债权人", "债权人姓名", "债权人名称"};
            for (String variant : creditorNameVariants) {
                value = row.get(variant);
                if (value != null) {
                    return value.toString().trim();
                }
                // 再次尝试包含变体的表头
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    String header = entry.getKey();
                    if (header != null && header.contains(variant)) {
                        return entry.getValue() != null ? entry.getValue().toString().trim() : "空";
                    }
                }
            }
        }
        
        // 尝试查找key的变体
        String[] keyVariants = getKeyVariants(key);
        for (String variant : keyVariants) {
            value = row.get(variant);
            if (value != null) {
                return value.toString().trim();
            }
            // 再次尝试包含变体的表头
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                String header = entry.getKey();
                if (header != null && header.contains(variant)) {
                    return entry.getValue() != null ? entry.getValue().toString().trim() : "空";
                }
            }
        }
        
        return "空";
    }
    
    /**
     * 获取key的常见变体
     * @param key 原始key
     * @return key的常见变体数组
     */
    private static String[] getKeyVariants(String key) {
        List<String> variants = new ArrayList<>();
        
        // 添加原始key
        variants.add(key);
        
        // 移除"名称"后缀
        if (key.endsWith("名称")) {
            variants.add(key.substring(0, key.length() - 2));
        }
        
        // 移除"人"后缀
        if (key.endsWith("人")) {
            variants.add(key.substring(0, key.length() - 1));
        }
        
        // 常见的字段映射
        Map<String, String[]> commonMappings = new HashMap<>();
        commonMappings.put("债权人名称", new String[]{"债权人", "债权人姓名"});
        commonMappings.put("申报金额", new String[]{"申报金额", "债权金额", "金额"});
        commonMappings.put("申报时间", new String[]{"申报时间", "申报日期", "时间"});
        commonMappings.put("联系电话", new String[]{"联系电话", "电话", "手机号"});
        commonMappings.put("法定代表人", new String[]{"法定代表人", "法人"});
        commonMappings.put("住所/邮编", new String[]{"住所/邮编", "住所", "地址", "邮编"});
        
        // 添加常见映射
        if (commonMappings.containsKey(key)) {
            for (String mapping : commonMappings.get(key)) {
                variants.add(mapping);
            }
        }
        
        return variants.toArray(new String[0]);
    }

    public static BigDecimal getBigDecimalValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value != null) {
            if (value instanceof BigDecimal) {
                return (BigDecimal) value;
            }
            if (value instanceof Number) {
                return new BigDecimal(value.toString());
            }
            try {
                return new BigDecimal(value.toString().trim().replace(",", ""));
            } catch (NumberFormatException e) {
                return new BigDecimal("0");
            }
        }
        
        // 尝试查找包含指定key的表头
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String header = entry.getKey();
            if (header != null && header.contains(key)) {
                value = entry.getValue();
                if (value != null) {
                    if (value instanceof BigDecimal) {
                        return (BigDecimal) value;
                    }
                    if (value instanceof Number) {
                        return new BigDecimal(value.toString());
                    }
                    try {
                        return new BigDecimal(value.toString().trim().replace(",", ""));
                    } catch (NumberFormatException e) {
                        return new BigDecimal("0");
                    }
                }
            }
        }
        
        // 尝试查找key的变体
        String[] keyVariants = getKeyVariants(key);
        for (String variant : keyVariants) {
            value = row.get(variant);
            if (value != null) {
                if (value instanceof BigDecimal) {
                    return (BigDecimal) value;
                }
                if (value instanceof Number) {
                    return new BigDecimal(value.toString());
                }
                try {
                    return new BigDecimal(value.toString().trim());
                } catch (NumberFormatException e) {
                    return new BigDecimal("0");
                }
            }
            // 再次尝试包含变体的表头
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                String header = entry.getKey();
                if (header != null && header.contains(variant)) {
                    value = entry.getValue();
                    if (value != null) {
                        if (value instanceof BigDecimal) {
                            return (BigDecimal) value;
                        }
                        if (value instanceof Number) {
                            return new BigDecimal(value.toString());
                        }
                        try {
                            return new BigDecimal(value.toString().trim().replace(",", ""));
                        } catch (NumberFormatException e) {
                            return new BigDecimal("0");
                        }
                    }
                }
            }
        }
        
        return new BigDecimal("0");
    }

    public static Integer getIntegerValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value != null) {
            if (value instanceof Integer) {
                return (Integer) value;
            }
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            try {
                return Integer.parseInt(value.toString().trim());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        
        // 尝试查找包含指定key的表头
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String header = entry.getKey();
            if (header != null && header.contains(key)) {
                value = entry.getValue();
                if (value != null) {
                    if (value instanceof Integer) {
                        return (Integer) value;
                    }
                    if (value instanceof Number) {
                        return ((Number) value).intValue();
                    }
                    try {
                        return Integer.parseInt(value.toString().trim());
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }
            }
        }
        
        // 尝试查找key的变体
        String[] keyVariants = getKeyVariants(key);
        for (String variant : keyVariants) {
            value = row.get(variant);
            if (value != null) {
                if (value instanceof Integer) {
                    return (Integer) value;
                }
                if (value instanceof Number) {
                    return ((Number) value).intValue();
                }
                try {
                    return Integer.parseInt(value.toString().trim());
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
            // 再次尝试包含变体的表头
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                String header = entry.getKey();
                if (header != null && header.contains(variant)) {
                    value = entry.getValue();
                    if (value != null) {
                        if (value instanceof Integer) {
                            return (Integer) value;
                        }
                        if (value instanceof Number) {
                            return ((Number) value).intValue();
                        }
                        try {
                            return Integer.parseInt(value.toString().trim());
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                }
            }
        }
        
        return 0;
    }

    public static Boolean getBooleanValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value != null) {
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            String strValue = value.toString().trim().toLowerCase();
            if ("是".equals(strValue) || "yes".equals(strValue) || "true".equals(strValue) || "1".equals(strValue)) {
                return true;
            }
            if ("否".equals(strValue) || "no".equals(strValue) || "false".equals(strValue) || "0".equals(strValue)) {
                return false;
            }
        }
        
        // 尝试查找包含指定key的表头
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String header = entry.getKey();
            if (header != null && header.contains(key)) {
                value = entry.getValue();
                if (value != null) {
                    if (value instanceof Boolean) {
                        return (Boolean) value;
                    }
                    String strValue = value.toString().trim().toLowerCase();
                    if ("是".equals(strValue) || "yes".equals(strValue) || "true".equals(strValue) || "1".equals(strValue)) {
                        return true;
                    }
                    if ("否".equals(strValue) || "no".equals(strValue) || "false".equals(strValue) || "0".equals(strValue)) {
                        return false;
                    }
                }
            }
        }
        
        // 尝试查找key的变体
        String[] keyVariants = getKeyVariants(key);
        for (String variant : keyVariants) {
            value = row.get(variant);
            if (value != null) {
                if (value instanceof Boolean) {
                    return (Boolean) value;
                }
                String strValue = value.toString().trim().toLowerCase();
                if ("是".equals(strValue) || "yes".equals(strValue) || "true".equals(strValue) || "1".equals(strValue)) {
                    return true;
                }
                if ("否".equals(strValue) || "no".equals(strValue) || "false".equals(strValue) || "0".equals(strValue)) {
                    return false;
                }
            }
            // 再次尝试包含变体的表头
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                String header = entry.getKey();
                if (header != null && header.contains(variant)) {
                    value = entry.getValue();
                    if (value != null) {
                        if (value instanceof Boolean) {
                            return (Boolean) value;
                        }
                        String strValue = value.toString().trim().toLowerCase();
                        if ("是".equals(strValue) || "yes".equals(strValue) || "true".equals(strValue) || "1".equals(strValue)) {
                            return true;
                        }
                        if ("否".equals(strValue) || "no".equals(strValue) || "false".equals(strValue) || "0".equals(strValue)) {
                            return false;
                        }
                    }
                }
            }
        }
        
        return false;
    }

    public static LocalDateTime getLocalDateTimeValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value != null) {
            if (value instanceof LocalDateTime) {
                return (LocalDateTime) value;
            }
            if (value instanceof String) {
                String strValue = ((String) value).trim();
                try {
                    if (strValue.length() > 10) {
                        return LocalDateTime.parse(strValue, DATE_FORMATTER);
                    } else {
                        return LocalDateTime.parse(strValue + " 00:00:00", DATE_FORMATTER);
                    }
                } catch (DateTimeParseException e) {
                    try {
                        return LocalDateTime.parse(strValue, DATE_FORMATTER_SIMPLE);
                    } catch (DateTimeParseException e2) {
                        // 返回当前时间作为默认值
                        return LocalDateTime.now();
                    }
                }
            }
        }
        
        // 尝试查找包含指定key的表头
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String header = entry.getKey();
            if (header != null && header.contains(key)) {
                value = entry.getValue();
                if (value != null) {
                    if (value instanceof LocalDateTime) {
                        return (LocalDateTime) value;
                    }
                    if (value instanceof String) {
                        String strValue = ((String) value).trim();
                        try {
                            if (strValue.length() > 10) {
                                return LocalDateTime.parse(strValue, DATE_FORMATTER);
                            } else {
                                return LocalDateTime.parse(strValue + " 00:00:00", DATE_FORMATTER);
                            }
                        } catch (DateTimeParseException e) {
                            try {
                                return LocalDateTime.parse(strValue, DATE_FORMATTER_SIMPLE);
                            } catch (DateTimeParseException e2) {
                                // 返回当前时间作为默认值
                                return LocalDateTime.now();
                            }
                        }
                    }
                }
            }
        }
        
        // 尝试查找key的变体
        String[] keyVariants = getKeyVariants(key);
        for (String variant : keyVariants) {
            value = row.get(variant);
            if (value != null) {
                if (value instanceof LocalDateTime) {
                    return (LocalDateTime) value;
                }
                if (value instanceof String) {
                    String strValue = ((String) value).trim();
                    try {
                        if (strValue.length() > 10) {
                            return LocalDateTime.parse(strValue, DATE_FORMATTER);
                        } else {
                            return LocalDateTime.parse(strValue + " 00:00:00", DATE_FORMATTER);
                        }
                    } catch (DateTimeParseException e) {
                        try {
                            return LocalDateTime.parse(strValue, DATE_FORMATTER_SIMPLE);
                        } catch (DateTimeParseException e2) {
                            // 返回当前时间作为默认值
                            return LocalDateTime.now();
                        }
                    }
                }
            }
            // 再次尝试包含变体的表头
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                String header = entry.getKey();
                if (header != null && header.contains(variant)) {
                    value = entry.getValue();
                    if (value != null) {
                        if (value instanceof LocalDateTime) {
                            return (LocalDateTime) value;
                        }
                        if (value instanceof String) {
                            String strValue = ((String) value).trim();
                            try {
                                if (strValue.length() > 10) {
                                    return LocalDateTime.parse(strValue, DATE_FORMATTER);
                                } else {
                                    return LocalDateTime.parse(strValue + " 00:00:00", DATE_FORMATTER);
                                }
                            } catch (DateTimeParseException e) {
                                try {
                                    return LocalDateTime.parse(strValue, DATE_FORMATTER_SIMPLE);
                                } catch (DateTimeParseException e2) {
                                    // 返回当前时间作为默认值
                                    return LocalDateTime.now();
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // 返回当前时间作为默认值
        return LocalDateTime.now();
    }
}

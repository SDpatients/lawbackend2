package com.lawbackend2.lawbackend2.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
public class BatchExportRequest {

    @NotNull(message = "Template ID is required")
    private Long templateId;

    @NotBlank(message = "File name is required")
    private String fileName;

    @NotNull(message = "Data list is required")
    private List<Map<String, Object>> dataList;

    private ExportOptions options;

    @Data
    public static class ExportOptions {
        private Boolean mergeCells = false;
        private Boolean addIndex = true;
        private String sheetName;
        private Integer startRow = 2;
        private Integer headerRow = 1;
        private Boolean shiftRows = false;  // 是否自动移动下方的行
    }
}

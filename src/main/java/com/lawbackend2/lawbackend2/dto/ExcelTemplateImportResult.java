package com.lawbackend2.lawbackend2.dto;

import lombok.Data;
import java.util.List;

@Data
public class ExcelTemplateImportResult {

    private Integer successCount;
    private Integer failCount;
    private Integer totalCount;
    private String message;
    private List<ImportError> errors;
    private String templateCode;
    private Long caseId;

    @Data
    public static class ImportError {
        private Integer row;
        private String message;
        private String data;

        public ImportError(Integer row, String message, String data) {
            this.row = row;
            this.message = message;
            this.data = data;
        }
    }
}

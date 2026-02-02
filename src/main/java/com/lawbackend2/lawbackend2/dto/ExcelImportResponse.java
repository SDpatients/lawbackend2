package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExcelImportResponse {
    private Integer successCount;
    private Integer failCount;
    private String message;
    private java.util.List<ImportError> errors;

    @Data
    public static class ImportError {
        private Integer row;
        private String message;
        private String creditorName;

        public ImportError(Integer row, String message, String creditorName) {
            this.row = row;
            this.message = message;
            this.creditorName = creditorName;
        }
    }
}

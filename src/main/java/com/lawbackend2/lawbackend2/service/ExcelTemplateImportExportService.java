package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.ExcelTemplateImportResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface ExcelTemplateImportExportService {

    ExcelTemplateImportResult importFromExcel(MultipartFile file, String templateCode, Long caseId, Integer sheetIndex, Long userId);

    void exportToExcel(HttpServletResponse response, String templateCode, Long caseId, String registrationStatus);
}

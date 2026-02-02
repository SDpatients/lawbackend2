package com.lawbackend2.lawbackend2.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ExcelParseService {
    
    Map<String, Object> parseExcel(MultipartFile file);
    
    Map<String, Object> parseExcelWithSheet(MultipartFile file, Integer sheetIndex);
    
    Map<String, Object> parseExcelWithSheet(MultipartFile file, Integer sheetIndex, String templateCode);
}

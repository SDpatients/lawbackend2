package com.lawbackend2.lawbackend2.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import org.apache.poi.ss.usermodel.*;
import com.lawbackend2.lawbackend2.dto.*;
import com.lawbackend2.lawbackend2.entity.ClaimRegistration;
import com.lawbackend2.lawbackend2.entity.CreditorInfo;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.service.ClaimRegistrationService;
import com.lawbackend2.lawbackend2.service.CreditorInfoService;
import com.lawbackend2.lawbackend2.service.ExcelTemplateImportExportService;
import com.lawbackend2.lawbackend2.service.ExcelTemplateService;
import com.lawbackend2.lawbackend2.repository.ClaimRegistrationRepository;
import com.lawbackend2.lawbackend2.repository.CreditorInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class ExcelTemplateImportExportServiceImpl implements ExcelTemplateImportExportService {

    private final ExcelTemplateService excelTemplateService;
    private final ClaimRegistrationService claimRegistrationService;
    private final ClaimRegistrationRepository claimRegistrationRepository;
    private final CreditorInfoService creditorInfoService;
    private final CreditorInfoRepository creditorInfoRepository;

    public ExcelTemplateImportExportServiceImpl(ExcelTemplateService excelTemplateService,
                                                ClaimRegistrationService claimRegistrationService,
                                                ClaimRegistrationRepository claimRegistrationRepository,
                                                CreditorInfoService creditorInfoService,
                                                CreditorInfoRepository creditorInfoRepository) {
        this.excelTemplateService = excelTemplateService;
        this.claimRegistrationService = claimRegistrationService;
        this.claimRegistrationRepository = claimRegistrationRepository;
        this.creditorInfoService = creditorInfoService;
        this.creditorInfoRepository = creditorInfoRepository;
    }

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

        // 1. 获取模板配置
        Map<String, String> fieldMappings = excelTemplateService.getFieldMappings(templateCode);
        if (fieldMappings == null || fieldMappings.isEmpty()) {
            result.setMessage("未找到模板配置或字段映射为空，模板编码: " + templateCode);
            log.error("未找到模板配置或字段映射为空，模板编码: {}", templateCode);
            return result;
        }
        log.info("加载模板字段映射成功，共{}个字段映射", fieldMappings.size());

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
            log.info("最佳表头行: {}", bestHeader);

            // 4. 构建列索引到字段名的映射
            Map<Integer, String> columnToFieldMap = new HashMap<>();
            for (Map.Entry<Integer, String> entry : bestHeader.entrySet()) {
                Integer colIndex = entry.getKey();
                String headerValue = entry.getValue();
                if (headerValue != null && !headerValue.trim().isEmpty()) {
                    String fieldName = matchField(headerValue, fieldMappings);
                    if (fieldName != null) {
                        columnToFieldMap.put(colIndex, fieldName);
                        log.debug("字段映射: 列{} -> '{}' -> '{}'", colIndex, headerValue, fieldName);
                    }
                }
            }

            if (columnToFieldMap.isEmpty()) {
                result.setMessage("未能匹配任何字段，请检查Excel表头与模板配置是否匹配");
                return result;
            }
            log.info("成功匹配{}个字段", columnToFieldMap.size());

            // 5. 根据模板编码判断导入类型
            boolean isCreditorInfo = templateCode != null && templateCode.toLowerCase().contains("creditor_info");

            // 6. 逐行处理数据
            result.setTotalCount(dataRows.size());
            int rowNum = 2; // 从第2行开始（第0行可能是表头，第1行开始是数据）

            for (Map<Integer, String> dataRow : dataRows) {
                try {
                    // 检查是否为空行
                    if (countNonEmptyCells(dataRow) == 0) {
                        rowNum++;
                        continue;
                    }

                    if (isCreditorInfo) {
                        // 导入债权人信息
                        CreditorCreateRequest request = buildCreditorCreateRequest(dataRow, columnToFieldMap, caseId);

                        // 验证必填字段
                        if (request.getCreditorName() == null || request.getCreditorName().trim().isEmpty()) {
                            result.setFailCount(result.getFailCount() + 1);
                            result.getErrors().add(new ExcelTemplateImportResult.ImportError(rowNum, "债权人名称为空", dataRow.toString()));
                            rowNum++;
                            continue;
                        }

                        // 创建债权人信息
                        creditorInfoService.createCreditor(request, userId);
                        result.setSuccessCount(result.getSuccessCount() + 1);
                        log.debug("第{}行债权人信息导入成功: {}", rowNum, request.getCreditorName());
                    } else {
                        // 导入债权申报
                        ClaimRegistrationCreateRequest request = buildCreateRequest(dataRow, columnToFieldMap, caseId);

                        // 验证必填字段
                        if (request.getCreditorName() == null || request.getCreditorName().trim().isEmpty()) {
                            result.setFailCount(result.getFailCount() + 1);
                            result.getErrors().add(new ExcelTemplateImportResult.ImportError(rowNum, "债权人名称为空", dataRow.toString()));
                            rowNum++;
                            continue;
                        }

                        // 创建债权申报
                        claimRegistrationService.createClaim(request, userId);
                        result.setSuccessCount(result.getSuccessCount() + 1);
                        log.debug("第{}行债权申报导入成功: {}", rowNum, request.getCreditorName());
                    }

                } catch (Exception e) {
                    result.setFailCount(result.getFailCount() + 1);
                    String creditorName = getValueFromRowByField(dataRow, columnToFieldMap, "creditorName");
                    result.getErrors().add(new ExcelTemplateImportResult.ImportError(rowNum, e.getMessage(), creditorName));
                    log.error("第{}行导入失败: {}", rowNum, e.getMessage());
                }
                rowNum++;
            }

            String importType = isCreditorInfo ? "债权人信息" : "债权申报";
            result.setMessage(String.format("%s导入完成，成功%d条，失败%d条，总计%d条",
                    importType, result.getSuccessCount(), result.getFailCount(), result.getTotalCount()));
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
            // 1. 获取模板配置
            Map<String, String> fieldMappings = excelTemplateService.getFieldMappings(templateCode);
            if (fieldMappings == null || fieldMappings.isEmpty()) {
                throw new BusinessException("未找到模板配置或字段映射为空，模板编码: " + templateCode);
            }

            // 2. 根据模板编码判断导出类型
            boolean isCreditorInfo = templateCode != null && templateCode.toLowerCase().contains("creditor_info");

            // 3. 设置响应头
            String fileName;
            String sheetName;
            if (isCreditorInfo) {
                fileName = "债权人信息导出_" + templateCode + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                sheetName = "债权人信息";
            } else {
                fileName = "债权申报导出_" + templateCode + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                sheetName = "债权申报数据";
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");

            // 4. 查询数据并准备导出
            List<List<Object>> data = new ArrayList<>();

            if (isCreditorInfo) {
                // 导出债权人信息
                List<CreditorInfo> creditorList = creditorInfoService.getCreditorList(1, 10000, caseId, null, null, null, null, null);
                log.info("查询到{}条债权人信息记录", creditorList.size());

                for (CreditorInfo creditor : creditorList) {
                    List<Object> rowData = new ArrayList<>();
                    for (String headerName : fieldMappings.keySet()) {
                        String fieldName = fieldMappings.get(headerName);
                        Object value = getCreditorFieldValue(creditor, fieldName);
                        rowData.add(value != null ? value : "");
                    }
                    data.add(rowData);
                }
            } else {
                // 导出债权申报
                List<ClaimRegistration> claimList = claimRegistrationService.getClaimList(1, 10000, caseId, registrationStatus);
                log.info("查询到{}条债权申报记录", claimList.size());

                for (ClaimRegistration claim : claimList) {
                    List<Object> rowData = new ArrayList<>();
                    for (String headerName : fieldMappings.keySet()) {
                        String fieldName = fieldMappings.get(headerName);
                        Object value = getClaimFieldValue(claim, fieldName);
                        rowData.add(value != null ? value : "");
                    }
                    data.add(rowData);
                }
            }

            // 5. 写入Excel - 使用动态表头
            List<List<String>> headers = new ArrayList<>();
            for (String headerName : fieldMappings.keySet()) {
                List<String> head = new ArrayList<>();
                head.add(headerName);
                headers.add(head);
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
                    .sheet(sheetName)
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

    private ClaimRegistrationCreateRequest buildCreateRequest(Map<Integer, String> dataRow, Map<Integer, String> columnToFieldMap, Long caseId) {
        ClaimRegistrationCreateRequest request = new ClaimRegistrationCreateRequest();
        request.setCaseId(caseId);

        // 遍历列映射，设置对应的字段值
        for (Map.Entry<Integer, String> entry : columnToFieldMap.entrySet()) {
            Integer colIndex = entry.getKey();
            String fieldName = entry.getValue();
            String value = dataRow.get(colIndex);

            if (value != null) {
                value = value.trim();
                setFieldValue(request, fieldName, value);
            }
        }

        // 设置默认值
        if (request.getCreditorType() == null || request.getCreditorType().isEmpty()) {
            request.setCreditorType("企业");
        }
        if (request.getClaimType() == null || request.getClaimType().isEmpty()) {
            request.setClaimType("普通债权");
        }
        if (request.getTotalAmount() == null) {
            request.setTotalAmount(BigDecimal.ZERO);
        }

        return request;
    }

    private CreditorCreateRequest buildCreditorCreateRequest(Map<Integer, String> dataRow, Map<Integer, String> columnToFieldMap, Long caseId) {
        CreditorCreateRequest request = new CreditorCreateRequest();
        request.setCaseId(caseId);

        // 遍历列映射，设置对应的字段值
        for (Map.Entry<Integer, String> entry : columnToFieldMap.entrySet()) {
            Integer colIndex = entry.getKey();
            String fieldName = entry.getValue();
            String value = dataRow.get(colIndex);

            if (value != null) {
                value = value.trim();
                setCreditorFieldValue(request, fieldName, value);
            }
        }

        // 设置默认值
        if (request.getCreditorType() == null || request.getCreditorType().isEmpty()) {
            request.setCreditorType("企业");
        }

        return request;
    }

    private void setCreditorFieldValue(CreditorCreateRequest request, String fieldName, String value) {
        if (fieldName == null || value == null) return;

        switch (fieldName) {
            case "creditorName":
            case "债权人名称":
            case "债权人":
                request.setCreditorName(value);
                break;
            case "creditorType":
            case "债权人类型":
            case "性质":
                request.setCreditorType(value);
                break;
            case "contactPhone":
            case "联系电话":
                request.setContactPhone(value);
                break;
            case "contactEmail":
            case "联系邮箱":
            case "邮箱":
                request.setContactEmail(value);
                break;
            case "address":
            case "地址":
            case "送达地址":
                request.setAddress(value);
                break;
            case "idNumber":
            case "身份证号":
            case "统一社会信用代码":
            case "身份证号/统一社会信用代码":
                request.setIdNumber(value);
                break;
            case "legalRepresentative":
            case "法定代表人":
                request.setLegalRepresentative(value);
                break;
            case "registeredCapital":
            case "注册资本":
                try {
                    request.setRegisteredCapital(parseBigDecimal(value));
                } catch (Exception e) {
                    request.setRegisteredCapital(BigDecimal.ZERO);
                }
                break;
        }
    }

    private void setFieldValue(ClaimRegistrationCreateRequest request, String fieldName, String value) {
        if (fieldName == null || value == null) return;

        switch (fieldName) {
            case "creditorName":
            case "债权人名称":
            case "债权人":
                request.setCreditorName(value);
                break;
            case "creditorType":
            case "债权人类型":
            case "性质":
                request.setCreditorType(value);
                break;
            case "creditCode":
            case "统一社会信用代码":
                request.setCreditCode(value);
                break;
            case "legalRepresentative":
            case "法定代表人":
                request.setLegalRepresentative(value);
                break;
            case "serviceAddress":
            case "送达地址":
            case "地址":
                request.setServiceAddress(value);
                break;
            case "agentName":
            case "代理人姓名":
            case "代理人":
                request.setAgentName(value);
                break;
            case "agentPhone":
            case "代理人电话":
            case "联系电话":
                request.setAgentPhone(value);
                break;
            case "agentIdCard":
            case "代理人身份证":
                request.setAgentIdCard(value);
                break;
            case "agentAddress":
            case "代理人地址":
                request.setAgentAddress(value);
                break;
            case "accountName":
            case "账户名称":
            case "开户名":
                request.setAccountName(value);
                break;
            case "creditorBankAccount":
            case "债权人银行账号":
            case "银行账号":
            case "账号":
                request.setCreditorBankAccount(value);
                break;
            case "bankName":
            case "开户行":
            case "开户银行":
                request.setBankName(value);
                break;
            case "principal":
            case "本金":
                try {
                    request.setPrincipal(parseBigDecimal(value));
                } catch (Exception e) {
                    request.setPrincipal(BigDecimal.ZERO);
                }
                break;
            case "interest":
            case "利息":
                try {
                    request.setInterest(parseBigDecimal(value));
                } catch (Exception e) {
                    request.setInterest(BigDecimal.ZERO);
                }
                break;
            case "penalty":
            case "违约金":
            case "罚息":
                try {
                    request.setPenalty(parseBigDecimal(value));
                } catch (Exception e) {
                    request.setPenalty(BigDecimal.ZERO);
                }
                break;
            case "otherLosses":
            case "其他损失":
            case "其他费用":
                try {
                    request.setOtherLosses(parseBigDecimal(value));
                } catch (Exception e) {
                    request.setOtherLosses(BigDecimal.ZERO);
                }
                break;
            case "totalAmount":
            case "总金额":
            case "申报金额":
            case "债权金额":
                try {
                    request.setTotalAmount(parseBigDecimal(value));
                } catch (Exception e) {
                    request.setTotalAmount(BigDecimal.ZERO);
                }
                break;
            case "claimNature":
            case "债权性质":
                request.setClaimNature(value);
                break;
            case "claimType":
            case "债权类型":
            case "债权种类":
                request.setClaimType(value);
                break;
            case "claimFacts":
            case "债权事实":
                request.setClaimFacts(value);
                break;
            case "claimIdentifier":
            case "债权标识":
                request.setClaimIdentifier(value);
                break;
            case "evidenceList":
            case "证据清单":
                request.setEvidenceList(value);
                break;
            case "remarks":
            case "备注":
                request.setRemarks(value);
                break;
            case "hasCourtJudgment":
            case "是否有法院判决":
            case "涉讼":
                request.setHasCourtJudgment(parseBoolean(value) ? 1 : 0);
                break;
            case "hasExecution":
            case "是否有执行":
                request.setHasExecution(parseBoolean(value) ? 1 : 0);
                break;
            case "hasCollateral":
            case "是否有担保":
                request.setHasCollateral(parseBoolean(value) ? 1 : 0);
                break;
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        // 移除千分位分隔符和货币符号
        String cleanValue = value.replaceAll(",", "")
                .replaceAll("￥", "")
                .replaceAll("\\$", "")
                .trim();
        return new BigDecimal(cleanValue);
    }

    private boolean parseBoolean(String value) {
        if (value == null) return false;
        String lower = value.trim().toLowerCase();
        return lower.equals("是") || lower.equals("yes") || lower.equals("true") || lower.equals("1") || lower.equals("有");
    }

    private String getValueFromRowByField(Map<Integer, String> dataRow, Map<Integer, String> columnToFieldMap, String fieldName) {
        for (Map.Entry<Integer, String> entry : columnToFieldMap.entrySet()) {
            if (fieldName.equals(entry.getValue())) {
                return dataRow.get(entry.getKey());
            }
        }
        return null;
    }

    private Map<String, Object> convertClaimToMap(ClaimRegistration claim, Map<String, String> fieldMappings) {
        Map<String, Object> map = new HashMap<>();

        for (String fieldName : fieldMappings.values()) {
            Object value = getClaimFieldValue(claim, fieldName);
            map.put(fieldName, value);
        }

        return map;
    }

    private Object getClaimFieldValue(ClaimRegistration claim, String fieldName) {
        if (fieldName == null) return null;

        switch (fieldName) {
            case "creditorName":
            case "债权人名称":
            case "债权人":
                return claim.getCreditorName();
            case "creditorType":
            case "债权人类型":
            case "性质":
                return claim.getCreditorType();
            case "creditCode":
            case "统一社会信用代码":
                return claim.getCreditCode();
            case "legalRepresentative":
            case "法定代表人":
                return claim.getLegalRepresentative();
            case "serviceAddress":
            case "送达地址":
            case "地址":
                return claim.getServiceAddress();
            case "agentName":
            case "代理人姓名":
            case "代理人":
                return claim.getAgentName();
            case "agentPhone":
            case "代理人电话":
            case "联系电话":
                return claim.getAgentPhone();
            case "agentIdCard":
            case "代理人身份证":
                return claim.getAgentIdCard();
            case "agentAddress":
            case "代理人地址":
                return claim.getAgentAddress();
            case "accountName":
            case "账户名称":
            case "开户名":
                return claim.getAccountName();
            case "creditorBankAccount":
            case "债权人银行账号":
            case "银行账号":
            case "账号":
                return claim.getCreditorBankAccount();
            case "bankName":
            case "开户行":
            case "开户银行":
                return claim.getBankName();
            case "principal":
            case "本金":
                return claim.getPrincipal();
            case "interest":
            case "利息":
                return claim.getInterest();
            case "penalty":
            case "违约金":
            case "罚息":
                return claim.getPenalty();
            case "otherLosses":
            case "其他损失":
            case "其他费用":
                return claim.getOtherLosses();
            case "totalAmount":
            case "总金额":
            case "申报金额":
            case "债权金额":
                return claim.getTotalAmount();
            case "claimNature":
            case "债权性质":
                return claim.getClaimNature();
            case "claimType":
            case "债权类型":
            case "债权种类":
                return claim.getClaimType();
            case "claimFacts":
            case "债权事实":
                return claim.getClaimFacts();
            case "claimIdentifier":
            case "债权标识":
                return claim.getClaimIdentifier();
            case "evidenceList":
            case "证据清单":
                return claim.getEvidenceList();
            case "remarks":
            case "备注":
                return claim.getRemarks();
            case "hasCourtJudgment":
            case "是否有法院判决":
            case "涉讼":
                return claim.getHasCourtJudgment() != null && claim.getHasCourtJudgment() ? "是" : "否";
            case "hasExecution":
            case "是否有执行":
                return claim.getHasExecution() != null && claim.getHasExecution() ? "是" : "否";
            case "hasCollateral":
            case "是否有担保":
                return claim.getHasCollateral() != null && claim.getHasCollateral() ? "是" : "否";
            case "claimNo":
            case "债权编号":
                return claim.getClaimNo();
            case "registrationStatus":
            case "登记状态":
                return claim.getRegistrationStatus();
            case "createTime":
            case "创建时间":
            case "申报时间":
                return claim.getCreateTime() != null ? claim.getCreateTime().toString() : null;
            default:
                return null;
        }
    }

    private Object getCreditorFieldValue(CreditorInfo creditor, String fieldName) {
        if (fieldName == null || creditor == null) return null;

        switch (fieldName) {
            case "caseId":
            case "案件ID":
                return creditor.getCaseId();
            case "creditorName":
            case "债权人名称":
            case "债权人":
                return creditor.getCreditorName();
            case "creditorType":
            case "债权人类型":
            case "性质":
                return creditor.getCreditorType();
            case "contactPhone":
            case "联系电话":
                return creditor.getContactPhone();
            case "contactEmail":
            case "联系邮箱":
            case "邮箱":
                return creditor.getContactEmail();
            case "address":
            case "地址":
            case "送达地址":
                return creditor.getAddress();
            case "idNumber":
            case "身份证号":
            case "统一社会信用代码":
            case "身份证号/统一社会信用代码":
                return creditor.getIdNumber();
            case "legalRepresentative":
            case "法定代表人":
                return creditor.getLegalRepresentative();
            case "registeredCapital":
            case "注册资本":
                return creditor.getRegisteredCapital();
            case "creditorStatus":
            case "债权人状态":
            case "状态":
                return creditor.getCreditorStatus() != null ? creditor.getCreditorStatus().getDescription() : null;
            case "createTime":
            case "创建时间":
                return creditor.getCreateTime() != null ? creditor.getCreateTime().toString() : null;
            default:
                return null;
        }
    }
}

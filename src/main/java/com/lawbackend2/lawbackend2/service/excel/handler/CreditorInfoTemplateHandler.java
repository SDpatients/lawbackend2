package com.lawbackend2.lawbackend2.service.excel.handler;

import com.lawbackend2.lawbackend2.dto.CreditorCreateRequest;
import com.lawbackend2.lawbackend2.entity.CreditorInfo;
import com.lawbackend2.lawbackend2.enums.CreditorStatus;
import com.lawbackend2.lawbackend2.service.CreditorInfoService;
import com.lawbackend2.lawbackend2.service.excel.AbstractTemplateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 债权人信息模板处理器
 */
@Slf4j
@Component
public class CreditorInfoTemplateHandler extends AbstractTemplateHandler {

    @Autowired
    private CreditorInfoService creditorInfoService;

    @Override
    public String getTemplateCode() {
        return "creditor_info";
    }

    @Override
    public String getTemplateName() {
        return "债权人信息模板";
    }

    @Override
    public Map<String, String> getDefaultFieldMappings() {
        Map<String, String> mappings = new LinkedHashMap<>();
        mappings.put("案件ID", "caseId");
        mappings.put("债权人名称", "creditorName");
        mappings.put("债权人类型", "creditorType");
        mappings.put("联系电话", "contactPhone");
        mappings.put("联系邮箱", "contactEmail");
        mappings.put("地址", "address");
        mappings.put("身份证号/统一社会信用代码", "idNumber");
        mappings.put("法定代表人", "legalRepresentative");
        mappings.put("注册资本", "registeredCapital");
        mappings.put("债权人状态", "creditorStatus");
        return mappings;
    }

    @Override
    public List<Map<String, Object>> exportData(Long caseId, String status) {
        List<CreditorInfo> creditors = creditorInfoService.getCreditorList(1, 10000, caseId, null, null, null, null, null);
        List<Map<String, Object>> result = new ArrayList<>();

        for (CreditorInfo creditor : creditors) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("caseId", creditor.getCaseId());
            row.put("creditorName", creditor.getCreditorName());
            row.put("creditorType", creditor.getCreditorType());
            row.put("contactPhone", creditor.getContactPhone());
            row.put("contactEmail", creditor.getContactEmail());
            row.put("address", creditor.getAddress());
            row.put("idNumber", creditor.getIdNumber());
            row.put("legalRepresentative", creditor.getLegalRepresentative());
            row.put("registeredCapital", creditor.getRegisteredCapital());
            row.put("creditorStatus", creditor.getCreditorStatus() != null ? creditor.getCreditorStatus().getDescription() : null);
            result.add(row);
        }

        return result;
    }

    @Override
    public String getExportFileName() {
        return "债权人信息导出";
    }

    @Override
    public String getExportSheetName() {
        return "债权人信息";
    }

    @Override
    public String validateRow(Map<String, String> row) {
        String creditorName = getStringValue(row, "creditorName", "债权人名称", "债权人");
        if (creditorName == null || creditorName.trim().isEmpty()) {
            return "债权人名称不能为空";
        }
        return null;
    }

    @Override
    protected void saveRow(Map<String, String> row, Long caseId, Long userId) {
        CreditorCreateRequest request = new CreditorCreateRequest();
        
        // 优先使用Excel中的caseId，如果没有则使用传入的caseId
        Long rowCaseId = getLongValue(row, "caseId", "案件ID");
        request.setCaseId(rowCaseId != null ? rowCaseId : caseId);
        
        request.setCreditorName(getStringValue(row, "creditorName", "债权人名称", "债权人"));
        request.setCreditorType(getStringValue(row, "creditorType", "债权人类型", "性质"));
        request.setContactPhone(getStringValue(row, "contactPhone", "联系电话"));
        request.setContactEmail(getStringValue(row, "contactEmail", "联系邮箱", "邮箱"));
        request.setAddress(getStringValue(row, "address", "地址", "送达地址"));
        request.setIdNumber(getStringValue(row, "idNumber", "身份证号", "统一社会信用代码", "身份证号/统一社会信用代码"));
        request.setLegalRepresentative(getStringValue(row, "legalRepresentative", "法定代表人"));
        request.setRegisteredCapital(getBigDecimalValue(row, "registeredCapital", "注册资本"));
        
        // 解析状态
        String statusStr = getStringValue(row, "creditorStatus", "债权人状态", "状态");
        if (statusStr != null) {
            try {
                request.setCreditorStatus(CreditorStatus.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("无法解析债权人状态: {}", statusStr);
            }
        }

        // 设置默认值
        if (request.getCreditorType() == null) {
            request.setCreditorType("企业");
        }

        creditorInfoService.createCreditor(request, userId);
    }
}

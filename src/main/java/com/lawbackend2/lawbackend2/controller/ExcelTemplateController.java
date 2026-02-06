package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.dto.*;
import com.lawbackend2.lawbackend2.dto.request.SystemFieldCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.SystemFieldUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.SystemFieldGroupResponse;
import com.lawbackend2.lawbackend2.entity.ExcelImportTemplate;
import com.lawbackend2.lawbackend2.service.ExcelTemplateImportExportService;
import com.lawbackend2.lawbackend2.service.ExcelTemplateService;
import com.lawbackend2.lawbackend2.service.SystemFieldService;
import com.lawbackend2.lawbackend2.service.excel.TemplateHandler;
import com.lawbackend2.lawbackend2.service.excel.TemplateHandlerRegistry;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "Excel导入模板管理")
@RestController
@RequestMapping("/excel-templates")
@Validated
public class ExcelTemplateController {

    private final ExcelTemplateService excelTemplateService;
    private final ExcelTemplateImportExportService excelTemplateImportExportService;
    private final TemplateHandlerRegistry handlerRegistry;
    private final SystemFieldService systemFieldService;

    @Autowired
    public ExcelTemplateController(ExcelTemplateService excelTemplateService,
                                   ExcelTemplateImportExportService excelTemplateImportExportService,
                                   TemplateHandlerRegistry handlerRegistry,
                                   SystemFieldService systemFieldService) {
        this.excelTemplateService = excelTemplateService;
        this.excelTemplateImportExportService = excelTemplateImportExportService;
        this.handlerRegistry = handlerRegistry;
        this.systemFieldService = systemFieldService;
    }
    
    @Operation(summary = "创建Excel导入模板")
    @PostMapping
    public Result<ExcelImportTemplate> createTemplate(@Valid @RequestBody ExcelTemplateCreateRequest request) {
        Long userId = getCurrentUserId();
        ExcelImportTemplate template = excelTemplateService.createTemplate(request, userId);
        return Result.success(template);
    }
    
    @Operation(summary = "更新Excel导入模板")
    @PutMapping("/{id}")
    public Result<ExcelImportTemplate> updateTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id,
            @Valid @RequestBody ExcelTemplateUpdateRequest request) {
        Long userId = getCurrentUserId();
        ExcelImportTemplate template = excelTemplateService.updateTemplate(id, request, userId);
        return Result.success(template);
    }
    
    @Operation(summary = "删除Excel导入模板")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTemplate(@Parameter(description = "模板ID") @PathVariable Long id) {
        Long userId = getCurrentUserId();
        excelTemplateService.deleteTemplate(id, userId);
        return Result.success();
    }
    
    @Operation(summary = "获取所有Excel导入模板")
    @GetMapping
    public Result<List<ExcelImportTemplate>> getAllTemplates() {
        List<ExcelImportTemplate> templates = excelTemplateService.getAllTemplates();
        return Result.success(templates);
    }
    
    @Operation(summary = "获取默认Excel导入模板")
    @GetMapping("/default")
    public Result<ExcelImportTemplate> getDefaultTemplate() {
        ExcelImportTemplate template = excelTemplateService.getDefaultTemplate();
        return Result.success(template);
    }
    
    @Operation(summary = "设置默认Excel导入模板")
    @PostMapping("/{id}/set-default")
    public Result<Void> setDefaultTemplate(@Parameter(description = "模板ID") @PathVariable Long id) {
        Long userId = getCurrentUserId();
        excelTemplateService.setDefaultTemplate(id, userId);
        return Result.success();
    }
    
    @Operation(summary = "获取模板字段映射")
    @GetMapping("/{code}/mappings")
    public Result<java.util.Map<String, String>> getTemplateMappings(
            @Parameter(description = "模板编码") @PathVariable String code) {
        java.util.Map<String, String> mappings = excelTemplateService.getFieldMappings(code);
        return Result.success(mappings);
    }

    @Operation(summary = "下载Excel导入模板")
    @GetMapping("/template")
    public void downloadTemplate(
            @Parameter(description = "模板编码") @RequestParam(value = "templateCode", required = false) String templateCode,
            HttpServletResponse response) throws IOException {
        log.info("下载Excel模板 - 模板编码: {}", templateCode);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        // 根据模板编码确定文件名和字段映射
        String fileName;
        String sheetName;
        Map<String, String> fieldMappings;

        // 优先从处理器获取配置
        TemplateHandler handler = handlerRegistry.getHandler(templateCode);
        if (handler != null) {
            fileName = handler.getTemplateName() + "导入模板";
            sheetName = handler.getExportSheetName();
            fieldMappings = handler.getDefaultFieldMappings();
        } else if (templateCode != null && templateCode.toLowerCase().contains("creditor_info")) {
            // 兼容旧逻辑
            fileName = "债权人信息导入模板";
            sheetName = "债权人信息";
            fieldMappings = new java.util.LinkedHashMap<>();
            fieldMappings.put("案件ID", "caseId");
            fieldMappings.put("债权人名称", "creditorName");
            fieldMappings.put("债权人类型", "creditorType");
            fieldMappings.put("联系电话", "contactPhone");
            fieldMappings.put("联系邮箱", "contactEmail");
            fieldMappings.put("地址", "address");
            fieldMappings.put("身份证号/统一社会信用代码", "idNumber");
            fieldMappings.put("法定代表人", "legalRepresentative");
            fieldMappings.put("注册资本", "registeredCapital");
            fieldMappings.put("债权人状态", "creditorStatus");
        } else {
            // 默认使用债权申报模板
            fileName = "债权登记导入模板";
            sheetName = "债权登记";
            fieldMappings = new java.util.LinkedHashMap<>();
            fieldMappings.put("案件名称", "caseName");
            fieldMappings.put("债务人", "debtor");
            fieldMappings.put("债权人名称", "creditorName");
            fieldMappings.put("债权人类型", "creditorType");
            fieldMappings.put("统一社会信用代码", "creditCode");
            fieldMappings.put("法定代表人", "legalRepresentative");
            fieldMappings.put("送达地址", "serviceAddress");
            fieldMappings.put("代理人姓名", "agentName");
            fieldMappings.put("代理人电话", "agentPhone");
            fieldMappings.put("代理人身份证", "agentIdCard");
            fieldMappings.put("代理人地址", "agentAddress");
            fieldMappings.put("账户名称", "accountName");
            fieldMappings.put("债权人银行账号", "creditorBankAccount");
            fieldMappings.put("开户行", "bankName");
            fieldMappings.put("本金", "principal");
            fieldMappings.put("利息", "interest");
            fieldMappings.put("违约金", "penalty");
            fieldMappings.put("其他损失", "otherLosses");
            fieldMappings.put("总金额", "totalAmount");
            fieldMappings.put("是否有法院判决", "hasCourtJudgment");
            fieldMappings.put("是否有执行", "hasExecution");
            fieldMappings.put("是否有担保", "hasCollateral");
            fieldMappings.put("债权性质", "claimNature");
            fieldMappings.put("债权类型", "claimType");
            fieldMappings.put("债权事实", "claimFacts");
            fieldMappings.put("债权标识", "claimIdentifier");
            fieldMappings.put("证据清单", "evidenceList");
            fieldMappings.put("备注", "remarks");
        }

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");

        // 构建字段注解映射
        Map<String, String> fieldAnnotations = new java.util.HashMap<>();
        // 为债权人状态添加注解
        if (templateCode != null && templateCode.toLowerCase().contains("creditor_info")) {
            fieldAnnotations.put("债权人状态", "只能为：KNOWN（已知债权人）或 CONFIRMED（确认债权人）");
        }
        // 为其他需要注解的字段添加说明
        fieldAnnotations.put("债权人类型", "例如：企业、个人");
        fieldAnnotations.put("是否有法院判决", "是/否 或 1/0");
        fieldAnnotations.put("是否有执行", "是/否 或 1/0");
        fieldAnnotations.put("是否有担保", "是/否 或 1/0");

        // 构建动态表头（包含注解）
        List<List<String>> headers = new ArrayList<>();
        for (String headerName : fieldMappings.keySet()) {
            List<String> head = new ArrayList<>();
            head.add(headerName);
            // 添加注解作为第二行表头
            String annotation = fieldAnnotations.get(headerName);
            if (annotation != null) {
                head.add(annotation);
            } else {
                head.add("");
            }
            headers.add(head);
        }

        // 构建列宽映射
        Map<String, Integer> columnWidths = new java.util.HashMap<>();
        // 为不同类型的字段设置合适的列宽
        for (String headerName : fieldMappings.keySet()) {
            if (headerName.contains("ID") || headerName.contains("状态") || 
                headerName.contains("是否") || headerName.contains("类型")) {
                // 短字段
                columnWidths.put(headerName, 20);
            } else if (headerName.contains("名称") || headerName.contains("电话") || 
                       headerName.contains("邮箱") || headerName.contains("代表人")) {
                // 中等长度字段
                columnWidths.put(headerName, 25);
            } else if (headerName.contains("地址") || headerName.contains("事实") || 
                       headerName.contains("清单") || headerName.contains("备注")) {
                // 长字段
                columnWidths.put(headerName, 40);
            } else if (headerName.contains("身份证号") || headerName.contains("统一社会信用代码")) {
                // 固定长度字段
                columnWidths.put(headerName, 30);
            } else if (headerName.contains("银行账号") || headerName.contains("账号")) {
                // 银行账号
                columnWidths.put(headerName, 35);
            } else if (headerName.contains("金额") || headerName.contains("本金") || 
                       headerName.contains("利息") || headerName.contains("违约金") || 
                       headerName.contains("其他损失")) {
                // 金额字段
                columnWidths.put(headerName, 20);
            } else {
                // 默认宽度
                columnWidths.put(headerName, 25);
            }
        }

        // 写入Excel
        com.alibaba.excel.EasyExcel.write(response.getOutputStream())
                .head(headers)
                .sheet(sheetName)
                .registerWriteHandler(new com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy())
                .doWrite(new ArrayList<>());

        log.info("下载Excel模板成功 - 模板编码: {}, 文件名: {}", templateCode, fileName);
    }

    @Operation(summary = "基于模板导入Excel数据")
    @PostMapping("/import")
    public Result<ExcelTemplateImportResult> importFromExcel(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "模板编码") @RequestParam("templateCode") String templateCode,
            @Parameter(description = "案件ID") @RequestParam(value = "caseId", required = false) Long caseId,
            @Parameter(description = "Sheet索引（从0开始，默认为0）") @RequestParam(value = "sheetIndex", required = false, defaultValue = "0") Integer sheetIndex) {
        Long userId = getCurrentUserId();
        log.info("基于模板导入Excel - 模板编码: {}, 案件ID: {}, Sheet索引: {}", templateCode, caseId, sheetIndex);
        ExcelTemplateImportResult result = excelTemplateImportExportService.importFromExcel(file, templateCode, caseId, sheetIndex, userId);
        return Result.success(result);
    }

    @Operation(summary = "基于模板导出Excel数据")
    @GetMapping("/export")
    public void exportToExcel(
            @Parameter(description = "模板编码") @RequestParam("templateCode") String templateCode,
            @Parameter(description = "案件ID") @RequestParam(value = "caseId", required = false) Long caseId,
            @Parameter(description = "登记状态") @RequestParam(value = "registrationStatus", required = false) String registrationStatus,
            HttpServletResponse response) {
        log.info("基于模板导出Excel - 模板编码: {}, 案件ID: {}, 状态: {}", templateCode, caseId, registrationStatus);
        excelTemplateImportExportService.exportToExcel(response, templateCode, caseId, registrationStatus);
    }

    @Operation(summary = "获取所有可用的模板处理器")
    @GetMapping("/handlers")
    public Result<List<java.util.Map<String, String>>> getAllHandlers() {
        List<java.util.Map<String, String>> handlers = new ArrayList<>();
        for (Map.Entry<String, TemplateHandler> entry : handlerRegistry.getAllHandlers().entrySet()) {
            java.util.Map<String, String> handlerInfo = new java.util.HashMap<>();
            handlerInfo.put("templateCode", entry.getValue().getTemplateCode());
            handlerInfo.put("templateName", entry.getValue().getTemplateName());
            handlers.add(handlerInfo);
        }
        return Result.success(handlers);
    }

    @Operation(summary = "获取系统字段分组")
    @GetMapping("/system-fields")
    public Result<List<SystemFieldGroupResponse>> getSystemFieldGroups() {
        List<SystemFieldGroupResponse> groups = systemFieldService.getSystemFieldGroups();
        return Result.success(groups);
    }

    @Operation(summary = "创建系统字段")
    @PostMapping("/system-fields")
    public Result<?> createSystemField(@Valid @RequestBody SystemFieldCreateRequest request) {
        systemFieldService.createSystemField(request);
        return Result.success();
    }

    @Operation(summary = "更新系统字段")
    @PutMapping("/system-fields/{id}")
    public Result<?> updateSystemField(
            @Parameter(description = "系统字段ID") @PathVariable Long id,
            @Valid @RequestBody SystemFieldUpdateRequest request) {
        systemFieldService.updateSystemField(id, request);
        return Result.success();
    }

    @Operation(summary = "删除系统字段")
    @DeleteMapping("/system-fields/{id}")
    public Result<?> deleteSystemField(@Parameter(description = "系统字段ID") @PathVariable Long id) {
        systemFieldService.deleteSystemField(id);
        return Result.success();
    }

    @Operation(summary = "获取单个系统字段")
    @GetMapping("/system-fields/{id}")
    public Result<?> getSystemFieldById(@Parameter(description = "系统字段ID") @PathVariable Long id) {
        return Result.success(systemFieldService.getSystemFieldById(id));
    }

    @Operation(summary = "获取指定分组的系统字段")
    @GetMapping("/system-fields/group/{groupName}")
    public Result<?> getSystemFieldsByGroup(@Parameter(description = "分组名称") @PathVariable String groupName) {
        return Result.success(systemFieldService.getSystemFieldsByGroup(groupName));
    }

    private Long getCurrentUserId() {
        return SecurityUtil.getCurrentUserId();
    }
}

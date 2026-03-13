package com.lawbackend2.lawbackend2.service.impl;

import com.deepoove.poi.XWPFTemplate;
import com.lawbackend2.lawbackend2.util.DocumentTemplateGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WordTemplateService {

    private static final String TEMPLATE_DIR = "templates";

    public String createDataReportTemplate(String templateName) throws IOException {
        Path templatePath = Paths.get(TEMPLATE_DIR, templateName + "_template.docx");
        Files.createDirectories(templatePath.getParent());
        
        DocumentTemplateGenerator.generateDataReportTemplate(templatePath.toString());
        
        log.info("数据报告模板已创建：{}", templatePath.toString());
        return templatePath.toString();
    }

    public String createCustomTemplate(String templateName, String title, String[] sections) throws IOException {
        Path templatePath = Paths.get(TEMPLATE_DIR, templateName + "_template.docx");
        Files.createDirectories(templatePath.getParent());
        
        DocumentTemplateGenerator.generateSimpleReportTemplate(
            templatePath.toString(),
            title,
            java.util.Arrays.asList(sections)
        );
        
        log.info("自定义模板已创建：{}", templatePath.toString());
        return templatePath.toString();
    }

    public byte[] fillTemplateWithData(String templatePath, Map<String, Object> data) throws IOException {
        File templateFile = new File(templatePath);
        if (!templateFile.exists()) {
            throw new IOException("模板文件不存在：" + templatePath);
        }
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XWPFTemplate template = XWPFTemplate.compile(templateFile).render(data);
            template.write(out);
            template.close();
            
            log.info("模板填充完成，数据项数：{}", data.size());
            return out.toByteArray();
        }
    }

    public void exportFilledTemplate(String templatePath, String outputPath, Map<String, Object> data) throws IOException {
        byte[] filledContent = fillTemplateWithData(templatePath, data);
        
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(filledContent);
        }
        
        log.info("模板导出完成：{}", outputPath);
    }

    public Map<String, Object> createSampleData() {
        Map<String, Object> data = new HashMap<>();
        
        data.put("reportTitle", "2024 年度数据分析报告");
        data.put("reportNo", "RPT-2024-001");
        data.put("reportDate", "2024-03-07");
        data.put("company", "某某科技公司");
        data.put("creator", "张三");
        data.put("reviewer", "李四");
        data.put("reportType", "年度报告");
        
        data.put("summary", "本报告详细分析了 2024 年度的各项业务数据，包括用户增长、收入情况、市场份额等关键指标。整体来看，公司业务保持稳健增长态势...");
        
        data.put("data1_0", "1");
        data.put("data1_1", "用户总数");
        data.put("data1_2", "1,000,000");
        data.put("data1_3", "+25%");
        data.put("data1_4", "+15%");
        
        data.put("data2_0", "2");
        data.put("data2_1", "活跃用户");
        data.put("data2_2", "800,000");
        data.put("data2_3", "+30%");
        data.put("data2_4", "+20%");
        
        data.put("data3_0", "3");
        data.put("data3_1", "收入总额");
        data.put("data3_2", "50,000,000");
        data.put("data3_3", "+40%");
        data.put("data3_4", "+25%");
        
        data.put("chart1", "[图表：用户增长趋势图]");
        data.put("chart2", "[图表：收入分布图]");
        
        data.put("conclusion", "基于以上数据分析，我们建议继续加大市场投入，优化产品结构，提升用户体验，以实现更高的业务增长目标。");
        
        data.put("attachment1", "附件 1：详细数据表格");
        data.put("attachment2", "附件 2：用户调研报告");
        data.put("attachment3", "附件 3：市场竞争分析");
        
        data.put("page", "1");
        data.put("totalPages", "10");
        
        return data;
    }

    public void generateSampleReport() throws IOException {
        String templatePath = createDataReportTemplate("sample_data_report");
        
        Map<String, Object> data = createSampleData();
        
        String outputPath = Paths.get("output", "sample_report_2024.docx").toString();
        Files.createDirectories(Paths.get("output"));
        
        exportFilledTemplate(templatePath, outputPath, data);
        
        log.info("示例报告已生成：{}", outputPath);
    }

    public byte[] generateReportBytes(String templateType, Map<String, Object> data) throws IOException {
        String templatePath = createDataReportTemplate("temp_" + templateType);
        return fillTemplateWithData(templatePath, data);
    }

    public void deleteTemplate(String templatePath) throws IOException {
        Path path = Paths.get(templatePath);
        if (Files.exists(path)) {
            Files.delete(path);
            log.info("模板已删除：{}", templatePath);
        }
    }

    public void cleanupTempTemplates() {
        try {
            Path templateDir = Paths.get(TEMPLATE_DIR);
            if (Files.exists(templateDir)) {
                Files.walk(templateDir)
                    .filter(path -> path.getFileName().toString().startsWith("temp_"))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            log.warn("删除临时模板失败：{}", path, e);
                        }
                    });
                log.info("临时模板清理完成");
            }
        } catch (IOException e) {
            log.error("清理临时模板失败", e);
        }
    }
}

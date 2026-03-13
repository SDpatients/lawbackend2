package com.lawbackend2.lawbackend2;

import com.lawbackend2.lawbackend2.util.DocumentTemplateGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentTemplateGeneratorTest {

    @TempDir
    Path tempDir;

    @Test
    public void testGenerateDataReportTemplate() throws IOException {
        String outputPath = tempDir.resolve("data_report_template.docx").toString();
        
        DocumentTemplateGenerator.generateDataReportTemplate(outputPath);
        
        File templateFile = new File(outputPath);
        assertTrue(templateFile.exists(), "模板文件应该被创建");
        assertTrue(templateFile.length() > 0, "模板文件不应该为空");
        System.out.println("✓ 数据报告模板生成成功：" + outputPath);
    }

    @Test
    public void testGenerateSimpleReportTemplate() throws IOException {
        String outputPath = tempDir.resolve("simple_report_template.docx").toString();
        String title = "测试报告";
        
        DocumentTemplateGenerator.generateSimpleReportTemplate(
            outputPath,
            title,
            Arrays.asList("第一章：引言", "第二章：正文", "第三章：结论")
        );
        
        File templateFile = new File(outputPath);
        assertTrue(templateFile.exists(), "模板文件应该被创建");
        assertTrue(templateFile.length() > 0, "模板文件不应该为空");
        System.out.println("✓ 简单报告模板生成成功：" + outputPath);
    }

    @Test
    public void testGenerateMultipleTemplates() throws IOException {
        String[] templateNames = {
            "data_report.docx",
            "financial_report.docx",
            "analysis_report.docx",
            "summary_report.docx"
        };
        
        for (String templateName : templateNames) {
            String outputPath = tempDir.resolve(templateName).toString();
            
            DocumentTemplateGenerator.generateSimpleReportTemplate(
                outputPath,
                templateName.replace(".docx", ""),
                Arrays.asList("第一节", "第二节", "第三节")
            );
            
            File templateFile = new File(outputPath);
            assertTrue(templateFile.exists(), "模板文件 " + templateName + " 应该被创建");
        }
        
        System.out.println("✓ 批量模板生成成功，共生成 " + templateNames.length + " 个模板");
    }

    @Test
    public void testTemplateFileFormat() throws IOException {
        String outputPath = tempDir.resolve("format_test.docx").toString();
        
        DocumentTemplateGenerator.generateDataReportTemplate(outputPath);
        
        File templateFile = new File(outputPath);
        assertTrue(templateFile.getName().endsWith(".docx"), "文件扩展名应该是 .docx");
        
        byte[] fileHeader = java.nio.file.Files.readAllBytes(templateFile.toPath());
        assertTrue(fileHeader.length > 0, "文件应该有内容");
        
        System.out.println("✓ 模板文件格式验证通过");
    }

    @Test
    public void testTemplateWithChineseContent() throws IOException {
        String outputPath = tempDir.resolve("chinese_template.docx").toString();
        
        DocumentTemplateGenerator.generateSimpleReportTemplate(
            outputPath,
            "中文数据报告",
            Arrays.asList(
                "一、报告概述",
                "二、数据分析",
                "三、结论建议"
            )
        );
        
        File templateFile = new File(outputPath);
        assertTrue(templateFile.exists(), "中文字符模板应该被创建");
        System.out.println("✓ 中文内容模板生成成功");
    }

    @Test
    public void testTemplateWithPlaceholder() throws IOException {
        String outputPath = tempDir.resolve("placeholder_template.docx").toString();
        
        DocumentTemplateGenerator.generateDataReportTemplate(outputPath);
        
        byte[] content = java.nio.file.Files.readAllBytes(new File(outputPath).toPath());
        String contentStr = new String(content);
        
        assertTrue(contentStr.contains("{{"), "模板应该包含占位符");
        System.out.println("✓ 模板占位符验证通过");
    }

    @Test
    public void testLargeTemplateGeneration() throws IOException {
        String outputPath = tempDir.resolve("large_template.docx").toString();
        
        long startTime = System.currentTimeMillis();
        
        DocumentTemplateGenerator.generateSimpleReportTemplate(
            outputPath,
            "大型报告",
            Arrays.asList(
                "第一章", "第二章", "第三章", "第四章", "第五章",
                "第六章", "第七章", "第八章", "第九章", "第十章"
            )
        );
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        File templateFile = new File(outputPath);
        assertTrue(templateFile.exists(), "大型模板应该被创建");
        assertTrue(duration < 5000, "模板生成时间应该小于 5 秒");
        
        System.out.println("✓ 大型模板生成成功，耗时：" + duration + "ms");
    }

    @Test
    public void testTemplateInDifferentPaths() throws IOException {
        Path subDir = tempDir.resolve("subdir");
        java.nio.file.Files.createDirectories(subDir);
        
        String outputPath = subDir.resolve("nested_template.docx").toString();
        
        DocumentTemplateGenerator.generateDataReportTemplate(outputPath);
        
        File templateFile = new File(outputPath);
        assertTrue(templateFile.exists(), "嵌套路径的模板应该被创建");
        System.out.println("✓ 嵌套路径模板生成成功");
    }
}

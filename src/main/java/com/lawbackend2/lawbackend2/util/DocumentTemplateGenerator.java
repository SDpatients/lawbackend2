package com.lawbackend2.lawbackend2.util;

import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DocumentTemplateGenerator {

    public static void generateDataReportTemplate(String outputPath) throws IOException {
        XWPFDocument document = new XWPFDocument();

        createTitle(document, "数据报告");
        createSubtitle(document, "{{reportTitle}}");
        createSection(document, "一、报告基本信息", true);
        createInfoTable(document, Map.of(
                "报告编号", "{{reportNo}}",
                "报告日期", "{{reportDate}}",
                "编制单位", "{{company}}",
                "编制人", "{{creator}}",
                "审核人", "{{reviewer}}",
                "报告类型", "{{reportType}}"
        ));

        createSection(document, "二、报告摘要", true);
        createParagraph(document, "{{summary}}", 12, false);

        createSection(document, "三、数据概览", true);
        createDataTable(document, "数据汇总表");
        
        createSection(document, "四、详细数据分析", true);
        createChartPlaceholder(document, "图表 1：数据趋势分析图", "{{chart1}}");
        createChartPlaceholder(document, "图表 2：数据分布图", "{{chart2}}");
        
        createSection(document, "五、结论与建议", true);
        createParagraph(document, "{{conclusion}}", 12, false);
        
        createSection(document, "六、附件", false);
        createAttachmentList(document);
        
        createFooter(document);

        try (FileOutputStream out = new FileOutputStream(outputPath)) {
            document.write(out);
        }
        document.close();
    }

    private static void createTitle(XWPFDocument document, String title) {
        XWPFParagraph titlePara = document.createParagraph();
        titlePara.setAlignment(ParagraphAlignment.CENTER);
        
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText(title);
        titleRun.setFontSize(22);
        titleRun.setBold(true);
        titleRun.setFontFamily("宋体");
        titleRun.addBreak();
    }

    private static void createSubtitle(XWPFDocument document, String subtitle) {
        XWPFParagraph subtitlePara = document.createParagraph();
        subtitlePara.setAlignment(ParagraphAlignment.CENTER);
        
        XWPFRun subtitleRun = subtitlePara.createRun();
        subtitleRun.setText(subtitle);
        subtitleRun.setFontSize(14);
        subtitleRun.setBold(true);
        subtitleRun.setFontFamily("宋体");
        subtitleRun.addBreak();
    }

    private static void createSection(XWPFDocument document, String sectionTitle, boolean addBreak) {
        if (addBreak) {
            XWPFParagraph breakPara = document.createParagraph();
            XWPFRun breakRun = breakPara.createRun();
            breakRun.addBreak(BreakType.PAGE);
        }
        
        XWPFParagraph sectionPara = document.createParagraph();
        sectionPara.setAlignment(ParagraphAlignment.LEFT);
        
        XWPFRun sectionRun = sectionPara.createRun();
        sectionRun.setText(sectionTitle);
        sectionRun.setFontSize(16);
        sectionRun.setBold(true);
        sectionRun.setFontFamily("黑体");
    }

    private static void createParagraph(XWPFDocument document, String text, int fontSize, boolean bold) {
        XWPFParagraph para = document.createParagraph();
        para.setAlignment(ParagraphAlignment.BOTH);
        
        XWPFRun run = para.createRun();
        run.setText(text);
        run.setFontSize(fontSize);
        run.setBold(bold);
        run.setFontFamily("宋体");
    }

    private static void createInfoTable(XWPFDocument document, Map<String, String> data) {
        XWPFTable table = document.createTable(data.size(), 2);
        
        int rowNum = 0;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            XWPFTableRow row = table.getRow(rowNum);
            
            XWPFTableCell keyCell = row.getCell(0);
            XWPFParagraph keyPara = keyCell.getParagraphs().get(0);
            XWPFRun keyRun = keyPara.createRun();
            keyRun.setText(entry.getKey());
            keyRun.setBold(true);
            keyRun.setFontSize(11);
            keyRun.setFontFamily("宋体");
            
            XWPFTableCell valueCell = row.getCell(1);
            XWPFParagraph valuePara = valueCell.getParagraphs().get(0);
            XWPFRun valueRun = valuePara.createRun();
            valueRun.setText(entry.getValue());
            valueRun.setFontSize(11);
            valueRun.setFontFamily("宋体");
            
            rowNum++;
        }
    }

    private static void createDataTable(XWPFDocument document, String tableName) {
        XWPFParagraph tableTitlePara = document.createParagraph();
        tableTitlePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun tableTitleRun = tableTitlePara.createRun();
        tableTitleRun.setText(tableName);
        tableTitleRun.setBold(true);
        tableTitleRun.setFontSize(12);
        tableTitleRun.setFontFamily("黑体");
        
        XWPFTable table = document.createTable(6, 5);
        
        XWPFTableRow headerRow = table.getRow(0);
        String[] headers = {"序号", "指标名称", "指标值", "同比", "环比"};
        for (int i = 0; i < headers.length; i++) {
            XWPFTableCell headerCell = headerRow.getCell(i);
            XWPFParagraph headerPara = headerCell.getParagraphs().get(0);
            headerPara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun headerRun = headerPara.createRun();
            headerRun.setText(headers[i]);
            headerRun.setBold(true);
            headerRun.setFontSize(11);
            headerRun.setFontFamily("黑体");
        }
        
        for (int i = 1; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                XWPFTableCell cell = table.getRow(i).getCell(j);
                XWPFParagraph cellPara = cell.getParagraphs().get(0);
                cellPara.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun cellRun = cellPara.createRun();
                cellRun.setText("{{data" + i + "_" + j + "}}");
                cellRun.setFontSize(10);
                cellRun.setFontFamily("宋体");
            }
        }
    }

    private static void createChartPlaceholder(XWPFDocument document, String chartTitle, String chartPlaceholder) {
        XWPFParagraph chartTitlePara = document.createParagraph();
        chartTitlePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun chartTitleRun = chartTitlePara.createRun();
        chartTitleRun.setText(chartTitle);
        chartTitleRun.setBold(true);
        chartTitleRun.setFontSize(12);
        chartTitleRun.setFontFamily("黑体");
        
        XWPFParagraph chartPara = document.createParagraph();
        chartPara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun chartRun = chartPara.createRun();
        chartRun.setText(chartPlaceholder);
        chartRun.setFontSize(10);
        chartRun.setFontFamily("宋体");
        chartRun.addBreak();
    }

    static void createAttachmentList(XWPFDocument document) {
        XWPFParagraph attachPara = document.createParagraph();
        XWPFRun attachRun = attachPara.createRun();
        attachRun.setText("1. {{attachment1}}");
        attachRun.setFontSize(11);
        attachRun.setFontFamily("宋体");
        attachRun.addBreak();
        attachRun.setText("2. {{attachment2}}");
        attachRun.setFontSize(11);
        attachRun.setFontFamily("宋体");
        attachRun.addBreak();
        attachRun.setText("3. {{attachment3}}");
        attachRun.setFontSize(11);
        attachRun.setFontFamily("宋体");
    }

    private static void createFooter(XWPFDocument document) {
        XWPFParagraph footerPara = document.createParagraph();
        footerPara.setAlignment(ParagraphAlignment.CENTER);
        
        XWPFRun footerRun = footerPara.createRun();
        footerRun.setText("第 {{page}} 页，共 {{totalPages}} 页");
        footerRun.setFontSize(9);
        footerRun.setFontFamily("宋体");
    }

    public static void generateSimpleReportTemplate(String outputPath, String title, List<String> sections) throws IOException {
        XWPFDocument document = new XWPFDocument();
        
        createTitle(document, title);
        
        for (int i = 0; i < sections.size(); i++) {
            createSection(document, sections.get(i), i > 0);
            createParagraph(document, "{{content_" + (i + 1) + "}}", 12, false);
        }
        
        createFooter(document);
        
        try (FileOutputStream out = new FileOutputStream(outputPath)) {
            document.write(out);
        }
        document.close();
    }
}

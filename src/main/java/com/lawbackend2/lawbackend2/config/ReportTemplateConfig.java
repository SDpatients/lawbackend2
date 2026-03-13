package com.lawbackend2.lawbackend2.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReportTemplateConfig {

    public static class TemplateType {
        public static final String DATA_REPORT = "DATA_REPORT";
        public static final String FINANCIAL_REPORT = "FINANCIAL_REPORT";
        public static final String ANALYSIS_REPORT = "ANALYSIS_REPORT";
        public static final String SUMMARY_REPORT = "SUMMARY_REPORT";
        public static final String CUSTOM_REPORT = "CUSTOM_REPORT";
    }

    public static class ReportSection {
        private String sectionTitle;
        private String sectionType;
        private Map<String, Object> config;

        public ReportSection(String sectionTitle, String sectionType, Map<String, Object> config) {
            this.sectionTitle = sectionTitle;
            this.sectionType = sectionType;
            this.config = config;
        }

        public String getSectionTitle() {
            return sectionTitle;
        }

        public String getSectionType() {
            return sectionType;
        }

        public Map<String, Object> getConfig() {
            return config;
        }
    }

    public static class TableConfig {
        private String tableName;
        private List<String> columns;
        private int rowCount;
        private boolean hasHeader;

        public TableConfig(String tableName, List<String> columns, int rowCount, boolean hasHeader) {
            this.tableName = tableName;
            this.columns = columns;
            this.rowCount = rowCount;
            this.hasHeader = hasHeader;
        }

        public String getTableName() {
            return tableName;
        }

        public List<String> getColumns() {
            return columns;
        }

        public int getRowCount() {
            return rowCount;
        }

        public boolean isHasHeader() {
            return hasHeader;
        }
    }

    public static class ChartConfig {
        private String chartTitle;
        private String chartType;
        private String placeholder;

        public ChartConfig(String chartTitle, String chartType, String placeholder) {
            this.chartTitle = chartTitle;
            this.chartType = chartType;
            this.placeholder = placeholder;
        }

        public String getChartTitle() {
            return chartTitle;
        }

        public String getChartType() {
            return chartType;
        }

        public String getPlaceholder() {
            return placeholder;
        }
    }

    public static Map<String, Object> getDataReportTemplate() {
        return Map.of(
            "templateName", "数据报告模板",
            "templateType", TemplateType.DATA_REPORT,
            "description", "标准的数据分析报告模板，包含基本信息、数据概览、详细分析等",
            "sections", Arrays.asList(
                new ReportSection("报告基本信息", "INFO_TABLE", Map.of(
                    "fields", Arrays.asList("报告编号", "报告日期", "编制单位", "编制人", "审核人", "报告类型")
                )),
                new ReportSection("报告摘要", "PARAGRAPH", Map.of(
                    "fontSize", 12,
                    "lineSpacing", 1.5f
                )),
                new ReportSection("数据概览", "DATA_TABLE", Map.of(
                    "tableName", "数据汇总表",
                    "columns", Arrays.asList("序号", "指标名称", "指标值", "同比", "环比"),
                    "rowCount", 6
                )),
                new ReportSection("详细数据分析", "CHARTS", Map.of(
                    "charts", Arrays.asList(
                        new ChartConfig("图表 1：数据趋势分析图", "LINE_CHART", "{{chart1}}"),
                        new ChartConfig("图表 2：数据分布图", "BAR_CHART", "{{chart2}}")
                    )
                )),
                new ReportSection("结论与建议", "PARAGRAPH", Map.of(
                    "fontSize", 12,
                    "lineSpacing", 1.5f
                )),
                new ReportSection("附件", "ATTACHMENT_LIST", Map.of(
                    "maxAttachments", 3
                ))
            )
        );
    }

    public static Map<String, Object> getFinancialReportTemplate() {
        return Map.of(
            "templateName", "财务报告模板",
            "templateType", TemplateType.FINANCIAL_REPORT,
            "description", "财务报表分析模板，包含资产负债表、利润表等",
            "sections", Arrays.asList(
                new ReportSection("财务报表基本信息", "INFO_TABLE", Map.of(
                    "fields", Arrays.asList("报表编号", "报表期间", "编制单位", "编制人", "审核人", "货币单位")
                )),
                new ReportSection("财务摘要", "PARAGRAPH", Map.of(
                    "fontSize", 12,
                    "lineSpacing", 1.5f
                )),
                new ReportSection("主要财务指标", "DATA_TABLE", Map.of(
                    "tableName", "主要财务指标表",
                    "columns", Arrays.asList("指标名称", "本期金额", "上期金额", "变动额", "变动率"),
                    "rowCount", 8
                )),
                new ReportSection("财务分析图表", "CHARTS", Map.of(
                    "charts", Arrays.asList(
                        new ChartConfig("图表 1：收入趋势图", "LINE_CHART", "{{revenueChart}}"),
                        new ChartConfig("图表 2：成本结构图", "PIE_CHART", "{{costChart}}"),
                        new ChartConfig("图表 3：利润对比图", "BAR_CHART", "{{profitChart}}")
                    )
                )),
                new ReportSection("财务分析结论", "PARAGRAPH", Map.of(
                    "fontSize", 12,
                    "lineSpacing", 1.5f
                ))
            )
        );
    }

    public static Map<String, Object> getAnalysisReportTemplate() {
        return Map.of(
            "templateName", "分析报告模板",
            "templateType", TemplateType.ANALYSIS_REPORT,
            "description", "综合分析报告模板，适用于业务分析、市场分析等场景",
            "sections", Arrays.asList(
                new ReportSection("分析背景", "PARAGRAPH", Map.of(
                    "fontSize", 12,
                    "lineSpacing", 1.5f
                )),
                new ReportSection("分析对象概况", "INFO_TABLE", Map.of(
                    "fields", Arrays.asList("对象名称", "分析期间", "数据来源", "分析方法")
                )),
                new ReportSection("分析数据", "DATA_TABLE", Map.of(
                    "tableName", "分析数据表",
                    "columns", Arrays.asList("序号", "分析维度", "指标 1", "指标 2", "指标 3", "备注"),
                    "rowCount", 10
                )),
                new ReportSection("分析图表", "CHARTS", Map.of(
                    "charts", Arrays.asList(
                        new ChartConfig("图表 1：综合分析图", "COMBO_CHART", "{{analysisChart1}}"),
                        new ChartConfig("图表 2：对比分析图", "BAR_CHART", "{{analysisChart2}}")
                    )
                )),
                new ReportSection("分析结论", "PARAGRAPH", Map.of(
                    "fontSize", 12,
                    "lineSpacing", 1.5f
                )),
                new ReportSection("建议措施", "PARAGRAPH", Map.of(
                    "fontSize", 12,
                    "lineSpacing", 1.5f
                ))
            )
        );
    }

    public static Map<String, Object> getSummaryReportTemplate() {
        return Map.of(
            "templateName", "总结报告模板",
            "templateType", TemplateType.SUMMARY_REPORT,
            "description", "工作总结报告模板，适用于周报、月报、年报等",
            "sections", Arrays.asList(
                new ReportSection("基本信息", "INFO_TABLE", Map.of(
                    "fields", Arrays.asList("报告人", "报告期间", "部门", "日期")
                )),
                new ReportSection("工作完成情况", "PARAGRAPH", Map.of(
                    "fontSize", 12,
                    "lineSpacing", 1.5f
                )),
                new ReportSection("工作数据统计", "DATA_TABLE", Map.of(
                    "tableName", "工作统计表",
                    "columns", Arrays.asList("序号", "工作项", "完成数量", "完成率", "备注"),
                    "rowCount", 8
                )),
                new ReportSection("工作亮点", "PARAGRAPH", Map.of(
                    "fontSize", 12,
                    "lineSpacing", 1.5f
                )),
                new ReportSection("存在问题", "PARAGRAPH", Map.of(
                    "fontSize", 12,
                    "lineSpacing", 1.5f
                )),
                new ReportSection("下一步计划", "PARAGRAPH", Map.of(
                    "fontSize", 12,
                    "lineSpacing", 1.5f
                ))
            )
        );
    }

    public static Map<String, Object> getCustomTemplate(List<ReportSection> sections) {
        return Map.of(
            "templateName", "自定义模板",
            "templateType", TemplateType.CUSTOM_REPORT,
            "description", "用户自定义的报告模板",
            "sections", sections
        );
    }
}

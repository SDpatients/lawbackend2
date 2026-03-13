# Word 模板生成器 - 快速开始

## 🚀 5 分钟快速上手

### 第一步：创建模板

```java
// 方式 1：使用基础生成器创建标准数据报告模板
String templatePath = "templates/my_report_template.docx";
DocumentTemplateGenerator.generateDataReportTemplate(templatePath);

// 方式 2：创建自定义模板
List<String> sections = Arrays.asList("第一章：概述", "第二章：详情", "第三章：总结");
DocumentTemplateGenerator.generateSimpleReportTemplate(
    "templates/custom_template.docx",
    "我的自定义报告",
    sections
);
```

### 第二步：准备数据

```java
Map<String, Object> data = new HashMap<>();
data.put("reportTitle", "2024 年度数据分析报告");
data.put("reportNo", "RPT-2024-001");
data.put("reportDate", "2024-03-07");
data.put("company", "某某科技公司");
data.put("creator", "张三");
data.put("summary", "这是报告摘要内容...");
data.put("conclusion", "这是结论内容...");
// 添加表格数据
data.put("data1_0", "1");
data.put("data1_1", "用户总数");
data.put("data1_2", "1,000,000");
// ... 更多数据
```

### 第三步：填充模板并导出

```java
// 使用 poi-tl 填充模板
XWPFTemplate template = XWPFTemplate.compile(templatePath).render(data);

// 导出到文件
template.write(new FileOutputStream("output/filled_report.docx"));
template.close();

// 或导出到字节数组（用于 HTTP 响应）
ByteArrayOutputStream out = new ByteArrayOutputStream();
template.write(out);
byte[] bytes = out.toByteArray();
template.close();
```

## 📋 完整示例

### 示例 1：生成数据报告

```java
@Service
public class ReportService {
    
    public byte[] generateReport() throws IOException {
        // 1. 生成模板
        String templatePath = "templates/data_report.docx";
        DocumentTemplateGenerator.generateDataReportTemplate(templatePath);
        
        // 2. 准备数据
        Map<String, Object> data = new HashMap<>();
        data.put("reportTitle", "2024 年度报告");
        data.put("reportNo", "2024-001");
        data.put("reportDate", "2024-03-07");
        data.put("company", "XX 公司");
        data.put("creator", "张三");
        data.put("summary", "本年度业务增长显著...");
        data.put("conclusion", "建议继续扩大市场投入...");
        
        // 3. 填充并导出
        XWPFTemplate template = XWPFTemplate.compile(templatePath).render(data);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        template.write(out);
        template.close();
        
        return out.toByteArray();
    }
}
```

### 示例 2：在 Controller 中使用

```java
@RestController
@RequestMapping("/api/report")
public class ReportController {
    
    @PostMapping("/export")
    public void exportReport(HttpServletResponse response) throws IOException {
        // 生成报告
        byte[] reportBytes = reportService.generateReport();
        
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=\"report.docx\"");
        
        // 写入响应
        response.getOutputStream().write(reportBytes);
        response.getOutputStream().flush();
    }
}
```

### 示例 3：使用 WordTemplateService

```java
@Service
public class ReportService {
    
    @Autowired
    private WordTemplateService templateService;
    
    public void createReport() throws IOException {
        // 1. 创建模板
        String templatePath = templateService.createDataReportTemplate("my_report");
        
        // 2. 准备数据
        Map<String, Object> data = templateService.createSampleData();
        data.put("reportTitle", "自定义标题");
        
        // 3. 导出
        templateService.exportFilledTemplate(
            templatePath,
            "output/my_report.docx",
            data
        );
    }
}
```

## 🎯 常用模板类型

### 1. 数据报告模板

```java
DocumentTemplateGenerator.generateDataReportTemplate("data_report.docx");
```

**包含章节：**
- 报告基本信息（表格）
- 报告摘要
- 数据概览（表格）
- 详细数据分析（图表）
- 结论与建议
- 附件

### 2. 法律案件报告模板

```java
AdvancedTemplateGenerator.generateLegalReportTemplate("legal_report.docx");
```

**包含章节：**
- 案件基本信息
- 案件摘要
- 当事人信息
- 案件进展
- 证据材料
- 法律分析
- 诉讼策略
- 风险评估
- 结论与建议

### 3. 项目进度报告模板

```java
AdvancedTemplateGenerator.generateProjectReportTemplate("project_report.docx");
```

**包含章节：**
- 项目概况
- 本周工作总结
- 任务完成情况
- 资源使用情况
- 问题与风险
- 下周计划
- 需要支持

### 4. 会议纪要模板

```java
AdvancedTemplateGenerator.generateMeetingMinutesTemplate("meeting_minutes.docx");
```

**包含章节：**
- 会议基本信息
- 会议议程
- 会议内容
- 决议事项
- 待办事项
- 附件

## 📊 模板变量参考

### 基本信息变量

| 变量名 | 说明 | 示例 |
|--------|------|------|
| {{reportTitle}} | 报告标题 | 2024 年度数据分析报告 |
| {{reportNo}} | 报告编号 | RPT-2024-001 |
| {{reportDate}} | 报告日期 | 2024-03-07 |
| {{company}} | 编制单位 | 某某科技公司 |
| {{creator}} | 编制人 | 张三 |
| {{reviewer}} | 审核人 | 李四 |
| {{reportType}} | 报告类型 | 年度报告 |

### 内容变量

| 变量名 | 说明 |
|--------|------|
| {{summary}} | 报告摘要 |
| {{conclusion}} | 结论 |
| {{chart1}} | 图表 1 占位符 |
| {{chart2}} | 图表 2 占位符 |

### 表格数据变量

格式：`{{data 行_列}}`

| 变量名 | 说明 |
|--------|------|
| {{data1_0}} | 第 1 行第 1 列 |
| {{data1_1}} | 第 1 行第 2 列 |
| {{data2_0}} | 第 2 行第 1 列 |

### 其他变量

| 变量名 | 说明 |
|--------|------|
| {{attachment1}} | 附件 1 |
| {{page}} | 当前页码 |
| {{totalPages}} | 总页数 |

## 🔧 进阶用法

### 使用构建器模式创建自定义模板

```java
AdvancedTemplateGenerator.buildTemplate("custom_report.docx")
    .addTitle("我的报告", 24, true)
    .addSection("一、概述", true)
    .addParagraph("{{overview}}", 12, false)
    .addSection("二、数据分析", true)
    .addTable("数据表", Arrays.asList("序号", "指标", "值"), 5)
    .addChartPlaceholder("趋势图", "{{chart}}")
    .addSection("三、结论", true)
    .addParagraph("{{conclusion}}", 12, false)
    .build();
```

### 使用配置类获取模板配置

```java
Map<String, Object> config = ReportTemplateConfig.getFinancialReportTemplate();
String templateName = (String) config.get("templateName");
List<ReportSection> sections = (List<ReportSection>) config.get("sections");

// 根据配置生成模板
DocumentTemplateGenerator.generateSimpleReportTemplate(
    "financial_report.docx",
    templateName,
    sections.stream()
        .map(ReportSection::getSectionTitle)
        .toList()
);
```

## 🧪 测试

```java
@SpringBootTest
public class TemplateTest {
    
    @Test
    public void testGenerateAndFill() throws IOException {
        // 生成模板
        String templatePath = "test_template.docx";
        DocumentTemplateGenerator.generateDataReportTemplate(templatePath);
        
        // 准备数据
        Map<String, Object> data = new HashMap<>();
        data.put("reportTitle", "测试报告");
        // ... 添加其他数据
        
        // 填充并验证
        XWPFTemplate template = XWPFTemplate.compile(templatePath).render(data);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        template.write(out);
        template.close();
        
        assertTrue(out.size() > 0);
    }
}
```

## 💡 提示

1. **模板路径**：建议将模板保存在 `templates/` 目录下
2. **变量命名**：使用 `{{variable}}` 格式，变量名使用英文
3. **中文支持**：确保设置中文字体（宋体、黑体等）
4. **性能优化**：模板可以缓存复用，无需每次都生成
5. **异常处理**：记得处理 IOException 等异常

## 📞 获取帮助

- 详细文档：查看 `WORD_TEMPLATE_GUIDE.md`
- 示例代码：查看 `WordTemplateExample.java`
- 测试用例：查看 `DocumentTemplateGeneratorTest.java`

现在就开始使用吧！🎉

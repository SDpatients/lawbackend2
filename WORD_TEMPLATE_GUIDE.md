# Apache POI Word 模板生成器使用指南

## 📋 概述

本项目提供了基于 Apache POI 的 Word (docx) 模板生成工具，支持快速生成各种类型的数据报告模板。

## 🛠️ 核心组件

### 1. DocumentTemplateGenerator - 基础模板生成器

**功能特性：**
- ✅ 生成标准数据报告模板
- ✅ 支持标题、副标题、章节
- ✅ 信息表格、数据表格
- ✅ 图表占位符
- ✅ 页脚页码
- ✅ 中文支持

**使用示例：**

```java
// 生成标准数据报告模板
DocumentTemplateGenerator.generateDataReportTemplate("output/data_report.docx");

// 生成简单报告模板
List<String> sections = Arrays.asList("第一章：引言", "第二章：正文", "第三章：结论");
DocumentTemplateGenerator.generateSimpleReportTemplate(
    "output/simple_report.docx",
    "我的报告",
    sections
);
```

### 2. AdvancedTemplateGenerator - 高级模板生成器

**功能特性：**
- ✅ 法律案件报告模板
- ✅ 项目进度报告模板
- ✅ 会议纪要模板
- ✅ 构建器模式
- ✅ 多种表格样式

**使用示例：**

```java
// 生成法律案件报告模板
AdvancedTemplateGenerator.generateLegalReportTemplate("output/legal_report.docx");

// 生成项目进度报告模板
AdvancedTemplateGenerator.generateProjectReportTemplate("output/project_report.docx");

// 生成会议纪要模板
AdvancedTemplateGenerator.generateMeetingMinutesTemplate("output/meeting_minutes.docx");

// 使用构建器模式
AdvancedTemplateGenerator.buildTemplate("output/custom_report.docx")
    .addTitle("自定义报告", 24, true)
    .addSection("一、报告概述", true)
    .addParagraph("{{content}}", 12, false)
    .addTable("数据表", Arrays.asList("序号", "名称", "值"), 5)
    .build();
```

### 3. ReportTemplateConfig - 模板配置类

**预定义模板配置：**
- `DATA_REPORT` - 数据报告模板
- `FINANCIAL_REPORT` - 财务报告模板
- `ANALYSIS_REPORT` - 分析报告模板
- `SUMMARY_REPORT` - 总结报告模板
- `CUSTOM_REPORT` - 自定义模板

**使用示例：**

```java
Map<String, Object> config = ReportTemplateConfig.getDataReportTemplate();
String templateName = (String) config.get("templateName");
List<ReportSection> sections = (List<ReportSection>) config.get("sections");
```

### 4. WordTemplateService - 模板服务类

**功能特性：**
- ✅ 创建模板
- ✅ 填充数据
- ✅ 导出文档
- ✅ 模板管理

**使用示例：**

```java
@Autowired
private WordTemplateService templateService;

// 创建模板
String templatePath = templateService.createDataReportTemplate("my_report");

// 准备数据
Map<String, Object> data = templateService.createSampleData();

// 填充模板并导出
templateService.exportFilledTemplate(templatePath, "output/filled_report.docx", data);

// 生成字节数组
byte[] reportBytes = templateService.generateReportBytes("report", data);
```

## 📊 模板变量说明

### 基本信息变量
```
{{reportTitle}}     - 报告标题
{{reportNo}}        - 报告编号
{{reportDate}}      - 报告日期
{{company}}         - 编制单位
{{creator}}         - 编制人
{{reviewer}}        - 审核人
{{reportType}}      - 报告类型
```

### 内容变量
```
{{summary}}         - 报告摘要
{{conclusion}}      - 结论
{{chart1}}          - 图表 1 占位符
{{chart2}}          - 图表 2 占位符
```

### 表格数据变量
```
{{data1_0}}, {{data1_1}}...  - 表格数据
```

### 其他变量
```
{{attachment1}}     - 附件 1
{{page}}            - 当前页码
{{totalPages}}      - 总页数
```

## 🎯 完整使用流程

### 步骤 1：生成模板

```java
// 方法 1：使用基础生成器
DocumentTemplateGenerator.generateDataReportTemplate("templates/report_template.docx");

// 方法 2：使用高级生成器
AdvancedTemplateGenerator.generateLegalReportTemplate("templates/legal_report.docx");
```

### 步骤 2：准备数据

```java
Map<String, Object> data = new HashMap<>();
data.put("reportTitle", "2024 年度数据分析报告");
data.put("reportNo", "RPT-2024-001");
data.put("reportDate", "2024-03-07");
data.put("company", "某某科技公司");
data.put("creator", "张三");
data.put("summary", "本报告详细分析了...");
// ... 更多数据
```

### 步骤 3：填充模板

```java
// 使用 poi-tl 进行模板填充
XWPFTemplate template = XWPFTemplate.compile("templates/report_template.docx")
    .render(data);

// 输出到文件或流
template.write(new FileOutputStream("output/report.docx"));
template.close();
```

### 步骤 4：在 Controller 中使用

```java
@PostMapping("/export/report")
public void exportReport(@RequestBody ReportRequest request, HttpServletResponse response) throws IOException {
    // 1. 创建模板
    String templatePath = templateService.createDataReportTemplate("temp_report");
    
    // 2. 准备数据
    Map<String, Object> data = prepareReportData(request);
    
    // 3. 填充模板
    byte[] reportBytes = templateService.fillTemplateWithData(templatePath, data);
    
    // 4. 设置响应
    response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    response.setHeader("Content-Disposition", "attachment; filename=\"report.docx\"");
    response.getOutputStream().write(reportBytes);
}
```

## 🔧 高级功能

### 1. 自定义表格样式

```java
XWPFTable table = document.createTable(rows, cols);
// 使用预定义样式
setTableStyle(table, TableStyle.PROFESSIONAL);
// 或自定义样式
CTTblPr tblPr = table.getCTTbl().getTblPr();
// 设置边框、宽度等
```

### 2. 自定义段落格式

```java
XWPFParagraph para = document.createParagraph();
para.setAlignment(ParagraphAlignment.CENTER);
para.setVerticalAlignment(TextAlignment.CENTER);

XWPFRun run = para.createRun();
run.setText("文本内容");
run.setFontSize(12);
run.setBold(true);
run.setFontFamily("宋体");
run.setColor("FF0000");
```

### 3. 添加图片

```java
XWPFParagraph para = document.createParagraph();
para.setAlignment(ParagraphAlignment.CENTER);

XWPFRun run = para.createRun();
run.addPicture(
    new FileInputStream("image.png"),
    Document.PICTURE_TYPE_PNG,
    "image.png",
    Units.toEMU(200),
    Units.toEMU(200)
);
```

### 4. 添加页眉页脚

```java
// 创建页眉
XWPFHeader header = document.createHeader();
XWPFParagraph headerPara = header.createParagraph();
XWPFRun headerRun = headerPara.createRun();
headerRun.setText("页眉文本");

// 创建页脚
XWPFParagraph footerPara = document.createParagraph();
footerPara.setAlignment(ParagraphAlignment.CENTER);
XWPFRun footerRun = footerPara.createRun();
footerRun.setText("第 {{page}} 页");
```

## 📝 最佳实践

### 1. 模板复用
```java
// 将常用模板保存到数据库或文件系统
// 使用时直接加载，避免重复生成
String templatePath = getTemplateFromCache(templateCode);
if (templatePath == null) {
    templatePath = generateTemplate(templateCode);
    saveToCache(templateCode, templatePath);
}
```

### 2. 数据验证
```java
// 填充前验证必要字段
validateRequiredFields(data, requiredFields);
```

### 3. 异常处理
```java
try {
    templateService.exportFilledTemplate(templatePath, outputPath, data);
} catch (IOException e) {
    log.error("模板导出失败", e);
    throw new BusinessException("导出失败：" + e.getMessage());
}
```

### 4. 性能优化
```java
// 使用缓存减少重复生成
// 批量处理时使用异步
@Async
public CompletableFuture<byte[]> generateReportAsync(Map<String, Object> data) {
    byte[] bytes = templateService.fillTemplateWithData(templatePath, data);
    return CompletableFuture.completedFuture(bytes);
}
```

## 🧪 测试示例

```java
@SpringBootTest
public class TemplateGeneratorTest {
    
    @Test
    public void testGenerateTemplate() throws IOException {
        String outputPath = "test_template.docx";
        
        DocumentTemplateGenerator.generateDataReportTemplate(outputPath);
        
        File file = new File(outputPath);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }
    
    @Test
    public void testFillTemplate() throws IOException {
        String templatePath = "test_template.docx";
        Map<String, Object> data = createTestData();
        
        byte[] result = templateService.fillTemplateWithData(templatePath, data);
        
        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}
```

## 📦 依赖说明

项目已包含以下依赖（pom.xml）：

```xml
<!-- Apache POI -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.2.3</version>
</dependency>

<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.3</version>
</dependency>

<!-- Word 模板引擎 poi-tl -->
<dependency>
    <groupId>com.deepoove</groupId>
    <artifactId>poi-tl</artifactId>
    <version>1.12.2</version>
</dependency>
```

## 🎨 支持的模板类型

1. **数据报告** - 标准数据分析报告
2. **财务报告** - 财务报表分析
3. **分析报告** - 业务分析报告
4. **总结报告** - 工作总结报告
5. **法律报告** - 法律案件分析
6. **项目报告** - 项目进度报告
7. **会议纪要** - 会议记录模板

## 💡 常见问题

### Q: 如何添加自定义字体？
A: 使用 `run.setFontFamily("字体名称")` 设置

### Q: 如何调整行间距？
A: 使用 `CTSpacing` 设置行间距参数

### Q: 如何添加分页？
A: 使用 `run.addBreak(BreakType.PAGE)` 

### Q: 如何支持中文？
A: 确保设置中文字体，如"宋体"、"黑体"

## 📞 技术支持

如有问题，请参考：
- Apache POI 官方文档：https://poi.apache.org/
- poi-tl 官方文档：http://deepoove.com/poi-tl/

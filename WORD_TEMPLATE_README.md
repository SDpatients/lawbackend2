# Apache POI Word 模板生成器

[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://openjdk.java.net/)
[![Apache POI](https://img.shields.io/badge/Apache%20POI-5.2.3-green.svg)](https://poi.apache.org/)
[![poi-tl](https://img.shields.io/badge/poi--tl-1.12.2-orange.svg)](http://deepoove.com/poi-tl/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7+-brightgreen.svg)](https://spring.io/projects/spring-boot)

## 📖 项目简介

这是一个功能强大的 **Word (docx) 模板生成工具**，基于 Apache POI 和 poi-tl 实现。提供多种预定义模板类型，支持快速生成数据报告、法律文档、项目报告、会议纪要等各种 Word 文档模板，并可通过模板引擎进行数据填充。

## ✨ 主要特性

- ✅ **多种模板类型**：数据报告、财务报告、法律报告、项目报告、会议纪要等
- ✅ **灵活的自定义能力**：支持构建器模式创建自定义模板
- ✅ **完整的 REST API**：提供模板生成、数据填充、文档导出等接口
- ✅ **中文完美支持**：内置中文字体设置
- ✅ **专业的文档格式**：表格、图表、页眉页脚、分页等
- ✅ **开箱即用**：提供详细文档和丰富示例

## 🚀 快速开始

### 1. 生成标准数据报告模板

```java
DocumentTemplateGenerator.generateDataReportTemplate("output/report.docx");
```

### 2. 准备数据

```java
Map<String, Object> data = new HashMap<>();
data.put("reportTitle", "2024 年度数据分析报告");
data.put("reportNo", "RPT-2024-001");
data.put("reportDate", "2024-03-07");
data.put("company", "某某科技公司");
data.put("summary", "本报告详细分析了...");
```

### 3. 填充模板

```java
XWPFTemplate template = XWPFTemplate.compile("output/report.docx").render(data);
template.write(new FileOutputStream("filled_report.docx"));
template.close();
```

## 📦 核心组件

| 组件 | 说明 | 位置 |
|------|------|------|
| **DocumentTemplateGenerator** | 基础模板生成器 | `util/DocumentTemplateGenerator.java` |
| **AdvancedTemplateGenerator** | 高级模板生成器（构建器模式） | `util/AdvancedTemplateGenerator.java` |
| **ReportTemplateConfig** | 模板配置类 | `config/ReportTemplateConfig.java` |
| **WordTemplateService** | 模板服务类 | `service/impl/WordTemplateService.java` |
| **WordTemplateController** | REST API 控制器 | `controller/WordTemplateController.java` |

## 📋 支持的模板类型

### 1. 数据报告模板
- 报告基本信息（表格）
- 报告摘要
- 数据概览（表格）
- 详细数据分析（图表）
- 结论与建议
- 附件

### 2. 法律案件报告模板
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
- 项目概况
- 本周工作总结
- 任务完成情况
- 资源使用情况
- 问题与风险
- 下周计划
- 需要支持

### 4. 会议纪要模板
- 会议基本信息
- 会议议程
- 会议内容
- 决议事项
- 待办事项
- 附件

### 5. 财务报告模板
- 财务报表基本信息
- 财务摘要
- 主要财务指标
- 财务分析图表
- 财务分析结论

## 🎯 使用示例

### 基础用法

```java
// 生成标准数据报告模板
DocumentTemplateGenerator.generateDataReportTemplate("report.docx");

// 生成简单报告模板
List<String> sections = Arrays.asList("第一章", "第二章", "第三章");
DocumentTemplateGenerator.generateSimpleReportTemplate(
    "simple_report.docx",
    "我的报告",
    sections
);
```

### 高级用法

```java
// 生成法律案件报告
AdvancedTemplateGenerator.generateLegalReportTemplate("legal_report.docx");

// 生成项目进度报告
AdvancedTemplateGenerator.generateProjectReportTemplate("project_report.docx");

// 使用构建器模式
AdvancedTemplateGenerator.buildTemplate("custom_report.docx")
    .addTitle("自定义报告", 24, true)
    .addSection("一、概述", true)
    .addParagraph("{{content}}", 12, false)
    .addTable("数据表", Arrays.asList("序号", "名称", "值"), 5)
    .build();
```

### 使用服务类

```java
@Autowired
private WordTemplateService templateService;

// 创建模板
String templatePath = templateService.createDataReportTemplate("my_report");

// 准备数据
Map<String, Object> data = templateService.createSampleData();

// 导出
templateService.exportFilledTemplate(templatePath, "output.docx", data);
```

### REST API

```bash
# 生成数据报告模板
curl -X POST "http://localhost:8080/api/template/generate/data-report?templateName=my_report"

# 填充模板数据
curl -X POST "http://localhost:8080/api/template/fill?templatePath=templates/my_report.docx" \
  -H "Content-Type: application/json" \
  -d '{"reportTitle": "我的报告", "summary": "摘要内容..."}'

# 导出示例报告
curl -X POST "http://localhost:8080/api/template/export/sample"
```

## 📊 模板变量说明

### 基本信息变量

| 变量 | 说明 | 示例 |
|------|------|------|
| `{{reportTitle}}` | 报告标题 | 2024 年度数据分析报告 |
| `{{reportNo}}` | 报告编号 | RPT-2024-001 |
| `{{reportDate}}` | 报告日期 | 2024-03-07 |
| `{{company}}` | 编制单位 | 某某科技公司 |
| `{{creator}}` | 编制人 | 张三 |
| `{{reviewer}}` | 审核人 | 李四 |

### 内容变量

| 变量 | 说明 |
|------|------|
| `{{summary}}` | 报告摘要 |
| `{{conclusion}}` | 结论 |
| `{{chart1}}`, `{{chart2}}` | 图表占位符 |

### 表格数据

格式：`{{data 行_列}}`

| 变量 | 说明 |
|------|------|
| `{{data1_0}}` | 第 1 行第 1 列 |
| `{{data1_1}}` | 第 1 行第 2 列 |

### 其他变量

| 变量 | 说明 |
|------|------|
| `{{attachment1}}` | 附件 1 |
| `{{page}}` | 当前页码 |
| `{{totalPages}}` | 总页数 |

## 📚 文档

- 📖 **[详细使用指南](WORD_TEMPLATE_GUIDE.md)** - 完整 API 说明、高级功能、最佳实践
- 🚀 **[快速开始](WORD_TEMPLATE_QUICKSTART.md)** - 5 分钟快速上手指南
- 📝 **[项目总结](WORD_TEMPLATE_SUMMARY.md)** - 组件说明、功能列表

## 🧪 测试

运行测试：
```bash
mvn test -Dtest=DocumentTemplateGeneratorTest
```

运行演示：
```bash
java -cp target/classes com.lawbackend2.lawbackend2.example.WordTemplateDemo
```

## 💡 最佳实践

### 1. 模板复用
```java
// 将常用模板缓存，避免重复生成
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
// 使用异步处理
@Async
public CompletableFuture<byte[]> generateReportAsync(Map<String, Object> data) {
    byte[] bytes = templateService.fillTemplateWithData(templatePath, data);
    return CompletableFuture.completedFuture(bytes);
}
```

## 🔧 技术栈

- **Apache POI** 5.2.3 - Word 文档处理
- **poi-tl** 1.12.2 - Word 模板引擎
- **Spring Boot** 2.7+ - Web 框架
- **JUnit 5** - 单元测试
- **Lombok** - 代码简化

## 📁 项目结构

```
src/
├── main/java/com/lawbackend2/lawbackend2/
│   ├── util/
│   │   ├── DocumentTemplateGenerator.java
│   │   └── AdvancedTemplateGenerator.java
│   ├── config/
│   │   └── ReportTemplateConfig.java
│   ├── service/impl/
│   │   └── WordTemplateService.java
│   ├── controller/
│   │   └── WordTemplateController.java
│   └── example/
│       ├── WordTemplateExample.java
│       └── WordTemplateDemo.java
└── test/java/com/lawbackend2/lawbackend2/
    └── DocumentTemplateGeneratorTest.java

docs/
├── WORD_TEMPLATE_GUIDE.md
├── WORD_TEMPLATE_QUICKSTART.md
└── WORD_TEMPLATE_SUMMARY.md
```

## 🎨 高级功能

### 1. 自定义表格样式
```java
XWPFTable table = document.createTable(rows, cols);
setTableStyle(table, TableStyle.PROFESSIONAL);
```

### 2. 自定义段落格式
```java
XWPFParagraph para = document.createParagraph();
para.setAlignment(ParagraphAlignment.CENTER);

XWPFRun run = para.createRun();
run.setText("文本");
run.setFontSize(12);
run.setBold(true);
run.setFontFamily("宋体");
```

### 3. 添加图片
```java
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
// 页眉
XWPFHeader header = document.createHeader();
XWPFRun headerRun = header.createParagraph().createRun();
headerRun.setText("页眉文本");

// 页脚
XWPFParagraph footer = document.createParagraph();
footer.setAlignment(ParagraphAlignment.CENTER);
XWPFRun footerRun = footer.createRun();
footerRun.setText("第 {{page}} 页");
```

## 📞 技术支持

- Apache POI 官方文档：https://poi.apache.org/
- poi-tl 官方文档：http://deepoove.com/poi-tl/
- 项目 Issue：https://github.com/your-repo/issues

## 📄 许可证

本项目采用 Apache License 2.0 许可证。

## 🙏 致谢

感谢以下开源项目：
- [Apache POI](https://poi.apache.org/)
- [poi-tl](http://deepoove.com/poi-tl/)
- [Spring Boot](https://spring.io/projects/spring-boot)

---

**立即开始使用 Apache POI Word 模板生成器，让文档生成变得简单！** 🚀

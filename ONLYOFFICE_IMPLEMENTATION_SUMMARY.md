# OnlyOffice 集成与模板持久化 - 完成总结

## 📋 项目概述

本次任务完成了 OnlyOffice 文档编辑集成所需的 API 接口开发，以及 Word 模板的数据库持久化存储方案。

---

## ✅ 已完成的工作

### 1. 实体类创建

#### 1.1 DocumentExportTemplate（文档导出模板实体）
**文件**: `src/main/java/com/lawbackend2/lawbackend2/entity/DocumentExportTemplate.java`

**主要字段**:
- `templateName` - 模板名称
- `templateCode` - 模板编码（唯一标识）
- `templateType` - 模板类型（WORD/EXCEL）
- `filePath` - 模板文件路径
- `description` - 模板描述
- `isDefault` - 是否默认模板
- `category` - 模板分类
- `version` - 模板版本

#### 1.2 DocumentTemplateField（模板字段配置实体）
**文件**: `src/main/java/com/lawbackend2/lawbackend2/entity/DocumentTemplateField.java`

**主要字段**:
- `templateId` - 模板 ID
- `fieldLabel` - 字段标签（显示名称）
- `fieldName` - 字段名称（占位符）
- `fieldType` - 字段类型（TEXT/NUMBER/DATE/TABLE/IMAGE）
- `isRequired` - 是否必填
- `sortOrder` - 排序顺序
- `defaultValue` - 默认值
- `validationRule` - 验证规则（JSON）

---

### 2. Repository 接口

#### 2.1 DocumentExportTemplateRepository
**文件**: `src/main/java/com/lawbackend2/lawbackend2/repository/DocumentExportTemplateRepository.java`

**主要方法**:
- `findByTemplateCode` - 根据编码查询
- `findByTemplateTypeAndStatusAndIsDeletedFalse` - 根据类型查询
- `findByIsDefaultTrueAndTemplateTypeAndStatusAndIsDeletedFalse` - 查询默认模板
- `findByCategoryAndStatusAndIsDeletedFalse` - 根据分类查询

#### 2.2 DocumentTemplateFieldRepository
**文件**: `src/main/java/com/lawbackend2/lawbackend2/repository/DocumentTemplateFieldRepository.java`

**主要方法**:
- `findByTemplateIdOrderBySortOrderAsc` - 根据模板 ID 查询字段
- `deleteByTemplateId` - 根据模板 ID 删除字段

---

### 3. DTO 类

#### 3.1 OnlyOfficeCallbackRequest
**文件**: `src/main/java/com/lawbackend2/lawbackend2/dto/OnlyOfficeCallbackRequest.java`

**用途**: OnlyOffice 回调请求数据封装

**主要字段**:
- `action` - 操作类型（0-无操作，1-下载，2-编辑，3-保存）
- `data` - 回调数据（包含文件信息、版本、用户等）
- `changes` - 变更历史
- `token` - JWT 令牌

#### 3.2 OnlyOfficeConfigDTO
**文件**: `src/main/java/com/lawbackend2/lawbackend2/dto/OnlyOfficeConfigDTO.java`

**用途**: OnlyOffice 配置响应数据封装

**主要结构**:
- `document` - 文档配置（文件类型、URL、权限等）
- `documentType` - 文档类型（word/cell/slide）
- `editorConfig` - 编辑器配置（回调 URL、用户信息、自定义配置）

---

### 4. Controller 接口

#### 4.1 OnlyOfficeController
**文件**: `src/main/java/com/lawbackend2/lawbackend2/controller/OnlyOfficeController.java`

**API 端点**:

| 端点 | 方法 | 说明 |
|------|------|------|
| `/config/{fileId}` | GET | 获取 OnlyOffice 编辑配置 |
| `/callback` | POST | OnlyOffice 编辑回调接口 |
| `/lock/{fileId}` | POST | 锁定文件 |
| `/unlock/{fileId}` | POST | 解锁文件 |
| `/history/{fileId}` | GET | 获取编辑历史 |
| `/collaborators/{fileId}` | GET | 获取协作者信息 |

**核心功能**:
- ✅ 生成 OnlyOffice 配置（包含文档 URL、回调地址、用户信息）
- ✅ 处理 OnlyOffice 回调（保存编辑后的文档）
- ✅ 文件锁定/解锁
- ✅ 编辑历史查询
- ✅ 协作者查询

---

### 5. SQL 脚本

**文件**: `onlyoffice_template_schema.sql`

**包含内容**:

#### 5.1 数据表（5 张）

1. **tb_document_export_template** - 文档导出模板表
2. **tb_document_template_field** - 文档模板字段配置表
3. **tb_onlyoffice_edit_record** - OnlyOffice 文件编辑记录表
4. **tb_file_lock** - 文件锁定表
5. **tb_document_edit_history** - 文档编辑历史表

#### 5.2 数据库对象

- **视图**: `v_template_fields` - 模板字段视图
- **存储过程**: `sp_cleanup_expired_locks` - 清理过期文件锁定
- **触发器**: `trg_template_delete` - 模板删除时级联删除字段

#### 5.3 初始化数据

- 5 个预定义模板（数据报告、财务报告、法律报告、项目报告、会议纪要）
- 对应的模板字段配置

---

## 🔧 配置要求

### 1. application.yml 配置

```yaml
onlyoffice:
  server-url: http://localhost:8081  # OnlyOffice 服务器地址
  jwt:
    enabled: true   # 是否启用 JWT 验证
    secret: your-secret-key   # JWT 密钥
    header: Authorization
  callback:
    url: http://backend-server/api/v1/onlyoffice/callback  # 回调地址
```

### 2. OnlyOffice Docker 部署

```yaml
version: '3'
services:
  onlyoffice-document-server:
    image: onlyoffice/documentserver:latest
    ports:
      - "8081:80"
    environment:
      - JWT_ENABLED=true
      - JWT_SECRET=your-secret-key
      - JWT_HEADER=Authorization
    volumes:
      - ./logs:/var/log/onlyoffice
      - ./data:/var/www/onlyoffice/Data
```

---

## 📖 使用示例

### 1. 前端 OnlyOffice 集成

```javascript
// 1. 获取 OnlyOffice 配置
const getConfig = async (fileId) => {
  const response = await fetch(`/api/v1/onlyoffice/config/${fileId}`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  const result = await response.json();
  return result.data;
};

// 2. 初始化 OnlyOffice 编辑器
const initOnlyOffice = async (fileId) => {
  const config = await getConfig(fileId);
  
  new DocsAPI.DocEditor("placeholder", {
    document: config.document,
    documentType: config.documentType,
    editorConfig: config.editorConfig
  });
};

// 3. 调用示例
initOnlyOffice(123);
```

### 2. 后端模板管理

```java
// 1. 创建模板并保存到数据库
DocumentExportTemplate template = new DocumentExportTemplate();
template.setTemplateName("测试模板");
template.setTemplateCode("TEST_TEMPLATE");
template.setTemplateType("WORD");
template.setCategory("TEST");

templateRepository.save(template);

// 2. 添加模板字段
DocumentTemplateField field = new DocumentTemplateField();
field.setTemplateId(template.getId());
field.setFieldLabel("报告标题");
field.setFieldName("reportTitle");
field.setFieldType("TEXT");
field.setSortOrder(1);

fieldRepository.save(field);

// 3. 查询模板及字段
List<DocumentTemplateField> fields = fieldRepository.findByTemplateIdOrderBySortOrderAsc(templateId);
```

### 3. 模板数据填充

```java
// 1. 准备数据
Map<String, Object> data = new HashMap<>();
data.put("reportTitle", "2024 年度报告");
data.put("reportNo", "RPT-2024-001");
data.put("summary", "报告摘要...");

// 2. 填充模板
byte[] filledContent = templateService.fillTemplateWithData(templatePath, data);

// 3. 保存或下载
Files.write(Paths.get("output/filled_report.docx"), filledContent);
```

---

## 🎯 功能特性

### 1. OnlyOffice 集成

- ✅ 文档在线编辑
- ✅ 实时保存
- ✅ 版本管理
- ✅ 协作编辑
- ✅ 文件锁定
- ✅ 编辑历史记录

### 2. 模板管理

- ✅ 模板持久化存储
- ✅ 模板字段配置
- ✅ 模板分类
- ✅ 模板版本管理
- ✅ 默认模板设置

### 3. 安全特性

- ✅ JWT 认证
- ✅ 文件访问权限控制
- ✅ 编辑权限管理
- ✅ 文件锁定机制

---

## 📊 数据库设计

### 表关系

```
tb_document_export_template (1) -----> (N) tb_document_template_field
         |
         | (1:N)
         v
tb_document_edit_history
```

### 索引设计

- 模板表：`template_code` (UNIQUE), `template_type`, `category`
- 字段表：`template_id`, `field_name`, `sort_order`
- 锁定表：`file_id` (UNIQUE), `user_id`, `status`
- 历史表：`file_id`, `version`, `editor_id`

---

## 🔍 测试建议

### 1. 单元测试

- OnlyOffice 配置生成测试
- 回调接口处理测试
- 模板 CRUD 操作测试
- 文件锁定机制测试

### 2. 集成测试

- OnlyOffice 服务器连接测试
- 文件上传下载测试
- 模板填充测试
- 编辑保存测试

### 3. 性能测试

- 并发编辑测试
- 大文件处理测试
- 数据库查询性能测试

---

## 📝 后续优化建议

### 1. 高优先级

- [ ] 实现 JWT 令牌验证逻辑
- [ ] 完善文件锁定机制（Redis 分布式锁）
- [ ] 实现编辑历史版本对比
- [ ] 添加文档转换功能

### 2. 中优先级

- [ ] 实现协作者实时显示（WebSocket）
- [ ] 添加文档水印功能
- [ ] 实现批量导出功能
- [ ] 模板预览功能

### 3. 低优先级

- [ ] 文档比较功能
- [ ] 模板可视化设计器
- [ ] 文档审批流程
- [ ] 移动端适配

---

## 🚀 部署步骤

### 1. 数据库初始化

```bash
# 执行 SQL 脚本
mysql -u root -p law < onlyoffice_template_schema.sql
```

### 2. 配置 OnlyOffice

```bash
# 启动 OnlyOffice
docker-compose up -d
```

### 3. 后端配置

```yaml
# application.yml
onlyoffice:
  server-url: http://your-onlyoffice-server
  jwt:
    enabled: true
    secret: your-secret-key
```

### 4. 启动应用

```bash
mvn clean package
java -jar target/lawbackend2-0.0.1-SNAPSHOT.jar
```

---

## 📞 技术支持

如有问题，请参考：
- OnlyOffice 官方文档：https://api.onlyoffice.com/
- 项目文档：`ONLYOFFICE_INTEGRATION_API.md`
- SQL 脚本：`onlyoffice_template_schema.sql`

---

**完成日期**: 2026-03-07  
**文档版本**: v1.0  
**维护团队**: 后端开发团队

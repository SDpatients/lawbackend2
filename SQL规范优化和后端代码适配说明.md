# SQL规范优化和后端代码适配说明

## 一、SQL文件优化说明

### 1.1 优化内容

根据`新建sql规范.md`文档的要求，对`EasyExcel.sql`进行了以下优化：

#### 1.1.1 表名规范化
- 所有表名统一使用`tb_`前缀
- `excel_import_template` → `tb_excel_import_template`
- `excel_import_history` → `tb_excel_import_history`
- `excel_field_validation_rule` → `tb_excel_field_validation_rule`

#### 1.1.2 添加表级注释
```sql
ALTER TABLE `tb_excel_import_template` COMMENT = 'Excel导入模板管理表，用于存储和管理Excel导入的模板配置';
ALTER TABLE `tb_excel_import_history` COMMENT = 'Excel导入历史记录表，用于记录每次Excel导入操作的详细信息，包括使用的模板、文件信息、处理结果等';
ALTER TABLE `tb_excel_field_validation_rule` COMMENT = 'Excel导入字段验证规则表，用于定义和管理Excel字段的数据验证规则，支持必填、格式、长度、范围、正则等多种验证类型';
```

#### 1.1.3 优化字段注释
- 字段注释更加详细，明确字段的含义和取值规范
- 例如：`field_mappings`字段注释从"字段映射配置"改为"字段映射配置(JSON格式)，存储Excel表头与系统字段的对应关系"

#### 1.1.4 添加性能优化索引
```sql
CREATE INDEX IF NOT EXISTS `idx_template_name` ON `tb_excel_import_template` (`template_name`);
CREATE INDEX IF NOT EXISTS `idx_template_code` ON `tb_excel_import_template` (`template_code`);
CREATE INDEX IF NOT EXISTS `idx_created_time` ON `tb_excel_import_template` (`created_time`);
CREATE INDEX IF NOT EXISTS `idx_is_active` ON `tb_excel_import_template` (`is_active`);
CREATE INDEX IF NOT EXISTS `idx_history_template_id` ON `tb_excel_import_history` (`template_id`);
CREATE INDEX IF NOT EXISTS `idx_history_imported_time` ON `tb_excel_import_history` (`imported_time`);
CREATE INDEX IF NOT EXISTS `idx_history_imported_by` ON `tb_excel_import_history` (`imported_by`);
CREATE INDEX IF NOT EXISTS `idx_history_import_status` ON `tb_excel_import_history` (`import_status`);
```

#### 1.1.5 外键约束优化
- 使用了正确的级联规则：`ON DELETE SET NULL ON UPDATE CASCADE`
- 确保数据一致性

### 1.2 符合规范检查

✅ 表命名规范：所有表名使用`tb_`前缀
✅ 字符集规范：使用`utf8mb4`字符集和`utf8mb4_unicode_ci`排序规则
✅ 存储引擎规范：使用`InnoDB`引擎
✅ 主键设计规范：所有表都有`id bigint AUTO_INCREMENT`主键
✅ 审计字段规范：包含了`created_time`、`updated_time`、`created_by`、`updated_by`字段
✅ 索引设计规范：包含了必要的索引和性能优化索引
✅ 外键约束规范：使用了正确的级联规则

## 二、后端代码适配说明

### 2.1 实体类修改

#### 2.1.1 ExcelImportTemplate.java
**修改内容**：更新表名以符合SQL规范
```java
@Table(name = "tb_excel_import_template")  // 修改前：excel_import_template
```

#### 2.1.2 新增实体类：ExcelImportHistory.java
**文件路径**：`src/main/java/com/lawbackend2/lawbackend2/entity/ExcelImportHistory.java`

**主要字段**：
- `id`：主键ID
- `templateId`：使用的模板ID
- `fileName`：文件名
- `fileSize`：文件大小(字节)
- `sheetIndex`：Sheet索引
- `totalRows`：总行数
- `successRows`：成功行数
- `failRows`：失败行数
- `importStatus`：导入状态
- `errorMessage`：错误信息
- `importedBy`：导入者用户ID
- `importedTime`：导入时间
- `processingTime`：处理耗时(毫秒)

#### 2.1.3 新增实体类：ExcelFieldValidationRule.java
**文件路径**：`src/main/java/com/lawbackend2/lawbackend2/entity/ExcelFieldValidationRule.java`

**主要字段**：
- `id`：主键ID
- `fieldName`：字段名
- `ruleType`：规则类型（REQUIRED、FORMAT、LENGTH、RANGE、PATTERN、CUSTOM）
- `ruleValue`：规则值(JSON格式)
- `errorMessage`：错误提示信息
- `isActive`：是否启用
- `priority`：优先级
- `createdBy`：创建者用户ID
- `createdTime`：创建时间

### 2.2 Repository接口

#### 2.2.1 新增：ExcelImportHistoryRepository.java
**文件路径**：`src/main/java/com/lawbackend2/lawbackend2/repository/ExcelImportHistoryRepository.java`

**主要方法**：
- `findByTemplateId(Long templateId)`：根据模板ID查询历史记录
- `findByImportedBy(Long importedBy)`：根据导入用户ID查询历史记录
- `findByImportStatus(String importStatus)`：根据导入状态查询历史记录
- `findByImportedByAndTimeRange(...)`：根据用户ID和时间范围查询历史记录
- `findByImportStatusOrderByImportedTimeDesc(...)`：根据状态查询并按时间倒序排列

#### 2.2.2 新增：ExcelFieldValidationRuleRepository.java
**文件路径**：`src/main/java/com/lawbackend2/lawbackend2/repository/ExcelFieldValidationRuleRepository.java`

**主要方法**：
- `findByFieldName(String fieldName)`：根据字段名查询验证规则
- `findByIsActive(Boolean isActive)`：根据启用状态查询验证规则
- `findByFieldNameAndIsActive(...)`：根据字段名和启用状态查询验证规则
- `findByFieldNameAndIsActiveOrderByPriority(...)`：根据字段名查询启用的规则并按优先级排序
- `findAllActiveRulesOrderByPriority()`：查询所有启用的规则并按优先级排序

### 2.3 DTO类

#### 2.3.1 新增：ExcelImportHistoryResponse.java
**文件路径**：`src/main/java/com/lawbackend2/lawbackend2/dto/ExcelImportHistoryResponse.java`

**主要字段**：
- `id`：历史记录ID
- `templateId`：模板ID
- `templateName`：模板名称
- `fileName`：文件名
- `fileSize`：文件大小
- `sheetIndex`：Sheet索引
- `totalRows`：总行数
- `successRows`：成功行数
- `failRows`：失败行数
- `importStatus`：导入状态
- `errorMessage`：错误信息
- `importedBy`：导入者用户ID
- `importedByName`：导入者用户名
- `importedTime`：导入时间
- `processingTime`：处理耗时

#### 2.3.2 新增：ExcelFieldValidationRuleResponse.java
**文件路径**：`src/main/java/com/lawbackend2/lawbackend2/dto/ExcelFieldValidationRuleResponse.java`

**主要字段**：
- `id`：规则ID
- `fieldName`：字段名
- `ruleType`：规则类型
- `ruleValue`：规则值
- `errorMessage`：错误信息
- `isActive`：是否启用
- `priority`：优先级
- `createdBy`：创建者用户ID
- `createdByName`：创建者用户名
- `createdTime`：创建时间

#### 2.3.3 新增：ExcelFieldValidationRuleCreateRequest.java
**文件路径**：`src/main/java/com/lawbackend2/lawbackend2/dto/ExcelFieldValidationRuleCreateRequest.java`

#### 2.3.4 新增：ExcelFieldValidationRuleUpdateRequest.java
**文件路径**：`src/main/java/com/lawbackend2/lawbackend2/dto/ExcelFieldValidationRuleUpdateRequest.java`

### 2.4 Service层

#### 2.4.1 新增：ExcelImportHistoryService.java
**文件路径**：`src/main/java/com/lawbackend2/lawbackend2/service/ExcelImportHistoryService.java`

**主要方法**：
- `createHistory(...)`：创建导入历史记录
- `updateHistory(...)`：更新导入历史记录
- `getHistoryById(Long id)`：根据ID查询历史记录
- `getHistoriesByTemplateId(Long templateId)`：根据模板ID查询历史记录
- `getHistoriesByImportedBy(Long importedBy)`：根据导入用户ID查询历史记录
- `getHistoriesByImportStatus(String importStatus)`：根据导入状态查询历史记录
- `getHistoriesByImportedByAndTimeRange(...)`：根据用户ID和时间范围查询历史记录
- `getAllHistories(Pageable pageable)`：分页查询所有历史记录
- `deleteHistory(Long id)`：删除历史记录

#### 2.4.2 新增：ExcelImportHistoryServiceImpl.java
**文件路径**：`src/main/java/com/lawbackend2/lawbackend2/service/impl/ExcelImportHistoryServiceImpl.java`

**实现说明**：
- 完整实现了ExcelImportHistoryService接口的所有方法
- 使用ObjectMapper进行JSON序列化
- 包含详细的日志记录
- 支持分页查询
- 时间格式化为"yyyy-MM-dd HH:mm:ss"

#### 2.4.3 新增：ExcelFieldValidationRuleService.java
**文件路径**：`src/main/java/com/lawbackend2/lawbackend2/service/ExcelFieldValidationRuleService.java`

**主要方法**：
- `createRule(...)`：创建验证规则
- `updateRule(...)`：更新验证规则
- `getRuleById(Long id)`：根据ID查询验证规则
- `getRulesByFieldName(String fieldName)`：根据字段名查询验证规则
- `getActiveRulesByFieldName(String fieldName)`：根据字段名查询启用的验证规则
- `getAllActiveRules()`：查询所有启用的验证规则
- `getAllRules()`：查询所有验证规则
- `deleteRule(Long id)`：删除验证规则
- `toggleRuleStatus(Long id, Boolean isActive)`：切换验证规则状态

#### 2.4.4 新增：ExcelFieldValidationRuleServiceImpl.java
**文件路径**：`src/main/java/com/lawbackend2/lawbackend2/service/impl/ExcelFieldValidationRuleServiceImpl.java`

**实现说明**：
- 完整实现了ExcelFieldValidationRuleService接口的所有方法
- 包含详细的日志记录
- 支持验证规则的启用/禁用切换
- 支持按优先级排序查询

### 2.5 Controller层

#### 2.5.1 新增：ExcelImportHistoryController.java
**文件路径**：`src/main/java/com/lawbackend2/lawbackend2/controller/ExcelImportHistoryController.java`

**API接口**：
- `GET /api/excel-import-history`：分页查询所有导入历史记录
- `GET /api/excel-import-history/{id}`：根据ID查询导入历史记录
- `GET /api/excel-import-history/template/{templateId}`：根据模板ID查询导入历史记录
- `GET /api/excel-import-history/user/{importedBy}`：根据导入用户ID查询导入历史记录
- `GET /api/excel-import-history/status/{importStatus}`：根据导入状态查询导入历史记录
- `GET /api/excel-import-history/user/{importedBy}/range`：根据用户ID和时间范围查询导入历史记录
- `DELETE /api/excel-import-history/{id}`：删除导入历史记录

#### 2.5.2 新增：ExcelFieldValidationRuleController.java
**文件路径**：`src/main/java/com/lawbackend2/lawbackend2/controller/ExcelFieldValidationRuleController.java`

**API接口**：
- `POST /api/excel-field-validation-rules`：创建字段验证规则
- `PUT /api/excel-field-validation-rules/{id}`：更新字段验证规则
- `GET /api/excel-field-validation-rules/{id}`：根据ID查询字段验证规则
- `GET /api/excel-field-validation-rules`：查询所有字段验证规则
- `GET /api/excel-field-validation-rules/active`：查询所有启用的字段验证规则
- `GET /api/excel-field-validation-rules/field/{fieldName}`：根据字段名查询验证规则
- `GET /api/excel-field-validation-rules/field/{fieldName}/active`：根据字段名查询启用的验证规则
- `PATCH /api/excel-field-validation-rules/{id}/status`：切换字段验证规则状态
- `DELETE /api/excel-field-validation-rules/{id}`：删除字段验证规则

## 三、数据库迁移说明

### 3.1 执行SQL脚本

请按照以下步骤执行SQL脚本：

1. 备份现有数据库（如果存在）
2. 执行优化后的`EasyExcel.sql`脚本
3. 验证表结构是否正确创建
4. 验证默认数据是否正确插入
5. 验证索引是否正确创建

### 3.2 验证SQL执行

```sql
-- 查看所有表
SHOW TABLES LIKE 'tb_excel%';

-- 查看表结构
DESC tb_excel_import_template;
DESC tb_excel_import_history;
DESC tb_excel_field_validation_rule;

-- 查看索引
SHOW INDEX FROM tb_excel_import_template;
SHOW INDEX FROM tb_excel_import_history;
SHOW INDEX FROM tb_excel_field_validation_rule;

-- 查看默认数据
SELECT * FROM tb_excel_import_template;
SELECT * FROM tb_excel_field_validation_rule;
```

## 四、测试建议

### 4.1 单元测试

建议为以下Service实现类编写单元测试：
- ExcelImportHistoryServiceImpl
- ExcelFieldValidationRuleServiceImpl

### 4.2 集成测试

建议测试以下场景：
1. 创建导入模板并使用该模板导入Excel
2. 查询导入历史记录
3. 创建字段验证规则并验证数据
4. 切换验证规则状态
5. 删除导入历史记录和验证规则

### 4.3 API测试

建议使用Postman或Swagger UI测试所有新增的API接口。

## 五、注意事项

1. **表名变更**：所有表名都使用了`tb_`前缀，确保前端API调用时使用正确的表名
2. **外键约束**：`tb_excel_import_history`表中的`template_id`字段有外键约束，删除模板时会将历史记录的`template_id`设置为NULL
3. **JSON字段**：`field_mappings`和`rule_value`字段使用JSON类型，确保Java代码中使用ObjectMapper正确处理
4. **时间格式**：所有时间字段在响应中格式化为"yyyy-MM-dd HH:mm:ss"
5. **分页查询**：导入历史记录支持分页查询，前端需要传递page和size参数
6. **优先级排序**：验证规则查询时按优先级升序排列，数字越小优先级越高

## 六、后续优化建议

1. **添加缓存**：对频繁查询的模板和验证规则添加缓存
2. **异步处理**：对于大文件导入，建议使用异步处理
3. **批量操作**：添加批量创建和更新验证规则的接口
4. **导入导出**：添加验证规则的导入导出功能
5. **权限控制**：添加API接口的权限控制
6. **日志审计**：添加更详细的操作日志和审计功能

# OnlyOffice 集成 API 文档

## 📋 概述

本文档说明了当前后端为前端 OnlyOffice 集成提供的 API 接口，以及未来建议开发的 API。

---

## 一、当前已提供的 API（可直接用于 OnlyOffice 集成）

### 1.1 文件管理相关 API

**基础路径**: `/api/v1/file`

#### 1.1.1 文件上传

```http
POST /api/v1/file/upload
Content-Type: multipart/form-data
Authorization: Bearer {token}

参数:
- file: 文件对象（必填）
- bizType: 业务类型（必填）
- bizId: 业务 ID（必填）
```

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "id": 123,
    "originalFileName": "合同.docx",
    "storedFileName": "uuid_contract.docx",
    "filePath": "C:\\law-upload\\2024\\03\\uuid_contract.docx",
    "fileSize": 102400,
    "mimeType": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    "bizType": "CASE_DOCUMENT",
    "bizId": "456",
    "status": "ACTIVE",
    "createTime": "2024-03-07T10:00:00"
  }
}
```

**OnlyOffice 集成说明**: 
- 前端上传文件后获取 fileId 和文件路径
- 用于 OnlyOffice 编辑前的文件准备

---

#### 1.1.2 文件下载

```http
GET /api/v1/file/download/{fileId}
Authorization: Bearer {token}
```

**响应**: 文件流（二进制数据）

**OnlyOffice 集成说明**:
- OnlyOffice 通过此接口获取文件内容进行编辑
- 需要返回正确的 Content-Type 和 Content-Disposition

---

#### 1.1.3 获取文件信息

```http
GET /api/v1/file/{fileId}
Authorization: Bearer {token}
```

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "id": 123,
    "originalFileName": "合同.docx",
    "storedFileName": "uuid_contract.docx",
    "filePath": "C:\\law-upload\\2024\\03\\uuid_contract.docx",
    "fileSize": 102400,
    "mimeType": "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
  }
}
```

**OnlyOffice 集成说明**:
- 获取文件详细信息，包括文件路径
- 用于 OnlyOffice 配置文档地址

---

#### 1.1.4 文件列表（分页）

```http
GET /api/v1/file/list
Authorization: Bearer {token}

参数:
- pageNum: 页码（默认 1）
- pageSize: 每页大小（默认 10）
- bizType: 业务类型（可选）
- bizId: 业务 ID（可选）
- status: 状态（可选）
```

**OnlyOffice 集成说明**:
- 获取某个业务下的所有文件列表
- 用于 OnlyOffice 文件选择界面

---

#### 1.1.5 删除文件

```http
DELETE /api/v1/file/{fileId}
Authorization: Bearer {token}
```

---

#### 1.1.6 批量删除文件

```http
DELETE /api/v1/file/batch
Content-Type: application/json
Authorization: Bearer {token}

Body: [123, 456, 789]
```

---

#### 1.1.7 文件重命名

```http
PUT /api/v1/file/{fileId}/rename
Authorization: Bearer {token}

参数:
- newFileName: 新文件名
```

**OnlyOffice 集成说明**:
- OnlyOffice 编辑保存后，可以更新文件名

---

#### 1.1.8 更新文件状态

```http
PUT /api/v1/file/{fileId}/status
Authorization: Bearer {token}

参数:
- status: 状态（ACTIVE, DELETED, ARCHIVED）
```

---

### 1.2 Word 模板生成 API

**基础路径**: `/api/v1/api/template`

#### 1.2.1 生成数据报告模板

```http
POST /api/v1/api/template/generate/data-report?templateName={name}
Authorization: Bearer {token}
```

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "templatePath": "templates/my_report_template.docx",
    "templateName": "my_report",
    "message": "数据报告模板生成成功"
  }
}
```

**OnlyOffice 集成说明**:
- 生成 Word 模板供 OnlyOffice 编辑
- 模板包含预定义的占位符和格式

---

#### 1.2.2 生成自定义模板

```http
POST /api/v1/api/template/generate/custom
Authorization: Bearer {token}

参数:
- templateName: 模板名称
- title: 模板标题
- sections: 章节数组
```

**请求示例**:
```json
{
  "templateName": "custom_report",
  "title": "自定义报告",
  "sections": ["第一章：概述", "第二章：详情", "第三章：总结"]
}
```

---

#### 1.2.3 填充模板数据

```http
POST /api/v1/api/template/fill?templatePath={path}
Content-Type: application/json
Authorization: Bearer {token}

Body:
{
  "reportTitle": "2024 年度报告",
  "reportNo": "RPT-2024-001",
  "summary": "报告摘要..."
}
```

**响应**: 填充后的 Word 文档（二进制流）

**OnlyOffice 集成说明**:
- 将数据填充到模板中
- 生成可编辑的 Word 文档

---

#### 1.2.4 导出示例报告

```http
POST /api/v1/api/template/export/sample
Authorization: Bearer {token}
```

**响应**: Word 文档（二进制流）

---

#### 1.2.5 生成模板并填充数据

```http
POST /api/v1/api/template/generate-and-fill?templateType={type}
Content-Type: application/json
Authorization: Bearer {token}

Body:
{
  "reportTitle": "报告标题",
  "reportNo": "编号",
  ...
}
```

**templateType 可选值**:
- `legal` - 法律案件报告
- `project` - 项目进度报告
- `meeting` - 会议纪要
- `default` - 数据报告

---

#### 1.2.6 获取示例数据

```http
GET /api/v1/api/template/sample-data
Authorization: Bearer {token}
```

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "reportTitle": "2024 年度数据分析报告",
    "reportNo": "RPT-2024-001",
    "reportDate": "2024-03-07",
    "company": "某某科技公司",
    "creator": "张三",
    "summary": "本报告详细分析了...",
    "conclusion": "基于以上数据分析..."
  }
}
```

---

#### 1.2.7 清理临时模板

```http
DELETE /api/v1/api/template/cleanup
Authorization: Bearer {token}
```

---

### 1.3 案件任务文件管理 API

**基础路径**: `/api/v1/files`

#### 1.3.1 上传案件任务文件

```http
POST /api/v1/files/case-task/upload
Authorization: Bearer {token}

参数:
- file: 文件对象
- taskId: 任务 ID
- description: 文件描述
```

---

#### 1.3.2 获取案件任务文件

```http
GET /api/v1/files/case-task/files?taskId={taskId}
Authorization: Bearer {token}
```

---

#### 1.3.3 删除案件任务文件

```http
DELETE /api/v1/files/case-task/files?fileId={fileId}
Authorization: Bearer {token}
```

---

## 二、OnlyOffice 集成完整流程示例

### 2.1 前端 OnlyOffice 集成步骤

```javascript
// 1. 上传文件到后端
const uploadFile = async (file, bizType, bizId) => {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('bizType', bizType);
  formData.append('bizId', bizId);
  
  const response = await fetch('/api/v1/file/upload', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    },
    body: formData
  });
  
  const result = await response.json();
  return result.data; // 返回文件信息，包含 fileId 和 filePath
};

// 2. 获取文件信息，配置 OnlyOffice
const configureOnlyOffice = async (fileId) => {
  const fileInfoResponse = await fetch(`/api/v1/file/${fileId}`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  const fileInfo = (await fileInfoResponse.json()).data;
  
  // 配置 OnlyOffice
  const config = {
    document: {
      fileType: fileInfo.originalFileName.split('.').pop(),
      key: fileInfo.storedFileName, // 使用存储文件名作为文档 key
      title: fileInfo.originalFileName,
      url: `${backendUrl}/api/v1/file/download/${fileId}` // OnlyOffice 服务访问的 URL
    },
    documentType: 'word', // word, cell, slide
    editorConfig: {
      callbackUrl: `${backendUrl}/api/v1/onlyoffice/callback?fileId=${fileId}`, // 回调地址
      user: {
        id: 'user-123',
        name: '张三'
      }
    }
  };
  
  return config;
};

// 3. OnlyOffice 回调接口（需要后端实现）
// POST /api/v1/onlyoffice/callback
// 接收 OnlyOffice 编辑后的数据并保存
```

### 2.2 使用 Word 模板生成器

```javascript
// 1. 生成模板
const generateTemplate = async (templateName) => {
  const response = await fetch(`/api/v1/api/template/generate/data-report?templateName=${templateName}`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  const result = await response.json();
  return result.data.templatePath;
};

// 2. 填充数据
const fillTemplate = async (templatePath, data) => {
  const response = await fetch(`/api/v1/api/template/fill?templatePath=${templatePath}`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  });
  
  // 返回填充后的文件 Blob
  const blob = await response.blob();
  return blob;
};

// 3. 上传填充后的文件到 OnlyOffice
const uploadToOnlyOffice = async (blob, fileName) => {
  const file = new File([blob], fileName, { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });
  return await uploadFile(file, 'CASE_DOCUMENT', 'case-123');
};
```

---

## 三、建议开发的 OnlyOffice 专用 API

### 3.1 高优先级（建议优先开发）

#### 1. OnlyOffice 回调接口

```http
POST /api/v1/onlyoffice/callback
Content-Type: application/json

参数:
- action: 操作类型 (0-无操作，1-下载，2-编辑，3-保存)
- data: 编辑后的数据
  - title: 文件名
  - url: 文件下载地址
  - users: 编辑用户列表
- changes: 变更历史
- token: OnlyOffice 签名令牌
```

**响应**:
```json
{
  "error": 0,
  "fileType": "docx"
}
```

**说明**: 
- OnlyOffice 编辑完成后回调此接口
- 后端需要下载编辑后的文件并保存到服务器
- 更新文件记录

**实现要点**:
```java
@PostMapping("/onlyoffice/callback")
public Result<Map<String, Object>> handleOnlyOfficeCallback(
    @RequestParam Long fileId,
    @RequestBody OnlyOfficeCallbackRequest request) {
    
    // 1. 验证签名
    // 2. 根据 action 处理不同逻辑
    // 3. 如果是保存操作，下载文件并更新
    // 4. 返回响应
}
```

---

#### 2. 获取 OnlyOffice 配置接口

```http
GET /api/v1/onlyoffice/config/{fileId}
Authorization: Bearer {token}
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "document": {
      "fileType": "docx",
      "key": "unique-key-123",
      "title": "合同.docx",
      "url": "http://backend/api/v1/file/download/123",
      "permissions": {
        "edit": true,
        "download": true,
        "print": true
      }
    },
    "documentType": "word",
    "editorConfig": {
      "callbackUrl": "http://backend/api/v1/onlyoffice/callback?fileId=123",
      "user": {
        "id": "user-123",
        "name": "张三"
      },
      "customization": {
        "autosave": true,
        "forcesave": true
      }
    }
  }
}
```

**说明**:
- 前端调用此接口获取 OnlyOffice 完整配置
- 简化前端集成复杂度

---

#### 3. 文件锁定接口

```http
POST /api/v1/onlyoffice/lock/{fileId}
Authorization: Bearer {token}
```

```http
POST /api/v1/onlyoffice/unlock/{fileId}
Authorization: Bearer {token}
```

**说明**:
- 防止多人同时编辑同一文件
- 编辑时锁定，保存后解锁

---

#### 4. 编辑历史记录接口

```http
GET /api/v1/onlyoffice/history/{fileId}
Authorization: Bearer {token}
```

**响应**:
```json
{
  "code": 200,
  "data": [
    {
      "version": 1,
      "editor": "张三",
      "editTime": "2024-03-07T10:00:00",
      "changes": "创建了文档"
    },
    {
      "version": 2,
      "editor": "李四",
      "editTime": "2024-03-07T14:30:00",
      "changes": "修改了第 3 章内容"
    }
  ]
}
```

---

### 3.2 中优先级（建议后续开发）

#### 5. 协作编辑接口

```http
GET /api/v1/onlyoffice/collaborators/{fileId}
Authorization: Bearer {token}
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "editingUsers": [
      {
        "id": "user-123",
        "name": "张三",
        "editStartTime": "2024-03-07T10:00:00"
      }
    ],
    "viewingUsers": [...]
  }
}
```

---

#### 6. 批量导出接口

```http
POST /api/v1/onlyoffice/batch-export
Content-Type: application/json
Authorization: Bearer {token}

Body:
{
  "fileIds": [123, 456, 789],
  "exportFormat": "pdf" // pdf, docx, odt 等
}
```

---

#### 7. 模板变量替换接口

```http
POST /api/v1/onlyoffice/template/replace
Content-Type: application/json
Authorization: Bearer {token}

Body:
{
  "templatePath": "templates/contract.docx",
  "variables": {
    "{{partyA}}": "甲公司",
    "{{partyB}}": "乙公司",
    "{{amount}}": "100 万元"
  },
  "outputPath": "output/contract_filled.docx"
}
```

---

#### 8. 文档比较接口

```http
POST /api/v1/onlyoffice/compare
Content-Type: application/json
Authorization: Bearer {token}

Body:
{
  "originalFileId": 123,
  "modifiedFileId": 456,
  "outputPath": "output/comparison.docx"
}
```

---

### 3.3 低优先级（可选功能）

#### 9. 文档水印接口

```http
POST /api/v1/onlyoffice/watermark
Authorization: Bearer {token}

参数:
- fileId: 文件 ID
- watermarkText: 水印文本
- position: 位置
```

---

#### 10. 文档转换接口

```http
POST /api/v1/onlyoffice/convert
Content-Type: application/json
Authorization: Bearer {token}

Body:
{
  "fileId": 123,
  "targetFormat": "pdf" // pdf, docx, txt 等
}
```

---

## 四、OnlyOffice 服务器配置建议

### 4.1 Docker 部署配置

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

### 4.2 后端配置

```yaml
# application.yml
onlyoffice:
  server-url: http://localhost:8081
  jwt:
    enabled: true
    secret: your-secret-key
    header: Authorization
  callback:
    url: http://backend-server/api/v1/onlyoffice/callback
```

---

## 五、安全建议

### 5.1 JWT 认证

- 所有 OnlyOffice API 都需要 JWT 认证
- OnlyOffice 回调接口需要验证签名

### 5.2 文件访问控制

- 验证用户是否有权限访问该文件
- 检查文件所属业务权限

### 5.3 文件类型限制

```java
private static final Set<String> ALLOWED_FILE_TYPES = Set.of(
    "docx", "doc", "xlsx", "xls", "pptx", "ppt"
);

public boolean isAllowedFileType(String fileType) {
    return ALLOWED_FILE_TYPES.contains(fileType.toLowerCase());
}
```

---

## 六、性能优化建议

### 6.1 文件存储

- 使用对象存储（如 MinIO、阿里云 OSS）
- 配置 CDN 加速文件访问

### 6.2 缓存策略

- 缓存 OnlyOffice 配置信息
- 缓存文件元数据

### 6.3 异步处理

- 文件转换使用异步处理
- 大批量操作使用消息队列

---

## 七、总结

### 当前可用 API

✅ 文件上传、下载、删除、重命名  
✅ 文件列表查询  
✅ Word 模板生成和填充  
✅ 案件任务文件管理  

### 建议优先开发

🔲 OnlyOffice 回调接口  
🔲 OnlyOffice 配置接口  
🔲 文件锁定接口  
🔲 编辑历史记录  

### 后续扩展

🔲 协作编辑  
🔲 批量导出  
🔲 文档比较  
🔲 文档转换  

---

**文档版本**: v1.0  
**更新日期**: 2026-03-07  
**维护团队**: 后端开发团队

# OnlyOffice 集成 - 快速使用指南

## 🚀 5 分钟快速上手

### 第一步：执行 SQL 脚本

```bash
# 登录 MySQL
mysql -u root -p

# 执行脚本
use law;
source D:/Ai/lawbackend2/onlyoffice_template_schema.sql;
```

### 第二步：配置 OnlyOffice

```yaml
# application.yml
onlyoffice:
  server-url: http://localhost:8081  # OnlyOffice 地址
  jwt:
    enabled: false  # 开发环境可关闭
    secret: your-secret-key
  callback:
    url: http://192.168.0.151:8080/api/v1/onlyoffice/callback
```

### 第三步：启动应用

```bash
# 项目已启动，访问 http://192.168.0.151:8080
```

### 第四步：测试 API

#### 1. 获取 OnlyOffice 配置

```bash
curl -X GET "http://192.168.0.151:8080/api/v1/onlyoffice/config/1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "document": {
      "fileType": "docx",
      "key": "stored_file_name.docx",
      "title": "合同.docx",
      "url": "http://192.168.0.151:8080/api/v1/file/download/1",
      "permissions": {
        "edit": true,
        "download": true,
        "print": true,
        "copy": true
      }
    },
    "documentType": "word",
    "editorConfig": {
      "callbackUrl": "http://192.168.0.151:8080/api/v1/onlyoffice/callback?fileId=1",
      "user": {
        "id": "1",
        "name": "张三"
      },
      "customization": {
        "autosave": true,
        "forcesave": true,
        "trackChanges": true
      }
    }
  }
}
```

#### 2. 前端集成 OnlyOffice

```html
<!DOCTYPE html>
<html>
<head>
  <title>OnlyOffice 集成示例</title>
  <script src="http://localhost:8081/web-apps/apps/api/documents/api.js"></script>
</head>
<body>
  <div id="placeholder" style="height: 600px;"></div>
  
  <script>
    // 获取配置
    fetch('/api/v1/onlyoffice/config/1', {
      headers: {
        'Authorization': 'Bearer ' + getToken()
      }
    })
    .then(res => res.json())
    .then(result => {
      // 初始化 OnlyOffice
      new DocsAPI.DocEditor("placeholder", result.data);
    });
    
    function getToken() {
      // 返回你的 JWT token
      return 'YOUR_TOKEN';
    }
  </script>
</body>
</html>
```

---

## 📋 完整使用流程

### 场景 1：在线编辑文档

```javascript
// 1. 上传文件
async function uploadFile(file) {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('bizType', 'CASE_DOCUMENT');
  formData.append('bizId', 'case-123');
  
  const res = await fetch('/api/v1/file/upload', {
    method: 'POST',
    headers: {
      'Authorization': 'Bearer ' + token
    },
    body: formData
  });
  
  const result = await res.json();
  return result.data.id; // 返回 fileId
}

// 2. 打开编辑器
async function openEditor(fileId) {
  const res = await fetch(`/api/v1/onlyoffice/config/${fileId}`, {
    headers: {
      'Authorization': 'Bearer ' + token
    }
  });
  
  const config = (await res.json()).data;
  
  // 初始化 OnlyOffice
  new DocsAPI.DocEditor("placeholder", config);
}

// 使用示例
const fileId = await uploadFile(myFile);
await openEditor(fileId);
```

### 场景 2：使用模板生成报告

```javascript
// 1. 生成模板
async function generateTemplate(templateName) {
  const res = await fetch(`/api/v1/api/template/generate/data-report?templateName=${templateName}`, {
    method: 'POST',
    headers: {
      'Authorization': 'Bearer ' + token
    }
  });
  
  const result = await res.json();
  return result.data.templatePath;
}

// 2. 填充数据
async function fillTemplate(templatePath, data) {
  const res = await fetch(`/api/v1/api/template/fill?templatePath=${templatePath}`, {
    method: 'POST',
    headers: {
      'Authorization': 'Bearer ' + token,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  });
  
  return await res.blob(); // 返回填充后的文件
}

// 3. 上传到 OnlyOffice 编辑
async function editFilledTemplate() {
  const templatePath = await generateTemplate('my_report');
  
  const data = {
    reportTitle: '2024 年度报告',
    reportNo: 'RPT-2024-001',
    summary: '报告摘要...'
  };
  
  const blob = await fillTemplate(templatePath, data);
  const file = new File([blob], 'report.docx');
  const fileId = await uploadFile(file);
  
  await openEditor(fileId);
}
```

---

## 🎯 常用 API 参考

### 文件管理

```javascript
// 上传文件
POST /api/v1/file/upload
Content-Type: multipart/form-data

// 下载文件
GET /api/v1/file/download/{fileId}

// 获取文件信息
GET /api/v1/file/{fileId}

// 文件列表
GET /api/v1/file/list?pageNum=1&pageSize=10

// 删除文件
DELETE /api/v1/file/{fileId}
```

### 模板管理

```javascript
// 生成数据报告模板
POST /api/v1/api/template/generate/data-report?templateName=xxx

// 生成自定义模板
POST /api/v1/api/template/generate/custom
{
  "templateName": "custom",
  "title": "我的报告",
  "sections": ["第一章", "第二章"]
}

// 填充模板数据
POST /api/v1/api/template/fill?templatePath=xxx
{
  "reportTitle": "标题",
  "reportNo": "编号"
}

// 获取示例数据
GET /api/v1/api/template/sample-data
```

### OnlyOffice 集成

```javascript
// 获取编辑配置
GET /api/v1/onlyoffice/config/{fileId}

// 回调接口（OnlyOffice 调用）
POST /api/v1/onlyoffice/callback?fileId=xxx

// 锁定文件
POST /api/v1/onlyoffice/lock/{fileId}

// 解锁文件
POST /api/v1/onlyoffice/unlock/{fileId}

// 获取编辑历史
GET /api/v1/onlyoffice/history/{fileId}
```

---

## 🔧 常见问题

### Q1: OnlyOffice 无法连接？

**A**: 检查 OnlyOffice 服务是否启动
```bash
docker ps | grep onlyoffice
docker logs onlyoffice-document-server
```

### Q2: 回调接口不工作？

**A**: 确保回调 URL 可以从 OnlyOffice 服务器访问
- 开发环境使用 ngrok 或内网穿透
- 生产环境配置正确的域名和 HTTPS

### Q3: 文件保存失败？

**A**: 检查文件路径权限
```bash
# Linux/Mac
chmod 755 /path/to/upload/dir

# Windows
右键文件夹 -> 属性 -> 安全 -> 编辑
```

### Q4: JWT 验证失败？

**A**: 确保 OnlyOffice 和后端的 JWT 配置一致
```yaml
# OnlyOffice docker-compose.yml
environment:
  - JWT_SECRET=your-secret-key

# application.yml
onlyoffice:
  jwt:
    secret: your-secret-key
```

---

## 📊 数据库查询

### 查询所有模板

```sql
SELECT * FROM tb_document_export_template WHERE is_deleted = 0;
```

### 查询模板字段

```sql
SELECT f.* 
FROM tb_document_template_field f
JOIN tb_document_export_template t ON f.template_id = t.id
WHERE t.template_code = 'DATA_REPORT' AND f.is_deleted = 0;
```

### 查询编辑历史

```sql
SELECT deh.*, u.real_name
FROM tb_document_edit_history deh
LEFT JOIN tb_user u ON deh.editor_id = u.id
WHERE deh.file_id = 123
ORDER BY deh.version DESC;
```

### 查询当前锁定的文件

```sql
SELECT fl.*, fr.original_file_name
FROM tb_file_lock fl
JOIN tb_file_record fr ON fl.file_id = fr.id
WHERE fl.status = 'LOCKED';
```

---

## 🎨 前端集成示例代码

### React 组件

```jsx
import React, { useEffect, useRef } from 'react';

const OnlyOfficeEditor = ({ fileId }) => {
  const editorRef = useRef(null);

  useEffect(() => {
    const initEditor = async () => {
      // 获取配置
      const res = await fetch(`/api/v1/onlyoffice/config/${fileId}`);
      const result = await res.json();
      
      // 初始化编辑器
      new window.DocsAPI.DocEditor(editorRef.current, result.data);
    };

    if (fileId) {
      initEditor();
    }

    return () => {
      // 清理编辑器
      if (editorRef.current) {
        editorRef.current.innerHTML = '';
      }
    };
  }, [fileId]);

  return <div ref={editorRef} style={{ height: '600px' }} />;
};

export default OnlyOfficeEditor;
```

### Vue 组件

```vue
<template>
  <div ref="editorContainer" style="height: 600px;"></div>
</template>

<script>
export default {
  props: ['fileId'],
  mounted() {
    this.initEditor();
  },
  methods: {
    async initEditor() {
      const res = await this.$axios.get(`/api/v1/onlyoffice/config/${this.fileId}`);
      const config = res.data.data;
      
      new DocsAPI.DocEditor(this.$refs.editorContainer, config);
    }
  }
};
</script>
```

---

## 📞 获取帮助

- **完整文档**: `ONLYOFFICE_INTEGRATION_API.md`
- **实现总结**: `ONLYOFFICE_IMPLEMENTATION_SUMMARY.md`
- **SQL 脚本**: `onlyoffice_template_schema.sql`
- **OnlyOffice 文档**: https://api.onlyoffice.com/

---

**立即开始使用吧！** 🎉

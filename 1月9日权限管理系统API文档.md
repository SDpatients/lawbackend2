# 权限管理系统 API 文档

## 概述

本文档描述了完整的权限管理相关CRUD API接口系统，该系统操作以下数据库表：
- tb_role（角色表）
- tb_role_permission（角色权限关联表）
- tb_permission（权限表）
- tb_user（用户表）
- tb_user_role（用户角色关联表）

## 通用说明

### 基础URL
```
http://localhost:8080/api
```

### 认证方式
所有API接口（除登录注册外）需要在请求头中携带JWT Token：
```
Authorization: Bearer {token}
```

### 统一响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### HTTP状态码
- 200: 请求成功
- 400: 请求参数错误
- 401: 未授权或Token过期
- 403: 无权限访问
- 404: 资源不存在
- 500: 服务器内部错误

### 权限说明
- system:user:add - 添加用户权限
- system:user:query - 查询用户权限
- system:user:edit - 编辑用户权限
- system:user:delete - 删除用户权限
- system:user:assign - 分配用户角色权限
- system:role:add - 添加角色权限
- system:role:query - 查询角色权限
- system:role:edit - 编辑角色权限
- system:role:delete - 删除角色权限
- system:role:assign - 分配角色权限权限
- system:permission:add - 添加权限权限
- system:permission:query - 查询权限权限
- system:permission:edit - 编辑权限权限
- system:permission:delete - 删除权限权限

---

## 1. 用户管理API

### 1.1 创建用户

**接口地址：** `POST /api/users`

**权限要求：** system:user:add

**请求参数：**
```json
{
  "username": "testuser",
  "password": "123456",
  "realName": "测试用户",
  "mobile": "13800138000",
  "email": "test@example.com",
  "phone": "021-12345678",
  "status": "ACTIVE"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "testuser",
    "realName": "测试用户",
    "mobile": "13800138000",
    "email": "test@example.com",
    "phone": "021-12345678",
    "isValid": "1",
    "status": "ACTIVE",
    "loginType": "1",
    "lastLoginTime": null,
    "lastLoginIp": null,
    "loginCount": 0,
    "createTime": "2026-01-09T10:00:00",
    "updateTime": "2026-01-09T10:00:00"
  }
}
```

### 1.2 获取用户列表

**接口地址：** `GET /api/users`

**权限要求：** system:user:query

**请求参数：**
- page: 页码（默认1）
- size: 每页数量（默认10）
- sortField: 排序字段（默认createTime）
- sortOrder: 排序方向（默认DESC）
- keyword: 搜索关键词（可选）
- status: 用户状态（可选）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "page": 1,
    "size": 10,
    "totalPages": 10,
    "users": [...]
  }
}
```

### 1.3 获取单个用户

**接口地址：** `GET /api/users/{id}`

**权限要求：** system:user:query

**路径参数：**
- id: 用户ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "testuser",
    "realName": "测试用户",
    ...
  }
}
```

### 1.4 更新用户

**接口地址：** `PUT /api/users/{id}`

**权限要求：** system:user:edit

**路径参数：**
- id: 用户ID

**请求参数：**
```json
{
  "username": "testuser2",
  "realName": "测试用户2",
  "mobile": "13800138001",
  "email": "test2@example.com",
  "phone": "021-87654321",
  "password": "newpassword",
  "status": "ACTIVE"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {...}
}
```

### 1.5 部分更新用户

**接口地址：** `PATCH /api/users/{id}`

**权限要求：** system:user:edit

**路径参数：**
- id: 用户ID

**请求参数：**
```json
{
  "realName": "测试用户更新",
  "status": "INACTIVE"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {...}
}
```

### 1.6 删除用户

**接口地址：** `DELETE /api/users/{id}`

**权限要求：** system:user:delete

**路径参数：**
- id: 用户ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 2. 角色管理API

### 2.1 创建角色

**接口地址：** `POST /api/roles`

**权限要求：** system:role:add

**请求参数：**
```json
{
  "roleCode": "ROLE_ADMIN",
  "roleName": "管理员",
  "roleDesc": "系统管理员角色",
  "status": "ACTIVE",
  "sortOrder": 1
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "roleCode": "ROLE_ADMIN",
    "roleName": "管理员",
    "roleDesc": "系统管理员角色",
    "isSystem": "0",
    "status": "ACTIVE",
    "sortOrder": 1,
    "createTime": "2026-01-09T10:00:00",
    "updateTime": "2026-01-09T10:00:00",
    "permissionIds": [],
    "permissionCount": 0
  }
}
```

### 2.2 获取角色列表

**接口地址：** `GET /api/roles`

**权限要求：** system:role:query

**请求参数：**
- page: 页码（默认1）
- size: 每页数量（默认10）
- sortField: 排序字段（默认sortOrder）
- sortOrder: 排序方向（默认ASC）
- keyword: 搜索关键词（可选）
- status: 角色状态（可选）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 10,
    "page": 1,
    "size": 10,
    "totalPages": 1,
    "roles": [
      {
        "id": 1,
        "roleCode": "ROLE_ADMIN",
        "roleName": "管理员",
        "roleDesc": "系统管理员角色",
        "isSystem": "0",
        "status": "ACTIVE",
        "sortOrder": 1,
        "createTime": "2026-01-09T10:00:00",
        "updateTime": "2026-01-09T10:00:00",
        "permissionIds": [1, 2, 3],
        "permissionCount": 3
      }
    ]
  }
}
```

### 2.3 获取单个角色

**接口地址：** `GET /api/roles/{id}`

**权限要求：** system:role:query

**路径参数：**
- id: 角色ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "roleCode": "ROLE_ADMIN",
    "roleName": "管理员",
    "roleDesc": "系统管理员角色",
    "isSystem": "0",
    "status": "ACTIVE",
    "sortOrder": 1,
    "createTime": "2026-01-09T10:00:00",
    "updateTime": "2026-01-09T10:00:00",
    "permissionIds": [1, 2, 3],
    "permissionCount": 3
  }
}
```

### 2.4 更新角色

**接口地址：** `PUT /api/roles/{id}`

**权限要求：** system:role:edit

**路径参数：**
- id: 角色ID

**请求参数：**
```json
{
  "roleCode": "ROLE_ADMIN",
  "roleName": "超级管理员",
  "roleDesc": "系统超级管理员角色",
  "status": "ACTIVE",
  "sortOrder": 1
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {...}
}
```

### 2.5 删除角色

**接口地址：** `DELETE /api/roles/{id}`

**权限要求：** system:role:delete

**路径参数：**
- id: 角色ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 2.6 为角色分配权限

**接口地址：** `POST /api/roles/{roleId}/permissions`

**权限要求：** system:role:assign

**路径参数：**
- roleId: 角色ID

**请求参数：**
```json
{
  "permissionIds": [1, 2, 3, 4, 5]
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 2.7 查询角色权限

**接口地址：** `GET /api/roles/{roleId}/permissions`

**权限要求：** system:role:query

**路径参数：**
- roleId: 角色ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [1, 2, 3, 4, 5]
}
```

### 2.8 移除角色权限

**接口地址：** `DELETE /api/roles/{roleId}/permissions`

**权限要求：** system:role:assign

**路径参数：**
- roleId: 角色ID

**请求参数：**
```json
{
  "permissionIds": [4, 5]
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 2.9 更新角色权限

**接口地址：** `PUT /api/roles/{roleId}/permissions`

**权限要求：** system:role:assign

**路径参数：**
- roleId: 角色ID

**请求参数：**
```json
{
  "permissionIds": [1, 2, 3]
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 3. 权限管理API

### 3.1 创建权限

**接口地址：** `POST /api/permissions`

**权限要求：** system:permission:add

**请求参数：**
```json
{
  "permCode": "system:user:add",
  "permName": "添加用户",
  "permType": "1",
  "parentId": 0,
  "path": "/system/user/add",
  "component": "UserAdd",
  "icon": "user-add",
  "sortOrder": 1,
  "status": "ACTIVE",
  "isExternal": "0"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "permCode": "system:user:add",
    "permName": "添加用户",
    "permType": "1",
    "parentId": 0,
    "path": "/system/user/add",
    "component": "UserAdd",
    "icon": "user-add",
    "sortOrder": 1,
    "status": "ACTIVE",
    "isExternal": "0",
    "createTime": "2026-01-09T10:00:00",
    "updateTime": "2026-01-09T10:00:00"
  }
}
```

### 3.2 获取权限列表

**接口地址：** `GET /api/permissions`

**权限要求：** system:permission:query

**请求参数：**
- page: 页码（默认1）
- size: 每页数量（默认10）
- sortField: 排序字段（默认sortOrder）
- sortOrder: 排序方向（默认ASC）
- keyword: 搜索关键词（可选）
- status: 权限状态（可选）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 50,
    "page": 1,
    "size": 10,
    "totalPages": 5,
    "permissions": [...]
  }
}
```

### 3.3 获取权限树

**接口地址：** `GET /api/permissions/tree`

**权限要求：** system:permission:query

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "permCode": "system",
      "permName": "系统管理",
      "permType": "0",
      "parentId": 0,
      "path": "/system",
      "component": null,
      "icon": "system",
      "sortOrder": 1,
      "status": "ACTIVE",
      "children": [
        {
          "id": 2,
          "permCode": "system:user",
          "permName": "用户管理",
          "permType": "0",
          "parentId": 1,
          "path": "/system/user",
          "component": "UserManagement",
          "icon": "user",
          "sortOrder": 1,
          "status": "ACTIVE",
          "children": [
            {
              "id": 3,
              "permCode": "system:user:add",
              "permName": "添加用户",
              "permType": "1",
              "parentId": 2,
              "path": "/system/user/add",
              "component": "UserAdd",
              "icon": "user-add",
              "sortOrder": 1,
              "status": "ACTIVE",
              "children": []
            }
          ]
        }
      ]
    }
  ]
}
```

### 3.4 获取单个权限

**接口地址：** `GET /api/permissions/{id}`

**权限要求：** system:permission:query

**路径参数：**
- id: 权限ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "permCode": "system:user:add",
    "permName": "添加用户",
    "permType": "1",
    "parentId": 0,
    "path": "/system/user/add",
    "component": "UserAdd",
    "icon": "user-add",
    "sortOrder": 1,
    "status": "ACTIVE",
    "isExternal": "0",
    "createTime": "2026-01-09T10:00:00",
    "updateTime": "2026-01-09T10:00:00"
  }
}
```

### 3.5 更新权限

**接口地址：** `PUT /api/permissions/{id}`

**权限要求：** system:permission:edit

**路径参数：**
- id: 权限ID

**请求参数：**
```json
{
  "permCode": "system:user:add",
  "permName": "添加用户",
  "permType": "1",
  "parentId": 0,
  "path": "/system/user/add",
  "component": "UserAdd",
  "icon": "user-add",
  "sortOrder": 1,
  "status": "ACTIVE",
  "isExternal": "0"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {...}
}
```

### 3.6 删除权限

**接口地址：** `DELETE /api/permissions/{id}`

**权限要求：** system:permission:delete

**路径参数：**
- id: 权限ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 4. 用户角色关联管理API

### 4.1 为用户分配角色

**接口地址：** `POST /api/user-roles/{userId}/roles`

**权限要求：** system:user:assign

**路径参数：**
- userId: 用户ID

**请求参数：**
```json
{
  "roleIds": [1, 2, 3]
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 4.2 查询用户角色

**接口地址：** `GET /api/user-roles/{userId}/roles`

**权限要求：** system:user:query

**路径参数：**
- userId: 用户ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [1, 2, 3]
}
```

### 4.3 移除用户角色

**接口地址：** `DELETE /api/user-roles/{userId}/roles`

**权限要求：** system:user:assign

**路径参数：**
- userId: 用户ID

**请求参数：**
```json
{
  "roleIds": [2, 3]
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 4.4 清空用户角色

**接口地址：** `DELETE /api/user-roles/{userId}/roles/all`

**权限要求：** system:user:assign

**路径参数：**
- userId: 用户ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 5. 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权或Token过期 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 6. 注意事项

1. **权限校验**：所有API接口都进行了权限校验，确保只有具有相应权限的用户才能访问
2. **事务处理**：所有涉及多个表的操作都使用了事务，确保数据一致性
3. **日志记录**：所有操作都会记录日志，便于审计和问题追踪
4. **参数验证**：所有请求参数都进行了验证，防止非法数据
5. **限流保护**：部分接口使用了限流注解，防止恶意请求
6. **逻辑删除**：删除操作采用逻辑删除，数据不会真正从数据库中删除
7. **系统角色保护**：系统角色（isSystem=1）不能被删除
8. **权限层级保护**：有子权限的权限不能被删除

## 7. 调用示例

### 使用curl调用

```bash
# 创建用户
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "username": "testuser",
    "password": "123456",
    "realName": "测试用户",
    "mobile": "13800138000",
    "email": "test@example.com",
    "status": "ACTIVE"
  }'

# 获取用户列表
curl -X GET "http://localhost:8080/api/users?page=1&size=10" \
  -H "Authorization: Bearer {token}"

# 为用户分配角色
curl -X POST http://localhost:8080/api/user-roles/1/roles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "roleIds": [1, 2, 3]
  }'
```

### 使用Postman调用

1. 创建新的请求
2. 设置请求方法（GET/POST/PUT/DELETE）
3. 设置请求URL
4. 在Headers中添加：
   - Content-Type: application/json
   - Authorization: Bearer {token}
5. 在Body中设置请求参数（对于POST/PUT请求）
6. 发送请求

---

## 8. Swagger文档

项目集成了Swagger/OpenAPI，可以通过以下地址访问在线API文档：

```
http://localhost:8080/swagger-ui.html
```

Swagger文档提供了更详细的接口说明、参数说明和在线测试功能。

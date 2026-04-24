# 用户角色检查API文档

> 本文档说明用户角色检查相关的API接口，用于前端判断当前用户是否具有管理员权限。

---

## 一、接口概述

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 检查管理员权限 | GET | `/auth/check-admin` | 检查当前用户是否为管理员 |
| 检查指定角色 | GET | `/auth/check-role/{roleCode}` | 检查当前用户是否具有指定角色 |

---

## 二、检查管理员权限

### 2.1 接口说明

**接口地址**：`GET /auth/check-admin`

**接口描述**：检查当前登录用户是否具有ADMIN或SUPER_ADMIN角色

**请求头**：
```
Authorization: Bearer {token}
```

**请求示例**：
```
GET /auth/check-admin
```

**响应示例（管理员用户）**：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "userId": 1,
        "username": "admin",
        "isAdmin": true,
        "isSuperAdmin": true,
        "roles": ["ADMIN", "SUPER_ADMIN"]
    }
}
```

**响应示例（普通用户）**：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "userId": 10,
        "username": "zhangsan",
        "isAdmin": false,
        "isSuperAdmin": false,
        "roles": ["USER"]
    }
}
```

**响应字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| userId | Long | 用户ID |
| username | String | 用户名 |
| isAdmin | Boolean | 是否为管理员（ADMIN或SUPER_ADMIN） |
| isSuperAdmin | Boolean | 是否为超级管理员 |
| roles | List<String> | 用户拥有的所有角色编码 |

---

## 三、检查指定角色

### 3.1 接口说明

**接口地址**：`GET /auth/check-role/{roleCode}`

**接口描述**：检查当前登录用户是否具有指定的角色

**路径参数**：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| roleCode | String | 是 | 角色编码（如：ADMIN、USER、MANAGER等） |

**请求头**：
```
Authorization: Bearer {token}
```

**请求示例**：
```
GET /auth/check-role/ADMIN
```

**响应示例**：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "userId": 1,
        "username": "admin",
        "isAdmin": true,
        "isSuperAdmin": true,
        "roles": ["ADMIN", "SUPER_ADMIN"]
    }
}
```

---

## 四、前端使用示例

### 4.1 uni-app 示例

```javascript
// 检查是否为管理员
async function checkAdmin() {
    try {
        const res = await uni.request({
            url: 'http://your-domain/api/auth/check-admin',
            method: 'GET',
            header: {
                'Authorization': 'Bearer ' + uni.getStorageSync('token')
            }
        });
        
        if (res.data.code === 200) {
            const data = res.data.data;
            if (data.isAdmin) {
                console.log('当前用户是管理员');
                // 显示管理员功能
                return true;
            } else {
                console.log('当前用户不是管理员');
                // 隐藏管理员功能
                return false;
            }
        }
    } catch (error) {
        console.error('检查管理员权限失败：', error);
        return false;
    }
}

// 在页面加载时检查权限
export default {
    data() {
        return {
            isAdmin: false
        }
    },
    async onLoad() {
        this.isAdmin = await checkAdmin();
    },
    methods: {
        // 只有管理员才能执行的操作
        approveReimbursement(id) {
            if (!this.isAdmin) {
                uni.showToast({
                    title: '无权限执行此操作',
                    icon: 'none'
                });
                return;
            }
            // 执行审批逻辑...
        }
    }
}
```

### 4.2 封装为全局方法

```javascript
// utils/auth.js
export default {
    async checkAdmin() {
        try {
            const res = await uni.request({
                url: 'http://your-domain/api/auth/check-admin',
                method: 'GET',
                header: {
                    'Authorization': 'Bearer ' + uni.getStorageSync('token')
                }
            });
            
            if (res.data.code === 200) {
                return res.data.data.isAdmin;
            }
            return false;
        } catch (error) {
            console.error('检查管理员权限失败：', error);
            return false;
        }
    },
    
    async checkRole(roleCode) {
        try {
            const res = await uni.request({
                url: 'http://your-domain/api/auth/check-role/' + roleCode,
                method: 'GET',
                header: {
                    'Authorization': 'Bearer ' + uni.getStorageSync('token')
                }
            });
            
            if (res.data.code === 200) {
                return res.data.data.roles.includes(roleCode);
            }
            return false;
        } catch (error) {
            console.error('检查角色权限失败：', error);
            return false;
        }
    }
}
```

### 4.3 在main.js中全局注册

```javascript
// main.js
import auth from './utils/auth'

Vue.prototype.$auth = auth
```

### 4.4 页面中使用

```javascript
export default {
    data() {
        return {
            isAdmin: false
        }
    },
    async onLoad() {
        this.isAdmin = await this.$auth.checkAdmin();
        
        // 根据权限显示/隐藏功能
        if (this.isAdmin) {
            // 加载管理员相关数据
            this.loadAdminData();
        }
    }
}
```

---

## 五、使用场景

### 5.1 页面权限控制

```javascript
// 页面加载时检查权限
async onLoad() {
    const isAdmin = await this.$auth.checkAdmin();
    if (!isAdmin) {
        uni.showToast({
            title: '无权限访问此页面',
            icon: 'none'
        });
        setTimeout(() => {
            uni.navigateBack();
        }, 1500);
        return;
    }
    // 继续加载页面数据
    this.loadData();
}
```

### 5.2 按钮权限控制

```html
<template>
    <view>
        <!-- 只有管理员可见的按钮 -->
        <button v-if="isAdmin" @click="approve">审批通过</button>
        <button v-if="isAdmin" @click="reject">审批拒绝</button>
        
        <!-- 普通用户可见的按钮 -->
        <button @click="viewDetail">查看详情</button>
    </view>
</template>
```

### 5.3 菜单权限控制

```javascript
// 根据权限动态生成菜单
async generateMenus() {
    const isAdmin = await this.$auth.checkAdmin();
    
    let menus = [
        { title: '首页', path: '/pages/index/index' },
        { title: '我的报销', path: '/pages/reimbursement/my' }
    ];
    
    if (isAdmin) {
        menus.push(
            { title: '审批管理', path: '/pages/approval/list' },
            { title: '用户管理', path: '/pages/user/list' },
            { title: '系统设置', path: '/pages/system/settings' }
        );
    }
    
    this.menus = menus;
}
```

---

## 六、注意事项

1. **Token有效性**：调用接口前确保Token有效，如果返回401错误，需要重新登录

2. **缓存优化**：可以在应用启动时调用一次，将结果缓存到本地，避免频繁请求

3. **实时性**：如果用户角色可能被管理员修改，建议定期刷新权限信息

4. **安全性**：前端权限检查仅用于UI展示控制，后端接口仍需进行权限校验

---

## 七、错误码说明

| 错误码 | 说明 | 处理方式 |
|--------|------|----------|
| 200 | 成功 | - |
| 401 | 未登录或Token失效 | 跳转登录页面 |
| 500 | 服务器错误 | 提示用户稍后重试 |

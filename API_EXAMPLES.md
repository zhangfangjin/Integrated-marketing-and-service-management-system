# API 使用示例

本文档提供主要 API 接口的使用示例。

## 1. 用户注册流程

### 1.1 验证身份证号
```http
GET /api/register/check?idCard=110101199001011234
```

**响应（已注册）：**
```json
{
  "registered": true,
  "username": "13800138000",
  "name": "张三"
}
```

**响应（未注册）：**
```json
{
  "registered": false,
  "username": null,
  "name": null
}
```

### 1.2 提交注册
```http
POST /api/register
Content-Type: application/json

{
  "name": "张三",
  "idCard": "110101199001011234",
  "phone": "13800138000",
  "birthDate": "1990-01-01",
  "gender": "男",
  "age": 34,
  "positionId": "岗位选项ID",
  "regionId": "所属片区选项ID"
}
```

## 2. 登录

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "13800138000",
  "password": "123456"
}
```

**响应：**
```json
{
  "userId": "用户ID",
  "username": "13800138000",
  "name": "张三",
  "token": "生成的token",
  "role": {
    "id": "角色ID",
    "roleName": "角色名",
    "roleDescription": "角色描述"
  }
}
```

## 3. 账号管理

### 3.1 获取待审核列表
```http
GET /api/register/pending
```

### 3.2 审核通过
```http
PUT /api/register/{id}/approve
Content-Type: application/json

{
  "roleId": "角色ID",
  "remark": "审核备注（可选）"
}
```

### 3.3 重置密码
```http
PUT /api/accounts/{id}/reset-password
Content-Type: application/json

{
  "newPassword": "新密码"
}
```

### 3.4 修改角色
```http
PUT /api/accounts/{id}/role
Content-Type: application/json

{
  "roleId": "新角色ID"
}
```

## 4. 角色管理

### 4.1 获取角色列表
```http
GET /api/roles
```

### 4.2 创建角色
```http
POST /api/roles
Content-Type: application/json

{
  "roleName": "管理员",
  "roleDescription": "系统管理员角色"
}
```

### 4.3 更新角色
```http
PUT /api/roles/{id}
Content-Type: application/json

{
  "roleName": "管理员",
  "roleDescription": "更新后的描述"
}
```

### 4.4 删除角色
```http
DELETE /api/roles/{id}
```

## 5. 选项管理

### 5.1 获取选项列表（按分组）
```http
GET /api/options?group=岗位
```

### 5.2 创建选项
```http
POST /api/options
Content-Type: application/json

{
  "group": "岗位",
  "title": "经理",
  "value": "manager",
  "order": 1
}
```

## 6. 模块管理

### 6.1 获取模块树
```http
GET /api/modules/tree
```

**响应示例：**
```json
[
  {
    "id": "模块ID",
    "zhName": "首页",
    "enName": "home",
    "level": 1,
    "orderNo": 1000,
    "path": "/home",
    "icon": "home-icon",
    "groupCode": null,
    "permissionKey": "/home",
    "parentNode": false,
    "expanded": false,
    "visible": true,
    "children": []
  },
  {
    "id": "模块ID",
    "zhName": "系统管理",
    "enName": "system",
    "level": 1,
    "orderNo": 2000,
    "path": "/system",
    "icon": "system-icon",
    "groupCode": null,
    "permissionKey": "/system",
    "parentNode": true,
    "expanded": false,
    "visible": true,
    "children": [
      {
        "id": "子模块ID",
        "zhName": "角色管理",
        "enName": "role",
        "level": 2,
        "orderNo": 2010,
        "path": "/system/role",
        "icon": "role-icon",
        "groupCode": null,
        "permissionKey": "/system/role",
        "parentNode": false,
        "expanded": false,
        "visible": true,
        "children": []
      }
    ]
  }
]
```

### 6.2 创建模块
```http
POST /api/modules
Content-Type: application/json

{
  "zhName": "模块管理",
  "enName": "module",
  "level": 2,
  "orderNo": 2030,
  "path": "/system/module",
  "icon": "module-icon",
  "groupCode": null,
  "permissionKey": "/system/module",
  "parentId": "父模块ID（可选）",
  "parentNode": false,
  "expanded": false,
  "visible": true
}
```

### 6.3 更新模块
```http
PUT /api/modules/{id}
Content-Type: application/json

{
  "zhName": "模块管理",
  "enName": "module",
  "level": 2,
  "orderNo": 2030,
  "path": "/system/module",
  "icon": "module-icon",
  "groupCode": null,
  "permissionKey": "/system/module",
  "parentId": "父模块ID（可选）",
  "parentNode": false,
  "expanded": false,
  "visible": true
}
```

## 7. 权限配置

### 7.1 获取带权限状态的模块树
```http
GET /api/permissions/role/{roleId}/tree
```

**响应示例：**
```json
[
  {
    "id": "模块ID",
    "zhName": "人员管理",
    "enName": "personnel",
    "level": 1,
    "orderNo": 3000,
    "path": "/personnel",
    "icon": "personnel-icon",
    "groupCode": null,
    "permissionKey": "/personnel",
    "parentNode": true,
    "expanded": false,
    "visible": true,
    "selected": true,
    "canRead": true,
    "canAdd": true,
    "canUpdate": true,
    "canSee": true,
    "children": [
      {
        "id": "子模块ID",
        "zhName": "人员档案",
        "enName": "personnel-archive",
        "level": 2,
        "orderNo": 3010,
        "path": "/personnel/archive",
        "icon": "archive-icon",
        "groupCode": null,
        "permissionKey": "/personnel/archive",
        "parentNode": false,
        "expanded": false,
        "visible": true,
        "selected": true,
        "canRead": true,
        "canAdd": true,
        "canUpdate": true,
        "canSee": true,
        "children": []
      }
    ]
  }
]
```

### 7.2 保存权限配置
```http
POST /api/permissions
Content-Type: application/json

{
  "roleId": "角色ID",
  "permissions": [
    {
      "moduleId": "模块ID",
      "canRead": true,
      "canAdd": true,
      "canUpdate": true,
      "canSee": true
    },
    {
      "moduleId": "另一个模块ID",
      "canRead": true,
      "canAdd": false,
      "canUpdate": false,
      "canSee": true
    }
  ]
}
```

## 错误响应格式

所有接口在出错时返回统一的错误格式：

```json
{
  "timestamp": "2024-01-01T00:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "错误信息",
  "path": "/api/xxx"
}
```

## 注意事项

1. 所有 UUID 类型的 ID 使用 36 位字符串格式
2. 日期格式使用 ISO 8601 格式（如：2024-01-01）
3. 密码在传输和存储时都已加密
4. 所有接口都需要适当的认证（当前为简化实现，实际应使用 JWT）


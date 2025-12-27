# API 接口文档

本文档提供了图书借阅管理系统所有 REST API 的详细说明和测试示例。

## 📡 基础信息

- **Base URL**: `http://localhost:8080`
- **API 网关端口**: 8080
- **认证方式**: JWT Bearer Token
- **请求格式**: application/json
- **响应格式**: application/json

## 🔑 统一响应格式

所有 API 响应都遵循以下统一格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { },
  "timestamp": 1703001234567
}
```

### 响应码说明

| 状态码 | 说明 |
|-------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未认证或认证失败 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 🔐 认证接口

### 1. 用户注册

**接口地址**: `POST /api/auth/register`

**请求头**:
```
Content-Type: application/json
```

**请求参数**:
```json
{
  "username": "testuser",
  "password": "123456",
  "confirmPassword": "123456",
  "email": "testuser@example.com",
  "phone": "13800138000",
  "realName": "测试用户"
}
```

**参数说明**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| username | string | 是 | 用户名，3-20位字母数字下划线 |
| password | string | 是 | 密码，6-20位 |
| confirmPassword | string | 是 | 确认密码，需与password一致 |
| email | string | 是 | 邮箱地址 |
| phone | string | 否 | 手机号，11位数字 |
| realName | string | 否 | 真实姓名 |

**响应示例**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 10,
    "username": "testuser",
    "email": "testuser@example.com",
    "phone": "13800138000",
    "realName": "测试用户",
    "role": "USER",
    "status": 1
  },
  "timestamp": 1703001234567
}
```

**CURL 示例**:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456",
    "confirmPassword": "123456",
    "email": "testuser@example.com",
    "phone": "13800138000",
    "realName": "测试用户"
  }'
```

---

### 2. 用户登录

**接口地址**: `POST /api/auth/login`

**请求头**:
```
Content-Type: application/json
```

**请求参数**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**参数说明**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTcwMzAwMTIzNCwiZXhwIjoxNzAzMDg3NjM0fQ.xxxxxxxxxxxxx",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTcwMzAwMTIzNCwiZXhwIjoxNzAzNjA2MDM0fQ.yyyyyyyyyyyyy",
    "tokenType": "JWT",
    "expiresIn": 86400,
    "userInfo": {
      "id": 1,
      "username": "admin",
      "email": "admin@library.com",
      "phone": "13800000000",
      "realName": "系统管理员",
      "role": "ADMIN",
      "status": 1
    }
  },
  "timestamp": 1703001234567
}
```

**CURL 示例**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

---

### 3. Token 验证

**接口地址**: `POST /api/auth/verify`

**请求头**:
```
Content-Type: application/json
```

**请求参数**:
```json
{
  "token": "Bearer eyJhbGciOiJIUzI1NiJ9..."
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "valid": true,
    "userId": 1,
    "username": "admin",
    "role": "ADMIN",
    "message": "Token 有效"
  },
  "timestamp": 1703001234567
}
```

---

### 4. 刷新 Token

**接口地址**: `POST /api/auth/refresh`

**请求头**:
```
X-Refresh-Token: eyJhbGciOiJIUzI1NiJ9...
```

**响应示例**:
```json
{
  "code": 200,
  "message": "Token刷新成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "JWT",
    "expiresIn": 86400,
    "userInfo": { }
  },
  "timestamp": 1703001234567
}
```

---

### 5. 退出登录

**接口地址**: `POST /api/auth/logout`

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**响应示例**:
```json
{
  "code": 200,
  "message": "退出登录成功",
  "data": null,
  "timestamp": 1703001234567
}
```

---

## 📚 图书接口

以下接口均需要携带有效的 JWT Token。

### 1. 获取图书列表

**接口地址**: `GET /api/books`

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认10，最大100 |
| keyword | string | 否 | 搜索关键词（书名、作者、ISBN） |
| categoryId | long | 否 | 分类ID |
| status | int | 否 | 状态：0-下架，1-上架 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "isbn": "978-7-111-42765-2",
        "title": "Java核心技术 卷I",
        "author": "Cay S. Horstmann",
        "publisher": "机械工业出版社",
        "publishDate": "2016-09-01",
        "categoryId": 17,
        "categoryName": "编程语言",
        "price": 149.00,
        "totalStock": 10,
        "availableStock": 8,
        "coverUrl": null,
        "description": "Java领域最有影响力和价值的著作之一...",
        "status": 1,
        "createTime": "2024-01-01T10:00:00"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  },
  "timestamp": 1703001234567
}
```

**CURL 示例**:
```bash
curl -X GET "http://localhost:8080/api/books?page=1&size=10&keyword=Java" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

---

### 2. 获取图书详情

**接口地址**: `GET /api/books/{id}`

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**路径参数**:
| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | 图书ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "isbn": "978-7-111-42765-2",
    "title": "Java核心技术 卷I",
    "author": "Cay S. Horstmann",
    "publisher": "机械工业出版社",
    "publishDate": "2016-09-01",
    "categoryId": 17,
    "categoryName": "编程语言",
    "price": 149.00,
    "totalStock": 10,
    "availableStock": 8,
    "coverUrl": null,
    "description": "Java领域最有影响力和价值的著作之一...",
    "status": 1,
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-01T10:00:00"
  },
  "timestamp": 1703001234567
}
```

---

### 3. 添加图书（管理员）

**接口地址**: `POST /api/books`

**权限要求**: ADMIN

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json
```

**请求参数**:
```json
{
  "isbn": "978-7-111-12345-6",
  "title": "新书名称",
  "author": "作者姓名",
  "publisher": "出版社",
  "publishDate": "2024-01-01",
  "categoryId": 17,
  "price": 99.00,
  "totalStock": 20,
  "coverUrl": "http://example.com/cover.jpg",
  "description": "图书简介",
  "status": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "id": 20,
    "isbn": "978-7-111-12345-6",
    "title": "新书名称",
    ...
  },
  "timestamp": 1703001234567
}
```

---

### 4. 更新图书（管理员）

**接口地址**: `PUT /api/books/{id}`

**权限要求**: ADMIN

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json
```

**路径参数**:
| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | 图书ID |

**请求参数**: 同添加图书

---

### 5. 删除图书（管理员）

**接口地址**: `DELETE /api/books/{id}`

**权限要求**: ADMIN

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1703001234567
}
```

---

### 6. 获取分类列表

**接口地址**: `GET /api/categories`

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "文学",
      "code": "LITERATURE",
      "parentId": 0,
      "sortOrder": 1,
      "description": "文学类图书",
      "status": 1,
      "children": [
        {
          "id": 9,
          "name": "小说",
          "code": "NOVEL",
          "parentId": 1,
          "sortOrder": 1
        }
      ]
    }
  ],
  "timestamp": 1703001234567
}
```

---

## 📖 借阅接口

### 1. 借阅图书

**接口地址**: `POST /api/borrow`

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json
```

**请求参数**:
```json
{
  "bookId": 1,
  "borrowDays": 30
}
```

**参数说明**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| bookId | long | 是 | 图书ID |
| borrowDays | int | 否 | 借阅天数，默认30天 |

**响应示例**:
```json
{
  "code": 200,
  "message": "借阅成功",
  "data": {
    "id": 100,
    "userId": 1,
    "username": "admin",
    "bookId": 1,
    "bookIsbn": "978-7-111-42765-2",
    "bookTitle": "Java核心技术 卷I",
    "quantity": 1,
    "borrowTime": "2024-01-01T10:00:00",
    "dueTime": "2024-01-31T10:00:00",
    "returnTime": null,
    "status": 0,
    "renewCount": 0,
    "remark": null
  },
  "timestamp": 1703001234567
}
```

---

### 2. 归还图书

**接口地址**: `POST /api/return/{id}`

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**路径参数**:
| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | 借阅记录ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "归还成功",
  "data": {
    "id": 100,
    "returnTime": "2024-01-15T10:00:00",
    "status": 1
  },
  "timestamp": 1703001234567
}
```

---

### 3. 续借图书

**接口地址**: `POST /api/renew/{id}`

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json
```

**路径参数**:
| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | 借阅记录ID |

**请求参数**:
```json
{
  "renewDays": 30
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "续借成功",
  "data": {
    "id": 100,
    "dueTime": "2024-03-01T10:00:00",
    "renewCount": 1,
    "status": 3
  },
  "timestamp": 1703001234567
}
```

---

### 4. 查询借阅记录

**接口地址**: `GET /api/borrow/records`

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认10 |
| userId | long | 否 | 用户ID（管理员可查询所有用户） |
| status | int | 否 | 状态：0-借阅中，1-已归还，2-已逾期，3-已续借 |

**响应示例**: 返回分页的借阅记录列表

---

### 5. 查询我的借阅

**接口地址**: `GET /api/borrow/my`

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "borrowing": [
      {
        "id": 100,
        "bookTitle": "Java核心技术 卷I",
        "borrowTime": "2024-01-01T10:00:00",
        "dueTime": "2024-01-31T10:00:00",
        "status": 0,
        "renewCount": 0
      }
    ],
    "returned": [],
    "overdue": []
  },
  "timestamp": 1703001234567
}
```

---

## 🧪 Postman 测试集合

可以使用以下 Postman 环境变量：

```json
{
  "baseUrl": "http://localhost:8080",
  "accessToken": "",
  "refreshToken": ""
}
```

### 测试流程

1. **注册/登录** → 获取 Token
2. **查询图书列表** → 选择要借阅的图书
3. **借阅图书** → 记录借阅ID
4. **查询我的借阅** → 查看借阅状态
5. **续借/归还图书** → 管理借阅

---

## ⚠️ 错误码

| 错误码 | 说明 |
|-------|------|
| 1001 | 用户不存在 |
| 1002 | 用户已存在 |
| 1003 | 用户名或密码错误 |
| 1004 | 用户已被禁用 |
| 2001 | Token无效 |
| 2002 | Token已过期 |
| 2003 | Token缺失 |
| 3001 | 图书不存在 |
| 3002 | 图书已存在 |
| 3003 | 图书库存不足 |
| 4001 | 借阅记录不存在 |
| 4002 | 图书已被借阅 |
| 4003 | 借阅数量超过限制 |
| 4004 | 图书未归还 |
| 4005 | 图书已归还 |

---

如有其他问题，请参考 [README.md](../README.md) 或提交 Issue。

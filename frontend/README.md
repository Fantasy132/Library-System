# 图书借阅管理系统 - 前端

基于 React + TypeScript + Ant Design 的图书借阅管理系统前端应用。

## 技术栈

- React 18
- TypeScript
- Ant Design 5
- React Router 6
- Axios
- Day.js

## 功能特性

### 用户功能
- 用户登录/注册
- 图书列表浏览
- 图书搜索和筛选
- 图书详情查看
- 图书借阅
- 我的借阅记录
- 图书归还
- 图书续借

### 管理员功能
- 图书管理（增删改查）
- 借阅记录查看
- 用户借阅统计

## 快速开始

### 安装依赖

```bash
npm install
```

### 配置环境变量

创建 `.env` 文件并配置后端 API 地址：

```
REACT_APP_API_BASE_URL=http://localhost:8080
```

### 启动开发服务器

```bash
npm start
```

应用将在 [http://localhost:3000](http://localhost:3000) 启动。

### 构建生产版本

```bash
npm run build
```

## 项目结构

```
src/
├── api/                 # API 请求封装
│   ├── auth.ts         # 认证相关 API
│   ├── book.ts         # 图书相关 API
│   └── borrow.ts       # 借阅相关 API
├── components/          # 公共组件
│   └── MainLayout.tsx  # 主布局组件
├── contexts/            # React Context
│   └── AuthContext.tsx # 认证上下文
├── pages/               # 页面组件
│   ├── Login.tsx       # 登录/注册页面
│   ├── BookList.tsx    # 图书列表页面
│   ├── MyBorrows.tsx   # 我的借阅页面
│   └── AdminPanel.tsx  # 管理员面板
├── types/               # TypeScript 类型定义
│   └── index.ts
├── utils/               # 工具函数
│   └── request.ts      # Axios 封装
├── App.tsx              # 应用主组件
└── index.tsx            # 应用入口
```

## 默认账号

### 管理员账号
- 用户名: `admin`
- 密码: `admin123`

### 测试用户
- 用户名: `user1` / `user2` / `user3`
- 密码: `user123`

## 主要功能说明

### 认证系统
- 基于 JWT 的身份认证
- 自动 Token 刷新机制
- 路由权限控制

### 图书管理
- 分页展示图书列表
- 支持关键词搜索
- 支持分类筛选
- 实时库存显示

### 借阅管理
- 借阅图书（可选择借阅天数）
- 查看借阅记录
- 归还图书
- 续借图书（最多2次）
- 逾期提醒

### 管理员功能
- 图书的增删改查
- 查看所有借阅记录
- 图书库存管理

## API 接口

所有 API 请求都通过 `/api` 前缀访问后端服务。

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/logout` - 退出登录
- `POST /api/auth/refresh` - 刷新 Token

### 图书接口
- `GET /api/books` - 获取图书列表
- `GET /api/books/:id` - 获取图书详情
- `POST /api/books` - 添加图书（管理员）
- `PUT /api/books/:id` - 更新图书（管理员）
- `DELETE /api/books/:id` - 删除图书（管理员）
- `GET /api/categories` - 获取图书分类

### 借阅接口
- `POST /api/borrow` - 借阅图书
- `POST /api/return/:id` - 归还图书
- `POST /api/renew/:id` - 续借图书
- `GET /api/borrow/my` - 查询我的借阅
- `GET /api/borrow/records` - 查询借阅记录

## 注意事项

1. 确保后端服务已启动并运行在 `http://localhost:8080`
2. 首次使用需要先注册账号或使用默认测试账号
3. 管理员功能需要使用管理员账号登录
4. Token 有效期为 24 小时，过期后会自动刷新

## License

MIT

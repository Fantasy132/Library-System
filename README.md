# 📚 图书借阅管理系统

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-green.svg)](https://spring.io/projects/spring-cloud)
[![React](https://img.shields.io/badge/React-18-61dafb.svg)](https://reactjs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5-blue.svg)](https://www.typescriptlang.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

一个基于 **Spring Cloud 微服务架构** + **React** 的现代化图书借阅管理系统，实现了用户认证、图书管理、借阅管理等核心功能。

## 🌟 项目特点

- ✅ **微服务架构**: 采用 Spring Cloud 微服务架构，服务独立部署，易于扩展
- ✅ **服务注册与发现**: 使用 Nacos 实现服务注册与配置管理
- ✅ **API 网关**: Spring Cloud Gateway 统一入口，JWT 认证，限流熔断
- ✅ **服务间通信**: OpenFeign 声明式服务调用，负载均衡
- ✅ **容错保护**: Resilience4j 实现熔断、限流、重试
- ✅ **前后端分离**: React + TypeScript + Ant Design 现代化前端
- ✅ **Docker 部署**: 完整的 Docker Compose 一键部署方案
- ✅ **数据库文档**: 完整的数据库结构文档和 SQL 脚本

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                     前端应用 (React)                         │
│                   http://localhost:3000                      │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                  API 网关 (Gateway:8080)                     │
│  ✓ 路由转发  ✓ JWT认证  ✓ 限流熔断  ✓ 跨域处理                │
└──────┬─────────────────┬─────────────────┬──────────────────┘
       │                 │                 │
       ▼                 ▼                 ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ 认证服务      │  │ 图书服务      │  │ 借阅服务      │
│ Auth:8081    │  │ Book:8082    │  │ Borrow:8083  │
└──────┬───────┘  └──────┬───────┘  └──────┬───────┘
       │                 │                 │
       └─────────────────┼─────────────────┘
                         ▼
           ┌──────────────────────────┐
           │   Nacos Server:8848      │
           │  ✓ 服务注册   ✓ 配置管理   │
           └──────────────────────────┘
       │                 │                 │
       ▼                 ▼                 ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ library_auth │  │ library_book │  │library_borrow│
│   数据库      │  │   数据库      │  │   数据库      │
└──────────────┘  └──────────────┘  └──────────────┘
```

## 📁 项目结构

```
library-system/
├── backend/                      # 后端微服务
│   ├── library-common/           # 公共模块
│   │   ├── config/              # 公共配置
│   │   ├── constant/            # 常量定义
│   │   ├── dto/                 # 数据传输对象
│   │   ├── exception/           # 异常处理
│   │   ├── result/              # 统一响应结果
│   │   └── utils/               # 工具类
│   ├── library-gateway/          # API 网关 (8080)
│   │   ├── config/              # 网关配置
│   │   ├── filter/              # 网关过滤器
│   │   └── handler/             # 异常处理
│   ├── library-auth/             # 认证服务 (8081)
│   │   ├── controller/          # 用户登录、注册
│   │   ├── service/             # JWT 认证
│   │   └── mapper/              # 用户数据访问
│   ├── library-book/             # 图书服务 (8082)
│   │   ├── controller/          # 图书CRUD、分类管理
│   │   ├── service/             # 图书业务逻辑
│   │   ├── mapper/              # 图书数据访问
│   │   └── feign/               # Feign 客户端
│   ├── library-borrow/           # 借阅服务 (8083)
│   │   ├── controller/          # 借阅、归还、续借
│   │   ├── service/             # 借阅业务逻辑
│   │   ├── mapper/              # 借阅数据访问
│   │   └── feign/               # Feign 客户端
│   ├── docs/                     # 文档目录
│   │   ├── sql/                 # 数据库脚本
│   │   │   ├── library_auth_schema.sql
│   │   │   ├── library_book_schema.sql
│   │   │   ├── library_borrow_schema.sql
│   │   │   ├── auth_init.sql
│   │   │   ├── nacos_init.sql
│   │   │   └── DATABASE_STRUCTURE.md
│   │   ├── nacos/               # Nacos 配置
│   │   └── api/                 # API 文档
│   ├── docker-compose.yml        # Docker 编排文件
│   ├── pom.xml                   # Maven 父 POM
│   └── README.md                 # 后端详细文档
├── frontend/                     # 前端应用
│   ├── public/                   # 静态资源
│   ├── src/
│   │   ├── api/                 # API 接口
│   │   ├── components/          # React 组件
│   │   ├── contexts/            # Context 上下文
│   │   ├── pages/               # 页面
│   │   │   ├── Login.tsx        # 登录页
│   │   │   ├── BookList.tsx     # 图书列表
│   │   │   ├── MyBorrows.tsx    # 我的借阅
│   │   │   └── AdminPanel.tsx   # 管理面板
│   │   ├── types/               # TypeScript 类型
│   │   └── utils/               # 工具函数
│   ├── package.json
│   └── README.md                 # 前端详细文档
└── README.md                     # 项目总览 (本文件)
```

## 💻 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | 开发语言 |
| Spring Boot | 3.5.7 | 应用框架 |
| Spring Cloud | 2025.0.0 | 微服务框架 |
| Spring Cloud Alibaba | 2025.0.0 | 阿里巴巴微服务组件 |
| Nacos | 3.1.0 | 服务注册与配置中心 |
| Spring Cloud Gateway | - | API 网关 |
| OpenFeign | - | 服务间通信 |
| Resilience4j | - | 容错保护 |
| MySQL | 8.4 | 关系型数据库 |
| MyBatis-Plus | 3.5.10.1 | ORM 框架 |
| JWT | 0.12.6 | 身份认证 |
| Maven | - | 构建工具 |
| Docker | - | 容器化部署 |

### 前端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| React | 18 | UI 框架 |
| TypeScript | 5 | 开发语言 |
| Ant Design | 5 | UI 组件库 |
| React Router | 6 | 路由管理 |
| Axios | - | HTTP 客户端 |
| Day.js | - | 时间处理 |

## 🚀 快速开始

### 环境要求

- **开发环境**:
  - JDK 21+
  - Maven 3.8+
  - Node.js 16+
  - MySQL 8.4+
  
- **生产环境**:
  - Docker
  - Docker Compose

### 方式一: Docker 部署 (推荐)

**一键启动所有服务**:

```powershell
# Windows 环境
cd backend
wsl docker-compose up -d

# Linux / Mac 环境
cd backend
docker-compose up -d
```

服务启动后访问:
- 前端应用: http://localhost:3000
- API 网关: http://localhost:8080
- Nacos 控制台: http://localhost:8848/nacos (用户名/密码: nacos/nacos)

**停止服务**:
```powershell
wsl docker-compose down
```

### 方式二: 本地开发部署

#### 1. 启动 MySQL 数据库

```powershell
# 使用 Docker 启动 MySQL
wsl docker run -d --name library-mysql \
  -e MYSQL_ROOT_PASSWORD=root123456 \
  -p 3308:3306 \
  mysql:8.4

# 初始化数据库
cd backend/docs/sql
wsl docker exec -i library-mysql mysql -uroot -proot123456 < nacos_init.sql
wsl docker exec -i library-mysql mysql -uroot -proot123456 < library_auth_schema.sql
wsl docker exec -i library-mysql mysql -uroot -proot123456 < auth_init.sql
wsl docker exec -i library-mysql mysql -uroot -proot123456 < library_book_schema.sql
wsl docker exec -i library-mysql mysql -uroot -proot123456 < library_borrow_schema.sql
```

#### 2. 启动 Nacos Server

```powershell
# 使用 Docker 启动 Nacos
wsl docker run -d --name library-nacos \
  -e MODE=standalone \
  -e MYSQL_SERVICE_HOST=host.docker.internal \
  -e MYSQL_SERVICE_PORT=3308 \
  -e MYSQL_SERVICE_DB_NAME=nacos_config \
  -e MYSQL_SERVICE_USER=root \
  -e MYSQL_SERVICE_PASSWORD=root123456 \
  -p 8848:8848 \
  -p 9848:9848 \
  nacos/nacos-server:v3.1.0
```

访问 Nacos 控制台: http://localhost:8848/nacos (nacos/nacos)

#### 3. 启动后端服务

```powershell
cd backend

# 编译打包
mvn clean package -DskipTests

# 启动各个服务 (按顺序)
# 1. 启动网关
cd library-gateway
mvn spring-boot:run

# 2. 启动认证服务
cd library-auth
mvn spring-boot:run

# 3. 启动图书服务
cd library-book
mvn spring-boot:run

# 4. 启动借阅服务
cd library-borrow
mvn spring-boot:run
```

#### 4. 启动前端服务

```powershell
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm start
```

访问前端应用: http://localhost:3000

## 👥 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 普通用户 | user1 | user123 |
| 普通用户 | user2 | user123 |
| 测试用户 | user3 | user123 |

## 📖 功能说明

### 用户功能

- ✅ 用户登录/注册
- ✅ 图书列表浏览（分页、搜索、筛选）
- ✅ 图书详情查看
- ✅ 图书借阅
- ✅ 我的借阅记录
- ✅ 图书归还
- ✅ 图书续借
- ✅ 借阅历史查询

### 管理员功能

- ✅ 图书管理（增删改查）
- ✅ 图书分类管理
- ✅ 所有借阅记录查看
- ✅ 用户借阅统计
- ✅ 逾期图书提醒

## 🔗 API 文档

详细的 API 文档请参考:
- [后端 API 文档](backend/docs/api/API.md)
- [数据库结构文档](backend/docs/sql/DATABASE_STRUCTURE.md)

主要 API 端点:

| 服务 | 端点 | 说明 |
|------|------|------|
| 认证服务 | POST /api/auth/login | 用户登录 |
| 认证服务 | POST /api/auth/register | 用户注册 |
| 图书服务 | GET /api/books | 获取图书列表 |
| 图书服务 | POST /api/books | 添加图书 (管理员) |
| 图书服务 | GET /api/categories | 获取分类列表 |
| 借阅服务 | POST /api/borrows | 借阅图书 |
| 借阅服务 | PUT /api/borrows/{id}/return | 归还图书 |
| 借阅服务 | PUT /api/borrows/{id}/renew | 续借图书 |
| 借阅服务 | GET /api/borrows/my | 我的借阅记录 |

## 📊 数据库设计

系统采用微服务架构，每个服务独立数据库:

| 数据库 | 表 | 说明 |
|--------|-----|------|
| library_auth | t_user | 用户表 |
| library_book | t_book, t_category | 图书表、分类表 |
| library_borrow | t_borrow_record | 借阅记录表 |
| nacos_config | - | Nacos 配置表 |

详细的数据库结构和字段说明请查看: [数据库结构文档](backend/docs/sql/DATABASE_STRUCTURE.md)

## 🔧 配置说明

### Nacos 配置

所有服务的配置都存储在 Nacos 配置中心，配置文件格式:

- `library-gateway.yml` - 网关配置
- `library-auth.yml` - 认证服务配置
- `library-book.yml` - 图书服务配置
- `library-borrow.yml` - 借阅服务配置

### 环境变量

Docker 部署时可以通过环境变量配置:

```yaml
# MySQL 配置
MYSQL_HOST: localhost
MYSQL_PORT: 3308
MYSQL_ROOT_PASSWORD: root123456

# Nacos 配置
NACOS_HOST: localhost
NACOS_PORT: 8848
```

## 🐛 常见问题

### 1. 服务启动失败

**问题**: 服务无法连接到 Nacos
**解决**: 
- 确保 Nacos 已启动并可访问
- 检查 `bootstrap.yml` 中的 Nacos 地址配置

### 2. 前端无法访问后端

**问题**: 前端请求返回 CORS 错误
**解决**: 
- 网关已配置跨域，检查前端 API 地址是否正确 (应该是 http://localhost:8080)

### 3. Docker 容器无法启动

**问题**: MySQL 容器无法启动
**解决**:
- 检查端口 3308 是否被占用
- 查看 Docker 日志: `wsl docker logs library-mysql`

### 4. JWT 认证失败

**问题**: 请求返回 401 未授权
**解决**:
- 确保请求头包含正确的 Authorization 令牌
- 检查令牌是否已过期 (默认 24 小时)

## 📝 开发指南

### 后端开发

详细的后端开发文档请参考: [backend/README.md](backend/README.md)

主要包含:
- 项目结构说明
- 代码规范
- 数据库操作
- Feign 客户端使用
- 异常处理
- 单元测试

### 前端开发

详细的前端开发文档请参考: [frontend/README.md](frontend/README.md)

主要包含:
- 项目结构说明
- 组件开发
- 路由配置
- API 调用
- 状态管理
- 样式开发

## 🧪 测试

### 后端测试

```powershell
# 运行所有测试
cd backend
mvn test

# 运行指定服务的测试
cd library-auth
mvn test
```

### 前端测试

```powershell
cd frontend
npm test
```

### API 测试

可以使用以下工具测试 API:
- [Postman Collection](backend/library-system-api-collection.json)
- [APIFOX 测试指南](backend/APIFOX测试指南.md)

## 📦 部署

### Docker 生产部署

```powershell
# 构建并启动所有服务
cd backend
wsl docker-compose up -d --build

# 查看服务状态
wsl docker-compose ps

# 查看日志
wsl docker-compose logs -f

# 停止服务
wsl docker-compose down
```

### 前端生产构建

```powershell
cd frontend

# 构建生产版本
npm run build

# build 目录包含生产环境的静态文件
# 可以部署到 Nginx、Apache 等 Web 服务器
```

## 📄 许可证

本项目采用 [MIT 许可证](LICENSE)

## 🤝 贡献

欢迎贡献代码和提出建议！

1. Fork 本项目
2. 创建你的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request

## 📧 联系方式

如有问题或建议，请通过以下方式联系:

- 提交 Issue
- 发送邮件

## 🙏 致谢

感谢以下开源项目:
- [Spring Cloud](https://spring.io/projects/spring-cloud)
- [Spring Cloud Alibaba](https://github.com/alibaba/spring-cloud-alibaba)
- [Nacos](https://nacos.io/)
- [React](https://reactjs.org/)
- [Ant Design](https://ant.design/)

---

**⭐ 如果这个项目对你有帮助，请给个 Star！**

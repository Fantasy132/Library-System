# 书籍借阅管理系统 - 微服务架构

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-green.svg)](https://spring.io/projects/spring-cloud)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

一个基于 Spring Cloud 微服务架构的图书借阅管理系统，实现了用户认证、图书管理、借阅管理等核心功能。

## 📖 项目简介

本项目是一个完整的微服务课程大作业示例，采用主流的微服务技术栈构建，包括服务注册与发现、API 网关、服务间通信、容错保护等核心功能。系统支持图书的借阅、归还、续借等操作，提供完整的用户权限管理。

## 🏗️ 技术架构

### 核心技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | 开发语言 |
| Spring Boot | 3.5.7 | 应用框架 |
| Spring Cloud | 2025.0.0 (Northfields) | 微服务框架 |
| Spring Cloud Alibaba | 2025.0.0 | 阿里巴巴微服务组件 |
| Nacos | 3.1.0 | 服务注册与发现、配置中心 |
| Spring Cloud Gateway | 随 Spring Cloud | API 网关 |
| OpenFeign | 随 Spring Cloud | 服务间通信 |
| Spring Cloud LoadBalancer | 随 Spring Cloud | 负载均衡 |
| Resilience4j | 随 Spring Cloud | 容错保护 |
| MySQL | 8.4 | 关系型数据库 |
| MyBatis-Plus | 3.5.10.1 | ORM 框架 |
| JWT | 0.12.6 | 身份认证 |
| Maven | - | 构建工具 |

### 系统架构图

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────────────┐
│      API Gateway (8080)                 │
│  - 路由转发                              │
│  - JWT 认证                              │
│  - 限流熔断                              │
│  - 跨域处理                              │
└──────┬──────────────┬──────────┬────────┘
       │              │          │
       ▼              ▼          ▼
┌──────────┐   ┌──────────┐   ┌──────────┐
│  Auth    │   │  Book    │   │  Borrow  │
│ Service  │   │ Service  │   │ Service  │
│  (8081)  │   │  (8082)  │   │  (8083)  │
└────┬─────┘   └────┬─────┘   └────┬─────┘
     │              │              │
     ▼              ▼              ▼
┌─────────────────────────────────────────┐
│            Nacos Server (8848)          │
│  - 服务注册与发现                         │
│  - 配置管理                               │
└─────────────────────────────────────────┘
     │              │              │
     ▼              ▼              ▼
┌──────────┐   ┌──────────┐   ┌──────────┐
│  Auth    │   │  Book    │   │  Borrow  │
│   DB     │   │   DB     │   │   DB     │
└──────────┘   └──────────┘   └──────────┘
```

## 📁 项目结构

```
library-system/
├── library-common/           # 公共模块
│   ├── config/              # 公共配置
│   ├── constant/            # 常量定义
│   ├── dto/                 # 数据传输对象
│   ├── exception/           # 异常处理
│   ├── result/              # 统一响应结果
│   ├── interceptor/         # 拦截器
│   └── utils/               # 工具类
├── library-gateway/          # API 网关服务
│   ├── config/              # 网关配置
│   ├── filter/              # 网关过滤器
│   ├── handler/             # 异常处理器
│   └── util/                # 工具类
├── library-auth/             # 认证服务
│   ├── controller/          # 控制器
│   ├── service/             # 业务服务
│   ├── mapper/              # 数据访问层
│   ├── entity/              # 实体类
│   ├── dto/                 # DTO
│   └── config/              # 配置类
├── library-book/             # 图书服务
│   ├── controller/          # 控制器
│   ├── service/             # 业务服务
│   ├── mapper/              # 数据访问层
│   ├── entity/              # 实体类
│   └── feign/               # Feign 客户端
├── library-borrow/           # 借阅服务
│   ├── controller/          # 控制器
│   ├── service/             # 业务服务
│   ├── mapper/              # 数据访问层
│   ├── entity/              # 实体类
│   └── feign/               # Feign 客户端
├── docs/                     # 文档目录
│   ├── sql/                 # SQL 脚本
│   ├── nacos/               # Nacos 配置
│   └── api/                 # API 文档
└── pom.xml                   # 根 POM 文件
```

## 🚀 快速开始

### 环境要求

- JDK 21+
- Maven 3.8+
- MySQL 8.4+
- Nacos Server 3.1.0

### 1. 安装 Nacos Server

#### Windows 环境

```powershell
# 下载 Nacos 3.1.0
Invoke-WebRequest -Uri https://github.com/alibaba/nacos/releases/download/3.1.0/nacos-server-3.1.0.zip -OutFile nacos-server-3.1.0.zip

# 解压
Expand-Archive -Path nacos-server-3.1.0.zip -DestinationPath ./

# 进入目录并启动（单机模式）
cd nacos/bin
.\startup.cmd -m standalone
```

#### Linux / Mac 环境

```bash
# 下载 Nacos 3.1.0
wget https://github.com/alibaba/nacos/releases/download/3.1.0/nacos-server-3.1.0.tar.gz

# 解压
tar -xvf nacos-server-3.1.0.tar.gz

# 进入目录并启动（单机模式）
cd nacos/bin
sh startup.sh -m standalone
```

访问 Nacos 控制台：http://localhost:8848/nacos
- 用户名: nacos
- 密码: nacos

### 2. 初始化数据库

执行以下 SQL 脚本创建数据库和表：

```sql
-- 1. 认证服务数据库
source docs/sql/auth_init.sql

-- 2. 图书服务数据库
source library-book/src/main/resources/sql/init.sql

-- 3. 借阅服务数据库
source library-borrow/src/main/resources/sql/init.sql
```

### 3. 配置数据库连接

修改各服务的 `application.yml` 文件，配置正确的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/database_name?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password
```

### 4. 编译项目

```bash
# 在项目根目录执行
mvn clean install -DskipTests
```

### 5. 启动服务

按以下顺序启动各个服务：

```bash
# 1. 启动 Auth Service (认证服务)
cd library-auth
mvn spring-boot:run

# 2. 启动 Book Service (图书服务)
cd library-book
mvn spring-boot:run

# 3. 启动 Borrow Service (借阅服务)
cd library-borrow
mvn spring-boot:run

# 4. 启动 Gateway Service (网关服务)
cd library-gateway
mvn spring-boot:run
```

或使用 IDE (如 IntelliJ IDEA) 分别启动各服务的 Application 主类。

### 6. 验证服务

访问 Nacos 控制台，确认所有服务已成功注册：
- http://localhost:8848/nacos

查看服务列表应该包含：
- library-gateway
- library-auth
- library-book
- library-borrow

## 📡 API 接口

### 认证接口

| 接口 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 用户注册 | POST | /api/auth/register | 注册新用户 |
| 用户登录 | POST | /api/auth/login | 用户登录获取 Token |
| Token 验证 | POST | /api/auth/verify | 验证 Token 有效性 |
| Token 刷新 | POST | /api/auth/refresh | 刷新 Token |
| 退出登录 | POST | /api/auth/logout | 退出登录 |

### 图书接口

| 接口 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 图书列表 | GET | /api/books | 获取图书列表（分页） |
| 图书详情 | GET | /api/books/{id} | 获取图书详情 |
| 添加图书 | POST | /api/books | 添加新图书（管理员） |
| 更新图书 | PUT | /api/books/{id} | 更新图书信息（管理员） |
| 删除图书 | DELETE | /api/books/{id} | 删除图书（管理员） |
| 分类列表 | GET | /api/categories | 获取分类列表 |

### 借阅接口

| 接口 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 借阅图书 | POST | /api/borrow | 借阅图书 |
| 归还图书 | POST | /api/return/{id} | 归还图书 |
| 续借图书 | POST | /api/renew/{id} | 续借图书 |
| 借阅记录 | GET | /api/borrow/records | 查询借阅记录 |
| 我的借阅 | GET | /api/borrow/my | 查询当前用户借阅 |

### 请求示例

#### 用户登录

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

响应：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "JWT",
    "expiresIn": 86400,
    "userInfo": {
      "id": 1,
      "username": "admin",
      "email": "admin@library.com",
      "role": "ADMIN"
    }
  },
  "timestamp": 1703001234567
}
```

#### 获取图书列表

```bash
curl -X GET "http://localhost:8080/api/books?page=1&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

#### 借阅图书

```bash
curl -X POST http://localhost:8080/api/borrow \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "bookId": 1,
    "borrowDays": 30
  }'
```

## 🔑 默认账号

### 管理员账号
- 用户名: `admin`
- 密码: `admin123`
- 角色: ADMIN

### 测试用户
- 用户名: `user1` / `user2` / `user3`
- 密码: `user123`
- 角色: USER

## 🛠️ 配置说明

### JWT 配置

在 `application.yml` 中配置 JWT 相关参数：

```yaml
jwt:
  secret: LibrarySystemSecretKey2024ForJWTTokenGenerationAndValidation
  expiration: 86400000         # Access Token 过期时间（24小时）
  refresh-expiration: 604800000 # Refresh Token 过期时间（7天）
  token-prefix: "Bearer "
  token-header: Authorization
```

### Nacos 配置

在 `application.yml` 中配置 Nacos 连接：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: public
        group: DEFAULT_GROUP
      config:
        server-addr: localhost:8848
        namespace: public
        group: DEFAULT_GROUP
        file-extension: yaml
```

### 网关路由配置

网关路由在 `library-gateway/src/main/resources/application.yml` 中配置：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://library-auth
          predicates:
            - Path=/api/auth/**,/api/users/**
          filters:
            - StripPrefix=1
            - name: CircuitBreaker
              args:
                name: authService
                fallbackUri: forward:/fallback
```

## 📊 监控端点

每个服务都提供了 Actuator 监控端点：

- Health Check: `http://localhost:PORT/actuator/health`
- Metrics: `http://localhost:PORT/actuator/metrics`
- Info: `http://localhost:PORT/actuator/info`

网关额外提供：
- Circuit Breakers: `http://localhost:8080/actuator/circuitbreakers`
- Gateway Routes: `http://localhost:8080/actuator/gateway/routes`

## 🧪 测试

### 运行单元测试

```bash
mvn test
```

### 运行集成测试

```bash
mvn verify
```

## 📦 Docker 部署

### 构建镜像

```bash
# 构建所有服务镜像
mvn clean package
docker-compose build
```

### 启动服务

```bash
docker-compose up -d
```

### 停止服务

```bash
docker-compose down
```

## ⚠️ 注意事项

1. **JDK 版本**: 必须使用 JDK 21 或更高版本
2. **Nacos 版本**: 使用 Nacos 3.1.0，与 Spring Cloud Alibaba 2025.0.0 兼容
3. **数据库编码**: 确保数据库使用 utf8mb4 字符集
4. **服务启动顺序**: 先启动 Nacos，再按顺序启动微服务
5. **端口占用**: 确保以下端口未被占用：
   - 8080 (Gateway)
   - 8081 (Auth Service)
   - 8082 (Book Service)
   - 8083 (Borrow Service)
   - 8848 (Nacos)

## 🐛 常见问题

### 1. 服务注册失败

**问题**: 服务无法注册到 Nacos

**解决方案**:
- 检查 Nacos Server 是否正常运行
- 检查 `application.yml` 中的 Nacos 地址配置
- 检查网络连接

### 2. JWT Token 验证失败

**问题**: 请求返回 401 Unauthorized

**解决方案**:
- 确保 Token 正确添加到 `Authorization` 头
- 检查 Token 格式: `Bearer YOUR_TOKEN`
- 确认 Token 未过期

### 3. 数据库连接失败

**问题**: 服务启动时报数据库连接错误

**解决方案**:
- 检查 MySQL 服务是否运行
- 验证数据库连接信息（URL、用户名、密码）
- 确认数据库已创建并执行了初始化脚本

### 4. 服务间调用失败

**问题**: Feign 调用报错

**解决方案**:
- 检查目标服务是否正常运行
- 查看 Nacos 控制台确认服务已注册
- 检查熔断器状态

## 📚 相关文档

- [Spring Boot 官方文档](https://docs.spring.io/spring-boot/index.html)
- [Spring Cloud 官方文档](https://spring.io/projects/spring-cloud)
- [Spring Cloud Alibaba 文档](https://spring-cloud-alibaba-group.github.io/github-pages/2023/zh-cn/index.html)
- [Nacos 官方文档](https://nacos.io/zh-cn/docs/v2/quickstart/quick-start.html)
- [MyBatis-Plus 官方文档](https://baomidou.com/)

## 📄 许可证

本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。

## 👥 贡献者

欢迎提交 Issue 和 Pull Request！

## 📧 联系方式

如有问题，请通过以下方式联系：

- 提交 Issue: [GitHub Issues](https://github.com/your-repo/library-system/issues)
- 邮箱: library-system@example.com

---

**注意**: 本项目仅用于学习和教学目的，不建议直接用于生产环境。

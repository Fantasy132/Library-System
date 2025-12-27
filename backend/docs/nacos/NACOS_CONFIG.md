# Nacos 配置说明

本文档说明如何在 Nacos 中配置书籍借阅管理系统的各个微服务。

## 📌 Nacos 版本

- Nacos Server: **3.1.0**
- 与 Spring Cloud Alibaba 2025.0.0 完全兼容

## 🚀 Nacos Server 安装

### Windows 环境

```powershell
# 1. 下载 Nacos 3.1.0
Invoke-WebRequest -Uri https://github.com/alibaba/nacos/releases/download/3.1.0/nacos-server-3.1.0.zip -OutFile nacos-server-3.1.0.zip

# 2. 解压
Expand-Archive -Path nacos-server-3.1.0.zip -DestinationPath ./

# 3. 启动 Nacos（单机模式）
cd nacos/bin
.\startup.cmd -m standalone
```

### Linux / Mac 环境

```bash
# 1. 下载 Nacos 3.1.0
wget https://github.com/alibaba/nacos/releases/download/3.1.0/nacos-server-3.1.0.tar.gz

# 2. 解压
tar -xvf nacos-server-3.1.0.tar.gz

# 3. 启动 Nacos（单机模式）
cd nacos/bin
sh startup.sh -m standalone
```

### Docker 环境

```bash
docker run -d \
  --name nacos-standalone \
  -e MODE=standalone \
  -p 8848:8848 \
  -p 9848:9848 \
  nacos/nacos-server:v3.1.0
```

### 访问 Nacos 控制台

- URL: http://localhost:8848/nacos
- 默认用户名: `nacos`
- 默认密码: `nacos`

## 📋 配置管理

### 配置文件命名规范

Nacos 配置文件的 Data ID 格式：`${spring.application.name}-${profile}.${file-extension}`

例如：
- `library-gateway-dev.yaml`
- `library-auth-prod.yaml`

### 命名空间 (Namespace)

建议为不同环境创建不同的命名空间：

| 命名空间 | 命名空间 ID | 用途 |
|---------|------------|------|
| public | | 开发环境 |
| test | test | 测试环境 |
| prod | prod | 生产环境 |

### 配置分组 (Group)

可以使用不同的分组来组织配置：

| 分组 | 用途 |
|------|------|
| DEFAULT_GROUP | 默认分组 |
| GATEWAY_GROUP | 网关服务配置 |
| SERVICE_GROUP | 业务服务配置 |
| DATABASE_GROUP | 数据库配置 |

## 🔧 服务配置示例

### 1. Gateway 网关配置

**Data ID**: `library-gateway.yaml`  
**Group**: `DEFAULT_GROUP`  
**配置格式**: `YAML`

```yaml
server:
  port: 8080

spring:
  application:
    name: library-gateway
  
  cloud:
    gateway:
      routes:
        # Auth Service 路由
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
        
        # Book Service 路由
        - id: book-service
          uri: lb://library-book
          predicates:
            - Path=/api/books/**,/api/categories/**
          filters:
            - StripPrefix=1
            - name: CircuitBreaker
              args:
                name: bookService
                fallbackUri: forward:/fallback
        
        # Borrow Service 路由
        - id: borrow-service
          uri: lb://library-borrow
          predicates:
            - Path=/api/borrow/**,/api/return/**,/api/renew/**
          filters:
            - StripPrefix=1
            - name: CircuitBreaker
              args:
                name: borrowService
                fallbackUri: forward:/fallback

# JWT 配置
jwt:
  secret: ${JWT_SECRET:LibrarySystemSecretKey2024ForJWTTokenGenerationAndValidation}
  header: Authorization
  prefix: "Bearer "
  whitelist:
    - /api/auth/login
    - /api/auth/register
    - /api/auth/verify
    - /actuator/**
    - /fallback

# Resilience4j 配置
resilience4j:
  circuitbreaker:
    configs:
      default:
        sliding-window-type: COUNT_BASED
        sliding-window-size: 10
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        permitted-number-of-calls-in-half-open-state: 3
        wait-duration-in-open-state: 10s
        automatic-transition-from-open-to-half-open-enabled: true
    instances:
      authService:
        base-config: default
      bookService:
        base-config: default
      borrowService:
        base-config: default

# 日志配置
logging:
  level:
    root: INFO
    com.library.gateway: DEBUG
    org.springframework.cloud.gateway: INFO
```

### 2. Auth 认证服务配置

**Data ID**: `library-auth.yaml`  
**Group**: `DEFAULT_GROUP`  
**配置格式**: `YAML`

```yaml
server:
  port: 8081

spring:
  application:
    name: library-auth
  
  # 数据源配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/library_auth?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root}
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 30000
      pool-name: AuthServiceHikariPool
      max-lifetime: 1800000
      connection-timeout: 30000

# MyBatis-Plus 配置
mybatis-plus:
  mapper-locations: classpath:mapper/**/*.xml
  type-aliases-package: com.library.auth.entity
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
      table-prefix: t_
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: false
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

# JWT 配置
jwt:
  secret: ${JWT_SECRET:LibrarySystemSecretKey2024ForJWTTokenGenerationAndValidation}
  expiration: 86400000  # 24小时
  refresh-expiration: 604800000  # 7天
  token-prefix: "Bearer "
  token-header: Authorization

# 日志配置
logging:
  level:
    root: INFO
    com.library.auth: DEBUG
    com.library.auth.mapper: DEBUG
```

### 3. Book 图书服务配置

**Data ID**: `library-book.yaml`  
**Group**: `DEFAULT_GROUP`  
**配置格式**: `YAML`

```yaml
server:
  port: 8082

spring:
  application:
    name: library-book
  
  # 数据源配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/library_book?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root}
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 30000
      pool-name: BookServiceHikariPool
      max-lifetime: 1800000
      connection-timeout: 30000

# MyBatis-Plus 配置
mybatis-plus:
  mapper-locations: classpath:mapper/**/*.xml
  type-aliases-package: com.library.book.entity
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
      table-prefix: t_
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: false
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

# Feign 配置
feign:
  client:
    config:
      default:
        connect-timeout: 5000
        read-timeout: 5000
  circuitbreaker:
    enabled: true

# 日志配置
logging:
  level:
    root: INFO
    com.library.book: DEBUG
    com.library.book.mapper: DEBUG
```

### 4. Borrow 借阅服务配置

**Data ID**: `library-borrow.yaml`  
**Group**: `DEFAULT_GROUP`  
**配置格式**: `YAML`

```yaml
server:
  port: 8083

spring:
  application:
    name: library-borrow
  
  # 数据源配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/library_borrow?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root}
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 30000
      pool-name: BorrowServiceHikariPool
      max-lifetime: 1800000
      connection-timeout: 30000

# MyBatis-Plus 配置
mybatis-plus:
  mapper-locations: classpath:mapper/**/*.xml
  type-aliases-package: com.library.borrow.entity
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
      table-prefix: t_
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: false
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

# Feign 配置
feign:
  client:
    config:
      default:
        connect-timeout: 5000
        read-timeout: 5000
  circuitbreaker:
    enabled: true

# Resilience4j 配置
resilience4j:
  circuitbreaker:
    configs:
      default:
        sliding-window-type: COUNT_BASED
        sliding-window-size: 10
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
    instances:
      bookService:
        base-config: default
      authService:
        base-config: default

# 借阅业务配置
library:
  borrow:
    max-borrow-count: 5  # 最大借阅数量
    default-borrow-days: 30  # 默认借阅天数
    max-renew-count: 2  # 最大续借次数

# 日志配置
logging:
  level:
    root: INFO
    com.library.borrow: DEBUG
    com.library.borrow.mapper: DEBUG
```

## 🔐 配置加密

对于敏感配置（如数据库密码），建议使用 Nacos 的配置加密功能。

### 1. 启用配置加密

在 Nacos Server 的 `application.properties` 中配置：

```properties
# 启用配置加密
nacos.core.config.plugin.encryption.enabled=true
# 加密密钥
nacos.core.config.plugin.encryption.key=YourEncryptionKey
```

### 2. 使用加密配置

```yaml
spring:
  datasource:
    password: ENC(加密后的密码)
```

## 🌍 环境变量

建议使用环境变量来管理不同环境的配置：

### 数据库配置

- `DB_HOST`: 数据库主机地址
- `DB_PORT`: 数据库端口
- `DB_USERNAME`: 数据库用户名
- `DB_PASSWORD`: 数据库密码

### JWT 配置

- `JWT_SECRET`: JWT 签名密钥（生产环境必须修改）

### Nacos 配置

- `NACOS_SERVER_ADDR`: Nacos Server 地址
- `NACOS_NAMESPACE`: 命名空间
- `NACOS_GROUP`: 配置分组

## 📝 配置优先级

配置的优先级从高到低：

1. 命令行参数
2. Java 系统属性
3. 操作系统环境变量
4. Nacos 配置中心
5. bootstrap.yml / bootstrap.properties
6. application.yml / application.properties

## 🔄 配置刷新

Nacos 支持动态配置刷新，无需重启服务。

### 启用配置刷新

在需要刷新的配置类上添加 `@RefreshScope` 注解：

```java
@RefreshScope
@Component
@ConfigurationProperties(prefix = "library.borrow")
public class BorrowConfig {
    private Integer maxBorrowCount;
    private Integer defaultBorrowDays;
    // getter and setter
}
```

### 监听配置变化

```java
@Component
public class ConfigChangeListener {
    
    @NacosConfigListener(dataId = "library-borrow.yaml", groupId = "DEFAULT_GROUP")
    public void onConfigChange(String newContent) {
        System.out.println("配置已更新: " + newContent);
    }
}
```

## 🎯 最佳实践

### 1. 配置分离

- 将公共配置放在共享配置中
- 将特定环境配置放在对应命名空间
- 将敏感配置使用加密存储

### 2. 配置命名

- 使用有意义的配置名称
- 遵循统一的命名规范
- 添加适当的注释说明

### 3. 配置版本管理

- 在 Nacos 控制台使用配置历史功能
- 重要变更前创建配置快照
- 记录配置变更日志

### 4. 配置验证

- 发布配置前先在测试环境验证
- 使用配置监听器监控配置变化
- 实现配置回滚机制

## 📊 监控配置

在 Nacos 控制台可以查看：

- 配置列表
- 配置详情
- 配置历史
- 监听查询

## ⚠️ 注意事项

1. **生产环境**必须修改 Nacos 默认密码
2. **JWT Secret** 在生产环境必须使用强密钥
3. 数据库密码建议使用配置加密
4. 定期备份 Nacos 配置数据
5. 使用独立的 MySQL 存储 Nacos 配置（而非自带的 Derby）

## 🔗 相关链接

- [Nacos 官方文档](https://nacos.io/zh-cn/docs/v2/quickstart/quick-start.html)
- [Spring Cloud Alibaba 文档](https://spring-cloud-alibaba-group.github.io/github-pages/2023/zh-cn/index.html)
- [Nacos 配置中心最佳实践](https://nacos.io/zh-cn/docs/v2/ecology/use-nacos-with-spring-cloud.html)

---

如有问题，请参考主 [README.md](../README.md) 或提交 Issue。

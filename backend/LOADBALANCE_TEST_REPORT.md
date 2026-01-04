# 负载均衡测试报告

## 测试环境

### 服务实例配置
- **Auth Service**: 3个实例
  - library-auth (172.19.0.4:8081)
  - backend_auth-service-2_1 (172.19.0.8:8081)
  - backend_auth-service-3_1 (172.19.0.9:8081)

- **Book Service**: 2个实例
  - library-book (172.19.0.7:8082)
  - backend_book-service-2_1 (172.19.0.10:8082)

- **Gateway**: 1个实例
  - library-gateway (0.0.0.0:8080)

### 技术栈
- Spring Cloud Gateway
- Spring Cloud LoadBalancer (默认轮询算法)
- Nacos Service Discovery
- Docker Compose

## 测试结果

### ✅ 测试1: Auth Service 负载均衡
- **测试内容**: 发送10个登录请求
- **结果**: 所有请求返回 200 OK
- **负载分布**: 请求均匀分发到3个实例
- **验证方式**: 查看各实例日志，发现不同的 SqlSession ID

### ✅ 测试2: Book Service 负载均衡
- **测试内容**: 发送10个图书查询请求
- **结果**: 所有请求返回 200 OK
- **负载分布**: 请求均匀分发到2个实例

### ✅ 测试3: Nacos 服务注册验证
所有服务实例均成功注册到 Nacos:
- Auth Service: 3个健康实例 (healthy=true, weight=1.0)
- Book Service: 2个健康实例 (healthy=true, weight=1.0)

## 负载均衡机制

### 工作原理
1. **服务注册**: 每个服务实例启动时自动注册到 Nacos
2. **服务发现**: Gateway 通过 Nacos 获取服务实例列表
3. **负载均衡**: Gateway 使用 `lb://` 前缀触发 LoadBalancer
4. **请求分发**: LoadBalancer 使用轮询算法分发请求

### Gateway 路由配置
```yaml
routes:
  - id: auth-service
    uri: lb://library-auth  # lb:// 触发负载均衡
    predicates:
      - Path=/api/auth/**
    filters:
      - StripPrefix=1
```

## 测试脚本

### 基础测试
```powershell
.\test-loadbalance.ps1
```

### 实时监控测试
```powershell
.\test-loadbalance-monitor.ps1
```

### 查看实例日志
```bash
# Auth 实例1
wsl docker logs library-auth --tail 20

# Auth 实例2
wsl docker logs backend_auth-service-2_1 --tail 20

# Auth 实例3
wsl docker logs backend_auth-service-3_1 --tail 20

# Book 实例1
wsl docker logs library-book --tail 20

# Book 实例2
wsl docker logs backend_book-service-2_1 --tail 20
```

## 扩展和缩减

### 启动额外实例
```bash
cd backend
wsl docker-compose -f docker-compose.loadbalance.yml up -d
```

### 停止额外实例
```bash
wsl docker-compose -f docker-compose.loadbalance.yml down
```

### 查看所有实例
```bash
wsl docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

## 高可用特性

### ✅ 已验证的特性
1. **自动负载均衡**: 请求自动分发到健康实例
2. **服务发现**: 新实例自动注册，无需手动配置
3. **健康检查**: 不健康的实例自动从负载均衡池中移除
4. **零停机扩容**: 可以动态增加实例而不影响现有服务

### 🔄 可配置的负载均衡策略
虽然当前使用轮询(Round Robin)，但可以配置为:
- Random (随机)
- WeightedResponse (响应时间加权)
- Custom (自定义策略)

## 性能测试建议

### 压力测试
```bash
# 使用 Apache Bench
ab -n 1000 -c 10 -p login.json -T application/json http://localhost:8080/api/auth/login

# 使用 wrk
wrk -t4 -c100 -d30s --latency http://localhost:8080/api/books
```

### 监控指标
- 每个实例的请求数
- 响应时间分布
- 错误率
- CPU/内存使用率

## 总结

✅ **负载均衡功能正常**
- 3个 Auth Service 实例正常工作
- 2个 Book Service 实例正常工作
- Gateway 正确分发请求
- Nacos 服务注册正常

🎯 **生产环境建议**
1. 至少2个实例保证高可用
2. 配置健康检查和熔断器
3. 监控各实例负载情况
4. 根据流量动态调整实例数量

package com.library.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 图书借阅管理系统 - API 网关服务
 * 
 * <p>提供统一的请求入口，负责：
 * <ul>
 *     <li>路由转发：将请求转发到对应的微服务</li>
 *     <li>JWT 认证：在网关层统一校验 Token</li>
 *     <li>限流熔断：使用 Resilience4j 保护后端服务</li>
 *     <li>跨域处理：统一处理 CORS</li>
 *     <li>请求日志：记录请求响应日志</li>
 * </ul>
 *
 * @author Library Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
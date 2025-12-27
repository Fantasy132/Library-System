package com.library.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 认证服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.library.auth", "com.library.common"})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.library.auth.mapper")
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
        System.out.println("====================================");
        System.out.println("  Auth Service 启动成功！");
        System.out.println("  端口: 8081");
        System.out.println("====================================");
    }
}
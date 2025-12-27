package com.library.borrow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 借阅服务启动类
 *
 * @author Library System
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = {"com.library.borrow", "com.library.common"})
@EnableDiscoveryClient
@EnableFeignClients
public class BorrowApplication {

    public static void main(String[] args) {
        SpringApplication.run(BorrowApplication.class, args);
        System.out.println("""
                ╔═══════════════════════════════════════════════════════════╗
                ║                                                           ║
                ║    📖 Library Borrow Service Started Successfully! 📖     ║
                ║                                                           ║
                ║     Port: 8083                                            ║
                ║     Swagger: http://localhost:8083/swagger-ui.html        ║
                ║                                                           ║
                ╚═══════════════════════════════════════════════════════════╝
                """);
    }
}
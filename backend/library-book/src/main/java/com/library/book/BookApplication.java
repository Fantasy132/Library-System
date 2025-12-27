package com.library.book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 图书服务启动类
 *
 * @author Library System
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = {"com.library.book", "com.library.common"})
@EnableDiscoveryClient
@EnableFeignClients
public class BookApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookApplication.class, args);
        System.out.println("""
                ╔═══════════════════════════════════════════════════════════╗
                ║                                                           ║
                ║     📚 Library Book Service Started Successfully! 📚       ║
                ║                                                           ║
                ║     Port: 8082                                            ║
                ║     Swagger: http://localhost:8082/swagger-ui.html        ║
                ║                                                           ║
                ╚═══════════════════════════════════════════════════════════╝
                """);
    }
}
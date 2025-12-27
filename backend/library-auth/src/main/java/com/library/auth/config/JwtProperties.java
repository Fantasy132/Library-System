package com.library.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * 密钥
     */
    private String secret = "LibrarySystemSecretKey2024ForJWTTokenGenerationAndValidation";

    /**
     * 过期时间（毫秒），默认24小时
     */
    private Long expiration = 86400000L;

    /**
     * 刷新Token过期时间（毫秒），默认7天
     */
    private Long refreshExpiration = 604800000L;

    /**
     * Token 前缀
     */
    private String tokenPrefix = "Bearer ";

    /**
     * Token 请求头
     */
    private String tokenHeader = "Authorization";
}
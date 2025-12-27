package com.library.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * JWT 配置属性
 * 
 * <p>从配置文件中读取 JWT 相关配置，包括：
 * <ul>
 *     <li>secret：JWT 签名密钥</li>
 *     <li>whitelist：不需要认证的白名单路径</li>
 * </ul>
 *
 * @author Library Team
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 签名密钥（至少 256 位，即 32 字节）
     */
    private String secret = "LibraryManagementSystemSecretKey2024!@#$%^&*()";

    /**
     * Token 请求头名称
     */
    private String header = "Authorization";

    /**
     * Token 前缀
     */
    private String prefix = "Bearer ";

    /**
     * 白名单路径列表（不需要认证）
     */
    private List<String> whitelist = new ArrayList<>();
}
package com.library.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 * 
 * <p>用于解析和验证 JWT Token
 *
 * @author Library Team
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * 解析 JWT Token
     *
     * @param token  JWT Token
     * @param secret 签名密钥
     * @return Claims 声明信息
     * @throws JwtException 解析失败时抛出
     */
    public Claims parseToken(String token, String secret) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token  JWT Token
     * @param secret 签名密钥
     * @return true-有效，false-无效
     */
    public boolean validateToken(String token, String secret) {
        try {
            Claims claims = parseToken(token, secret);
            return claims.getExpiration().after(new Date());
        } catch (ExpiredJwtException e) {
            log.warn("Token 已过期: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.warn("Token 验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从 Token 中获取用户 ID
     *
     * @param token  JWT Token
     * @param secret 签名密钥
     * @return 用户 ID
     */
    public Long getUserId(String token, String secret) {
        Claims claims = parseToken(token, secret);
        return claims.get("userId", Long.class);
    }

    /**
     * 从 Token 中获取用户名
     *
     * @param token  JWT Token
     * @param secret 签名密钥
     * @return 用户名
     */
    public String getUsername(String token, String secret) {
        Claims claims = parseToken(token, secret);
        return claims.getSubject();
    }

    /**
     * 从 Token 中获取用户角色
     *
     * @param token  JWT Token
     * @param secret 签名密钥
     * @return 用户角色
     */
    public String getRole(String token, String secret) {
        Claims claims = parseToken(token, secret);
        return claims.get("role", String.class);
    }
}
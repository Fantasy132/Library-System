package com.library.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 */
@Slf4j
public final class JwtUtils {

    private JwtUtils() {
        throw new IllegalStateException("工具类不允许实例化");
    }

    /**
     * 默认密钥（生产环境请使用配置文件配置）
     */
    private static final String DEFAULT_SECRET = "LibrarySystemSecretKey2024ForJWTTokenGenerationAndValidation";

    /**
     * 默认过期时间：24小时
     */
    private static final long DEFAULT_EXPIRATION = 24 * 60 * 60 * 1000L;

    /**
     * 刷新Token过期时间：7天
     */
    private static final long REFRESH_EXPIRATION = 7 * 24 * 60 * 60 * 1000L;

    /**
     * Claims 键名
     */
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_USERNAME = "username";
    public static final String CLAIM_ROLE = "role";

    /**
     * 获取签名密钥
     */
    private static SecretKey getSigningKey(String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param role     角色
     * @param secret   密钥
     * @param expiration 过期时间（毫秒）
     * @return Token 字符串
     */
    public static String generateToken(Long userId, String username, String role, String secret, long expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USER_ID, userId);
        claims.put(CLAIM_USERNAME, username);
        claims.put(CLAIM_ROLE, role);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(secret))
                .compact();
    }

    /**
     * 生成 Token（使用默认配置）
     */
    public static String generateToken(Long userId, String username, String role) {
        return generateToken(userId, username, role, DEFAULT_SECRET, DEFAULT_EXPIRATION);
    }

    /**
     * 生成 Token（自定义密钥）
     */
    public static String generateToken(Long userId, String username, String role, String secret) {
        return generateToken(userId, username, role, secret, DEFAULT_EXPIRATION);
    }

    /**
     * 生成刷新 Token
     */
    public static String generateRefreshToken(Long userId, String username, String role, String secret) {
        return generateToken(userId, username, role, secret, REFRESH_EXPIRATION);
    }

    /**
     * 解析 Token
     *
     * @param token  Token 字符串
     * @param secret 密钥
     * @return Claims
     */
    public static Claims parseToken(String token, String secret) {
        return Jwts.parser()
                .verifyWith(getSigningKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 解析 Token（使用默认密钥）
     */
    public static Claims parseToken(String token) {
        return parseToken(token, DEFAULT_SECRET);
    }

    /**
     * 从 Token 中获取用户ID
     */
    public static Long getUserId(String token, String secret) {
        Claims claims = parseToken(token, secret);
        return claims.get(CLAIM_USER_ID, Long.class);
    }

    public static Long getUserId(String token) {
        return getUserId(token, DEFAULT_SECRET);
    }

    /**
     * 从 Token 中获取用户名
     */
    public static String getUsername(String token, String secret) {
        Claims claims = parseToken(token, secret);
        return claims.get(CLAIM_USERNAME, String.class);
    }

    public static String getUsername(String token) {
        return getUsername(token, DEFAULT_SECRET);
    }

    /**
     * 从 Token 中获取角色
     */
    public static String getRole(String token, String secret) {
        Claims claims = parseToken(token, secret);
        return claims.get(CLAIM_ROLE, String.class);
    }

    public static String getRole(String token) {
        return getRole(token, DEFAULT_SECRET);
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token  Token 字符串
     * @param secret 密钥
     * @return 是否有效
     */
    public static boolean validateToken(String token, String secret) {
        try {
            parseToken(token, secret);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Token格式错误: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("Token签名错误: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Token为空: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("Token验证失败: {}", e.getMessage());
        }
        return false;
    }

    public static boolean validateToken(String token) {
        return validateToken(token, DEFAULT_SECRET);
    }

    /**
     * 判断 Token 是否过期
     */
    public static boolean isTokenExpired(String token, String secret) {
        try {
            Claims claims = parseToken(token, secret);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean isTokenExpired(String token) {
        return isTokenExpired(token, DEFAULT_SECRET);
    }

    /**
     * 获取 Token 过期时间
     */
    public static Date getExpiration(String token, String secret) {
        Claims claims = parseToken(token, secret);
        return claims.getExpiration();
    }

    public static Date getExpiration(String token) {
        return getExpiration(token, DEFAULT_SECRET);
    }
}
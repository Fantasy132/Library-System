package com.library.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.gateway.config.JwtProperties;
import com.library.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 认证全局过滤器
 * 
 * <p>在网关层统一校验 JWT Token：
 * <ul>
 *     <li>白名单路径直接放行</li>
 *     <li>其他路径验证 Token 有效性</li>
 *     <li>验证通过后将用户信息放入请求头传递给下游服务</li>
 * </ul>
 *
 * @author Library Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtProperties jwtProperties;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 用户 ID 请求头名称
     */
    private static final String USER_ID_HEADER = "X-User-Id";

    /**
     * 用户名请求头名称
     */
    private static final String USERNAME_HEADER = "X-Username";

    /**
     * 用户角色请求头名称
     */
    private static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        log.debug("Gateway 收到请求: {} {}", method, path);

        // 检查是否是白名单路径
        if (isWhitelistPath(path, method)) {
            log.debug("白名单路径放行: {}", path);
            return chain.filter(exchange);
        }

        // 获取 Token
        String token = getToken(request);
        if (!StringUtils.hasText(token)) {
            log.warn("请求未携带 Token: {}", path);
            return unauthorizedResponse(exchange, "请先登录");
        }

        // 验证 Token
        try {
            if (!jwtUtil.validateToken(token, jwtProperties.getSecret())) {
                log.warn("Token 验证失败或已过期: {}", path);
                return unauthorizedResponse(exchange, "登录已过期，请重新登录");
            }

            // 解析 Token 获取用户信息
            Claims claims = jwtUtil.parseToken(token, jwtProperties.getSecret());
            Long userId = claims.get("userId", Long.class);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            log.debug("Token 验证成功, userId={}, username={}, role={}", userId, username, role);

            // 将用户信息放入请求头，传递给下游服务
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header(USER_ID_HEADER, String.valueOf(userId))
                    .header(USERNAME_HEADER, username)
                    .header(USER_ROLE_HEADER, role)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            log.error("Token 解析异常: {}", e.getMessage());
            return unauthorizedResponse(exchange, "无效的 Token");
        }
    }

    /**
     * 检查是否是白名单路径
     *
     * @param path   请求路径
     * @param method 请求方法
     * @return true-是白名单路径
     */
    private boolean isWhitelistPath(String path, HttpMethod method) {
        // 配置文件中的白名单
        for (String pattern : jwtProperties.getWhitelist()) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }

        // 内置白名单规则
        // 1. 登录、注册、退出接口
        if (pathMatcher.match("/api/auth/login", path) ||
            pathMatcher.match("/api/auth/register", path) ||
            pathMatcher.match("/api/auth/logout", path)) {
            return true;
        }

        // 2. 图书查询接口（GET 请求）
        if (method == HttpMethod.GET) {
            if (pathMatcher.match("/api/books", path) ||
                pathMatcher.match("/api/books/**", path) ||
                pathMatcher.match("/api/categories", path) ||
                pathMatcher.match("/api/categories/**", path)) {
                return true;
            }
        }

        // 3. 健康检查接口
        if (pathMatcher.match("/actuator/**", path)) {
            return true;
        }

        return false;
    }

    /**
     * 从请求头中获取 Token
     *
     * @param request HTTP 请求
     * @return Token 字符串（不含前缀）
     */
    private String getToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(jwtProperties.getHeader());
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(jwtProperties.getPrefix())) {
            return authHeader.substring(jwtProperties.getPrefix().length());
        }
        return null;
    }

    /**
     * 返回未授权响应
     *
     * @param exchange ServerWebExchange
     * @param message  错误消息
     * @return Mono<Void>
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", message);
        result.put("data", null);
        result.put("timestamp", System.currentTimeMillis());

        try {
            String json = objectMapper.writeValueAsString(result);
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("JSON 序列化失败", e);
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        // 优先级较高，在其他过滤器之前执行
        return -100;
    }
}
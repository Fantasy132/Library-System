package com.library.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 请求日志过滤器
 * 
 * <p>记录每个请求的关键信息：
 * <ul>
 *     <li>请求 ID（用于链路追踪）</li>
 *     <li>请求方法和路径</li>
 *     <li>客户端 IP</li>
 *     <li>响应状态码</li>
 *     <li>请求耗时</li>
 * </ul>
 *
 * @author Library Team
 */
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    /**
     * 请求 ID 请求头名称
     */
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    /**
     * 请求开始时间属性名
     */
    private static final String REQUEST_START_TIME = "requestStartTime";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // 生成请求 ID
        String requestId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        
        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        exchange.getAttributes().put(REQUEST_START_TIME, startTime);

        // 获取请求信息
        HttpMethod method = request.getMethod();
        String path = request.getURI().getPath();
        String queryString = request.getURI().getQuery();
        String clientIp = getClientIp(request);
        String fullPath = queryString != null ? path + "?" + queryString : path;

        // 记录请求日志
        log.info(">>> [{}] {} {} - IP: {} - Time: {}",
                requestId, method, fullPath, clientIp,
                LocalDateTime.now().format(FORMATTER));

        // 将请求 ID 添加到请求头
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(REQUEST_ID_HEADER, requestId)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .then(Mono.fromRunnable(() -> {
                    // 记录响应日志
                    ServerHttpResponse response = exchange.getResponse();
                    long duration = System.currentTimeMillis() - startTime;
                    int statusCode = response.getStatusCode() != null 
                            ? response.getStatusCode().value() 
                            : 0;

                    if (statusCode >= 400) {
                        log.warn("<<< [{}] {} {} - Status: {} - Duration: {}ms",
                                requestId, method, path, statusCode, duration);
                    } else {
                        log.info("<<< [{}] {} {} - Status: {} - Duration: {}ms",
                                requestId, method, path, statusCode, duration);
                    }
                }));
    }

    /**
     * 获取客户端真实 IP
     *
     * @param request HTTP 请求
     * @return 客户端 IP
     */
    private String getClientIp(ServerHttpRequest request) {
        // 优先从代理头获取
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // 多级代理时取第一个 IP
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index).trim();
            }
            return ip;
        }

        ip = request.getHeaders().getFirst("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeaders().getFirst("Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeaders().getFirst("WL-Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        // 从远程地址获取
        if (request.getRemoteAddress() != null) {
            return request.getRemoteAddress().getAddress().getHostAddress();
        }

        return "unknown";
    }

    @Override
    public int getOrder() {
        // 最高优先级，最先执行
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
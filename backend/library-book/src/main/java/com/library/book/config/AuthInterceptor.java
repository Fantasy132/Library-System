package com.library.book.config;

import com.library.book.feign.AuthClient;
import com.library.common.exception.BusinessException;
import com.library.common.result.Result;
import com.library.common.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器
 *
 * @author Library System
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthClient authClient;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 用于存储当前请求的用户信息
     */
    private static final ThreadLocal<AuthClient.AuthUserInfo> CURRENT_USER = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果不是处理方法，直接放行
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 检查方法或类上是否有 @RequireAdmin 注解
        boolean requireAdmin = handlerMethod.hasMethodAnnotation(RequireAdmin.class)
                || handlerMethod.getBeanType().isAnnotationPresent(RequireAdmin.class);

        // 获取 Token
        String token = extractToken(request);

        // 如果需要管理员权限，必须验证 Token
        if (requireAdmin) {
            if (!StringUtils.hasText(token)) {
                throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
            }

            // 调用认证服务验证 Token
            Result<AuthClient.AuthUserInfo> result = authClient.verifyToken(BEARER_PREFIX + token);
            if (!result.isSuccess()) {
                throw new BusinessException(ResultCode.UNAUTHORIZED, result.getMessage());
            }

            AuthClient.AuthUserInfo userInfo = result.getData();

            // 检查是否是管理员
            if (!"ADMIN".equals(userInfo.role())) {
                throw new BusinessException(ResultCode.FORBIDDEN, "需要管理员权限");
            }

            // 存储用户信息
            CURRENT_USER.set(userInfo);
            log.debug("用户 {} 验证通过，角色: {}", userInfo.username(), userInfo.role());
        } else if (StringUtils.hasText(token)) {
            // 非必须验证的接口，如果有 Token 也进行验证
            try {
                Result<AuthClient.AuthUserInfo> result = authClient.verifyToken(BEARER_PREFIX + token);
                if (result.isSuccess()) {
                    CURRENT_USER.set(result.getData());
                }
            } catch (Exception e) {
                log.warn("Token 验证失败，但接口不要求认证: {}", e.getMessage());
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清除当前线程的用户信息
        CURRENT_USER.remove();
    }

    /**
     * 从请求头中提取 Token
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return bearerToken;
    }

    /**
     * 获取当前用户信息
     */
    public static AuthClient.AuthUserInfo getCurrentUser() {
        return CURRENT_USER.get();
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        AuthClient.AuthUserInfo userInfo = CURRENT_USER.get();
        return userInfo != null ? userInfo.userId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        AuthClient.AuthUserInfo userInfo = CURRENT_USER.get();
        return userInfo != null ? userInfo.username() : null;
    }
}
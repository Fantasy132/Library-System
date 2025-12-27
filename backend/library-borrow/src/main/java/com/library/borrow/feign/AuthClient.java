package com.library.borrow.feign;

import com.library.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 认证服务 Feign 客户端
 *
 * @author Library System
 * @since 1.0.0
 */
@FeignClient(name = "library-auth", contextId = "authClient", fallbackFactory = AuthClientFallbackFactory.class)
public interface AuthClient {

    /**
     * 验证Token
     *
     * @param token JWT Token
     * @return 验证结果，包含用户信息
     */
    @GetMapping("/auth/verify")
    Result<AuthUserInfo> verifyToken(@RequestHeader("Authorization") String token);

    /**
     * 认证用户信息
     */
    record AuthUserInfo(
            Long userId,
            String username,
            String role
    ) {
    }
}
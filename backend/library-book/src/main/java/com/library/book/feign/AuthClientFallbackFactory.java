package com.library.book.feign;

import com.library.common.result.Result;
import com.library.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 认证服务 Feign 客户端降级工厂
 *
 * @author Library System
 * @since 1.0.0
 */
@Slf4j
@Component
public class AuthClientFallbackFactory implements FallbackFactory<AuthClient> {

    @Override
    public AuthClient create(Throwable cause) {
        log.error("调用认证服务失败: {}", cause.getMessage(), cause);

        return new AuthClient() {
            @Override
            public Result<AuthUserInfo> verifyToken(String token) {
                log.error("认证服务不可用，Token验证失败: {}", cause.getMessage());
                return Result.fail(ResultCode.SERVICE_UNAVAILABLE, "认证服务暂不可用，请稍后重试");
            }
        };
    }
}
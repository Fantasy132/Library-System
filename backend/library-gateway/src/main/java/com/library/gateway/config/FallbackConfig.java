package com.library.gateway.config;

import com.library.gateway.handler.FallbackHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * 降级路由配置
 * 
 * <p>配置熔断降级时的路由处理
 *
 * @author Library Team
 */
@Configuration
@RequiredArgsConstructor
public class FallbackConfig {

    private final FallbackHandler fallbackHandler;

    /**
     * 配置降级路由
     *
     * @return RouterFunction
     */
    @Bean
    public RouterFunction<ServerResponse> fallbackRouter() {
        return RouterFunctions.route()
                .GET("/fallback", fallbackHandler)
                .POST("/fallback", fallbackHandler)
                .PUT("/fallback", fallbackHandler)
                .DELETE("/fallback", fallbackHandler)
                .build();
    }
}
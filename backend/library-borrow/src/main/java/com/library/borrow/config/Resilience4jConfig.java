package com.library.borrow.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Resilience4j 熔断器配置
 *
 * @author Library System
 * @since 1.0.0
 */
@Configuration
public class Resilience4jConfig {

    /**
     * 全局熔断器配置
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(10))
                        .build())
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        // 滑动窗口类型：基于计数
                        .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                        // 滑动窗口大小
                        .slidingWindowSize(10)
                        // 最小调用次数
                        .minimumNumberOfCalls(5)
                        // 失败率阈值（50%）
                        .failureRateThreshold(50)
                        // 慢调用持续时间阈值
                        .slowCallDurationThreshold(Duration.ofSeconds(5))
                        // 慢调用率阈值
                        .slowCallRateThreshold(50)
                        // 熔断持续时间
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        // 半开状态允许的调用次数
                        .permittedNumberOfCallsInHalfOpenState(3)
                        // 自动从开启状态过渡到半开状态
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .build())
                .build());
    }

    /**
     * 图书服务专用熔断器配置
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> bookServiceCustomizer() {
        return factory -> factory.configure(builder -> builder
                        .timeLimiterConfig(TimeLimiterConfig.custom()
                                .timeoutDuration(Duration.ofSeconds(5))
                                .build())
                        .circuitBreakerConfig(CircuitBreakerConfig.custom()
                                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                                .slidingWindowSize(10)
                                .minimumNumberOfCalls(5)
                                .failureRateThreshold(50)
                                .waitDurationInOpenState(Duration.ofSeconds(20))
                                .permittedNumberOfCallsInHalfOpenState(3)
                                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                                .build()),
                "bookService");
    }
}
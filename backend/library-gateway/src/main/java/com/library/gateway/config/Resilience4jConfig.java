package com.library.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Resilience4j 配置
 * 
 * <p>配置熔断器和限流器，保护后端服务
 *
 * @author Library Team
 */
@Configuration
public class Resilience4jConfig {

    /**
     * 配置默认的熔断器
     *
     * @return Customizer
     */
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        // 滑动窗口类型：基于计数
                        .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                        // 滑动窗口大小
                        .slidingWindowSize(10)
                        // 最小调用次数（达到此数量后才计算失败率）
                        .minimumNumberOfCalls(5)
                        // 失败率阈值（百分比）
                        .failureRateThreshold(50)
                        // 半开状态允许的调用次数
                        .permittedNumberOfCallsInHalfOpenState(3)
                        // 从打开状态到半开状态的等待时间
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        // 自动从半开状态转换到打开状态
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        // 超时时间
                        .timeoutDuration(Duration.ofSeconds(10))
                        .build())
                .build());
    }

    /**
     * 为 Auth 服务配置专用熔断器
     *
     * @return Customizer
     */
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> authServiceCustomizer() {
        return factory -> factory.configure(builder -> builder
                        .circuitBreakerConfig(CircuitBreakerConfig.custom()
                                .slidingWindowSize(5)
                                .minimumNumberOfCalls(3)
                                .failureRateThreshold(50)
                                .waitDurationInOpenState(Duration.ofSeconds(5))
                                .build())
                        .timeLimiterConfig(TimeLimiterConfig.custom()
                                .timeoutDuration(Duration.ofSeconds(5))
                                .build()),
                "authService");
    }

    /**
     * 为 Book 服务配置专用熔断器
     *
     * @return Customizer
     */
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> bookServiceCustomizer() {
        return factory -> factory.configure(builder -> builder
                        .circuitBreakerConfig(CircuitBreakerConfig.custom()
                                .slidingWindowSize(10)
                                .minimumNumberOfCalls(5)
                                .failureRateThreshold(50)
                                .waitDurationInOpenState(Duration.ofSeconds(10))
                                .build())
                        .timeLimiterConfig(TimeLimiterConfig.custom()
                                .timeoutDuration(Duration.ofSeconds(10))
                                .build()),
                "bookService");
    }

    /**
     * 为 Borrow 服务配置专用熔断器
     *
     * @return Customizer
     */
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> borrowServiceCustomizer() {
        return factory -> factory.configure(builder -> builder
                        .circuitBreakerConfig(CircuitBreakerConfig.custom()
                                .slidingWindowSize(10)
                                .minimumNumberOfCalls(5)
                                .failureRateThreshold(50)
                                .waitDurationInOpenState(Duration.ofSeconds(15))
                                .build())
                        .timeLimiterConfig(TimeLimiterConfig.custom()
                                .timeoutDuration(Duration.ofSeconds(15))
                                .build()),
                "borrowService");
    }
}
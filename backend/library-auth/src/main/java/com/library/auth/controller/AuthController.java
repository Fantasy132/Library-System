package com.library.auth.controller;

import com.library.auth.dto.*;
import com.library.auth.service.AuthService;
import com.library.common.constant.CommonConstants;
import com.library.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户信息
     */
    @PostMapping("/register")
    public Result<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        log.info("用户注册请求: username={}", request.getUsername());
        UserDTO userDTO = authService.register(request);
        return Result.success("注册成功", userDTO);
    }

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（包含Token）
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("用户登录请求: username={}", request.getUsername());
        LoginResponse response = authService.login(request);
        return Result.success("登录成功", response);
    }

    /**
     * 验证 Token
     *
     * @param request Token 验证请求
     * @return 验证结果
     */
    @PostMapping("/verify")
    public Result<TokenVerifyResponse> verify(@Valid @RequestBody TokenVerifyRequest request) {
        log.debug("Token验证请求");
        TokenVerifyResponse response = authService.verifyToken(request.getToken());
        return Result.success(response);
    }

    /**
     * 验证 Token（从 Header 获取）
     *
     * @param authorization Authorization 头
     * @return 验证结果
     */
    @GetMapping("/verify")
    public Result<TokenVerifyResponse> verifyFromHeader(
            @RequestHeader(value = CommonConstants.TOKEN_HEADER, required = false) String authorization) {
        log.debug("Token验证请求（从Header）");
        TokenVerifyResponse response = authService.verifyToken(authorization);
        return Result.success(response);
    }

    /**
     * 刷新 Token
     *
     * @param refreshToken 刷新Token
     * @return 新的登录响应
     */
    @PostMapping("/refresh")
    public Result<LoginResponse> refresh(
            @RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken,
            @RequestBody(required = false) TokenVerifyRequest request) {
        log.info("Token刷新请求");
        // 优先从 Header 获取，其次从 Body 获取
        String token = refreshToken;
        if (token == null && request != null) {
            token = request.getToken();
        }
        LoginResponse response = authService.refreshToken(token);
        return Result.success("Token刷新成功", response);
    }

    /**
     * 退出登录
     *
     * @param authorization Authorization 头
     * @return 成功响应
     */
    @PostMapping("/logout")
    public Result<Void> logout(
            @RequestHeader(value = CommonConstants.TOKEN_HEADER, required = false) String authorization) {
        log.info("用户退出登录");
        authService.logout(authorization);
        return Result.success("退出登录成功", null);
    }
}
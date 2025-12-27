package com.library.auth.service;

import com.library.auth.dto.*;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户信息
     */
    UserDTO register(RegisterRequest request);

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（包含Token）
     */
    LoginResponse login(LoginRequest request);

    /**
     * 验证 Token
     *
     * @param token Token 字符串
     * @return 验证结果
     */
    TokenVerifyResponse verifyToken(String token);

    /**
     * 刷新 Token
     *
     * @param refreshToken 刷新Token
     * @return 新的登录响应
     */
    LoginResponse refreshToken(String refreshToken);

    /**
     * 退出登录
     *
     * @param token Token 字符串
     */
    void logout(String token);
}
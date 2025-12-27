package com.library.auth.service.impl;

import com.library.auth.config.JwtProperties;
import com.library.auth.dto.*;
import com.library.auth.entity.User;
import com.library.auth.mapper.UserMapper;
import com.library.auth.service.AuthService;
import com.library.auth.service.UserService;
import com.library.common.constant.CommonConstants;
import com.library.common.exception.BusinessException;
import com.library.common.result.ResultCode;
import com.library.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO register(RegisterRequest request) {
        // 验证两次密码是否一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        if (userService.existsByUsername(request.getUsername())) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS, "用户名已存在");
        }

        // 检查邮箱是否已存在
        if (request.getEmail() != null && userService.existsByEmail(request.getEmail())) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS, "邮箱已被注册");
        }

        // 创建用户
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .realName(request.getRealName())
                .role(CommonConstants.ROLE_USER)
                .status(CommonConstants.USER_STATUS_NORMAL)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .deleted(0)
                .build();

        userMapper.insert(user);
        log.info("用户注册成功: username={}", user.getUsername());

        return userService.convertToDTO(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("========== 开始登录流程 ==========");
        log.info("用户名: {}", request.getUsername());
        log.info("输入密码: {}", request.getPassword());
        
        // 根据用户名查询用户
        User user = userService.getByUsername(request.getUsername());
        if (user == null) {
            log.warn("用户不存在: {}", request.getUsername());
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }
        
        log.info("找到用户: {}, 角色: {}", user.getUsername(), user.getRole());
        log.info("数据库密码哈希: {}", user.getPassword());

        // 验证密码
        log.info("开始验证密码...");
        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        log.info("密码匹配结果: {}", matches);
        if (!matches) {
            log.warn("密码不匹配");
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }
        
        log.info("密码验证成功");

        // 检查用户状态
        if (CommonConstants.USER_STATUS_DISABLED.equals(user.getStatus())) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 生成 Token
        String accessToken = JwtUtils.generateToken(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                jwtProperties.getSecret(),
                jwtProperties.getExpiration()
        );

        String refreshToken = JwtUtils.generateRefreshToken(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                jwtProperties.getSecret()
        );

        log.info("用户登录成功: username={}", user.getUsername());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpiration() / 1000)
                .user(userService.convertToDTO(user))
                .build();
    }

    @Override
    public TokenVerifyResponse verifyToken(String token) {
        // 移除 Bearer 前缀
        if (token != null && token.startsWith(CommonConstants.TOKEN_PREFIX)) {
            token = token.substring(CommonConstants.TOKEN_PREFIX.length());
        }

        // 验证 Token
        boolean valid = JwtUtils.validateToken(token, jwtProperties.getSecret());
        if (!valid) {
            return TokenVerifyResponse.builder()
                    .valid(false)
                    .build();
        }

        try {
            Claims claims = JwtUtils.parseToken(token, jwtProperties.getSecret());
            Long userId = claims.get(JwtUtils.CLAIM_USER_ID, Long.class);
            String username = claims.get(JwtUtils.CLAIM_USERNAME, String.class);
            String role = claims.get(JwtUtils.CLAIM_ROLE, String.class);

            // 验证用户是否有效
            if (!userService.validateUser(userId)) {
                return TokenVerifyResponse.builder()
                        .valid(false)
                        .build();
            }

            return TokenVerifyResponse.builder()
                    .valid(true)
                    .userId(userId)
                    .username(username)
                    .role(role)
                    .expiration(claims.getExpiration().getTime())
                    .build();
        } catch (Exception e) {
            log.warn("Token验证失败: {}", e.getMessage());
            return TokenVerifyResponse.builder()
                    .valid(false)
                    .build();
        }
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        // 移除 Bearer 前缀
        if (refreshToken != null && refreshToken.startsWith(CommonConstants.TOKEN_PREFIX)) {
            refreshToken = refreshToken.substring(CommonConstants.TOKEN_PREFIX.length());
        }

        // 验证刷新 Token
        if (!JwtUtils.validateToken(refreshToken, jwtProperties.getSecret())) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        try {
            Claims claims = JwtUtils.parseToken(refreshToken, jwtProperties.getSecret());
            Long userId = claims.get(JwtUtils.CLAIM_USER_ID, Long.class);
            String username = claims.get(JwtUtils.CLAIM_USERNAME, String.class);
            String role = claims.get(JwtUtils.CLAIM_ROLE, String.class);

            // 验证用户是否有效
            User user = userMapper.selectById(userId);
            if (user == null || CommonConstants.USER_STATUS_DISABLED.equals(user.getStatus())) {
                throw new BusinessException(ResultCode.USER_DISABLED);
            }

            // 生成新的 Token
            String newAccessToken = JwtUtils.generateToken(
                    userId,
                    username,
                    role,
                    jwtProperties.getSecret(),
                    jwtProperties.getExpiration()
            );

            String newRefreshToken = JwtUtils.generateRefreshToken(
                    userId,
                    username,
                    role,
                    jwtProperties.getSecret()
            );

            log.info("Token刷新成功: username={}", username);

            return LoginResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtProperties.getExpiration() / 1000)
                    .user(userService.convertToDTO(user))
                    .build();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Token刷新失败: {}", e.getMessage());
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }
    }

    @Override
    public void logout(String token) {
        // 这里可以实现 Token 黑名单机制（可选）
        // 简单实现：直接返回成功，客户端清除本地 Token
        log.info("用户退出登录");
    }
}
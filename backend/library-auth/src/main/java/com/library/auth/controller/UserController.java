package com.library.auth.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.auth.dto.UserDTO;
import com.library.auth.entity.User;
import com.library.auth.service.UserService;
import com.library.common.constant.CommonConstants;
import com.library.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取用户信息（根据ID）
     * 供其他微服务内部调用
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")
    public Result<UserDTO> getUserById(@PathVariable Long userId) {
        log.debug("获取用户信息: userId={}", userId);
        UserDTO userDTO = userService.getUserById(userId);
        return Result.success(userDTO);
    }

    /**
     * 校验用户是否有效
     * 供其他微服务内部调用
     *
     * @param userId 用户ID
     * @return 是否有效
     */
    @GetMapping("/{userId}/validate")
    public Result<Boolean> validateUser(@PathVariable Long userId) {
        log.debug("校验用户有效性: userId={}", userId);
        boolean valid = userService.validateUser(userId);
        return Result.success(valid);
    }

    /**
     * 分页查询用户列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param keyword  关键词
     * @return 用户分页列表
     */
    @GetMapping
    public Result<Page<UserDTO>> getUserPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        log.debug("分页查询用户: pageNum={}, pageSize={}, keyword={}", pageNum, pageSize, keyword);
        
        // 限制最大页大小
        if (pageSize > CommonConstants.MAX_PAGE_SIZE) {
            pageSize = CommonConstants.MAX_PAGE_SIZE;
        }
        
        Page<User> page = new Page<>(pageNum, pageSize);
        Page<UserDTO> userPage = userService.getUserPage(page, keyword);
        return Result.success(userPage);
    }

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 成功响应
     */
    @PutMapping("/{userId}/status")
    public Result<Void> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam Integer status) {
        log.info("更新用户状态: userId={}, status={}", userId, status);
        userService.updateUserStatus(userId, status);
        return Result.success("用户状态更新成功", null);
    }

    /**
     * 获取当前登录用户信息
     * 
     * @param userId 从 Header 中获取的用户ID（由网关传递）
     * @return 用户信息
     */
    @GetMapping("/me")
    public Result<UserDTO> getCurrentUser(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        log.debug("获取当前用户信息: userId={}", userId);
        if (userId == null) {
            return Result.failure("用户未登录");
        }
        UserDTO userDTO = userService.getUserById(userId);
        return Result.success(userDTO);
    }
}
package com.library.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.auth.dto.UserDTO;
import com.library.auth.entity.User;
import com.library.auth.mapper.UserMapper;
import com.library.auth.service.UserService;
import com.library.common.constant.CommonConstants;
import com.library.common.exception.BusinessException;
import com.library.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return convertToDTO(user);
    }

    @Override
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMapper.existsByUsername(username) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return userMapper.existsByEmail(email) > 0;
    }

    @Override
    public boolean validateUser(Long userId) {
        User user = userMapper.selectById(userId);
        return user != null && CommonConstants.USER_STATUS_NORMAL.equals(user.getStatus());
    }

    @Override
    public Page<UserDTO> getUserPage(Page<User> page, String keyword) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索（用户名、邮箱、手机号、真实姓名）
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(User::getUsername, keyword)
                    .or()
                    .like(User::getEmail, keyword)
                    .or()
                    .like(User::getPhone, keyword)
                    .or()
                    .like(User::getRealName, keyword)
            );
        }

        queryWrapper.orderByDesc(User::getCreateTime);

        Page<User> userPage = userMapper.selectPage(page, queryWrapper);

        // 转换为 DTO
        Page<UserDTO> dtoPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserDTO> dtoList = userPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        int rows = userMapper.updateById(user);

        log.info("更新用户状态: userId={}, status={}", userId, status);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePassword(Long userId, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        int rows = userMapper.updateById(user);

        log.info("管理员重置用户密码: userId={}", userId);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(Long userId, String role) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 检查角色值是否合法
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "角色只能是 USER 或 ADMIN");
        }

        user.setRole(role);
        user.setUpdateTime(LocalDateTime.now());
        int rows = userMapper.updateById(user);

        log.info("更新用户角色: userId={}, role={}", userId, role);
        return rows > 0;
    }

    @Override
    public UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .realName(user.getRealName())
                .role(user.getRole())
                .status(user.getStatus())
                .createTime(user.getCreateTime())
                .build();
    }
}
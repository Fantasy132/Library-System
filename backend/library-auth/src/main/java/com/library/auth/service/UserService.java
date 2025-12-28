package com.library.auth.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.auth.dto.UserDTO;
import com.library.auth.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 根据ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserDTO getUserById(Long userId);

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    User getByUsername(String username);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 验证用户是否有效
     *
     * @param userId 用户ID
     * @return 是否有效
     */
    boolean validateUser(Long userId);

    /**
     * 分页查询用户列表
     *
     * @param page     分页参数
     * @param keyword  关键词
     * @return 用户分页列表
     */
    Page<UserDTO> getUserPage(Page<User> page, String keyword);

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateUserStatus(Long userId, Integer status);

    /**
     * 更新用户密码（管理员重置）
     *
     * @param userId      用户ID
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean updatePassword(Long userId, String newPassword);

    /**
     * 更新用户角色
     *
     * @param userId  用户ID
     * @param role    新角色
     * @return 是否成功
     */
    boolean updateRole(Long userId, String role);

    /**
     * 将 User 实体转换为 UserDTO
     *
     * @param user 用户实体
     * @return UserDTO
     */
    UserDTO convertToDTO(User user);
}
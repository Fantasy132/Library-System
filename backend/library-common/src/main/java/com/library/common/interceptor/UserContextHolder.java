package com.library.common.interceptor;

import com.library.common.dto.UserInfo;

/**
 * 用户上下文 - 基于 ThreadLocal
 */
public final class UserContextHolder {

    private static final ThreadLocal<UserInfo> CONTEXT = new ThreadLocal<>();

    private UserContextHolder() {
        throw new IllegalStateException("工具类不允许实例化");
    }

    /**
     * 设置当前用户信息
     */
    public static void setUser(UserInfo userInfo) {
        CONTEXT.set(userInfo);
    }

    /**
     * 获取当前用户信息
     */
    public static UserInfo getUser() {
        return CONTEXT.get();
    }

    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        UserInfo userInfo = getUser();
        return userInfo != null ? userInfo.getUserId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        UserInfo userInfo = getUser();
        return userInfo != null ? userInfo.getUsername() : null;
    }

    /**
     * 获取当前用户角色
     */
    public static String getRole() {
        UserInfo userInfo = getUser();
        return userInfo != null ? userInfo.getRole() : null;
    }

    /**
     * 清除当前用户信息
     */
    public static void clear() {
        CONTEXT.remove();
    }
}

package com.library.auth.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

/**
 * Token 验证响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenVerifyResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否有效
     */
    private Boolean valid;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 角色
     */
    private String role;

    /**
     * 过期时间戳
     */
    private Long expiration;
}
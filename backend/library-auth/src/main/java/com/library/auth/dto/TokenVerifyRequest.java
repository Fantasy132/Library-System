package com.library.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * Token 验证请求
 */
@Data
public class TokenVerifyRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Token
     */
    @NotBlank(message = "Token不能为空")
    private String token;
}
package com.library.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 更新角色请求
 */
@Data
public class UpdateRoleRequest {

    /**
     * 角色（USER-普通用户，ADMIN-管理员）
     */
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "^(USER|ADMIN)$", message = "角色只能是 USER 或 ADMIN")
    private String role;
}

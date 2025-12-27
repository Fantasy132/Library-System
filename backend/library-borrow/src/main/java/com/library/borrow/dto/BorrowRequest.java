package com.library.borrow.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 借阅请求DTO
 *
 * @author Library System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRequest {

    /**
     * 图书ID
     */
    @NotNull(message = "图书ID不能为空")
    private Long bookId;

    /**
     * 借阅数量
     */
    @NotNull(message = "借阅数量不能为空")
    @Min(value = 1, message = "借阅数量至少为1")
    private Integer quantity;

    /**
     * 借阅天数（默认30天）
     */
    @Builder.Default
    private Integer borrowDays = 30;

    /**
     * 备注
     */
    private String remark;
}
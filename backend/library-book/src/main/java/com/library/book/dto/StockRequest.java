package com.library.book.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 库存操作DTO
 *
 * @author Library System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockRequest {

    /**
     * 图书ID
     */
    @NotNull(message = "图书ID不能为空")
    private Long bookId;

    /**
     * 操作数量（正数增加，负数减少）
     */
    @NotNull(message = "操作数量不能为空")
    @Min(value = 1, message = "操作数量必须大于0")
    private Integer quantity;

    /**
     * 操作类型：BORROW-借出，RETURN-归还，ADD-入库，REDUCE-减库存
     */
    @NotNull(message = "操作类型不能为空")
    private StockOperationType operationType;

    /**
     * 库存操作类型枚举
     */
    public enum StockOperationType {
        /**
         * 借出
         */
        BORROW,
        /**
         * 归还
         */
        RETURN,
        /**
         * 入库
         */
        ADD,
        /**
         * 减库存
         */
        REDUCE
    }
}
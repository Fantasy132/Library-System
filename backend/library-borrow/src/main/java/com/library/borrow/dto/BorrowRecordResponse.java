package com.library.borrow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 借阅记录响应DTO
 *
 * @author Library System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecordResponse {

    /**
     * 借阅记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 图书ID
     */
    private Long bookId;

    /**
     * 图书ISBN
     */
    private String bookIsbn;

    /**
     * 图书名称
     */
    private String bookTitle;

    /**
     * 借阅数量
     */
    private Integer quantity;

    /**
     * 借阅时间
     */
    private LocalDateTime borrowTime;

    /**
     * 应还时间
     */
    private LocalDateTime dueTime;

    /**
     * 实际归还时间
     */
    private LocalDateTime returnTime;

    /**
     * 状态：0-借阅中，1-已归还，2-已逾期，3-已续借
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 续借次数
     */
    private Integer renewCount;

    /**
     * 是否逾期
     */
    private Boolean overdue;

    /**
     * 逾期天数
     */
    private Long overdueDays;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
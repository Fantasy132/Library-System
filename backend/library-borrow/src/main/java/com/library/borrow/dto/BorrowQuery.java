package com.library.borrow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 借阅记录查询DTO
 *
 * @author Library System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowQuery {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 图书ID
     */
    private Long bookId;

    /**
     * 图书名称关键词
     */
    private String bookTitle;

    /**
     * 状态：0-借阅中，1-已归还，2-已逾期，3-已续借
     */
    private Integer status;

    /**
     * 借阅开始时间
     */
    private LocalDateTime borrowStartTime;

    /**
     * 借阅结束时间
     */
    private LocalDateTime borrowEndTime;

    /**
     * 是否只查询逾期记录
     */
    private Boolean overdueOnly;

    /**
     * 当前页码（从1开始）
     */
    @Builder.Default
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Builder.Default
    private Integer pageSize = 10;
}
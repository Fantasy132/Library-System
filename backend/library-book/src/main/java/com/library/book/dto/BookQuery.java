package com.library.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图书查询DTO
 *
 * @author Library System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookQuery {

    /**
     * 关键词（搜索书名、作者、ISBN）
     */
    private String keyword;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 作者
     */
    private String author;

    /**
     * 出版社
     */
    private String publisher;

    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;

    /**
     * 是否有库存
     */
    private Boolean hasStock;

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
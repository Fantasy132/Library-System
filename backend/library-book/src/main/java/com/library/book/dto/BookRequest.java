package com.library.book.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 图书创建/更新请求DTO
 *
 * @author Library System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {

    /**
     * ISBN编号
     */
    @NotBlank(message = "ISBN不能为空")
    @Size(max = 20, message = "ISBN长度不能超过20个字符")
    private String isbn;

    /**
     * 书名
     */
    @NotBlank(message = "书名不能为空")
    @Size(max = 200, message = "书名长度不能超过200个字符")
    private String title;

    /**
     * 作者
     */
    @NotBlank(message = "作者不能为空")
    @Size(max = 100, message = "作者名称长度不能超过100个字符")
    private String author;

    /**
     * 出版社
     */
    @Size(max = 100, message = "出版社名称长度不能超过100个字符")
    private String publisher;

    /**
     * 出版日期
     */
    private LocalDate publishDate;

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /**
     * 价格
     */
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.00", message = "价格不能小于0")
    @Digits(integer = 8, fraction = 2, message = "价格格式不正确")
    private BigDecimal price;

    /**
     * 总库存数量
     */
    @NotNull(message = "库存数量不能为空")
    @Min(value = 0, message = "库存数量不能小于0")
    private Integer totalStock;

    /**
     * 图书封面URL
     */
    @Size(max = 500, message = "封面URL长度不能超过500个字符")
    private String coverUrl;

    /**
     * 图书简介
     */
    @Size(max = 2000, message = "图书简介长度不能超过2000个字符")
    private String description;

    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;
}
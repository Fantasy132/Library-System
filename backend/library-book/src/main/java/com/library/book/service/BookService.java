package com.library.book.service;

import com.library.book.dto.*;
import com.library.common.result.PageResult;

/**
 * 图书服务接口
 *
 * @author Library System
 * @since 1.0.0
 */
public interface BookService {

    /**
     * 分页查询图书列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageResult<BookResponse> listBooks(BookQuery query);

    /**
     * 根据ID查询图书详情
     *
     * @param id 图书ID
     * @return 图书详情
     */
    BookResponse getBookById(Long id);

    /**
     * 根据ISBN查询图书
     *
     * @param isbn ISBN
     * @return 图书详情
     */
    BookResponse getBookByIsbn(String isbn);

    /**
     * 新增图书
     *
     * @param request 图书信息
     * @return 图书ID
     */
    Long createBook(BookRequest request);

    /**
     * 更新图书
     *
     * @param id      图书ID
     * @param request 图书信息
     */
    void updateBook(Long id, BookRequest request);

    /**
     * 删除图书
     *
     * @param id 图书ID
     */
    void deleteBook(Long id);

    /**
     * 上架/下架图书
     *
     * @param id     图书ID
     * @param status 状态：0-下架，1-上架
     */
    void updateBookStatus(Long id, Integer status);

    /**
     * 库存操作
     *
     * @param request 库存操作请求
     * @return 操作后的可用库存
     */
    Integer operateStock(StockRequest request);

    /**
     * 检查库存是否充足
     *
     * @param bookId   图书ID
     * @param quantity 需要的数量
     * @return true-充足，false-不足
     */
    boolean checkStock(Long bookId, Integer quantity);
}
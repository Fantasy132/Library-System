package com.library.book.controller;

import com.library.book.config.RequireAdmin;
import com.library.book.dto.*;
import com.library.book.service.BookService;
import com.library.common.result.PageResult;
import com.library.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 图书管理 Controller
 *
 * @author Library System
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /**
     * 分页查询图书列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @GetMapping
    public Result<PageResult<BookResponse>> listBooks(BookQuery query) {
        PageResult<BookResponse> result = bookService.listBooks(query);
        return Result.success(result);
    }

    /**
     * 根据ID查询图书详情
     *
     * @param id 图书ID
     * @return 图书详情
     */
    @GetMapping("/{id}")
    public Result<BookResponse> getBook(@PathVariable("id") Long id) {
        BookResponse book = bookService.getBookById(id);
        return Result.success(book);
    }

    /**
     * 根据ISBN查询图书
     *
     * @param isbn ISBN
     * @return 图书详情
     */
    @GetMapping("/isbn/{isbn}")
    public Result<BookResponse> getBookByIsbn(@PathVariable("isbn") String isbn) {
        BookResponse book = bookService.getBookByIsbn(isbn);
        return Result.success(book);
    }

    /**
     * 新增图书（需要管理员权限）
     *
     * @param request 图书信息
     * @return 图书ID
     */
    @PostMapping
    @RequireAdmin
    public Result<Long> createBook(@Valid @RequestBody BookRequest request) {
        Long bookId = bookService.createBook(request);
        return Result.success("新增图书成功", bookId);
    }

    /**
     * 更新图书（需要管理员权限）
     *
     * @param id      图书ID
     * @param request 图书信息
     * @return 结果
     */
    @PutMapping("/{id}")
    @RequireAdmin
    public Result<Void> updateBook(@PathVariable("id") Long id, @Valid @RequestBody BookRequest request) {
        bookService.updateBook(id, request);
        return Result.success("更新图书成功");
    }

    /**
     * 删除图书（需要管理员权限）
     *
     * @param id 图书ID
     * @return 结果
     */
    @DeleteMapping("/{id}")
    @RequireAdmin
    public Result<Void> deleteBook(@PathVariable("id") Long id) {
        bookService.deleteBook(id);
        return Result.success("删除图书成功");
    }

    /**
     * 上架/下架图书（需要管理员权限）
     *
     * @param id     图书ID
     * @param status 状态：0-下架，1-上架
     * @return 结果
     */
    @PutMapping("/{id}/status")
    @RequireAdmin
    public Result<Void> updateBookStatus(@PathVariable("id") Long id, @RequestParam("status") Integer status) {
        bookService.updateBookStatus(id, status);
        return Result.success(status == 1 ? "上架成功" : "下架成功");
    }

    /**
     * 库存操作（需要管理员权限）
     *
     * @param request 库存操作请求
     * @return 操作后的可用库存
     */
    @PostMapping("/stock/operate")
    @RequireAdmin
    public Result<Integer> operateStock(@Valid @RequestBody StockRequest request) {
        Integer availableStock = bookService.operateStock(request);
        return Result.success("库存操作成功", availableStock);
    }

    /**
     * 检查库存是否充足
     *
     * @param bookId   图书ID
     * @param quantity 需要的数量
     * @return true-充足，false-不足
     */
    @GetMapping("/{bookId}/stock/check")
    public Result<Boolean> checkStock(@PathVariable("bookId") Long bookId, @RequestParam("quantity") Integer quantity) {
        boolean hasStock = bookService.checkStock(bookId, quantity);
        return Result.success(hasStock);
    }

    /**
     * 借书时减少库存（供借阅服务调用）
     *
     * @param bookId   图书ID
     * @param quantity 数量
     * @return 操作后的可用库存
     */
    @PostMapping("/{bookId}/stock/borrow")
    public Result<Integer> borrowStock(@PathVariable("bookId") Long bookId, @RequestParam("quantity") Integer quantity) {
        StockRequest request = StockRequest.builder()
                .bookId(bookId)
                .quantity(quantity)
                .operationType(StockRequest.StockOperationType.BORROW)
                .build();
        Integer availableStock = bookService.operateStock(request);
        return Result.success("借书成功", availableStock);
    }

    /**
     * 还书时增加库存（供借阅服务调用）
     *
     * @param bookId   图书ID
     * @param quantity 数量
     * @return 操作后的可用库存
     */
    @PostMapping("/{bookId}/stock/return")
    public Result<Integer> returnStock(@PathVariable("bookId") Long bookId, @RequestParam("quantity") Integer quantity) {
        StockRequest request = StockRequest.builder()
                .bookId(bookId)
                .quantity(quantity)
                .operationType(StockRequest.StockOperationType.RETURN)
                .build();
        Integer availableStock = bookService.operateStock(request);
        return Result.success("还书成功", availableStock);
    }
}
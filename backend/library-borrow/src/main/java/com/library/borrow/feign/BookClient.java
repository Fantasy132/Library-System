package com.library.borrow.feign;

import com.library.common.result.Result;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 图书服务 Feign 客户端（带熔断保护）
 *
 * @author Library System
 * @since 1.0.0
 */
@FeignClient(name = "library-book", contextId = "bookClient", fallbackFactory = BookClientFallbackFactory.class)
public interface BookClient {

    /**
     * 根据ID查询图书详情
     *
     * @param id 图书ID
     * @return 图书详情
     */
    @GetMapping("/books/{id}")
    @CircuitBreaker(name = "bookService", fallbackMethod = "getBookFallback")
    Result<BookInfo> getBookById(@PathVariable("id") Long id);

    /**
     * 检查库存是否充足
     *
     * @param bookId   图书ID
     * @param quantity 需要的数量
     * @return true-充足，false-不足
     */
    @GetMapping("/books/{bookId}/stock/check")
    @CircuitBreaker(name = "bookService", fallbackMethod = "checkStockFallback")
    Result<Boolean> checkStock(@PathVariable("bookId") Long bookId, @RequestParam("quantity") Integer quantity);

    /**
     * 借书时减少库存
     *
     * @param bookId   图书ID
     * @param quantity 数量
     * @return 操作后的可用库存
     */
    @PostMapping("/books/{bookId}/stock/borrow")
    @CircuitBreaker(name = "bookService", fallbackMethod = "borrowStockFallback")
    Result<Integer> borrowStock(@PathVariable("bookId") Long bookId, @RequestParam("quantity") Integer quantity);

    /**
     * 还书时增加库存
     *
     * @param bookId   图书ID
     * @param quantity 数量
     * @return 操作后的可用库存
     */
    @PostMapping("/books/{bookId}/stock/return")
    @CircuitBreaker(name = "bookService", fallbackMethod = "returnStockFallback")
    Result<Integer> returnStock(@PathVariable("bookId") Long bookId, @RequestParam("quantity") Integer quantity);

    /**
     * 图书信息
     */
    record BookInfo(
            Long id,
            String isbn,
            String title,
            String author,
            String publisher,
            LocalDate publishDate,
            Long categoryId,
            String categoryName,
            BigDecimal price,
            Integer totalStock,
            Integer availableStock,
            String coverUrl,
            String description,
            Integer status,
            String statusDesc
    ) {
    }
}
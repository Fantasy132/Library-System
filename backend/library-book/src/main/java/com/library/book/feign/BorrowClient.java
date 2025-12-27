package com.library.book.feign;

import com.library.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 借阅服务 Feign 客户端
 *
 * @author Library System
 * @since 1.0.0
 */
@FeignClient(name = "library-borrow", contextId = "borrowClient", fallbackFactory = BorrowClientFallbackFactory.class)
public interface BorrowClient {

    /**
     * 更新借阅记录中的图书书名
     *
     * @param bookId 图书ID
     * @param title  新书名
     * @return 更新的记录数
     */
    @PutMapping("/internal/borrow/book/{bookId}/title")
    Result<Integer> updateBookTitle(@PathVariable("bookId") Long bookId, @RequestParam("title") String title);
}

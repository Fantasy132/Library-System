package com.library.book.feign;

import com.library.common.result.Result;
import com.library.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 借阅服务 Feign 客户端降级工厂
 *
 * @author Library System
 * @since 1.0.0
 */
@Slf4j
@Component
public class BorrowClientFallbackFactory implements FallbackFactory<BorrowClient> {

    @Override
    public BorrowClient create(Throwable cause) {
        log.error("调用借阅服务失败: {}", cause.getMessage(), cause);

        return new BorrowClient() {
            @Override
            public Result<Integer> updateBookTitle(Long bookId, String title) {
                log.error("借阅服务不可用，更新图书书名失败: {}", cause.getMessage());
                // 降级时返回0，表示未更新任何记录，但不影响图书本身的更新
                return Result.success(0);
            }
        };
    }
}

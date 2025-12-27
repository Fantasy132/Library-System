package com.library.borrow.feign;

import com.library.common.result.Result;
import com.library.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 图书服务 Feign 客户端降级工厂（Resilience4j 熔断保护）
 *
 * @author Library System
 * @since 1.0.0
 */
@Slf4j
@Component
public class BookClientFallbackFactory implements FallbackFactory<BookClient> {

    @Override
    public BookClient create(Throwable cause) {
        log.error("调用图书服务失败，触发熔断降级: {}", cause.getMessage(), cause);

        return new BookClient() {
            @Override
            public Result<BookInfo> getBookById(Long id) {
                log.error("获取图书信息失败，图书ID: {}, 原因: {}", id, cause.getMessage());
                return Result.fail(ResultCode.SERVICE_UNAVAILABLE, "图书服务暂不可用，请稍后重试");
            }

            @Override
            public Result<Boolean> checkStock(Long bookId, Integer quantity) {
                log.error("检查库存失败，图书ID: {}, 原因: {}", bookId, cause.getMessage());
                return Result.fail(ResultCode.SERVICE_UNAVAILABLE, "图书服务暂不可用，无法检查库存");
            }

            @Override
            public Result<Integer> borrowStock(Long bookId, Integer quantity) {
                log.error("借书减库存失败，图书ID: {}, 原因: {}", bookId, cause.getMessage());
                return Result.fail(ResultCode.SERVICE_UNAVAILABLE, "图书服务暂不可用，借书操作失败");
            }

            @Override
            public Result<Integer> returnStock(Long bookId, Integer quantity) {
                log.error("还书加库存失败，图书ID: {}, 原因: {}", bookId, cause.getMessage());
                return Result.fail(ResultCode.SERVICE_UNAVAILABLE, "图书服务暂不可用，还书操作失败");
            }
        };
    }
}
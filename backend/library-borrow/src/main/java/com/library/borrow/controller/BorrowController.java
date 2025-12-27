package com.library.borrow.controller;

import com.library.borrow.config.AuthInterceptor;
import com.library.borrow.dto.*;
import com.library.borrow.service.BorrowService;
import com.library.common.exception.BusinessException;
import com.library.common.result.PageResult;
import com.library.common.result.Result;
import com.library.common.result.ResultCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 借阅管理 Controller
 *
 * @author Library System
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    /**
     * 借书
     *
     * @param request 借阅请求
     * @return 借阅记录ID
     */
    @PostMapping("/borrow")
    public Result<Long> borrowBook(@Valid @RequestBody BorrowRequest request) {
        Long userId = AuthInterceptor.getCurrentUserId();
        String username = AuthInterceptor.getCurrentUsername();

        Long borrowId = borrowService.borrowBook(userId, username, request);
        return Result.success("借书成功", borrowId);
    }

    /**
     * 还书
     *
     * @param request 归还请求
     * @return 结果
     */
    @PostMapping("/return")
    public Result<Void> returnBook(@Valid @RequestBody ReturnRequest request) {
        Long userId = AuthInterceptor.getCurrentUserId();

        borrowService.returnBook(userId, request);
        return Result.success("还书成功");
    }

    /**
     * 续借
     *
     * @param request 续借请求
     * @return 结果
     */
    @PostMapping("/renew")
    public Result<Void> renewBook(@Valid @RequestBody RenewRequest request) {
        Long userId = AuthInterceptor.getCurrentUserId();

        borrowService.renewBook(userId, request);
        return Result.success("续借成功");
    }

    /**
     * 查询借阅记录详情
     *
     * @param borrowId 借阅记录ID
     * @return 借阅记录详情
     */
    @GetMapping("/borrow/{borrowId}")
    public Result<BorrowRecordResponse> getBorrowRecord(@PathVariable("borrowId") Long borrowId) {
        Long userId = AuthInterceptor.getCurrentUserId();
        boolean isAdmin = AuthInterceptor.isAdmin();

        BorrowRecordResponse record = borrowService.getBorrowRecord(borrowId);

        // 非管理员只能查看自己的借阅记录
        if (!isAdmin && !record.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权查看他人的借阅记录");
        }

        return Result.success(record);
    }

    /**
     * 查询当前用户的借阅记录
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @GetMapping("/borrow/my")
    public Result<PageResult<BorrowRecordResponse>> listMyBorrowRecords(BorrowQuery query) {
        Long userId = AuthInterceptor.getCurrentUserId();

        PageResult<BorrowRecordResponse> result = borrowService.listUserBorrowRecords(userId, query);
        return Result.success(result);
    }

    /**
     * 查询指定用户的借阅记录
     *
     * @param userId 用户ID
     * @param query  查询条件
     * @return 分页结果
     */
    @GetMapping("/borrow/user/{userId}")
    public Result<PageResult<BorrowRecordResponse>> listUserBorrowRecords(
            @PathVariable("userId") Long userId,
            BorrowQuery query) {
        Long currentUserId = AuthInterceptor.getCurrentUserId();
        boolean isAdmin = AuthInterceptor.isAdmin();

        // 非管理员只能查看自己的借阅记录
        if (!isAdmin && !userId.equals(currentUserId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权查看他人的借阅记录");
        }

        PageResult<BorrowRecordResponse> result = borrowService.listUserBorrowRecords(userId, query);
        return Result.success(result);
    }

    /**
     * 查询所有借阅记录（管理员）
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @GetMapping("/borrow/all")
    public Result<PageResult<BorrowRecordResponse>> listAllBorrowRecords(BorrowQuery query) {
        if (!AuthInterceptor.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN, "需要管理员权限");
        }

        PageResult<BorrowRecordResponse> result = borrowService.listAllBorrowRecords(query);
        return Result.success(result);
    }

    /**
     * 获取当前用户借阅统计
     *
     * @return 借阅统计
     */
    @GetMapping("/borrow/statistics")
    public Result<BorrowService.BorrowStatistics> getMyStatistics() {
        Long userId = AuthInterceptor.getCurrentUserId();

        BorrowService.BorrowStatistics statistics = borrowService.getUserStatistics(userId);
        return Result.success(statistics);
    }

    /**
     * 获取指定用户借阅统计（管理员）
     *
     * @param userId 用户ID
     * @return 借阅统计
     */
    @GetMapping("/borrow/statistics/{userId}")
    public Result<BorrowService.BorrowStatistics> getUserStatistics(@PathVariable("userId") Long userId) {
        Long currentUserId = AuthInterceptor.getCurrentUserId();
        boolean isAdmin = AuthInterceptor.isAdmin();

        // 非管理员只能查看自己的统计
        if (!isAdmin && !userId.equals(currentUserId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权查看他人的借阅统计");
        }

        BorrowService.BorrowStatistics statistics = borrowService.getUserStatistics(userId);
        return Result.success(statistics);
    }

    /**
     * 获取当前用户正在借阅的数量
     *
     * @return 借阅数量
     */
    @GetMapping("/borrow/count")
    public Result<Integer> getMyBorrowingCount() {
        Long userId = AuthInterceptor.getCurrentUserId();

        int count = borrowService.countUserBorrowing(userId);
        return Result.success(count);
    }

    /**
     * 手动触发逾期检查（管理员）
     *
     * @return 更新的记录数
     */
    @PostMapping("/borrow/check-overdue")
    public Result<Integer> checkOverdue() {
        if (!AuthInterceptor.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN, "需要管理员权限");
        }

        int count = borrowService.updateOverdueStatus();
        return Result.success(String.format("已更新 %d 条逾期记录", count), count);
    }

    /**
     * 更新借阅记录中的图书信息（内部接口，由图书服务调用）
     *
     * @param bookId 图书ID
     * @param title 新书名
     * @return 更新的记录数
     */
    @PutMapping("/internal/borrow/book/{bookId}/title")
    public Result<Integer> updateBookTitle(
            @PathVariable("bookId") Long bookId,
            @RequestParam("title") String title) {
        int count = borrowService.updateBookTitle(bookId, title);
        log.info("更新图书ID: {} 的借阅记录书名，共 {} 条记录", bookId, count);
        return Result.success(count);
    }
}
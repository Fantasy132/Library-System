package com.library.borrow.service;

import com.library.borrow.dto.*;
import com.library.common.result.PageResult;

/**
 * 借阅服务接口
 *
 * @author Library System
 * @since 1.0.0
 */
public interface BorrowService {

    /**
     * 借书
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param request  借阅请求
     * @return 借阅记录ID
     */
    Long borrowBook(Long userId, String username, BorrowRequest request);

    /**
     * 还书
     *
     * @param userId  用户ID
     * @param request 归还请求
     */
    void returnBook(Long userId, ReturnRequest request);

    /**
     * 续借
     *
     * @param userId  用户ID
     * @param request 续借请求
     */
    void renewBook(Long userId, RenewRequest request);

    /**
     * 查询借阅记录详情
     *
     * @param borrowId 借阅记录ID
     * @return 借阅记录详情
     */
    BorrowRecordResponse getBorrowRecord(Long borrowId);

    /**
     * 分页查询用户借阅记录
     *
     * @param userId 用户ID
     * @param query  查询条件
     * @return 分页结果
     */
    PageResult<BorrowRecordResponse> listUserBorrowRecords(Long userId, BorrowQuery query);

    /**
     * 分页查询所有借阅记录（管理员）
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageResult<BorrowRecordResponse> listAllBorrowRecords(BorrowQuery query);

    /**
     * 查询用户当前借阅数量
     *
     * @param userId 用户ID
     * @return 借阅数量
     */
    int countUserBorrowing(Long userId);

    /**
     * 更新逾期状态（定时任务调用）
     *
     * @return 更新的记录数
     */
    int updateOverdueStatus();

    /**
     * 获取用户借阅统计
     *
     * @param userId 用户ID
     * @return 借阅统计
     */
    BorrowStatistics getUserStatistics(Long userId);

    /**
     * 更新借阅记录中的图书书名
     *
     * @param bookId 图书ID
     * @param title  新书名
     * @return 更新的记录数
     */
    int updateBookTitle(Long bookId, String title);

    /**
     * 借阅统计
     */
    record BorrowStatistics(
            int totalBorrowed,
            int currentBorrowing,
            int overdueCount
    ) {
    }
}
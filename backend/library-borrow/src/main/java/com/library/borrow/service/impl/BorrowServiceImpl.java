package com.library.borrow.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.borrow.dto.*;
import com.library.borrow.entity.BorrowRecord;
import com.library.borrow.entity.BorrowStatus;
import com.library.borrow.feign.BookClient;
import com.library.borrow.mapper.BorrowRecordMapper;
import com.library.borrow.service.BorrowService;
import com.library.common.exception.BusinessException;
import com.library.common.result.PageResult;
import com.library.common.result.Result;
import com.library.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 借阅服务实现类
 *
 * @author Library System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordMapper borrowRecordMapper;
    private final BookClient bookClient;

    /**
     * 用户最大借阅数量
     */
    @Value("${library.borrow.max-borrow-count:10}")
    private int maxBorrowCount;

    /**
     * 最大续借次数
     */
    @Value("${library.borrow.max-renew-count:2}")
    private int maxRenewCount;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long borrowBook(Long userId, String username, BorrowRequest request) {
        Long bookId = request.getBookId();
        Integer quantity = request.getQuantity();

        // 1. 检查用户借阅数量是否超过限制
        int currentBorrowing = borrowRecordMapper.countBorrowingByUser(userId);
        if (currentBorrowing + quantity > maxBorrowCount) {
            throw new BusinessException(ResultCode.BAD_REQUEST,
                    String.format("借阅数量超过限制，当前借阅: %d，最大允许: %d", currentBorrowing, maxBorrowCount));
        }

        // 2. 检查是否已借阅同一本书
        List<BorrowRecord> existingRecords = borrowRecordMapper.selectBorrowingByUserAndBook(userId, bookId);
        if (!existingRecords.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "您已借阅此书，请先归还后再借");
        }

        // 3. 获取图书信息
        Result<BookClient.BookInfo> bookResult = bookClient.getBookById(bookId);
        if (!bookResult.isSuccess()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, bookResult.getMessage());
        }
        BookClient.BookInfo bookInfo = bookResult.getData();
        if (bookInfo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "图书不存在");
        }

        // 4. 检查图书状态
        if (bookInfo.status() != 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "图书已下架，无法借阅");
        }

        // 5. 检查库存
        Result<Boolean> stockResult = bookClient.checkStock(bookId, quantity);
        if (!stockResult.isSuccess() || !Boolean.TRUE.equals(stockResult.getData())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "库存不足，无法借阅");
        }

        // 6. 扣减库存
        Result<Integer> borrowStockResult = bookClient.borrowStock(bookId, quantity);
        if (!borrowStockResult.isSuccess()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "借书失败: " + borrowStockResult.getMessage());
        }

        // 7. 创建借阅记录
        LocalDateTime now = LocalDateTime.now();
        int borrowDays = request.getBorrowDays() != null ? request.getBorrowDays() : 30;

        BorrowRecord record = BorrowRecord.builder()
                .userId(userId)
                .username(username)
                .bookId(bookId)
                .bookIsbn(bookInfo.isbn())
                .bookTitle(bookInfo.title())
                .quantity(quantity)
                .borrowTime(now)
                .dueTime(now.plusDays(borrowDays))
                .status(BorrowStatus.BORROWING.getCode())
                .renewCount(0)
                .remark(request.getRemark())
                .deleted(0)
                .build();

        borrowRecordMapper.insert(record);
        log.info("借书成功，用户: {}, 图书: {}, 借阅ID: {}", username, bookInfo.title(), record.getId());

        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(Long userId, ReturnRequest request) {
        Long borrowId = request.getBorrowId();

        // 1. 查询借阅记录
        BorrowRecord record = borrowRecordMapper.selectById(borrowId);
        if (record == null || record.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "借阅记录不存在");
        }

        // 2. 检查是否是本人的借阅记录
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权操作他人的借阅记录");
        }

        // 3. 检查状态
        if (BorrowStatus.RETURNED.getCode().equals(record.getStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该书已归还，请勿重复操作");
        }

        // 4. 归还库存
        Result<Integer> returnStockResult = bookClient.returnStock(record.getBookId(), record.getQuantity());
        if (!returnStockResult.isSuccess()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "还书失败: " + returnStockResult.getMessage());
        }

        // 5. 更新借阅记录
        LocalDateTime now = LocalDateTime.now();
        BorrowRecord updateRecord = BorrowRecord.builder()
                .id(borrowId)
                .returnTime(now)
                .status(BorrowStatus.RETURNED.getCode())
                .remark(request.getRemark())
                .build();

        borrowRecordMapper.updateById(updateRecord);
        log.info("还书成功，借阅ID: {}, 用户: {}, 图书: {}", borrowId, record.getUsername(), record.getBookTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void renewBook(Long userId, RenewRequest request) {
        Long borrowId = request.getBorrowId();

        // 1. 查询借阅记录
        BorrowRecord record = borrowRecordMapper.selectById(borrowId);
        if (record == null || record.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "借阅记录不存在");
        }

        // 2. 检查是否是本人的借阅记录
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权操作他人的借阅记录");
        }

        // 3. 检查状态
        if (BorrowStatus.RETURNED.getCode().equals(record.getStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该书已归还，无法续借");
        }

        // 4. 检查续借次数
        if (record.getRenewCount() >= maxRenewCount) {
            throw new BusinessException(ResultCode.BAD_REQUEST,
                    String.format("续借次数已达上限（最多%d次）", maxRenewCount));
        }

        // 5. 检查是否已逾期（逾期不允许续借）
        if (LocalDateTime.now().isAfter(record.getDueTime())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "已逾期，无法续借，请先归还");
        }

        // 6. 更新借阅记录
        int renewDays = request.getRenewDays() != null ? request.getRenewDays() : 15;
        LocalDateTime newDueTime = record.getDueTime().plusDays(renewDays);

        BorrowRecord updateRecord = BorrowRecord.builder()
                .id(borrowId)
                .dueTime(newDueTime)
                .status(BorrowStatus.BORROWING.getCode())  // 续借后保持"借阅中"状态
                .renewCount(record.getRenewCount() + 1)
                .build();

        borrowRecordMapper.updateById(updateRecord);
        log.info("续借成功，借阅ID: {}, 用户: {}, 新到期时间: {}, 续借天数: {}", borrowId, record.getUsername(), newDueTime, renewDays);
    }

    @Override
    public BorrowRecordResponse getBorrowRecord(Long borrowId) {
        BorrowRecord record = borrowRecordMapper.selectById(borrowId);
        if (record == null || record.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "借阅记录不存在");
        }
        return convertToResponse(record);
    }

    @Override
    public PageResult<BorrowRecordResponse> listUserBorrowRecords(Long userId, BorrowQuery query) {
        query.setUserId(userId);
        return listAllBorrowRecords(query);
    }

    @Override
    public PageResult<BorrowRecordResponse> listAllBorrowRecords(BorrowQuery query) {
        Page<BorrowRecordResponse> page = new Page<>(query.getPageNum(), query.getPageSize());
        var result = borrowRecordMapper.selectBorrowPage(page, query);

        // 设置状态描述和逾期信息
        result.getRecords().forEach(this::enrichResponse);

        return PageResult.of(result.getRecords(), result.getTotal(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public int countUserBorrowing(Long userId) {
        return borrowRecordMapper.countBorrowingByUser(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateOverdueStatus() {
        List<BorrowRecord> overdueRecords = borrowRecordMapper.selectOverdueRecords();
        if (overdueRecords.isEmpty()) {
            return 0;
        }

        List<Long> ids = overdueRecords.stream().map(BorrowRecord::getId).toList();
        int count = borrowRecordMapper.batchUpdateOverdueStatus(ids);
        log.info("更新逾期状态完成，共更新 {} 条记录", count);
        return count;
    }

    @Override
    public BorrowStatistics getUserStatistics(Long userId) {
        int totalBorrowed = borrowRecordMapper.countTotalByUser(userId);
        int currentBorrowing = borrowRecordMapper.countBorrowingByUser(userId);
        int overdueCount = borrowRecordMapper.countOverdueByUser(userId);

        return new BorrowStatistics(totalBorrowed, currentBorrowing, overdueCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBookTitle(Long bookId, String title) {
        log.info("更新图书ID: {} 的借阅记录书名为: {}", bookId, title);
        return borrowRecordMapper.updateBookTitleByBookId(bookId, title);
    }

    /**
     * 转换为响应对象
     */
    private BorrowRecordResponse convertToResponse(BorrowRecord record) {
        BorrowRecordResponse response = BorrowRecordResponse.builder()
                .id(record.getId())
                .userId(record.getUserId())
                .username(record.getUsername())
                .bookId(record.getBookId())
                .bookIsbn(record.getBookIsbn())
                .bookTitle(record.getBookTitle())
                .quantity(record.getQuantity())
                .borrowTime(record.getBorrowTime())
                .dueTime(record.getDueTime())
                .returnTime(record.getReturnTime())
                .status(record.getStatus())
                .renewCount(record.getRenewCount())
                .remark(record.getRemark())
                .createTime(record.getCreateTime())
                .build();

        enrichResponse(response);
        return response;
    }

    /**
     * 丰富响应信息
     */
    private void enrichResponse(BorrowRecordResponse response) {
        // 设置状态描述
        response.setStatusDesc(BorrowStatus.getDescByCode(response.getStatus()));

        // 计算是否逾期及逾期天数
        if (!BorrowStatus.RETURNED.getCode().equals(response.getStatus())) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(response.getDueTime())) {
                response.setOverdue(true);
                response.setOverdueDays(ChronoUnit.DAYS.between(response.getDueTime(), now));
            } else {
                response.setOverdue(false);
                response.setOverdueDays(0L);
            }
        } else {
            // 已归还的，检查是否逾期归还
            if (response.getReturnTime() != null && response.getReturnTime().isAfter(response.getDueTime())) {
                response.setOverdue(true);
                response.setOverdueDays(ChronoUnit.DAYS.between(response.getDueTime(), response.getReturnTime()));
            } else {
                response.setOverdue(false);
                response.setOverdueDays(0L);
            }
        }
    }
}
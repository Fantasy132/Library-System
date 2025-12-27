package com.library.book.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.book.dto.*;
import com.library.book.entity.Book;
import com.library.book.entity.Category;
import com.library.book.feign.BorrowClient;
import com.library.book.mapper.BookMapper;
import com.library.book.mapper.CategoryMapper;
import com.library.book.service.BookService;
import com.library.common.exception.BusinessException;
import com.library.common.result.PageResult;
import com.library.common.result.Result;
import com.library.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 图书服务实现类
 *
 * @author Library System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final CategoryMapper categoryMapper;
    private final BorrowClient borrowClient;

    @Override
    public PageResult<BookResponse> listBooks(BookQuery query) {
        Page<BookResponse> page = new Page<>(query.getPageNum(), query.getPageSize());
        var result = bookMapper.selectBookPage(page, query);

        // 设置状态描述
        result.getRecords().forEach(this::setStatusDesc);

        // 手动查询总数（因为分页插件可能未正确配置）
        Long total = bookMapper.countBooks(query);
        if (total == null) {
            total = 0L;
        }

        return PageResult.of(result.getRecords(), total, query.getPageNum(), query.getPageSize());
    }

    @Override
    public BookResponse getBookById(Long id) {
        BookResponse book = bookMapper.selectBookDetail(id);
        if (book == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "图书不存在");
        }
        setStatusDesc(book);
        return book;
    }

    @Override
    public BookResponse getBookByIsbn(String isbn) {
        Book book = bookMapper.selectByIsbn(isbn);
        if (book == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "图书不存在");
        }
        return getBookById(book.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBook(BookRequest request) {
        // 检查ISBN是否已存在
        Book existBook = bookMapper.selectByIsbn(request.getIsbn());
        if (existBook != null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "ISBN已存在");
        }

        // 检查分类是否存在
        Category category = categoryMapper.selectById(request.getCategoryId());
        if (category == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "分类不存在");
        }

        // 构建图书实体
        Book book = Book.builder()
                .isbn(request.getIsbn())
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .publishDate(request.getPublishDate())
                .categoryId(request.getCategoryId())
                .price(request.getPrice())
                .totalStock(request.getTotalStock())
                .availableStock(request.getTotalStock()) // 新增图书，可借数量等于总库存
                .coverUrl(request.getCoverUrl())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : 1) // 默认上架
                .deleted(0)
                .build();

        bookMapper.insert(book);
        log.info("新增图书成功，ID: {}, 书名: {}", book.getId(), book.getTitle());

        return book.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBook(Long id, BookRequest request) {
        // 检查图书是否存在
        Book existBook = bookMapper.selectById(id);
        if (existBook == null || existBook.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "图书不存在");
        }

        // 检查ISBN是否与其他图书重复
        Book bookByIsbn = bookMapper.selectByIsbn(request.getIsbn());
        if (bookByIsbn != null && !Objects.equals(bookByIsbn.getId(), id)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "ISBN已被其他图书使用");
        }

        // 检查分类是否存在
        Category category = categoryMapper.selectById(request.getCategoryId());
        if (category == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "分类不存在");
        }

        // 计算库存变化
        int stockDiff = request.getTotalStock() - existBook.getTotalStock();
        int newAvailableStock = existBook.getAvailableStock() + stockDiff;
        if (newAvailableStock < 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "库存不足，无法减少总库存");
        }

        // 更新图书
        Book book = Book.builder()
                .id(id)
                .isbn(request.getIsbn())
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .publishDate(request.getPublishDate())
                .categoryId(request.getCategoryId())
                .price(request.getPrice())
                .totalStock(request.getTotalStock())
                .availableStock(newAvailableStock)
                .coverUrl(request.getCoverUrl())
                .description(request.getDescription())
                .status(request.getStatus())
                .build();

        bookMapper.updateById(book);
        log.info("更新图书成功，ID: {}", id);

        // 如果书名发生变化，同步更新借阅记录中的书名
        if (!existBook.getTitle().equals(request.getTitle())) {
            try {
                Result<Integer> result = borrowClient.updateBookTitle(id, request.getTitle());
                if (result != null && result.getData() != null) {
                    log.info("同步更新了 {} 条借阅记录的书名", result.getData());
                }
            } catch (Exception e) {
                log.error("同步更新借阅记录书名失败，但图书更新已成功: {}", e.getMessage());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBook(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null || book.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "图书不存在");
        }

        // 检查是否有未归还的借阅
        if (book.getAvailableStock() < book.getTotalStock()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该图书存在未归还的借阅记录，无法删除");
        }

        // 逻辑删除
        bookMapper.deleteById(id);
        log.info("删除图书成功，ID: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBookStatus(Long id, Integer status) {
        Book book = bookMapper.selectById(id);
        if (book == null || book.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "图书不存在");
        }

        if (status != 0 && status != 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "状态值无效");
        }

        Book updateBook = Book.builder()
                .id(id)
                .status(status)
                .build();
        bookMapper.updateById(updateBook);
        log.info("更新图书状态成功，ID: {}, 状态: {}", id, status == 1 ? "上架" : "下架");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer operateStock(StockRequest request) {
        Long bookId = request.getBookId();
        Integer quantity = request.getQuantity();

        // 检查图书是否存在
        Book book = bookMapper.selectById(bookId);
        if (book == null || book.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "图书不存在");
        }

        int affectedRows;
        switch (request.getOperationType()) {
            case BORROW -> {
                // 借出：减少可借库存
                affectedRows = bookMapper.decreaseAvailableStock(bookId, quantity);
                if (affectedRows == 0) {
                    throw new BusinessException(ResultCode.BAD_REQUEST, "库存不足");
                }
                log.info("图书借出成功，ID: {}, 数量: {}", bookId, quantity);
            }
            case RETURN -> {
                // 归还：增加可借库存
                affectedRows = bookMapper.increaseAvailableStock(bookId, quantity);
                if (affectedRows == 0) {
                    throw new BusinessException(ResultCode.BAD_REQUEST, "归还数量超过已借数量");
                }
                log.info("图书归还成功，ID: {}, 数量: {}", bookId, quantity);
            }
            case ADD -> {
                // 入库：同时增加总库存和可借库存
                affectedRows = bookMapper.addStock(bookId, quantity);
                if (affectedRows == 0) {
                    throw new BusinessException(ResultCode.BAD_REQUEST, "入库失败");
                }
                log.info("图书入库成功，ID: {}, 数量: {}", bookId, quantity);
            }
            case REDUCE -> {
                // 减库存：同时减少总库存和可借库存
                affectedRows = bookMapper.reduceStock(bookId, quantity);
                if (affectedRows == 0) {
                    throw new BusinessException(ResultCode.BAD_REQUEST, "库存不足，无法减少");
                }
                log.info("图书减库存成功，ID: {}, 数量: {}", bookId, quantity);
            }
            default -> throw new BusinessException(ResultCode.BAD_REQUEST, "未知的操作类型");
        }

        // 返回操作后的可用库存
        Book updatedBook = bookMapper.selectById(bookId);
        return updatedBook.getAvailableStock();
    }

    @Override
    public boolean checkStock(Long bookId, Integer quantity) {
        Book book = bookMapper.selectById(bookId);
        if (book == null || book.getDeleted() == 1) {
            return false;
        }
        return book.getAvailableStock() >= quantity;
    }

    /**
     * 设置状态描述
     */
    private void setStatusDesc(BookResponse book) {
        if (book != null && book.getStatus() != null) {
            book.setStatusDesc(book.getStatus() == 1 ? "上架" : "下架");
        }
    }
}
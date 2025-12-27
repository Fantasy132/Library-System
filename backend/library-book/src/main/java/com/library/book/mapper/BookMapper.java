package com.library.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.book.dto.BookQuery;
import com.library.book.dto.BookResponse;
import com.library.book.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 图书 Mapper
 *
 * @author Library System
 * @since 1.0.0
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {

    /**
     * 分页查询图书（带分类名称）
     *
     * @param page  分页参数
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<BookResponse> selectBookPage(Page<BookResponse> page, @Param("query") BookQuery query);

    /**
     * 查询图书总数
     *
     * @param query 查询条件
     * @return 总数
     */
    Long countBooks(@Param("query") BookQuery query);

    /**
     * 查询图书详情（带分类名称）
     *
     * @param id 图书ID
     * @return 图书详情
     */
    BookResponse selectBookDetail(@Param("id") Long id);

    /**
     * 根据ISBN查询图书
     *
     * @param isbn ISBN
     * @return 图书
     */
    @Select("SELECT * FROM t_book WHERE isbn = #{isbn} AND deleted = 0")
    Book selectByIsbn(@Param("isbn") String isbn);

    /**
     * 减少可借库存（借书时调用）
     *
     * @param bookId   图书ID
     * @param quantity 数量
     * @return 影响行数
     */
    @Update("UPDATE t_book SET available_stock = available_stock - #{quantity}, " +
            "update_time = NOW() " +
            "WHERE id = #{bookId} AND available_stock >= #{quantity} AND deleted = 0")
    int decreaseAvailableStock(@Param("bookId") Long bookId, @Param("quantity") Integer quantity);

    /**
     * 增加可借库存（还书时调用）
     *
     * @param bookId   图书ID
     * @param quantity 数量
     * @return 影响行数
     */
    @Update("UPDATE t_book SET available_stock = available_stock + #{quantity}, " +
            "update_time = NOW() " +
            "WHERE id = #{bookId} AND (available_stock + #{quantity}) <= total_stock AND deleted = 0")
    int increaseAvailableStock(@Param("bookId") Long bookId, @Param("quantity") Integer quantity);

    /**
     * 增加总库存和可借库存（入库时调用）
     *
     * @param bookId   图书ID
     * @param quantity 数量
     * @return 影响行数
     */
    @Update("UPDATE t_book SET total_stock = total_stock + #{quantity}, " +
            "available_stock = available_stock + #{quantity}, " +
            "update_time = NOW() " +
            "WHERE id = #{bookId} AND deleted = 0")
    int addStock(@Param("bookId") Long bookId, @Param("quantity") Integer quantity);

    /**
     * 减少总库存和可借库存（减库存时调用）
     *
     * @param bookId   图书ID
     * @param quantity 数量
     * @return 影响行数
     */
    @Update("UPDATE t_book SET total_stock = total_stock - #{quantity}, " +
            "available_stock = available_stock - #{quantity}, " +
            "update_time = NOW() " +
            "WHERE id = #{bookId} AND available_stock >= #{quantity} AND deleted = 0")
    int reduceStock(@Param("bookId") Long bookId, @Param("quantity") Integer quantity);

    /**
     * 统计分类下的图书数量
     *
     * @param categoryId 分类ID
     * @return 图书数量
     */
    @Select("SELECT COUNT(*) FROM t_book WHERE category_id = #{categoryId} AND deleted = 0")
    int countByCategoryId(@Param("categoryId") Long categoryId);
}
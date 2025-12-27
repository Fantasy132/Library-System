package com.library.borrow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.borrow.dto.BorrowQuery;
import com.library.borrow.dto.BorrowRecordResponse;
import com.library.borrow.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 借阅记录 Mapper
 *
 * @author Library System
 * @since 1.0.0
 */
@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {

    /**
     * 分页查询借阅记录
     *
     * @param page  分页参数
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<BorrowRecordResponse> selectBorrowPage(Page<BorrowRecordResponse> page, @Param("query") BorrowQuery query);

    /**
     * 查询用户正在借阅的某本书的记录
     *
     * @param userId 用户ID
     * @param bookId 图书ID
     * @return 借阅记录列表
     */
    @Select("SELECT * FROM t_borrow_record WHERE user_id = #{userId} AND book_id = #{bookId} " +
            "AND status IN (0, 2, 3) AND deleted = 0")
    List<BorrowRecord> selectBorrowingByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);

    /**
     * 查询用户所有借阅中的记录数量
     *
     * @param userId 用户ID
     * @return 借阅数量
     */
    @Select("SELECT COUNT(*) FROM t_borrow_record WHERE user_id = #{userId} " +
            "AND status IN (0, 2, 3) AND deleted = 0")
    int countBorrowingByUser(@Param("userId") Long userId);

    /**
     * 查询所有逾期未还的记录
     *
     * @return 逾期记录列表
     */
    @Select("SELECT * FROM t_borrow_record WHERE status IN (0, 3) AND due_time < NOW() AND deleted = 0")
    List<BorrowRecord> selectOverdueRecords();

    /**
     * 批量更新逾期状态
     *
     * @param ids 记录ID列表
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE t_borrow_record SET status = 2, update_time = NOW() " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchUpdateOverdueStatus(@Param("ids") List<Long> ids);

    /**
     * 统计用户借阅总数
     *
     * @param userId 用户ID
     * @return 借阅总数
     */
    @Select("SELECT COUNT(*) FROM t_borrow_record WHERE user_id = #{userId} AND deleted = 0")
    int countTotalByUser(@Param("userId") Long userId);

    /**
     * 统计用户逾期次数
     *
     * @param userId 用户ID
     * @return 逾期次数
     */
    @Select("SELECT COUNT(*) FROM t_borrow_record WHERE user_id = #{userId} " +
            "AND (status = 2 OR (status = 1 AND return_time > due_time)) AND deleted = 0")
    int countOverdueByUser(@Param("userId") Long userId);

    /**
     * 根据图书ID更新所有借阅记录中的书名
     *
     * @param bookId 图书ID
     * @param title  新书名
     * @return 影响行数
     */
    @Update("UPDATE t_borrow_record SET book_title = #{title}, update_time = NOW() " +
            "WHERE book_id = #{bookId} AND deleted = 0")
    int updateBookTitleByBookId(@Param("bookId") Long bookId, @Param("title") String title);
}
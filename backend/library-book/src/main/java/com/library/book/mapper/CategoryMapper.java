package com.library.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.book.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 分类 Mapper
 *
 * @author Library System
 * @since 1.0.0
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 根据编码查询分类
     *
     * @param code 分类编码
     * @return 分类
     */
    @Select("SELECT * FROM t_category WHERE code = #{code} AND deleted = 0")
    Category selectByCode(@Param("code") String code);

    /**
     * 查询子分类列表
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @Select("SELECT * FROM t_category WHERE parent_id = #{parentId} AND deleted = 0 ORDER BY sort_order ASC")
    List<Category> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询所有启用的分类
     *
     * @return 分类列表
     */
    @Select("SELECT * FROM t_category WHERE status = 1 AND deleted = 0 ORDER BY sort_order ASC")
    List<Category> selectAllEnabled();
}
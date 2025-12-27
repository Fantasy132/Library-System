package com.library.book.service;

import com.library.book.dto.CategoryRequest;
import com.library.book.dto.CategoryResponse;

import java.util.List;

/**
 * 分类服务接口
 *
 * @author Library System
 * @since 1.0.0
 */
public interface CategoryService {

    /**
     * 查询所有分类（树形结构）
     *
     * @return 分类树
     */
    List<CategoryResponse> listCategoryTree();

    /**
     * 查询所有启用的分类（扁平结构）
     *
     * @return 分类列表
     */
    List<CategoryResponse> listAllCategories();

    /**
     * 根据ID查询分类
     *
     * @param id 分类ID
     * @return 分类详情
     */
    CategoryResponse getCategoryById(Long id);

    /**
     * 新增分类
     *
     * @param request 分类信息
     * @return 分类ID
     */
    Long createCategory(CategoryRequest request);

    /**
     * 更新分类
     *
     * @param id      分类ID
     * @param request 分类信息
     */
    void updateCategory(Long id, CategoryRequest request);

    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    void deleteCategory(Long id);

    /**
     * 更新分类状态
     *
     * @param id     分类ID
     * @param status 状态：0-禁用，1-启用
     */
    void updateCategoryStatus(Long id, Integer status);
}
package com.library.book.controller;

import com.library.book.config.RequireAdmin;
import com.library.book.dto.CategoryRequest;
import com.library.book.dto.CategoryResponse;
import com.library.book.service.CategoryService;
import com.library.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理 Controller
 *
 * @author Library System
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 查询分类树
     *
     * @return 分类树
     */
    @GetMapping("/tree")
    public Result<List<CategoryResponse>> listCategoryTree() {
        List<CategoryResponse> tree = categoryService.listCategoryTree();
        return Result.success(tree);
    }

    /**
     * 查询所有启用的分类（扁平结构）
     *
     * @return 分类列表
     */
    @GetMapping
    public Result<List<CategoryResponse>> listAllCategories() {
        List<CategoryResponse> categories = categoryService.listAllCategories();
        return Result.success(categories);
    }

    /**
     * 根据ID查询分类
     *
     * @param id 分类ID
     * @return 分类详情
     */
    @GetMapping("/{id}")
    public Result<CategoryResponse> getCategory(@PathVariable Long id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        return Result.success(category);
    }

    /**
     * 新增分类（需要管理员权限）
     *
     * @param request 分类信息
     * @return 分类ID
     */
    @PostMapping
    @RequireAdmin
    public Result<Long> createCategory(@Valid @RequestBody CategoryRequest request) {
        Long categoryId = categoryService.createCategory(request);
        return Result.success("新增分类成功", categoryId);
    }

    /**
     * 更新分类（需要管理员权限）
     *
     * @param id      分类ID
     * @param request 分类信息
     * @return 结果
     */
    @PutMapping("/{id}")
    @RequireAdmin
    public Result<Void> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        categoryService.updateCategory(id, request);
        return Result.success("更新分类成功");
    }

    /**
     * 删除分类（需要管理员权限）
     *
     * @param id 分类ID
     * @return 结果
     */
    @DeleteMapping("/{id}")
    @RequireAdmin
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("删除分类成功");
    }

    /**
     * 更新分类状态（需要管理员权限）
     *
     * @param id     分类ID
     * @param status 状态：0-禁用，1-启用
     * @return 结果
     */
    @PutMapping("/{id}/status")
    @RequireAdmin
    public Result<Void> updateCategoryStatus(@PathVariable Long id, @RequestParam Integer status) {
        categoryService.updateCategoryStatus(id, status);
        return Result.success(status == 1 ? "启用成功" : "禁用成功");
    }
}
package com.library.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.book.dto.CategoryRequest;
import com.library.book.dto.CategoryResponse;
import com.library.book.entity.Category;
import com.library.book.mapper.BookMapper;
import com.library.book.mapper.CategoryMapper;
import com.library.book.service.CategoryService;
import com.library.common.exception.BusinessException;
import com.library.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 *
 * @author Library System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final BookMapper bookMapper;

    @Override
    public List<CategoryResponse> listCategoryTree() {
        // 查询所有分类
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSortOrder);
        List<Category> categories = categoryMapper.selectList(wrapper);

        // 转换为响应对象
        List<CategoryResponse> allCategories = categories.stream()
                .map(this::convertToResponse)
                .toList();

        // 构建树形结构
        return buildTree(allCategories, 0L);
    }

    @Override
    public List<CategoryResponse> listAllCategories() {
        List<Category> categories = categoryMapper.selectAllEnabled();
        return categories.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null || category.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "分类不存在");
        }
        return convertToResponse(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(CategoryRequest request) {
        // 检查编码是否已存在
        Category existCategory = categoryMapper.selectByCode(request.getCode());
        if (existCategory != null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "分类编码已存在");
        }

        // 如果有父分类，检查父分类是否存在
        if (request.getParentId() != null && request.getParentId() > 0) {
            Category parent = categoryMapper.selectById(request.getParentId());
            if (parent == null || parent.getDeleted() == 1) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "父分类不存在");
            }
        }

        // 构建分类实体
        Category category = Category.builder()
                .name(request.getName())
                .code(request.getCode())
                .parentId(request.getParentId() != null ? request.getParentId() : 0L)
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .deleted(0)
                .build();

        categoryMapper.insert(category);
        log.info("新增分类成功，ID: {}, 名称: {}", category.getId(), category.getName());

        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Long id, CategoryRequest request) {
        // 检查分类是否存在
        Category existCategory = categoryMapper.selectById(id);
        if (existCategory == null || existCategory.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "分类不存在");
        }

        // 检查编码是否与其他分类重复
        Category categoryByCode = categoryMapper.selectByCode(request.getCode());
        if (categoryByCode != null && !Objects.equals(categoryByCode.getId(), id)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "分类编码已被其他分类使用");
        }

        // 如果有父分类，检查父分类是否存在且不是自己
        if (request.getParentId() != null && request.getParentId() > 0) {
            if (Objects.equals(request.getParentId(), id)) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "父分类不能是自己");
            }
            Category parent = categoryMapper.selectById(request.getParentId());
            if (parent == null || parent.getDeleted() == 1) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "父分类不存在");
            }
        }

        // 更新分类
        Category category = Category.builder()
                .id(id)
                .name(request.getName())
                .code(request.getCode())
                .parentId(request.getParentId())
                .sortOrder(request.getSortOrder())
                .description(request.getDescription())
                .status(request.getStatus())
                .build();

        categoryMapper.updateById(category);
        log.info("更新分类成功，ID: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null || category.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "分类不存在");
        }

        // 检查是否有子分类
        List<Category> children = categoryMapper.selectByParentId(id);
        if (!children.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该分类下存在子分类，无法删除");
        }

        // 检查是否有关联的图书
        int bookCount = bookMapper.countByCategoryId(id);
        if (bookCount > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该分类下存在图书，无法删除");
        }

        // 逻辑删除
        categoryMapper.deleteById(id);
        log.info("删除分类成功，ID: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategoryStatus(Long id, Integer status) {
        Category category = categoryMapper.selectById(id);
        if (category == null || category.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "分类不存在");
        }

        if (status != 0 && status != 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "状态值无效");
        }

        Category updateCategory = Category.builder()
                .id(id)
                .status(status)
                .build();
        categoryMapper.updateById(updateCategory);
        log.info("更新分类状态成功，ID: {}, 状态: {}", id, status == 1 ? "启用" : "禁用");
    }

    /**
     * 转换为响应对象
     */
    private CategoryResponse convertToResponse(Category category) {
        // 获取图书数量
        int bookCount = bookMapper.countByCategoryId(category.getId());

        // 获取父分类名称
        String parentName = null;
        if (category.getParentId() != null && category.getParentId() > 0) {
            Category parent = categoryMapper.selectById(category.getParentId());
            if (parent != null) {
                parentName = parent.getName();
            }
        }

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .code(category.getCode())
                .parentId(category.getParentId())
                .parentName(parentName)
                .sortOrder(category.getSortOrder())
                .description(category.getDescription())
                .status(category.getStatus())
                .statusDesc(category.getStatus() == 1 ? "启用" : "禁用")
                .bookCount(bookCount)
                .createTime(category.getCreateTime())
                .updateTime(category.getUpdateTime())
                .build();
    }

    /**
     * 构建树形结构
     */
    private List<CategoryResponse> buildTree(List<CategoryResponse> categories, Long parentId) {
        // 按父ID分组
        Map<Long, List<CategoryResponse>> parentMap = categories.stream()
                .collect(Collectors.groupingBy(CategoryResponse::getParentId));

        // 递归构建树
        List<CategoryResponse> tree = new ArrayList<>();
        List<CategoryResponse> children = parentMap.get(parentId);
        if (children != null) {
            for (CategoryResponse child : children) {
                child.setChildren(buildTree(categories, child.getId()));
                tree.add(child);
            }
        }
        return tree;
    }
}
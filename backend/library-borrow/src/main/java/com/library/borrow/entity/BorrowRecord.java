package com.library.borrow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 借阅记录实体类
 *
 * @author Library System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_borrow_record")
public class BorrowRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 借阅记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 图书ID
     */
    private Long bookId;

    /**
     * 图书ISBN
     */
    private String bookIsbn;

    /**
     * 图书名称
     */
    private String bookTitle;

    /**
     * 借阅数量
     */
    private Integer quantity;

    /**
     * 借阅时间
     */
    private LocalDateTime borrowTime;

    /**
     * 应还时间
     */
    private LocalDateTime dueTime;

    /**
     * 实际归还时间
     */
    private LocalDateTime returnTime;

    /**
     * 状态：0-借阅中，1-已归还，2-已逾期，3-已续借
     */
    private Integer status;

    /**
     * 续借次数
     */
    private Integer renewCount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}
package com.library.borrow.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 借阅状态枚举
 *
 * @author Library System
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum BorrowStatus {

    /**
     * 借阅中
     */
    BORROWING(0, "借阅中"),

    /**
     * 已归还
     */
    RETURNED(1, "已归还"),

    /**
     * 已逾期
     */
    OVERDUE(2, "已逾期"),

    /**
     * 已续借
     */
    RENEWED(3, "已续借");

    private final Integer code;
    private final String desc;

    /**
     * 根据状态码获取枚举
     */
    public static BorrowStatus fromCode(Integer code) {
        for (BorrowStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据状态码获取描述
     */
    public static String getDescByCode(Integer code) {
        BorrowStatus status = fromCode(code);
        return status != null ? status.getDesc() : "未知状态";
    }
}
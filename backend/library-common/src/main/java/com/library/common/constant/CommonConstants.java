package com.library.common.constant;

/**
 * 公共常量
 */
public final class CommonConstants {

    private CommonConstants() {
        throw new IllegalStateException("常量类不允许实例化");
    }

    /**
     * JWT 相关常量
     */
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";

    /**
     * 用户角色
     */
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    /**
     * 用户状态
     */
    public static final Integer USER_STATUS_NORMAL = 1;
    public static final Integer USER_STATUS_DISABLED = 0;

    /**
     * 借阅状态
     */
    public static final Integer BORROW_STATUS_BORROWED = 1;
    public static final Integer BORROW_STATUS_RETURNED = 2;
    public static final Integer BORROW_STATUS_OVERDUE = 3;

    /**
     * 默认分页参数
     */
    public static final Integer DEFAULT_PAGE_NUM = 1;
    public static final Integer DEFAULT_PAGE_SIZE = 10;
    public static final Integer MAX_PAGE_SIZE = 100;

    /**
     * 默认借阅天数
     */
    public static final Integer DEFAULT_BORROW_DAYS = 30;

    /**
     * 最大借阅数量
     */
    public static final Integer MAX_BORROW_COUNT = 5;
}
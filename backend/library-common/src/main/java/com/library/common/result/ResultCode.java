package com.library.common.result;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 响应状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误 4xx
    FAILURE(400, "操作失败"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证或认证失败"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),

    // 业务错误 5xx
    INTERNAL_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    // 用户相关 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    USERNAME_OR_PASSWORD_ERROR(1003, "用户名或密码错误"),
    USER_DISABLED(1004, "用户已被禁用"),
    PASSWORD_ERROR(1005, "密码错误"),
    OLD_PASSWORD_ERROR(1006, "原密码错误"),

    // Token相关 2xxx
    TOKEN_INVALID(2001, "Token无效"),
    TOKEN_EXPIRED(2002, "Token已过期"),
    TOKEN_MISSING(2003, "Token缺失"),

    // 图书相关 3xxx
    BOOK_NOT_FOUND(3001, "图书不存在"),
    BOOK_ALREADY_EXISTS(3002, "图书已存在"),
    BOOK_STOCK_NOT_ENOUGH(3003, "图书库存不足"),

    // 借阅相关 4xxx
    BORROW_RECORD_NOT_FOUND(4001, "借阅记录不存在"),
    BOOK_ALREADY_BORROWED(4002, "图书已被借阅"),
    BORROW_LIMIT_EXCEEDED(4003, "借阅数量超过限制"),
    BOOK_NOT_RETURNED(4004, "图书未归还"),
    BOOK_ALREADY_RETURNED(4005, "图书已归还");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 消息
     */
    private final String message;
}
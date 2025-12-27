package com.library.book.config;

import java.lang.annotation.*;

/**
 * 需要管理员权限的注解
 *
 * @author Library System
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireAdmin {
}
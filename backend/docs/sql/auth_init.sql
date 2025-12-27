-- =====================================================
-- 书籍借阅管理系统 - Auth Service 数据库初始化脚本
-- 数据库: MySQL 8.4
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS library_auth 
    DEFAULT CHARACTER SET utf8mb4 
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE library_auth;

-- =====================================================
-- 用户表
-- =====================================================
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    real_name VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色（USER-普通用户，ADMIN-管理员）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（1-正常，0-禁用）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标志（0-未删除，1-已删除）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_email (email),
    KEY idx_phone (phone),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================================
-- 初始化数据
-- =====================================================

-- 插入管理员账号（密码: admin123，使用 BCrypt 加密）
-- BCrypt 加密后的 admin123: $2b$12$AelQGowbmCjXlEY1pgQMhO/5gcyQBfhyNkU94eGun9LySJSj77wKS
INSERT INTO t_user (username, password, email, phone, real_name, role, status) VALUES
('admin', '$2b$12$AelQGowbmCjXlEY1pgQMhO/5gcyQBfhyNkU94eGun9LySJSj77wKS', 'admin@library.com', '13800000000', '系统管理员', 'ADMIN', 1);

-- 插入测试用户（密码: user123）
-- BCrypt 加密后的 user123: $2b$12$azwlmiNLDFLToM9WTHvqw.DkOZUCCJLszYPKCpyRibdDmtsiILRFK
INSERT INTO t_user (username, password, email, phone, real_name, role, status) VALUES
('user1', '$2b$12$azwlmiNLDFLToM9WTHvqw.DkOZUCCJLszYPKCpyRibdDmtsiILRFK', 'user1@library.com', '13800000001', '张三', 'USER', 1),
('user2', '$2b$12$azwlmiNLDFLToM9WTHvqw.DkOZUCCJLszYPKCpyRibdDmtsiILRFK', 'user2@library.com', '13800000002', '李四', 'USER', 1),
('user3', '$2b$12$azwlmiNLDFLToM9WTHvqw.DkOZUCCJLszYPKCpyRibdDmtsiILRFK', 'user3@library.com', '13800000003', '王五', 'USER', 1);

-- =====================================================
-- 说明
-- =====================================================
-- 1. 默认管理员账号: admin / admin123
-- 2. 测试用户账号: user1, user2, user3 / user123
-- 3. 密码使用 BCrypt 加密存储
-- 4. 角色分为 USER（普通用户）和 ADMIN（管理员）
-- 5. 状态 1 表示正常，0 表示禁用
-- =====================================================
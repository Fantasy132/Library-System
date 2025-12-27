-- =====================================================
-- 借阅服务数据库初始化脚本
-- 数据库名称：library_borrow
-- 适用版本：MySQL 8.4
-- =====================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `library_borrow` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `library_borrow`;

-- =====================================================
-- 1. 借阅记录表
-- =====================================================
DROP TABLE IF EXISTS `t_borrow_record`;
CREATE TABLE `t_borrow_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '借阅记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `book_id` BIGINT NOT NULL COMMENT '图书ID',
    `book_isbn` VARCHAR(20) NOT NULL COMMENT '图书ISBN',
    `book_title` VARCHAR(200) NOT NULL COMMENT '图书名称',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '借阅数量',
    `borrow_time` DATETIME NOT NULL COMMENT '借阅时间',
    `due_time` DATETIME NOT NULL COMMENT '应还时间',
    `return_time` DATETIME DEFAULT NULL COMMENT '实际归还时间',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-借阅中，1-已归还，2-已逾期，3-已续借',
    `renew_count` INT NOT NULL DEFAULT 0 COMMENT '续借次数',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_book_id` (`book_id`),
    KEY `idx_status` (`status`),
    KEY `idx_borrow_time` (`borrow_time`),
    KEY `idx_due_time` (`due_time`),
    KEY `idx_user_book` (`user_id`, `book_id`),
    KEY `idx_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借阅记录表';

-- =====================================================
-- 2. 初始化测试数据（可选）
-- =====================================================
-- 注意：这些数据依赖于 auth 和 book 服务的数据
-- 请确保用户ID和图书ID在对应的服务中存在

-- 示例借阅记录（假设用户ID=1存在，图书ID=1,2,3存在）
INSERT INTO `t_borrow_record` (`user_id`, `username`, `book_id`, `book_isbn`, `book_title`, `quantity`, `borrow_time`, `due_time`, `return_time`, `status`, `renew_count`, `remark`) VALUES
-- 已归还的记录
(1, 'testuser', 1, '978-7-111-42765-2', 'Java核心技术 卷I', 1, DATE_SUB(NOW(), INTERVAL 45 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY), 1, 0, '按时归还'),
(1, 'testuser', 2, '978-7-115-41768-2', 'Spring实战（第5版）', 1, DATE_SUB(NOW(), INTERVAL 30 DAY), NOW(), DATE_SUB(NOW(), INTERVAL 5 DAY), 1, 0, NULL),

-- 借阅中的记录
(1, 'testuser', 3, '978-7-115-52808-0', 'Python编程：从入门到实践', 1, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), NULL, 0, 0, '正在阅读'),

-- 续借的记录
(1, 'testuser', 4, '978-7-121-35533-2', '深入理解Java虚拟机', 1, DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), NULL, 3, 1, '续借一次');

-- =====================================================
-- 3. 创建索引优化查询
-- =====================================================

-- 复合索引用于查询用户借阅中的图书
ALTER TABLE `t_borrow_record` ADD INDEX `idx_user_status_deleted` (`user_id`, `status`, `deleted`);

-- 用于逾期查询的索引
ALTER TABLE `t_borrow_record` ADD INDEX `idx_status_due_deleted` (`status`, `due_time`, `deleted`);

-- =====================================================
-- 完成
-- =====================================================
SELECT '借阅服务数据库初始化完成！' AS message;
SELECT CONCAT('借阅记录数量: ', COUNT(*)) AS info FROM `t_borrow_record`;
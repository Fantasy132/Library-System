-- =====================================================
-- 图书服务数据库初始化脚本
-- 数据库名称：library_book
-- 适用版本：MySQL 8.4
-- =====================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `library_book` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `library_book`;

-- =====================================================
-- 1. 分类表
-- =====================================================
DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `code` VARCHAR(50) NOT NULL COMMENT '分类编码',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父分类ID（0表示顶级分类）',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序号',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '分类描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书分类表';

-- =====================================================
-- 2. 图书表
-- =====================================================
DROP TABLE IF EXISTS `t_book`;
CREATE TABLE `t_book` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '图书ID',
    `isbn` VARCHAR(20) NOT NULL COMMENT 'ISBN编号',
    `title` VARCHAR(200) NOT NULL COMMENT '书名',
    `author` VARCHAR(100) NOT NULL COMMENT '作者',
    `publisher` VARCHAR(100) DEFAULT NULL COMMENT '出版社',
    `publish_date` DATE DEFAULT NULL COMMENT '出版日期',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格',
    `total_stock` INT NOT NULL DEFAULT 0 COMMENT '总库存数量',
    `available_stock` INT NOT NULL DEFAULT 0 COMMENT '可借数量',
    `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '图书封面URL',
    `description` TEXT DEFAULT NULL COMMENT '图书简介',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_isbn` (`isbn`),
    KEY `idx_title` (`title`),
    KEY `idx_author` (`author`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书表';

-- =====================================================
-- 3. 初始化分类数据
-- =====================================================
INSERT INTO `t_category` (`name`, `code`, `parent_id`, `sort_order`, `description`, `status`) VALUES
-- 一级分类
('文学', 'LITERATURE', 0, 1, '文学类图书', 1),
('历史', 'HISTORY', 0, 2, '历史类图书', 1),
('科技', 'TECHNOLOGY', 0, 3, '科技类图书', 1),
('艺术', 'ART', 0, 4, '艺术类图书', 1),
('教育', 'EDUCATION', 0, 5, '教育类图书', 1),
('经济管理', 'BUSINESS', 0, 6, '经济管理类图书', 1),
('生活', 'LIFESTYLE', 0, 7, '生活类图书', 1),
('少儿', 'CHILDREN', 0, 8, '少儿类图书', 1);

-- 二级分类（文学下的子分类）
INSERT INTO `t_category` (`name`, `code`, `parent_id`, `sort_order`, `description`, `status`) VALUES
('小说', 'NOVEL', 1, 1, '小说类图书', 1),
('散文', 'PROSE', 1, 2, '散文类图书', 1),
('诗歌', 'POETRY', 1, 3, '诗歌类图书', 1),
('戏剧', 'DRAMA', 1, 4, '戏剧类图书', 1);

-- 二级分类（科技下的子分类）
INSERT INTO `t_category` (`name`, `code`, `parent_id`, `sort_order`, `description`, `status`) VALUES
('计算机', 'COMPUTER', 3, 1, '计算机类图书', 1),
('自然科学', 'NATURAL_SCIENCE', 3, 2, '自然科学类图书', 1),
('工程技术', 'ENGINEERING', 3, 3, '工程技术类图书', 1),
('医学', 'MEDICINE', 3, 4, '医学类图书', 1);

-- 三级分类（计算机下的子分类）
INSERT INTO `t_category` (`name`, `code`, `parent_id`, `sort_order`, `description`, `status`) VALUES
('编程语言', 'PROGRAMMING', 13, 1, '编程语言类图书', 1),
('数据库', 'DATABASE', 13, 2, '数据库类图书', 1),
('网络技术', 'NETWORK', 13, 3, '网络技术类图书', 1),
('人工智能', 'AI', 13, 4, '人工智能类图书', 1);

-- =====================================================
-- 4. 初始化图书数据（示例）
-- =====================================================
INSERT INTO `t_book` (`isbn`, `title`, `author`, `publisher`, `publish_date`, `category_id`, `price`, `total_stock`, `available_stock`, `description`, `status`) VALUES
-- 编程语言类
('978-7-111-42765-2', 'Java核心技术 卷I', 'Cay S. Horstmann', '机械工业出版社', '2016-09-01', 17, 149.00, 10, 10, 'Java领域最有影响力和价值的著作之一，系统全面讲解Java语言的核心概念、语法、重要特性和开发方法。', 1),
('978-7-115-41768-2', 'Spring实战（第5版）', 'Craig Walls', '人民邮电出版社', '2019-01-01', 17, 89.00, 8, 8, '深入浅出Spring框架开发，涵盖Spring Boot、Spring MVC、Spring Security等核心模块。', 1),
('978-7-115-52808-0', 'Python编程：从入门到实践', 'Eric Matthes', '人民邮电出版社', '2020-07-01', 17, 99.00, 15, 15, '一本全面的Python编程入门书，讲解Python基础知识和项目实战。', 1),
('978-7-121-35533-2', '深入理解Java虚拟机', '周志明', '电子工业出版社', '2019-12-01', 17, 129.00, 6, 6, '全面深入地讲解Java虚拟机的技术原理与最佳实践。', 1),

-- 数据库类
('978-7-111-58612-4', 'MySQL必知必会', 'Ben Forta', '机械工业出版社', '2018-05-01', 18, 59.00, 12, 12, 'MySQL入门经典书籍，通过简洁的实例快速掌握MySQL的使用。', 1),
('978-7-121-30458-5', 'Redis设计与实现', '黄健宏', '电子工业出版社', '2014-06-01', 18, 79.00, 7, 7, '详细讲解Redis的设计与实现原理。', 1),

-- 人工智能类
('978-7-115-51014-6', '深度学习入门', '斋藤康毅', '人民邮电出版社', '2018-07-01', 20, 59.00, 10, 10, '从零开始讲解深度学习的基础知识，使用Python实现各种深度学习算法。', 1),
('978-7-111-63748-3', '机器学习', '周志华', '机械工业出版社', '2016-01-01', 20, 88.00, 5, 5, '机器学习领域的权威教材，被誉为"西瓜书"。', 1),

-- 小说类
('978-7-020-00857-4', '红楼梦', '曹雪芹', '人民文学出版社', '2008-07-01', 9, 59.80, 20, 20, '中国古典四大名著之一，描写了贾、史、王、薛四大家族的兴衰。', 1),
('978-7-544-27851-5', '百年孤独', '加西亚·马尔克斯', '南海出版公司', '2017-08-01', 9, 55.00, 15, 15, '魔幻现实主义文学代表作，讲述布恩迪亚家族七代人的传奇故事。', 1),
('978-7-020-02308-9', '三国演义', '罗贯中', '人民文学出版社', '1998-05-01', 9, 39.50, 18, 18, '中国古典四大名著之一，描写了三国时期的历史故事。', 1),

-- 历史类
('978-7-101-00352-0', '史记', '司马迁', '中华书局', '2011-01-01', 2, 198.00, 8, 8, '中国历史上第一部纪传体通史，被鲁迅誉为"史家之绝唱，无韵之离骚"。', 1),
('978-7-108-04153-8', '万历十五年', '黄仁宇', '生活·读书·新知三联书店', '2006-06-01', 2, 28.00, 12, 12, '以1587年为切入点，探讨明朝政治、经济、文化等方面的问题。', 1),

-- 经济管理类
('978-7-111-40701-2', '经济学原理', 'N. Gregory Mankiw', '机械工业出版社', '2012-08-01', 6, 98.00, 10, 10, '世界上最流行的经济学入门教材之一。', 1),
('978-7-5086-5008-6', '创新者的窘境', 'Clayton M. Christensen', '中信出版社', '2014-01-01', 6, 48.00, 6, 6, '管理学经典著作，讲述创新与管理的关系。', 1);

-- =====================================================
-- 5. 创建索引优化查询
-- =====================================================

-- 图书表复合索引
ALTER TABLE `t_book` ADD INDEX `idx_category_status` (`category_id`, `status`);
ALTER TABLE `t_book` ADD INDEX `idx_title_author` (`title`, `author`);

-- =====================================================
-- 完成
-- =====================================================
SELECT '图书服务数据库初始化完成！' AS message;
SELECT CONCAT('分类数量: ', COUNT(*)) AS info FROM `t_category`;
SELECT CONCAT('图书数量: ', COUNT(*)) AS info FROM `t_book`;
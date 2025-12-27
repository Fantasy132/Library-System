-- MySQL dump 10.13  Distrib 8.4.7, for Linux (x86_64)
--
-- Host: localhost    Database: library_borrow
-- ------------------------------------------------------
-- Server version       8.4.7
-- 
-- 数据库结构导出时间: 2025-12-27
-- 说明: 借阅服务数据库，存储借阅记录信息

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- 创建数据库
--
CREATE DATABASE IF NOT EXISTS `library_borrow` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `library_borrow`;

--
-- Table structure for table `t_borrow_record`
--

DROP TABLE IF EXISTS `t_borrow_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_borrow_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '借阅记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `book_id` bigint NOT NULL COMMENT '图书ID',
  `book_isbn` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图书ISBN',
  `book_title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图书名称',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '借阅数量',
  `borrow_time` datetime NOT NULL COMMENT '借阅时间',
  `due_time` datetime NOT NULL COMMENT '应还时间',
  `return_time` datetime DEFAULT NULL COMMENT '实际归还时间',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-借阅中，1-已归还，2-已逾期，3-已续借',
  `renew_count` int NOT NULL DEFAULT '0' COMMENT '续借次数',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_status` (`status`),
  KEY `idx_borrow_time` (`borrow_time`),
  KEY `idx_due_time` (`due_time`),
  KEY `idx_user_book` (`user_id`,`book_id`),
  KEY `idx_user_status` (`user_id`,`status`),
  KEY `idx_user_status_deleted` (`user_id`,`status`,`deleted`),
  KEY `idx_status_due_deleted` (`status`,`due_time`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借阅记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

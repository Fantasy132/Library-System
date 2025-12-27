# 图书管理系统 - 数据库结构说明

**导出日期**: 2025-12-27  
**数据库版本**: MySQL 8.4.7  
**字符集**: utf8mb4_unicode_ci

## 数据库概览

图书管理系统采用微服务架构，使用独立的数据库分离不同业务领域的数据：

| 数据库名称 | 说明 | 相关服务 | 结构文件 |
|----------|------|---------|---------|
| `library_auth` | 认证服务数据库 | library-auth | [library_auth_schema.sql](library_auth_schema.sql) |
| `library_book` | 图书服务数据库 | library-book | [library_book_schema.sql](library_book_schema.sql) |
| `library_borrow` | 借阅服务数据库 | library-borrow | [library_borrow_schema.sql](library_borrow_schema.sql) |
| `nacos_config` | Nacos配置中心数据库 | Nacos | [nacos_init.sql](nacos_init.sql) |

---

## 1. library_auth - 认证服务数据库

### 1.1 t_user - 用户表

存储系统用户的基本信息和认证数据。

| 字段名 | 类型 | 约束 | 说明 |
|-------|------|------|------|
| id | bigint | PK, AUTO_INCREMENT | 用户ID |
| username | varchar(50) | NOT NULL, UNIQUE | 用户名 |
| password | varchar(100) | NOT NULL | 密码（BCrypt加密） |
| email | varchar(100) | NULL | 邮箱 |
| phone | varchar(20) | NULL | 手机号 |
| real_name | varchar(50) | NULL | 真实姓名 |
| role | varchar(20) | NOT NULL, DEFAULT 'USER' | 角色（USER/ADMIN） |
| status | tinyint | NOT NULL, DEFAULT 1 | 状态（1-正常，0-禁用） |
| create_time | datetime | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| deleted | tinyint | NOT NULL, DEFAULT 0 | 逻辑删除标志 |

**索引**:
- `uk_username`: 唯一索引 - username
- `idx_email`: 普通索引 - email
- `idx_phone`: 普通索引 - phone
- `idx_status`: 普通索引 - status
- `idx_create_time`: 普通索引 - create_time

**业务规则**:
- 用户名唯一，用于登录
- 密码使用BCrypt加密存储
- 支持两种角色：USER（普通用户）和ADMIN（管理员）
- 使用逻辑删除，不物理删除用户数据

---

## 2. library_book - 图书服务数据库

### 2.1 t_book - 图书表

存储图书的详细信息和库存状态。

| 字段名 | 类型 | 约束 | 说明 |
|-------|------|------|------|
| id | bigint | PK, AUTO_INCREMENT | 图书ID |
| isbn | varchar(20) | NOT NULL, UNIQUE | ISBN编号 |
| title | varchar(200) | NOT NULL | 书名 |
| author | varchar(100) | NOT NULL | 作者 |
| publisher | varchar(100) | NULL | 出版社 |
| publish_date | date | NULL | 出版日期 |
| category_id | bigint | NOT NULL | 分类ID |
| price | decimal(10,2) | NOT NULL, DEFAULT 0.00 | 价格 |
| total_stock | int | NOT NULL, DEFAULT 0 | 总库存数量 |
| available_stock | int | NOT NULL, DEFAULT 0 | 可借数量 |
| cover_url | varchar(500) | NULL | 图书封面URL |
| description | text | NULL | 图书简介 |
| status | tinyint | NOT NULL, DEFAULT 1 | 状态（0-下架，1-上架） |
| create_time | datetime | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| deleted | tinyint | NOT NULL, DEFAULT 0 | 逻辑删除标志 |

**索引**:
- `uk_isbn`: 唯一索引 - isbn
- `idx_title`: 普通索引 - title
- `idx_author`: 普通索引 - author
- `idx_category_id`: 普通索引 - category_id
- `idx_status`: 普通索引 - status
- `idx_create_time`: 普通索引 - create_time
- `idx_category_status`: 组合索引 - category_id + status
- `idx_title_author`: 组合索引 - title + author

**业务规则**:
- ISBN编号唯一，作为图书的全球唯一标识
- total_stock: 图书总库存量
- available_stock: 当前可借数量，借阅时减少，归还时增加
- status: 控制图书是否可见和可借

### 2.2 t_category - 图书分类表

存储图书分类的层级结构。

| 字段名 | 类型 | 约束 | 说明 |
|-------|------|------|------|
| id | bigint | PK, AUTO_INCREMENT | 分类ID |
| name | varchar(50) | NOT NULL | 分类名称 |
| code | varchar(50) | NOT NULL, UNIQUE | 分类编码 |
| parent_id | bigint | NOT NULL, DEFAULT 0 | 父分类ID（0表示顶级） |
| sort_order | int | NOT NULL, DEFAULT 0 | 排序号 |
| description | varchar(200) | NULL | 分类描述 |
| status | tinyint | NOT NULL, DEFAULT 1 | 状态（0-禁用，1-可用） |
| create_time | datetime | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| deleted | tinyint | NOT NULL, DEFAULT 0 | 逻辑删除标志 |

**索引**:
- `uk_code`: 唯一索引 - code
- `idx_parent_id`: 普通索引 - parent_id
- `idx_status`: 普通索引 - status

**业务规则**:
- 支持多级分类，通过parent_id构建树形结构
- parent_id = 0 表示顶级分类
- code作为分类的唯一编码标识

---

## 3. library_borrow - 借阅服务数据库

### 3.1 t_borrow_record - 借阅记录表

存储用户的图书借阅历史和当前借阅状态。

| 字段名 | 类型 | 约束 | 说明 |
|-------|------|------|------|
| id | bigint | PK, AUTO_INCREMENT | 借阅记录ID |
| user_id | bigint | NOT NULL | 用户ID |
| username | varchar(50) | NOT NULL | 用户名（冗余字段） |
| book_id | bigint | NOT NULL | 图书ID |
| book_isbn | varchar(20) | NOT NULL | 图书ISBN（冗余字段） |
| book_title | varchar(200) | NOT NULL | 图书名称（冗余字段） |
| quantity | int | NOT NULL, DEFAULT 1 | 借阅数量 |
| borrow_time | datetime | NOT NULL | 借阅时间 |
| due_time | datetime | NOT NULL | 应还时间 |
| return_time | datetime | NULL | 实际归还时间 |
| status | tinyint | NOT NULL, DEFAULT 0 | 状态（0-借阅中，1-已归还，2-已逾期，3-已续借） |
| renew_count | int | NOT NULL, DEFAULT 0 | 续借次数 |
| remark | varchar(500) | NULL | 备注 |
| create_time | datetime | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| deleted | tinyint | NOT NULL, DEFAULT 0 | 逻辑删除标志 |

**索引**:
- `idx_user_id`: 普通索引 - user_id
- `idx_book_id`: 普通索引 - book_id
- `idx_status`: 普通索引 - status
- `idx_borrow_time`: 普通索引 - borrow_time
- `idx_due_time`: 普通索引 - due_time
- `idx_user_book`: 组合索引 - user_id + book_id
- `idx_user_status`: 组合索引 - user_id + status
- `idx_user_status_deleted`: 组合索引 - user_id + status + deleted
- `idx_status_due_deleted`: 组合索引 - status + due_time + deleted

**业务规则**:
- 借阅状态说明：
  - 0: 借阅中 - 用户正在借阅该图书
  - 1: 已归还 - 用户已归还图书
  - 2: 已逾期 - 超过应还时间未归还
  - 3: 已续借 - 用户已申请续借
- 冗余字段（username, book_isbn, book_title）用于提高查询性能，避免跨服务查询
- 支持续借功能，通过renew_count记录续借次数
- 借阅时长默认30天（通过应用层控制）

---

## 数据库关系说明

### 跨服务数据关联

由于采用微服务架构，不同服务的数据库之间没有外键约束，而是通过应用层维护数据一致性：

1. **用户 ↔ 借阅记录**
   - library_auth.t_user.id ←→ library_borrow.t_borrow_record.user_id
   - 通过用户ID关联借阅记录

2. **图书 ↔ 借阅记录**
   - library_book.t_book.id ←→ library_borrow.t_borrow_record.book_id
   - 通过图书ID关联借阅记录

3. **分类 ↔ 图书**
   - library_book.t_category.id ←→ library_book.t_book.category_id
   - 通过分类ID关联图书

### 数据一致性保证

- 借阅记录中冗余存储了username、book_isbn、book_title等字段
- 图书库存的更新通过分布式事务或补偿机制保证
- 逾期状态通过定时任务扫描更新

---

## 初始化说明

### 数据库创建顺序

1. 创建Nacos配置中心数据库
   ```bash
   mysql -uroot -p < nacos_init.sql
   ```

2. 创建认证服务数据库
   ```bash
   mysql -uroot -p < library_auth_schema.sql
   mysql -uroot -p < auth_init.sql
   ```

3. 创建图书服务数据库
   ```bash
   mysql -uroot -p < library_book_schema.sql
   ```

4. 创建借阅服务数据库
   ```bash
   mysql -uroot -p < library_borrow_schema.sql
   ```

### Docker环境初始化

使用Docker Compose时，数据库会自动初始化。相关配置在：
- `backend/docker-compose.yml`
- SQL初始化脚本会通过volume挂载自动执行

---

## 维护建议

### 索引优化
- 定期分析慢查询日志，优化索引
- 关注组合索引的使用率
- 考虑为大表添加分区

### 数据清理
- 定期归档历史借阅记录
- 清理已逾期很久的记录
- 删除软删除的数据（根据业务需求）

### 备份策略
- 每日全量备份
- 实时binlog备份
- 定期进行恢复演练

---

## 附录

### 字符集说明
- 数据库字符集：utf8mb4
- 排序规则：utf8mb4_unicode_ci
- 支持存储emoji和特殊Unicode字符

### 存储引擎
- 所有表使用InnoDB存储引擎
- 支持事务
- 支持外键（虽然微服务架构下未使用）

### 时区处理
- 数据库时区设置为 '+00:00' (UTC)
- 应用层需要处理时区转换

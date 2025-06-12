-- 创建数据库
CREATE DATABASE IF NOT EXISTS penny_pals DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 切换数据库
USE penny_pals;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. 用户表
DROP TABLE IF EXISTS user;
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT COMMENT '用户ID' PRIMARY KEY,
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '用户头像',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱地址',
    phone_number VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    birthday DATE DEFAULT NULL COMMENT '生日',
    union_id VARCHAR(255) DEFAULT NULL COMMENT '微信开放平台ID',
    open_id VARCHAR(255) DEFAULT NULL COMMENT '微信小程序ID',
    user_role VARCHAR(50) NOT NULL DEFAULT 'user' COMMENT '用户角色（user/admin/ban）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    delete_time DATETIME DEFAULT NULL COMMENT '删除时间',
    INDEX idx_username (username),
    INDEX idx_union_id (union_id),
    INDEX idx_open_id (open_id)
) COMMENT '用户表' COLLATE = utf8mb4_unicode_ci;

-- 2. 账本表
DROP TABLE IF EXISTS ledger;
CREATE TABLE IF NOT EXISTS ledger (
    id BIGINT AUTO_INCREMENT COMMENT '账本ID' PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '账本名称',
    description TEXT COMMENT '账本描述',
    icon VARCHAR(255) DEFAULT NULL COMMENT '账本图标',
    delete_time DATETIME DEFAULT NULL COMMENT '删除时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '账本表' COLLATE = utf8mb4_unicode_ci;

-- 3. 账本-用户关联表（权限管理）
DROP TABLE IF EXISTS ledger_user;
CREATE TABLE IF NOT EXISTS ledger_user (
    id BIGINT AUTO_INCREMENT COMMENT '关联ID' PRIMARY KEY,
    ledger_id BIGINT NOT NULL COMMENT '账本ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role ENUM('owner', 'editor', 'viewer') DEFAULT 'viewer' COMMENT '权限角色',
    delete_time DATETIME DEFAULT NULL COMMENT '删除时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uniq_ledger_user (ledger_id, user_id),
    FOREIGN KEY (ledger_id) REFERENCES ledger(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_ledger_id (ledger_id),
    INDEX idx_user_id (user_id)
) COMMENT '账本-用户权限表' COLLATE = utf8mb4_unicode_ci;

-- 4. 账目记录表
DROP TABLE IF EXISTS entry;
CREATE TABLE IF NOT EXISTS entry (
    id BIGINT AUTO_INCREMENT COMMENT '账目ID' PRIMARY KEY,
    ledger_id BIGINT NOT NULL COMMENT '账本ID',
    user_id BIGINT NOT NULL COMMENT '记录人ID',
    type ENUM('income', 'expense') NOT NULL COMMENT '账目类型',
    category VARCHAR(255) NOT NULL COMMENT '分类（如餐饮、交通）',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    date DATE NOT NULL COMMENT '记账日期',
    note TEXT COMMENT '备注',
    icon VARCHAR(255) DEFAULT NULL COMMENT '账目图标',
    delete_time DATETIME DEFAULT NULL COMMENT '删除时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (ledger_id) REFERENCES ledger(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_ledger_id (ledger_id),
    INDEX idx_user_id (user_id),
    INDEX idx_date (date),
    INDEX idx_category (category)
) COMMENT '账目记录表' COLLATE = utf8mb4_unicode_ci;

-- 5. 预算配置表（支持多维度）
DROP TABLE IF EXISTS budget_config;
CREATE TABLE IF NOT EXISTS budget_config (
    id BIGINT AUTO_INCREMENT COMMENT '预算配置ID' PRIMARY KEY,
    ledger_id BIGINT NOT NULL COMMENT '账本ID',
    user_id BIGINT DEFAULT NULL COMMENT '用户ID（可为空，表示账本总体预算）',
    period ENUM('total', 'year', 'quarter', 'month', 'week', 'day') NOT NULL COMMENT '预算周期',
    budget_amount DECIMAL(10, 2) NOT NULL COMMENT '预算金额',
    delete_time DATETIME DEFAULT NULL COMMENT '删除时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (ledger_id) REFERENCES ledger(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_ledger_period (ledger_id, period),
    INDEX idx_user_id (user_id)
) COMMENT '预算配置表' COLLATE = utf8mb4_unicode_ci;

INSERT INTO `penny_pals`.`user` (`id`, `username`, `password`, `avatar`, `email`, `phone_number`, `birthday`, `union_id`, `open_id`, `user_role`, `create_time`, `update_time`, `delete_time`) VALUES (1001, 'zhexun', '194344925efb6fdd57eb0384376fa278', NULL, 'zhexunchen@gmail.com', '19557864422', '2002-02-27', NULL, NULL, 'admin', '2025-06-10 18:41:59', '2025-06-10 21:04:41', NULL);
INSERT INTO `penny_pals`.`user` (`id`, `username`, `password`, `avatar`, `email`, `phone_number`, `birthday`, `union_id`, `open_id`, `user_role`, `create_time`, `update_time`, `delete_time`) VALUES (1002, 'jeffery', '194344925efb6fdd57eb0384376fa278', NULL, 'zhexunchen+1@gmail.com', '19557864422', NULL, NULL, NULL, 'admin', '2025-06-10 20:56:32', '2025-06-10 21:04:39', NULL);
INSERT INTO `penny_pals`.`user` (`id`, `username`, `password`, `avatar`, `email`, `phone_number`, `birthday`, `union_id`, `open_id`, `user_role`, `create_time`, `update_time`, `delete_time`) VALUES (1003, 'user', '194344925efb6fdd57eb0384376fa278', NULL, 'zhexunchen+2@gmail.com', '13311112222', NULL, NULL, NULL, 'user', '2025-06-10 21:04:22', '2025-06-10 21:04:22', NULL);

INSERT INTO `penny_pals`.`ledger` (`id`, `name`, `description`, `icon`, `delete_time`, `create_time`, `update_time`) VALUES (1, '川西旅游', '川西旅游账本，5天4晚', NULL, NULL, '2025-06-10 20:58:25', '2025-06-10 21:04:52');

INSERT INTO `penny_pals`.`ledger_user` (`id`, `ledger_id`, `user_id`, `role`, `delete_time`, `create_time`, `update_time`) VALUES (1, 1, 1001, 'owner', NULL, '2025-06-10 20:58:25', '2025-06-10 20:58:25');
INSERT INTO `penny_pals`.`ledger_user` (`id`, `ledger_id`, `user_id`, `role`, `delete_time`, `create_time`, `update_time`) VALUES (3, 1, 1003, 'editor', NULL, '2025-06-10 21:53:51', '2025-06-10 21:53:51');

INSERT INTO `penny_pals`.`entry` (`id`, `ledger_id`, `user_id`, `type`, `category`, `amount`, `date`, `note`, `delete_time`, `create_time`, `update_time`) VALUES (1, 1, 1001, 'expense', '医疗', 40.00, '2025-05-25', '氧气瓶', NULL, '2025-06-10 21:51:24', '2025-06-10 21:51:24');
INSERT INTO `penny_pals`.`entry` (`id`, `ledger_id`, `user_id`, `type`, `category`, `amount`, `date`, `note`, `delete_time`, `create_time`, `update_time`) VALUES (2, 1, 1001, 'expense', '交通', 279.00, '2025-05-25', '加油', NULL, '2025-06-10 21:52:00', '2025-06-10 21:52:00');
INSERT INTO `penny_pals`.`entry` (`id`, `ledger_id`, `user_id`, `type`, `category`, `amount`, `date`, `note`, `delete_time`, `create_time`, `update_time`) VALUES (3, 1, 1001, 'income', '其他', 200.00, '2025-05-25', '姑妈给的', NULL, '2025-06-10 21:52:35', '2025-06-10 21:52:35');
INSERT INTO `penny_pals`.`entry` (`id`, `ledger_id`, `user_id`, `type`, `category`, `amount`, `date`, `note`, `delete_time`, `create_time`, `update_time`) VALUES (4, 1, 1001, 'expense', '服饰', 15.00, '2025-05-26', '帽子', NULL, '2025-06-10 21:54:49', '2025-06-10 21:54:49');

SET FOREIGN_KEY_CHECKS = 1;

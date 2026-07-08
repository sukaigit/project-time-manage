-- ========================================
-- 项目工时管理系统 - 数据库初始化脚本
-- 数据库：MySQL 5.7
-- ========================================

CREATE DATABASE IF NOT EXISTS project_time_manage DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
USE project_time_manage;

-- 用户表
DROP TABLE IF EXISTS tb_user;
CREATE TABLE tb_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    dept VARCHAR(100) DEFAULT '研发与交付中心',
    status TINYINT DEFAULT 1 COMMENT '1=启用, 0=停用',
    first_login TINYINT DEFAULT 1 COMMENT '1=首次登录',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 角色表
DROP TABLE IF EXISTS tb_role;
CREATE TABLE tb_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    status TINYINT DEFAULT 1 COMMENT '1=启用, 0=停用',
    note VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 用户角色关联表
DROP TABLE IF EXISTS tb_user_role;
CREATE TABLE tb_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    UNIQUE KEY uk_user_role (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES tb_user(id),
    FOREIGN KEY (role_id) REFERENCES tb_role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 角色权限表
DROP TABLE IF EXISTS tb_role_permission;
CREATE TABLE tb_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    menu_key VARCHAR(50) NOT NULL COMMENT '菜单模块标识',
    actions VARCHAR(500) NOT NULL COMMENT 'JSON数组',
    UNIQUE KEY uk_role_menu (role_id, menu_key),
    FOREIGN KEY (role_id) REFERENCES tb_role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 项目表
DROP TABLE IF EXISTS tb_project;
CREATE TABLE tb_project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(500),
    dept VARCHAR(100) DEFAULT '研发与交付中心',
    status TINYINT DEFAULT 1 COMMENT '1=启用, 0=停用',
    start_date DATE,
    end_date DATE,
    create_by BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (create_by) REFERENCES tb_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 项目成员表
DROP TABLE IF EXISTS tb_project_member;
CREATE TABLE tb_project_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    UNIQUE KEY uk_project_user (project_id, user_id),
    FOREIGN KEY (project_id) REFERENCES tb_project(id),
    FOREIGN KEY (user_id) REFERENCES tb_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 任务表
DROP TABLE IF EXISTS tb_task;
CREATE TABLE tb_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    project_id BIGINT NOT NULL,
    status TINYINT DEFAULT 1 COMMENT '1=启用, 0=停用',
    create_by BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES tb_project(id),
    FOREIGN KEY (create_by) REFERENCES tb_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 工时记录表
DROP TABLE IF EXISTS tb_time_entry;
CREATE TABLE tb_time_entry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    task_id BIGINT,
    work_date DATE NOT NULL,
    hours DECIMAL(4,1) NOT NULL COMMENT '0.5~24',
    content VARCHAR(500),
    status TINYINT DEFAULT 0 COMMENT '0=待审批, 1=已通过, 2=已驳回',
    reject_reason VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES tb_user(id),
    FOREIGN KEY (project_id) REFERENCES tb_project(id),
    FOREIGN KEY (task_id) REFERENCES tb_task(id),
    INDEX idx_user (user_id),
    INDEX idx_project (project_id),
    INDEX idx_date (work_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 审批记录表
DROP TABLE IF EXISTS tb_approval_log;
CREATE TABLE tb_approval_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    time_entry_id BIGINT NOT NULL,
    approver_id BIGINT NOT NULL,
    action TINYINT NOT NULL COMMENT '1=通过, 2=驳回',
    reason VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (time_entry_id) REFERENCES tb_time_entry(id),
    FOREIGN KEY (approver_id) REFERENCES tb_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 预置数据
-- 管理员用户 (密码: uu888888 -> BCrypt)
INSERT INTO tb_user (username, password, name, dept, status, first_login) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '系统管理员', '研发与交付中心', 1, 1);

-- 预置角色
INSERT INTO tb_role (name, code, status, note) VALUES
('系统管理员', 'admin', 1, '系统管理员'),
('部门经理', 'dept_manager', 1, '部门经理'),
('项目经理', 'project_manager', 1, '项目经理'),
('普通员工', 'employee', 1, '普通员工');

-- 管理员关联 admin 角色
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);

创建数据库
CREATE DATABASE clinic_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

创建用户表 (users)
USE clinic_db;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密后）',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    role TINYINT DEFAULT 1 COMMENT '角色：1-医生 2-护士 3-管理员',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除', -- 添加逻辑删除字段
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

ALTER TABLE users ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除';

-- 1. 创建角色表 (roles)
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_name VARCHAR(50) UNIQUE NOT NULL COMMENT '角色名称，如：医生、护士、管理员',
    role_code VARCHAR(50) UNIQUE NOT NULL COMMENT '角色编码，如：DOCTOR、NURSE、ADMIN，用于代码中',
    description VARCHAR(255) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 2. 创建权限表 (permissions)
CREATE TABLE permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称，如：查看用户、创建患者',
    permission_code VARCHAR(100) UNIQUE NOT NULL COMMENT '权限编码，如：user:read、patient:create，用于代码中',
    description VARCHAR(255) COMMENT '权限描述',
    resource_type VARCHAR(50) COMMENT '资源类型，可选，如：user, patient, appointment',
    action VARCHAR(20) COMMENT '操作类型，可选，如：read, write, delete',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 3. 创建用户角色关联表 (user_roles) - 多对多关系
CREATE TABLE user_roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    assigned_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_role (user_id, role_id), -- 确保一个用户不能重复分配同一个角色
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, -- 如果用户被删除，关联的角色也删除
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE  -- 如果角色被删除，关联的用户角色也删除
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 4. 创建角色权限关联表 (role_permissions) - 多对多关系
CREATE TABLE role_permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    assigned_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_permission (role_id, permission_id), -- 确保一个角色不能重复分配同一个权限
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE, -- 如果角色被删除，关联的权限也删除
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE -- 如果权限被删除，关联的角色权限也删除
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 插入初始角色 (对应你原来的 1-医生, 2-护士, 3-管理员)
INSERT INTO roles (role_name, role_code, description) VALUES
('医生', 'DOCTOR', '负责诊疗的医生'),
('护士', 'NURSE', '负责护理的护士'),
('管理员', 'ADMIN', '系统管理员，拥有最高权限');

-- 插入一些示例权限
INSERT INTO permissions (permission_name, permission_code, description, resource_type, action) VALUES
('用户-查看', 'user:read', '查看用户信息', 'user', 'read'),
('用户-创建', 'user:create', '创建新用户', 'user', 'create'),
('用户-更新', 'user:update', '更新用户信息', 'user', 'update'),
('用户-删除', 'user:delete', '删除用户', 'user', 'delete'),
('患者-查看', 'patient:read', '查看患者信息', 'patient', 'read'),
('患者-创建', 'patient:create', '创建患者信息', 'patient', 'create');

-- 将 users 表中现有的 role 字段值迁移到 user_roles 表
-- 这里的逻辑是：users 表中的 role=1 对应 roles 表中 role_code='DOCTOR' 的 id
-- 需要根据 roles 表中实际的 id 进行关联。假设 DOCTOR 的 id=1, NURSE id=2, ADMIN id=3

-- 为 DOCTOR 角色的用户创建关联
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.role_code = 'DOCTOR'
WHERE u.role = 1;

-- 为 NURSE 角色的用户创建关联
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.role_code = 'NURSE'
WHERE u.role = 2;

-- 为 ADMIN 角色的用户创建关联
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.role_code = 'ADMIN'
WHERE u.role = 3;

-- 为所有用户分配基本权限（可选）
-- 例如，给所有用户分配 'user:read' 权限
-- 需要先查询权限ID
-- SELECT id FROM permissions WHERE permission_code = 'user:read'; -- 假设 id=1
-- 然后为所有用户的角色分配此权限
-- INSERT INTO role_permissions (role_id, permission_id)
-- SELECT r.id, 1 FROM roles r; -- 这会为所有角色分配 user:read 权限
-- 但这样可能不够精确，建议手动为角色分配具体权限

-- 删除 users 表中的 role 字段
ALTER TABLE users DROP COLUMN role;


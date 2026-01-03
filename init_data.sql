-- 权限管理平台 - 初始化数据脚本
-- 使用方法：mysql -u root -p rootmanage < init_data.sql

USE rootmanage;

-- ============================================
-- 1. 创建管理员角色
-- ============================================
INSERT INTO role (id, name, description, create_time, update_time) 
VALUES (UNHEX(REPLACE(UUID(), '-', '')), 'ADMIN', '系统管理员', NOW(), NOW())
ON DUPLICATE KEY UPDATE name = name;

-- ============================================
-- 2. 创建岗位选项 (POSITION)
-- ============================================
INSERT INTO option_item (id, group_code, title, value, order_no, create_time, update_time)
VALUES 
  (UNHEX(REPLACE(UUID(), '-', '')), 'POSITION', '项目经理', 'PROJECT_MANAGER', 1, NOW(), NOW()),
  (UNHEX(REPLACE(UUID(), '-', '')), 'POSITION', '开发工程师', 'DEVELOPER', 2, NOW(), NOW()),
  (UNHEX(REPLACE(UUID(), '-', '')), 'POSITION', '测试工程师', 'TESTER', 3, NOW(), NOW()),
  (UNHEX(REPLACE(UUID(), '-', '')), 'POSITION', '产品经理', 'PRODUCT_MANAGER', 4, NOW(), NOW()),
  (UNHEX(REPLACE(UUID(), '-', '')), 'POSITION', 'UI设计师', 'UI_DESIGNER', 5, NOW(), NOW()),
  (UNHEX(REPLACE(UUID(), '-', '')), 'POSITION', '运维工程师', 'DEVOPS', 6, NOW(), NOW())
ON DUPLICATE KEY UPDATE title = title;

-- ============================================
-- 3. 创建片区选项 (AREA)
-- ============================================
INSERT INTO option_item (id, group_code, title, value, order_no, create_time, update_time)
VALUES 
  (UNHEX(REPLACE(UUID(), '-', '')), 'AREA', '华东区', 'EAST', 1, NOW(), NOW()),
  (UNHEX(REPLACE(UUID(), '-', '')), 'AREA', '华南区', 'SOUTH', 2, NOW(), NOW()),
  (UNHEX(REPLACE(UUID(), '-', '')), 'AREA', '华北区', 'NORTH', 3, NOW(), NOW()),
  (UNHEX(REPLACE(UUID(), '-', '')), 'AREA', '华西区', 'WEST', 4, NOW(), NOW()),
  (UNHEX(REPLACE(UUID(), '-', '')), 'AREA', '华中区', 'CENTRAL', 5, NOW(), NOW())
ON DUPLICATE KEY UPDATE title = title;

-- ============================================
-- 4. 创建初始管理员账号（可选）
-- 账号：13800000000
-- 密码：123456
-- ============================================
INSERT INTO user_account (
    id, 
    username, 
    password, 
    name, 
    id_card, 
    phone, 
    gender, 
    birth_date, 
    age, 
    status, 
    create_time, 
    update_time, 
    role_id
) 
SELECT 
    UNHEX(REPLACE(UUID(), '-', '')),
    '13800000000',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',  -- 密码：123456
    '系统管理员',
    '110101199001011234',
    '13800000000',
    '男',
    '1990-01-01',
    34,
    'APPROVED',
    NOW(),
    NOW(),
    (SELECT id FROM role WHERE name = 'ADMIN' LIMIT 1)
WHERE NOT EXISTS (
    SELECT 1 FROM user_account WHERE username = '13800000000'
);

-- ============================================
-- 5. 验证数据
-- ============================================
SELECT '角色数据：' AS '验证';
SELECT id, name, description FROM role;

SELECT '岗位选项：' AS '验证';
SELECT id, group_code, title, value, order_no FROM option_item WHERE group_code = 'POSITION';

SELECT '片区选项：' AS '验证';
SELECT id, group_code, title, value, order_no FROM option_item WHERE group_code = 'AREA';

SELECT '管理员账号：' AS '验证';
SELECT u.username, u.name, u.status, r.name AS role_name
FROM user_account u 
LEFT JOIN role r ON u.role_id = r.id 
WHERE u.username = '13800000000';

SELECT '初始化完成！' AS '状态';


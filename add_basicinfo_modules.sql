-- 添加基本信息管理模块到现有数据库
-- 使用方法：mysql -u root -p rootmanage < add_basicinfo_modules.sql

USE rootmanage;

-- 检查是否已存在基本信息管理模块，如果存在则先删除
SET @basicinfo_exists = (SELECT COUNT(*) FROM module WHERE en_name = 'basicinfo_management' AND parent_id IS NULL);

-- 如果已存在，先删除相关模块（包括子模块）
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM role_module_permission WHERE module_id IN (
    SELECT id FROM module WHERE en_name IN ('basicinfo_management', 'customer_management', 'team_management', 'product_info_management')
);
DELETE FROM module WHERE en_name IN ('basicinfo_management', 'customer_management', 'team_management', 'product_info_management');
SET FOREIGN_KEY_CHECKS = 1;

-- 6. 基本信息管理（一级菜单，父节点，展开状态）
SET @basicinfo_management_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @basicinfo_management_id,
    '基本信息管理',
    'basicinfo_management',
    1,
    6000,
    NULL,
    'basicinfo-icon',
    NULL,
    '/basicinfo',
    NULL,
    1,  -- 是父节点
    1,  -- 展开
    1,  -- 可见
    NOW(),
    NOW()
);

-- 6.1 客户管理（二级菜单）
SET @customer_management_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @customer_management_id,
    '客户管理',
    'customer_management',
    2,
    6010,
    '/basicinfo/customers',
    NULL,
    NULL,
    '/basicinfo/customers',
    @basicinfo_management_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

-- 6.2 团队信息管理（二级菜单）
SET @team_management_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @team_management_id,
    '团队信息管理',
    'team_management',
    2,
    6020,
    '/basicinfo/teams',
    NULL,
    NULL,
    '/basicinfo/teams',
    @basicinfo_management_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

-- 6.3 产品管理（二级菜单）
SET @product_info_management_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @product_info_management_id,
    '产品管理',
    'product_info_management',
    2,
    6030,
    '/basicinfo/products',
    NULL,
    NULL,
    '/basicinfo/products',
    @basicinfo_management_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

SELECT '基本信息管理模块添加完成！' AS '状态';
SELECT 
    m1.zh_name AS '一级菜单',
    m2.zh_name AS '二级菜单',
    m2.path AS '路径'
FROM module m1
LEFT JOIN module m2 ON m2.parent_id = m1.id
WHERE m1.en_name = 'basicinfo_management'
ORDER BY m2.order_no;


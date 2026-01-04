-- 添加价格本管理和应收账管理模块到现有数据库
-- 使用方法：mysql -u root -p rootmanage < add_pricebook_receivable_modules.sql

USE rootmanage;

-- 检查是否已存在价格本管理模块，如果存在则先删除
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM role_module_permission WHERE module_id IN (
    SELECT id FROM module WHERE en_name IN ('pricebook_management', 'receivable_management', 'receivable_plan', 'receivable_entry', 'receivable_query')
);
DELETE FROM module WHERE en_name IN ('pricebook_management', 'receivable_management', 'receivable_plan', 'receivable_entry', 'receivable_query');
SET FOREIGN_KEY_CHECKS = 1;

-- 4.4 价格本管理（一级菜单）
SET @pricebook_management_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @pricebook_management_id,
    '价格本管理',
    'pricebook_management',
    1,
    4400,
    '/pricebook',
    'pricebook-icon',
    NULL,
    '/api/pricebook',
    NULL,
    0,  -- 不是父节点
    0,  -- 不展开
    1,  -- 可见
    NOW(),
    NOW()
);

-- 4.5 应收账管理（一级菜单，父节点，展开状态）
SET @receivable_management_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @receivable_management_id,
    '应收账管理',
    'receivable_management',
    1,
    4500,
    NULL,
    'receivable-icon',
    NULL,
    '/api/receivable',
    NULL,
    1,  -- 是父节点
    1,  -- 展开
    1,  -- 可见
    NOW(),
    NOW()
);

-- 4.5.1 应收账计划（二级菜单）
SET @receivable_plan_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @receivable_plan_id,
    '应收账计划',
    'receivable_plan',
    2,
    4510,
    '/receivable/plan',
    NULL,
    NULL,
    '/api/receivable/plan',
    @receivable_management_id,
    0,  -- 不是父节点
    0,  -- 不展开
    1,  -- 可见
    NOW(),
    NOW()
);

-- 4.5.2 应收账录入（二级菜单）
SET @receivable_entry_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @receivable_entry_id,
    '应收账录入',
    'receivable_entry',
    2,
    4520,
    '/receivable/entry',
    NULL,
    NULL,
    '/api/receivable',
    @receivable_management_id,
    0,  -- 不是父节点
    0,  -- 不展开
    1,  -- 可见
    NOW(),
    NOW()
);

-- 4.5.3 应收账查询（二级菜单）
SET @receivable_query_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @receivable_query_id,
    '应收账查询',
    'receivable_query',
    2,
    4530,
    '/receivable/query',
    NULL,
    NULL,
    '/api/receivable',
    @receivable_management_id,
    0,  -- 不是父节点
    0,  -- 不展开
    1,  -- 可见
    NOW(),
    NOW()
);


-- 权限管理平台 - 模块初始化数据脚本
-- 使用方法：mysql -u root -p rootmanage < init_modules.sql
-- 注意：字段名根据 JPA 命名策略，驼峰命名会转换为下划线命名

USE rootmanage;

-- ============================================
-- 创建系统功能模块树
-- ============================================

-- 清空现有模块数据（防止重复插入）
-- 由于 module 表有自引用外键，需要先禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM role_module_permission;
DELETE FROM module;

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 1. 首页（一级菜单）
SET @home_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @home_id,
    '首页',
    'home',
    1,
    1000,
    '/home',
    'home-icon',
    NULL,
    '/home',
    NULL,
    0,  -- 不是父节点
    0,  -- 不展开
    1,  -- 可见
    NOW(),
    NOW()
);

-- 2. 组织架构（一级菜单，父节点）
SET @org_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @org_id,
    '组织架构',
    'organization',
    1,
    2000,
    NULL,
    'organization-icon',
    NULL,
    '/organization',
    NULL,
    1,  -- 是父节点
    1,  -- 展开
    1,  -- 可见
    NOW(),
    NOW()
);

-- 2.1 集团公司（二级菜单）
SET @group_company_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @group_company_id,
    '集团公司',
    'group_company',
    2,
    2010,
    '/organization/group-company',
    NULL,
    NULL,
    '/organization/group-company',
    @org_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

-- 2.2 职能中心（二级菜单）
SET @functional_center_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @functional_center_id,
    '职能中心',
    'functional_center',
    2,
    2020,
    '/organization/functional-center',
    NULL,
    NULL,
    '/organization/functional-center',
    @org_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

-- 2.3 部门（二级菜单）
SET @department_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @department_id,
    '部门',
    'department',
    2,
    2030,
    '/organization/department',
    NULL,
    NULL,
    '/organization/department',
    @org_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

-- 2.4 项目部（二级菜单）
SET @project_dept_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @project_dept_id,
    '项目部',
    'project_department',
    2,
    2040,
    '/organization/project-department',
    NULL,
    NULL,
    '/organization/project-department',
    @org_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

-- 2.5 项目（二级菜单）
SET @project_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @project_id,
    '项目',
    'project',
    2,
    2050,
    '/organization/project',
    NULL,
    NULL,
    '/organization/project',
    @org_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

-- 3. 人员管理（一级菜单，父节点）
SET @personnel_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @personnel_id,
    '人员管理',
    'personnel',
    1,
    3000,
    NULL,
    'personnel-icon',
    NULL,
    '/personnel',
    NULL,
    1,  -- 是父节点
    1,  -- 展开
    1,  -- 可见
    NOW(),
    NOW()
);

-- 3.1 人员档案（二级菜单）
SET @personnel_archive_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @personnel_archive_id,
    '人员档案',
    'personnel_archive',
    2,
    3010,
    '/personnel/archive',
    NULL,
    NULL,
    '/personnel/archive',
    @personnel_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

-- 3.2 账号管理（二级菜单）
SET @account_management_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @account_management_id,
    '账号管理',
    'account_management',
    2,
    3020,
    '/accounts',
    NULL,
    NULL,
    '/accounts',
    @personnel_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

-- 3.3 我的信息（二级菜单）
SET @my_info_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @my_info_id,
    '我的信息',
    'my_info',
    2,
    3030,
    '/personnel/my-info',
    NULL,
    NULL,
    '/personnel/my-info',
    @personnel_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

-- 4. 系统管理（一级菜单，父节点，折叠状态）
SET @system_management_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @system_management_id,
    '系统管理',
    'system_management',
    1,
    4000,
    NULL,
    'system-icon',
    NULL,
    '/system',
    NULL,
    1,  -- 是父节点
    0,  -- 不展开（折叠）
    1,  -- 可见
    NOW(),
    NOW()
);

-- 4.1 角色管理（二级菜单）
SET @role_management_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @role_management_id,
    '角色管理',
    'role_management',
    2,
    4010,
    '/roles',
    NULL,
    NULL,
    '/roles',
    @system_management_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

-- 4.2 模块管理（二级菜单）
SET @module_management_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @module_management_id,
    '模块管理',
    'module_management',
    2,
    4020,
    '/modules',
    'anticon-menu',
    NULL,
    '/modules',
    @system_management_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

-- 4.3 角色配置（二级菜单，对应权限配置）
SET @permission_config_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @permission_config_id,
    '角色配置',
    'permission_config',
    2,
    4030,
    '/permissions',
    NULL,
    NULL,
    '/permissions',
    @system_management_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

-- 4.4 日志管理（二级菜单）
SET @log_management_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @log_management_id,
    '日志管理',
    'log_management',
    2,
    4040,
    '/system/logs',
    NULL,
    NULL,
    '/system/logs',
    @system_management_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

-- 4.5 选项管理（二级菜单）
SET @option_management_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @option_management_id,
    '选项管理',
    'option_management',
    2,
    4050,
    '/options',
    NULL,
    NULL,
    '/options',
    @system_management_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

-- 5. 商城管理（一级菜单，父节点，折叠状态）
SET @mall_management_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @mall_management_id,
    '商城管理',
    'mall_management',
    1,
    5000,
    NULL,
    'mall-icon',
    NULL,
    '/mall',
    NULL,
    1,  -- 是父节点
    0,  -- 不展开（折叠）
    1,  -- 可见
    NOW(),
    NOW()
);

-- 5.1 广告设置（二级菜单）
SET @ad_settings_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @ad_settings_id,
    '广告设置',
    'ad_settings',
    2,
    5010,
    '/mall/ad-settings',
    NULL,
    NULL,
    '/mall/ad-settings',
    @mall_management_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

-- 5.2 商场类型（二级菜单）
SET @mall_type_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @mall_type_id,
    '商场类型',
    'mall_type',
    2,
    5020,
    '/mall/mall-type',
    NULL,
    NULL,
    '/mall/mall-type',
    @mall_management_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

-- 5.3 商店管理（二级菜单）
SET @store_management_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @store_management_id,
    '商店管理',
    'store_management',
    2,
    5030,
    '/mall/stores',
    NULL,
    NULL,
    '/mall/stores',
    @mall_management_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

-- 5.4 商品管理（二级菜单）
SET @product_management_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @product_management_id,
    '商品管理',
    'product_management',
    2,
    5040,
    '/mall/products',
    NULL,
    NULL,
    '/mall/products',
    @mall_management_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

-- 5.5 订单管理（二级菜单）
SET @order_management_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO module (
    id, zh_name, en_name, `level`, order_no, path, icon, group_code, permission_key, 
    parent_id, parent_node, expanded, visible, create_time, update_time
) VALUES (
    @order_management_id,
    '订单管理',
    'order_management',
    2,
    5050,
    '/mall/orders',
    NULL,
    NULL,
    '/mall/orders',
    @mall_management_id,
    0,
    0,
    1,
    NOW(),
    NOW()
);

-- ============================================
-- 验证数据
-- ============================================
SELECT '模块数据：' AS '验证';
SELECT 
    m1.zh_name AS '一级菜单',
    m2.zh_name AS '二级菜单',
    m1.level,
    m1.order_no,
    m1.parent_node,
    m1.expanded
FROM module m1
LEFT JOIN module m2 ON m2.parent_id = m1.id
WHERE m1.parent_id IS NULL
ORDER BY m1.order_no, m2.order_no;

SELECT '模块初始化完成！' AS '状态';


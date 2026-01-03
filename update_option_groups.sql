-- 更新选项数据的分组信息
-- 使用方法：mysql -u root -p rootmanage < update_option_groups.sql

USE rootmanage;

-- ============================================
-- 更新岗位选项的分组信息 (根据值判断)
-- ============================================
UPDATE option_item 
SET group_code = 'position', update_time = NOW()
WHERE value IN ('PROJECT_MANAGER', 'DEVELOPER', 'TESTER', 'PRODUCT_MANAGER', 'UI_DESIGNER', 'DEVOPS')
  AND (group_code IS NULL OR group_code = '');

-- ============================================
-- 更新片区选项的分组信息 (根据值判断)
-- ============================================
UPDATE option_item 
SET group_code = 'region', update_time = NOW()
WHERE value IN ('EAST', 'SOUTH', 'NORTH', 'WEST', 'CENTRAL')
  AND (group_code IS NULL OR group_code = '');

-- ============================================
-- 如果数据使用的是大写分组代码，统一转换为小写
-- ============================================
UPDATE option_item 
SET group_code = LOWER(group_code)
WHERE group_code IN ('POSITION', 'AREA');

-- 将 POSITION 映射为 position
UPDATE option_item 
SET group_code = 'position', update_time = NOW()
WHERE group_code = 'POSITION';

-- 将 AREA 映射为 region（因为前端使用的是 region）
UPDATE option_item 
SET group_code = 'region', update_time = NOW()
WHERE group_code = 'AREA';

-- ============================================
-- 验证更新结果
-- ============================================
SELECT '岗位选项：' AS '验证';
SELECT id, group_code, title, value, order_no 
FROM option_item 
WHERE group_code = 'position'
ORDER BY order_no;

SELECT '片区选项：' AS '验证';
SELECT id, group_code, title, value, order_no 
FROM option_item 
WHERE group_code = 'region'
ORDER BY order_no;

SELECT '未分组的数据：' AS '验证';
SELECT id, group_code, title, value 
FROM option_item 
WHERE group_code IS NULL OR group_code = '';

SELECT '更新完成！' AS '状态';


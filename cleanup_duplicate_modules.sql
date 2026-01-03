-- 清理重复的模块数据
-- 使用方法：mysql -u root -p rootmanage < cleanup_duplicate_modules.sql

USE rootmanage;

-- 删除所有模块数据（包括关联的权限数据）
-- 由于 module 表有自引用外键，需要先禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM role_module_permission;
DELETE FROM module;

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 验证清理结果
SELECT COUNT(*) AS '剩余模块数量' FROM module;
SELECT '清理完成！请重新执行 init_modules.sql' AS '状态';


@echo off
chcp 65001 >nul 2>&1
cd /d "%~dp0"

echo ========================================
echo    诊断启动问题 - 查看详细错误信息
echo ========================================
echo.
echo 这将直接运行 Java 命令并显示所有错误信息...
echo.

java -jar target\rootmanage-1.0-SNAPSHOT.jar

echo.
echo ========================================
echo    程序已退出
echo ========================================
echo.
echo 请查看上面的错误信息，常见问题：
echo   1. 数据库连接失败 - 检查 MySQL 服务是否启动
echo   2. 数据库不存在 - 需要先创建 rootmanage 数据库
echo   3. 用户名密码错误 - 检查 application.yml 配置
echo   4. 表结构问题 - 检查数据库表是否正确创建
echo.
pause


@echo off
chcp 65001 >nul
echo ========================================
echo    权限管理平台后端服务启动脚本
echo ========================================
echo.

REM 检查 JAR 文件是否存在
if not exist "target\rootmanage-1.0-SNAPSHOT.jar" (
    echo [错误] 找不到 JAR 文件: target\rootmanage-1.0-SNAPSHOT.jar
    echo.
    echo 请先执行打包命令:
    echo   mvn clean package -DskipTests
    echo.
    pause
    exit /b 1
)

REM 检查端口是否被占用
netstat -ano | findstr ":8080" | findstr "LISTENING" >nul
if %errorlevel% == 0 (
    echo [警告] 端口 8080 已被占用！
    echo.
    echo 请先停止占用端口的进程，或修改 application.yml 中的端口配置。
    echo.
    pause
    exit /b 1
)

echo [信息] 正在启动应用...
echo [信息] JAR 文件: target\rootmanage-1.0-SNAPSHOT.jar
echo [信息] 服务地址: http://localhost:8080
echo.
echo 提示: 按 Ctrl+C 可以停止应用
echo ========================================
echo.

REM 启动应用
java -jar target\rootmanage-1.0-SNAPSHOT.jar

REM 如果应用异常退出，显示错误信息
if %errorlevel% neq 0 (
    echo.
    echo [错误] 应用启动失败，退出代码: %errorlevel%
    echo.
    echo 可能的原因:
    echo   1. Java 环境未正确配置
    echo   2. 数据库连接失败
    echo   3. 端口被占用
    echo   4. 配置文件错误
    echo.
)

pause


@echo off
chcp 65001 >nul 2>&1
cd /d "%~dp0"

echo ========================================
echo    权限管理平台后端服务启动脚本
echo ========================================
echo.

REM 检查 JAR 文件是否存在
if not exist "target\rootmanage-1.0-SNAPSHOT.jar" (
    echo [ERROR] JAR file not found: target\rootmanage-1.0-SNAPSHOT.jar
    echo.
    echo Please run the build command first:
    echo   mvn clean package -DskipTests
    echo.
    pause
    exit /b 1
)

REM 检查端口是否被占用
netstat -ano 2>nul | findstr ":8080" 2>nul | findstr "LISTENING" >nul 2>&1
if %errorlevel% == 0 (
    echo [WARNING] Port 8080 is already in use!
    echo.
    echo Please stop the process using the port, or modify the port in application.yml
    echo.
    pause
    exit /b 1
)

echo [INFO] Starting application...
echo [INFO] JAR file: target\rootmanage-1.0-SNAPSHOT.jar
echo [INFO] Service URL: http://localhost:8080
echo.
echo Press Ctrl+C to stop the application
echo ========================================
echo.

REM 启动应用
java -jar target\rootmanage-1.0-SNAPSHOT.jar

REM 保存退出代码
set EXIT_CODE=%errorlevel%

REM 如果应用异常退出，显示错误信息
if %EXIT_CODE% neq 0 (
    echo.
    echo [ERROR] Application startup failed, exit code: %EXIT_CODE%
    echo.
    echo Possible reasons:
    echo   1. Java environment not configured correctly
    echo   2. Database connection failed
    echo   3. Port is in use
    echo   4. Configuration file error
    echo.
    echo Please check the error messages above for details.
    echo.
)

pause
exit /b %EXIT_CODE%

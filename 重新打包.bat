@echo off
chcp 65001 >nul
echo ========================================
echo   重新打包 JAR 文件
echo ========================================
echo.

REM 获取当前脚本所在目录
cd /d "%~dp0"

echo [信息] 正在停止可能正在运行的 Java 进程...
taskkill /F /IM java.exe >nul 2>&1
if %errorlevel% == 0 (
    echo [信息] Java 进程已停止
) else (
    echo [信息] 没有发现正在运行的 Java 进程
)
echo.

echo [信息] 等待进程完全退出...
timeout /t 2 /nobreak >nul
echo.

echo [信息] 开始重新打包项目...
echo.
mvn clean package -DskipTests

if %errorlevel% == 0 (
    echo.
    echo ========================================
    echo   打包成功！
    echo ========================================
    echo.
    echo JAR 文件位置: target\rootmanage-1.0-SNAPSHOT.jar
    echo.
) else (
    echo.
    echo ========================================
    echo   打包失败！
    echo ========================================
    echo.
    echo 如果仍然提示文件被占用，请：
    echo 1. 关闭所有 Java 应用程序
    echo 2. 关闭 IDE（如果打开了项目）
    echo 3. 重新运行此脚本image.png
    echo.
)

pause


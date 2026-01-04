@echo off
chcp 65001 >nul
echo ========================================
echo    运行价格本和应收账管理模块测试
echo ========================================
echo.

cd /d "%~dp0"

echo [信息] 正在编译测试代码...
call mvn test-compile
if %errorlevel% neq 0 (
    echo.
    echo [错误] 测试代码编译失败！
    echo.
    pause
    exit /b 1
)

echo.
echo [信息] 正在运行测试...
echo.

echo 选择要运行的测试：
echo   1. 运行所有测试
echo   2. 仅运行价格本管理模块测试
echo   3. 仅运行应收账管理模块测试
echo   4. 运行价格本和应收账模块测试
echo   5. 退出
echo.
set /p choice="请输入选项 (1-5): "

if "%choice%"=="1" (
    echo.
    echo [信息] 运行所有测试...
    call mvn test
) else if "%choice%"=="2" (
    echo.
    echo [信息] 运行价格本管理模块测试...
    call mvn test -Dtest=PriceBookControllerTest
) else if "%choice%"=="3" (
    echo.
    echo [信息] 运行应收账管理模块测试...
    call mvn test -Dtest=AccountsReceivableControllerTest
) else if "%choice%"=="4" (
    echo.
    echo [信息] 运行价格本和应收账模块测试...
    call mvn test -Dtest=PriceBookControllerTest,AccountsReceivableControllerTest
) else if "%choice%"=="5" (
    exit /b 0
) else (
    echo.
    echo [错误] 无效的选项！
    echo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo   测试完成！
echo ========================================
echo.
pause


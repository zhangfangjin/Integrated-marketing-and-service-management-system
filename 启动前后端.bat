@echo off
chcp 65001 >nul
echo ========================================
echo   权限管理平台 - 前后端一键启动
echo ========================================
echo.

REM 获取当前脚本所在目录
cd /d "%~dp0"

REM 检查后端端口是否被占用
netstat -ano | findstr ":8080" | findstr "LISTENING" >nul
if %errorlevel% == 0 (
    echo [警告] 后端端口 8080 已被占用！
    echo.
    echo 请先停止占用端口的进程，或修改 application.yml 中的端口配置。
    echo.
    pause
    exit /b 1
)

REM 检查前端端口是否被占用
netstat -ano | findstr ":3000" | findstr "LISTENING" >nul
if %errorlevel% == 0 (
    echo [警告] 前端端口 3000 已被占用！
    echo.
    echo 请先停止占用端口的进程，或修改 vite.config.js 中的端口配置。
    echo.
    pause
    exit /b 1
)

echo [信息] 正在启动后端服务（使用 Maven）...
echo [信息] 后端地址: http://localhost:8080
echo.

REM 在新窗口中启动后端（使用 Maven）
start "权限管理平台-后端服务" cmd /k "mvn spring-boot:run"

REM 等待后端启动（给后端一些时间启动）
echo [信息] 等待后端服务启动...
timeout /t 5 /nobreak >nul

echo.
echo [信息] 正在启动前端服务...
echo [信息] 前端地址: http://localhost:3000
echo.

REM 检查前端目录是否存在
if not exist "frontend" (
    echo [错误] 找不到前端目录: frontend
    echo.
    pause
    exit /b 1
)

REM 进入前端目录并检查 node_modules
cd frontend
if not exist "node_modules" (
    echo [提示] 首次运行，正在安装前端依赖，请稍候...
    echo.
    call npm install
    if %errorlevel% neq 0 (
        echo.
        echo [错误] 前端依赖安装失败！
        echo 提示: 可以尝试使用国内镜像: npm install --registry=https://registry.npmmirror.com
        echo.
        pause
        exit /b 1
    )
)

REM 在新窗口中启动前端
start "权限管理平台-前端服务" cmd /k "npm run dev"

cd ..

echo.
echo ========================================
echo   启动完成！
echo ========================================
echo.
echo [后端] http://localhost:8080
echo [前端] http://localhost:3000
echo.
echo 提示: 
echo   - 两个服务窗口已打开，请勿关闭
echo   - 关闭窗口将停止对应的服务
echo   - 在服务窗口按 Ctrl+C 可以停止服务
echo.
pause


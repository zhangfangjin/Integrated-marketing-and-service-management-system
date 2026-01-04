@echo off
chcp 65001 >nul 2>&1
cd /d "%~dp0"

echo ========================================
echo    检查启动问题
echo ========================================
echo.

echo [1] Checking JAR file...
if exist "target\rootmanage-1.0-SNAPSHOT.jar" (
    echo     [OK] JAR file exists
    dir "target\rootmanage-1.0-SNAPSHOT.jar" | findstr "rootmanage"
) else (
    echo     [ERROR] JAR file NOT found!
    echo     Please run: mvn clean package -DskipTests
    echo.
    pause
    exit /b 1
)
echo.

echo [2] Checking Java version...
java -version 2>&1 | findstr "version"
if %errorlevel% neq 0 (
    echo     [ERROR] Java not found or not in PATH!
    echo.
    pause
    exit /b 1
) else (
    echo     [OK] Java is available
)
echo.

echo [3] Checking port 8080...
netstat -ano 2>nul | findstr ":8080" 2>nul | findstr "LISTENING" >nul 2>&1
if %errorlevel% == 0 (
    echo     [WARNING] Port 8080 is in use!
    echo     Processes using port 8080:
    netstat -ano | findstr ":8080" | findstr "LISTENING"
    echo.
) else (
    echo     [OK] Port 8080 is available
)
echo.

echo [4] Checking database configuration...
echo     Database URL: jdbc:mysql://localhost:3306/rootmanage
echo     Please verify:
echo     - MySQL service is running
echo     - Database 'rootmanage' exists
echo     - Username and password in application.yml are correct
echo.

echo ========================================
echo     Trying to start application...
echo ========================================
echo.
echo This will show detailed error messages...
echo Press Ctrl+C to stop
echo.

java -jar target\rootmanage-1.0-SNAPSHOT.jar

echo.
echo ========================================
echo     Application stopped
echo ========================================
pause


@echo off
echo =====================================================
echo   Smart Perishable Monitoring System - Launcher
echo =====================================================
echo.

:: Check Java
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java is not installed or not in PATH!
    echo Please install Java 17+ from https://adoptium.net
    pause
    exit /b 1
)

:: Check Maven
where mvn >nul 2>&1
if errorlevel 1 (
    echo [INFO] Maven not found. Using Maven Wrapper...
    set MVN_CMD=mvnw.cmd
) else (
    set MVN_CMD=mvn
)

echo.
echo [1/3] Building Spring Boot Backend...
echo =====================================================
cd /d "%~dp0backend"

:: Check if Maven wrapper exists, if not use mvn directly
if exist "mvnw.cmd" (
    set MVN_CMD=mvnw.cmd
) else (
    set MVN_CMD=mvn
)

echo Using: %MVN_CMD%
call %MVN_CMD% clean package -DskipTests

if errorlevel 1 (
    echo.
    echo [ERROR] Build failed! Check if MySQL is running and credentials are correct.
    echo MySQL Config in: backend\src\main\resources\application.properties
    pause
    exit /b 1
)

echo.
echo [2/3] Starting Spring Boot Server on port 8080...
echo =====================================================
start /b java -jar target\smart-monitoring-1.0.0.jar

echo.
echo [3/3] Backend is starting up...
echo =====================================================
echo.
timeout /t 8 /nobreak >nul

echo.
echo =====================================================
echo   System is READY!
echo =====================================================
echo.
echo   Backend API:   http://localhost:8080
echo   Frontend:      Open frontend\login.html in browser
echo.
echo   Demo Accounts:
echo     Admin:  admin / admin123
echo     Staff:  staff1 / staff123
echo.
echo   Press Ctrl+C to stop the server.
echo =====================================================

:: Open frontend in browser
start "" "%~dp0frontend\login.html"

:: Keep window open
pause

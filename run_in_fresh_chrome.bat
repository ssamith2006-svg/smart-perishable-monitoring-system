@echo off
echo =====================================================
echo   Smart Perishable Monitoring System - Fresh Chrome
echo =====================================================
echo.
echo Starting Backend...
cd /d "%~dp0backend"

:: Start backend in background
start /b mvn clean package -DskipTests
timeout /t 5 /nobreak >nul
start /b java -jar target\smart-monitoring-1.0.0.jar

echo.
echo Waiting for backend to initialize...
timeout /t 8 /nobreak >nul

echo.
echo Launching Frontend in a Fresh Chrome Window (Incognito)...
echo =====================================================
:: Open in Chrome Incognito so it doesn't use your default profile cache
start chrome --incognito "%~dp0frontend\login.html"

echo.
echo System is running! Keep this window open.
echo Press Ctrl+C to stop the server.
pause

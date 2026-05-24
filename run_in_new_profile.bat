@echo off
echo =====================================================
echo   Smart Perishable Monitoring System - Demo Profile
echo =====================================================
echo.
echo Launching Frontend in a completely separate Chrome Profile...
echo This ensures no conflicts with your personal bookmarks or history.
echo.

:: Create a temp directory for the demo profile
set "DEMO_PROFILE=%temp%\smart_perishable_profile"
if not exist "%DEMO_PROFILE%" mkdir "%DEMO_PROFILE%"

:: Launch Chrome with the separate profile
start chrome --user-data-dir="%DEMO_PROFILE%" --no-first-run --no-default-browser-check "%~dp0frontend\login.html"

echo.
echo Chrome has been launched with a fresh profile.
echo You can now proceed with your viva presentation.
echo.
pause

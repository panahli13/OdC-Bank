@echo off
cd /d "%~dp0"
echo Starting OdC Bank Backend...
echo.
call mvnw.cmd spring-boot:run
echo.
echo ========================================
echo Backend stopped. Check errors above.
echo ========================================
pause

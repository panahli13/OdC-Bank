@echo off
set PGPASSWORD=1234
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -c "SELECT 1" -w
if %ERRORLEVEL% EQU 0 (
    echo SUCCESS with password: 1234
    goto :end
)

set PGPASSWORD=postgres
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -c "SELECT 1" -w
if %ERRORLEVEL% EQU 0 (
    echo SUCCESS with password: postgres
    goto :end
)

set PGPASSWORD=admin
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -c "SELECT 1" -w
if %ERRORLEVEL% EQU 0 (
    echo SUCCESS with password: admin
    goto :end
)

set PGPASSWORD=password
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -c "SELECT 1" -w
if %ERRORLEVEL% EQU 0 (
    echo SUCCESS with password: password
    goto :end
)

set PGPASSWORD=root
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -c "SELECT 1" -w
if %ERRORLEVEL% EQU 0 (
    echo SUCCESS with password: root
    goto :end
)

echo FAILED - none of the common passwords worked
echo Please check your PostgreSQL password

:end
pause

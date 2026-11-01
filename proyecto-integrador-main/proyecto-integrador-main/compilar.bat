@echo off
chcp 65001 >nul
echo ===================================================
echo   COMPILADOR - SISTEMA SALÓN DE BELLEZA AREMI
echo ===================================================
echo.

if not exist "out" mkdir out

echo [*] Compilando clases Java con codificación UTF-8...
javac -encoding UTF-8 -d out -cp "lib/*;out" src/*.java

if %errorlevel% neq 0 (
    echo.
    echo [X] ERROR: Hubo un fallo durante la compilación.
    echo.
    pause
    exit /b 1
)

echo.
echo ===================================================
echo   [OK] COMPILACIÓN EXITOSA DE TODOS LOS MÓDULOS
echo ===================================================
echo.
echo Para iniciar la aplicación, ejecute: ejecutar.bat
echo.
pause

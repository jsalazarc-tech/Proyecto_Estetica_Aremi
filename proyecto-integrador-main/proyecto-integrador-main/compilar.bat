@echo off
echo ===================================================
echo   COMPILADOR - SALON DE BELLEZA AREMI (MVC + DAO)
echo ===================================================
echo.
if not exist "out" mkdir out
echo [*] Escaneando clases del sistema MVC...
dir /s /b src\*.java > sources.txt
echo [*] Compilando paquetes model, dao, controller, util y view...
javac -encoding UTF-8 -d out -cp "lib/*;out" @sources.txt
if errorlevel 1 (
    echo [X] Error durante la compilacion.
    del sources.txt 2>nul
    pause
    exit /b 1
)
del sources.txt 2>nul
echo [*] Copiando recursos graficos...
copy /y src\*.png out\ >nul 2>nul
copy /y src\*.jpg out\ >nul 2>nul
echo.
echo ===================================================
echo   [OK] Compilacion Exitosa: Arquitectura MVC lista
echo ===================================================
pause

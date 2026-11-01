@echo off
echo ===================================================
echo   COMPILADOR - SALON DE BELLEZA AREMI
echo ===================================================
echo.
if not exist "out" mkdir out
echo [*] Compilando clases Java...
javac -encoding UTF-8 -d out -cp "lib/*;out" src/*.java
if errorlevel 1 (
    echo [X] Error durante la compilacion.
    pause
    exit /b 1
)
echo [OK] Compilacion exitosa.
pause
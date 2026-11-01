@echo off
chcp 65001 >nul
echo ===================================================
echo   SISTEMA DE GESTIÓN - SALÓN DE BELLEZA AREMI
echo ===================================================
echo.

if not exist "out\MainApplication.class" (
    echo [*] Compilando proyecto por primera vez...
    javac -encoding UTF-8 -d out -cp "lib/*;out" src/*.java
    if %errorlevel% neq 0 (
        echo [X] Error compilando el proyecto.
        pause
        exit /b 1
    )
)

echo [*] Iniciando aplicación AREMI...
REM -Xms32m -Xmx256m -XX:+UseSerialGC para optimizar consumo de RAM y evitar errores nativos de memoria
java -Xms32m -Xmx256m -XX:+UseSerialGC -cp "out;lib/*" MainApplication

if %errorlevel% neq 0 (
    echo.
    echo [!] La aplicación se cerró con código %errorlevel%.
    echo Verifique que el servicio de MySQL esté iniciado.
    pause
)

@echo off
echo ========================================
echo   SISTEMA AREMI v2.0
echo   Salon de Belleza - ERP
echo ========================================
echo.
echo Iniciando aplicacion...
echo.

REM Verificar que existan los archivos compilados
if not exist "out\MainApplication.class" (
    echo ERROR: El proyecto no esta compilado.
    echo Por favor ejecute compilar.bat primero.
    echo.
    pause
    exit /b 1
)

REM Ejecutar la aplicacion
java -cp "out;lib/*" MainApplication

if %errorlevel% neq 0 (
    echo.
    echo ERROR: La aplicacion termino con errores.
    echo Verifique:
    echo   1. La base de datos MySQL esta corriendo
    echo   2. Las credenciales de DB son correctas
    echo   3. Las tablas necesarias existen
    echo.
    pause
    exit /b 1
)

echo.
echo Aplicacion cerrada correctamente.
pause
@echo off
echo ========================================
echo   SISTEMA AREMI v2.0 - PRUEBAS
echo ========================================
echo.

REM Verificar que existan los archivos compilados
if not exist "out\TestInventario.class" (
    echo ERROR: Las pruebas no estan compiladas.
    echo Por favor ejecute compilar.bat primero.
    echo.
    pause
    exit /b 1
)

echo Ejecutando pruebas de validacion...
echo.

echo ========================================
echo   PRUEBAS DE MODULO INVENTARIO
echo ========================================
java -cp "out;lib/*" TestInventario

echo.
echo.

echo ========================================
echo   PRUEBAS DE MODULO SEGURIDAD
echo ========================================
java -cp "out;lib/*" TestSeguridad

echo.
echo.
echo ========================================
echo   PRUEBAS FINALIZADAS
echo ========================================
echo.
pause
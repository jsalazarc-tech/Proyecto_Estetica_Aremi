@echo off
echo ========================================
echo   COMPILADOR DEL SISTEMA AREMI v2.0
echo ========================================
echo.

REM Crear directorio out si no existe
if not exist "out" mkdir out

echo [1/3] Compilando modelos y utilidades...
javac -encoding UTF-8 -d out -cp "lib/*" src/ItemInventario.java src/SeguridadManager.java src/Cliente.java src/Usuario.java src/Servicio.java src/Gasto.java src/DatabaseConnection.java src/Facturas.java

if %errorlevel% neq 0 (
    echo ERROR: Fallo en compilacion de modelos
    pause
    exit /b 1
)

echo [2/3] Compilando interfaces graficas...
javac -encoding UTF-8 -d out -cp "lib/*;out" src/InventarioGUI.java src/NominaGUI.java src/LoginSystemAremi.java src/AgendarCitaGUI.java src/InterfazClientes.java src/SalonBelleza.java src/Pago.java src/PagoDetailsDialog.java src/Pedido.java

if %errorlevel% neq 0 (
    echo ERROR: Fallo en compilacion de GUIs
    pause
    exit /b 1
)

echo [3/4] Compilando aplicacion principal...
javac -encoding UTF-8 -d out -cp "lib/*;out" src/MainApplication.java

if %errorlevel% neq 0 (
    echo ERROR: Fallo en compilacion de MainApplication
    pause
    exit /b 1
)

echo [4/4] Compilando pruebas de validacion...
javac -encoding UTF-8 -d out -cp "lib/*;out" src/TestInventario.java src/TestSeguridad.java

if %errorlevel% neq 0 (
    echo ERROR: Fallo en compilacion de pruebas
    pause
    exit /b 1
)

echo.
echo ========================================
echo   COMPILACION EXITOSA!
echo ========================================
echo.
echo Para ejecutar la aplicacion: ejecutar.bat
echo Para ejecutar pruebas: pruebas.bat
echo.
pause
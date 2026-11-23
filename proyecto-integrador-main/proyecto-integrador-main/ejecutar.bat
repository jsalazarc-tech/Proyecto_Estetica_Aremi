@echo off
echo ===================================================
echo   SALON DE BELLEZA AREMI - Iniciando Sistema...
echo ===================================================
echo.
java -cp "out;lib/*" view.MainApplication
if errorlevel 1 (
    echo.
    echo [X] Error ejecutando la aplicacion. Verifique que Java este instalado.
    pause
)

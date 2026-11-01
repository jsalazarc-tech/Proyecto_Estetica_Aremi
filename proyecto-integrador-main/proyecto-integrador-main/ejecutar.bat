@echo off
echo ===================================================
echo   SALON DE BELLEZA AREMI - Iniciando...
echo ===================================================
echo.
java -Xms16m -Xmx96m -XX:+UseSerialGC -XX:MaxMetaspaceSize=64m -cp "out;lib/*" MainApplication
if errorlevel 1 (
    echo.
    echo [X] Error ejecutando la aplicacion.
    pause
)

@echo off
echo ===================================================
echo   SALON DE BELLEZA AREMI - Iniciando...
echo ===================================================
echo.
java -Xms16m -Xmx96m -XX:+UseSerialGC -XX:MaxMetaspaceSize=64m -cp "out;lib/*" LoginSystemAremi
if errorlevel 1 (
    echo [X] Error ejecutando el proyecto.
    pause
)

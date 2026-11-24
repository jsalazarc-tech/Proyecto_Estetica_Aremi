@echo off
setlocal

REM =========================================================================
REM  DETECCION AUTOMATICA DE JAVA (java.exe)
REM =========================================================================
java -version >nul 2>&1
if not errorlevel 1 goto :JAVA_LISTO

if exist "%USERPROFILE%\jdk-21\jdk-21.0.13.1+2\bin\java.exe" (
    set "PATH=%USERPROFILE%\jdk-21\jdk-21.0.13.1+2\bin;%PATH%"
    goto :JAVA_LISTO
)

if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "PATH=%JAVA_HOME%\bin;%PATH%"
        goto :JAVA_LISTO
    )
)

if exist "C:\Program Files\Java\jdk-21\bin\java.exe" (
    set "PATH=C:\Program Files\Java\jdk-21\bin;%PATH%"
    goto :JAVA_LISTO
)

if exist "C:\Program Files\Java\jdk-17\bin\java.exe" (
    set "PATH=C:\Program Files\Java\jdk-17\bin;%PATH%"
    goto :JAVA_LISTO
)

:JAVA_LISTO
echo ===================================================
echo   SALON DE BELLEZA AREMI - Iniciando Sistema...
echo ===================================================
echo.

java -version >nul 2>&1
if not errorlevel 1 goto :VERIFICAR_CLASES

echo [X] ERROR: No se encontro Java en el sistema.
echo Por favor instale Java JDK 21 para ejecutar la aplicacion.
echo.
pause
exit /b 1

:VERIFICAR_CLASES
REM Si los binarios compilados no existen, compilar automaticamente
if exist "out\view\MainApplication.class" goto :EJECUTAR_APP

echo [*] No se detectaron clases compiladas en la carpeta out.
echo [*] Compilando el proyecto automaticamente por primera vez...
echo.
call compilar.bat --nopause
if errorlevel 1 goto :ERROR_AUTOCOMPILAR
echo.

:EJECUTAR_APP
REM Ejecutar aplicacion principal MVC
java -cp "out;lib/*" view.MainApplication
if errorlevel 1 goto :ERROR_EJECUCION
goto :FIN

:ERROR_AUTOCOMPILAR
echo [X] La auto-compilacion fallo. Revise los errores anteriores.
pause
exit /b 1

:ERROR_EJECUCION
echo.
echo [X] La aplicacion finalizo con un error.
pause
exit /b 1

:FIN
endlocal

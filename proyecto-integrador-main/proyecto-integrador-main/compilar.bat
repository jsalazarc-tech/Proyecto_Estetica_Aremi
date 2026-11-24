@echo off
setlocal

REM =========================================================================
REM  DETECCION AUTOMATICA DE JAVA JDK (javac)
REM =========================================================================
javac -version >nul 2>&1
if not errorlevel 1 goto :JAVAC_LISTO

if exist "%USERPROFILE%\jdk-21\jdk-21.0.13.1+2\bin\javac.exe" (
    set "PATH=%USERPROFILE%\jdk-21\jdk-21.0.13.1+2\bin;%PATH%"
    goto :JAVAC_LISTO
)

if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javac.exe" (
        set "PATH=%JAVA_HOME%\bin;%PATH%"
        goto :JAVAC_LISTO
    )
)

if exist "C:\Program Files\Java\jdk-21\bin\javac.exe" (
    set "PATH=C:\Program Files\Java\jdk-21\bin;%PATH%"
    goto :JAVAC_LISTO
)

if exist "C:\Program Files\Java\jdk-17\bin\javac.exe" (
    set "PATH=C:\Program Files\Java\jdk-17\bin;%PATH%"
    goto :JAVAC_LISTO
)

:JAVAC_LISTO
echo ===================================================
echo   COMPILADOR - SALON DE BELLEZA AREMI - MVC + DAO
echo ===================================================
echo.

javac -version >nul 2>&1
if not errorlevel 1 goto :INICIAR_COMPILACION

echo [X] ERROR: No se encontro el compilador de Java javac.
echo Asegurese de tener instalado Java JDK version 17 o 21.
echo.
pause
exit /b 1

:INICIAR_COMPILACION
if not exist "out" mkdir out

echo [*] Escaneando clases del sistema MVC...
dir /s /b src\*.java > sources.txt

echo [*] Compilando paquetes model, dao, controller, util y view...
javac -encoding UTF-8 -d out -cp "lib/*;out" @sources.txt
if errorlevel 1 goto :ERROR_COMPILACION

if exist sources.txt del sources.txt

echo [*] Copiando recursos graficos e iconos...
copy /y src\*.png out\ >nul 2>nul
copy /y src\*.jpg out\ >nul 2>nul

echo.
echo ===================================================
echo   [OK] Compilacion Exitosa: Arquitectura MVC lista
echo ===================================================
echo.

if "%1"=="--nopause" goto :SALIR
pause
goto :SALIR

:ERROR_COMPILACION
echo.
echo [X] Error durante la compilacion de los archivos Java.
if exist sources.txt del sources.txt
pause
exit /b 1

:SALIR
endlocal

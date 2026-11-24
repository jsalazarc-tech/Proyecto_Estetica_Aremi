@echo off
setlocal
cd /d "%~dp0proyecto-integrador-main\proyecto-integrador-main"
call compilar.bat %*
endlocal

@echo off
setlocal ENABLEDELAYEDEXPANSION
cd /d "%~dp0"

set "LOG=%~dp0portable_start.log"
echo ================================================== > "%LOG%"
echo SIGPRA PORTABLE START %date% %time% >> "%LOG%"
echo Working dir: %cd% >> "%LOG%"

if not exist "PROYECTO_VISTAS_NETBEANS.jar" (
  echo [ERROR] No se encontro PROYECTO_VISTAS_NETBEANS.jar
  echo [ERROR] No se encontro PROYECTO_VISTAS_NETBEANS.jar >> "%LOG%"
  pause
  exit /b 1
)

where java >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Java no esta instalado o no esta en PATH.
  echo Instala Java 17 o superior y vuelve a intentar.
  echo [ERROR] Java no encontrado en PATH >> "%LOG%"
  pause
  exit /b 1
)

for /f "tokens=2 delims=\"" %%v in ('java -version 2^>^&1 ^| findstr /i "version"') do set "JV=%%v"
echo Java detectado: !JV!
echo Java detectado: !JV! >> "%LOG%"

echo Iniciando aplicacion... >> "%LOG%"
java -Dfile.encoding=UTF-8 -jar "PROYECTO_VISTAS_NETBEANS.jar" 1>>"%LOG%" 2>>&1
set "EC=%ERRORLEVEL%"

if not "%EC%"=="0" (
  echo [ERROR] La aplicacion termino con codigo %EC%.
  echo Revisa el archivo portable_start.log para el detalle.
  echo [ERROR] Exit code %EC% >> "%LOG%"
  pause
  exit /b %EC%
)

exit /b 0

@echo off
setlocal
cd /d "%~dp0"

set "DB_URL=jdbc:oracle:thin:@localhost:1521/XE"
set "DB_USER=practicas_app"
set "DB_PASSWORD=practicas2026"

if not exist "ojdbc11-21.9.0.0.jar" (
  echo Falta ojdbc11-21.9.0.0.jar en esta carpeta.
  pause
  exit /b 1
)

if not exist "PROYECTO_VISTAS_NETBEANS.jar" (
  echo Falta PROYECTO_VISTAS_NETBEANS.jar en esta carpeta.
  pause
  exit /b 1
)

echo Iniciando SIGPRA con BD real Oracle XE...
java -cp ".;PROYECTO_VISTAS_NETBEANS.jar;ojdbc11-21.9.0.0.jar" co.udi.integrador.ui.launchers.AbrirMenuVistas

endlocal

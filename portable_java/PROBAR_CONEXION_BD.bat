@echo off
setlocal
set ORACLE_HOME=C:\oraclexe\app\oracle\product\10.2.0\server
set PATH=%ORACLE_HOME%\bin;%PATH%
if not exist "%TEMP%\sigpra_probe.sql" (
  > "%TEMP%\sigpra_probe.sql" echo set heading off
  >> "%TEMP%\sigpra_probe.sql" echo set feedback off
  >> "%TEMP%\sigpra_probe.sql" echo select 'CONEXION_OK' from dual^
  >> "%TEMP%\sigpra_probe.sql" echo exit
)
if not exist "%~dp0tns_admin" mkdir "%~dp0tns_admin"
> "%~dp0tns_admin\sqlnet.ora" echo SQLNET.AUTHENTICATION_SERVICES=(NONE)
set TNS_ADMIN=%~dp0tns_admin
sqlplus -L practicas_app/practicas2026@XE @"%TEMP%\sigpra_probe.sql"
endlocal
pause

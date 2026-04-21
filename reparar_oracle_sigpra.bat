@echo off
setlocal

set ORACLE_HOME=C:\oraclexe\app\oracle\product\10.2.0\server
set PATH=%ORACLE_HOME%\bin;%PATH%

if "%1"=="" (
  echo Uso: reparar_oracle_sigpra.bat CLAVE_SYSTEM [XE]
  echo Ejemplo: reparar_oracle_sigpra.bat MiClaveSystem XE
  exit /b 1
)

set SYS_PASS=%1
set DB_SVC=%2
if "%DB_SVC%"=="" set DB_SVC=XE

if not exist "tns_admin" mkdir "tns_admin"
> "tns_admin\sqlnet.ora" echo SQLNET.AUTHENTICATION_SERVICES=(NONE)
set TNS_ADMIN=%CD%\tns_admin

(
  echo WHENEVER SQLERROR EXIT SQL.SQLCODE
  echo DECLARE n NUMBER; BEGIN
  echo   SELECT COUNT(*) INTO n FROM dba_users WHERE username='PRACTICAS_APP';
  echo   IF n=0 THEN EXECUTE IMMEDIATE 'CREATE USER PRACTICAS_APP IDENTIFIED BY Practicas2026'; END IF;
  echo END;
  echo /
  echo ALTER USER PRACTICAS_APP IDENTIFIED BY Practicas2026 ACCOUNT UNLOCK;
  echo GRANT CONNECT, RESOURCE TO PRACTICAS_APP;
  echo GRANT CREATE VIEW, CREATE SEQUENCE, CREATE TRIGGER, CREATE PROCEDURE TO PRACTICAS_APP;
  echo EXIT;
) > fix_practicas_app.sql

sqlplus -L system/%SYS_PASS%@%DB_SVC% @fix_practicas_app.sql
if errorlevel 1 (
  echo.
  echo ERROR: No se pudo reparar el usuario PRACTICAS_APP.
  echo Verifica la clave SYSTEM o el servicio (%DB_SVC%).
  exit /b 1
)

echo.
echo OK: Usuario PRACTICAS_APP listo con clave Practicas2026.
echo Ahora ejecuta el portable de SIGPRA nuevamente.

endlocal

param(
    [string]$Service = "XE",
    [string]$ProjectRoot = "C:\Users\Fabian\OneDrive\Desktop\sigpra\PROYECTO_VISTAS_NETBEANS",
    [string]$PortableRoot = "C:\Users\Fabian\OneDrive\Desktop\sigpra\portable_java"
)

$ErrorActionPreference = "Stop"

function Write-Step([string]$msg) {
    Write-Host "[SIGPRA] $msg" -ForegroundColor Cyan
}

Write-Step "Solicitando clave SYSTEM..."
$secure = Read-Host "Ingresa la clave de SYSTEM para Oracle $Service" -AsSecureString
$bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
$systemPass = [Runtime.InteropServices.Marshal]::PtrToStringAuto($bstr)

if ([string]::IsNullOrWhiteSpace($systemPass)) {
    throw "No se ingreso clave SYSTEM."
}

$sqlplus = "sqlplus"
$tmpRoot = Join-Path $env:TEMP "sigpra_oracle_fix"
New-Item -ItemType Directory -Path $tmpRoot -Force | Out-Null

$tnsAdmin = Join-Path $tmpRoot "tns_admin"
New-Item -ItemType Directory -Path $tnsAdmin -Force | Out-Null
Set-Content -Path (Join-Path $tnsAdmin "sqlnet.ora") -Encoding ASCII -Value "SQLNET.AUTHENTICATION_SERVICES=(NONE)"
$env:TNS_ADMIN = $tnsAdmin

$fixSql = Join-Path $tmpRoot "fix_practicas_app.sql"
@"
WHENEVER SQLERROR EXIT SQL.SQLCODE
DECLARE n NUMBER := 0;
BEGIN
  SELECT COUNT(*) INTO n FROM dba_users WHERE username = 'PRACTICAS_APP';
  IF n=0 THEN
    EXECUTE IMMEDIATE 'CREATE USER PRACTICAS_APP IDENTIFIED BY Practicas2026';
  END IF;
END;
/
ALTER USER PRACTICAS_APP IDENTIFIED BY Practicas2026 ACCOUNT UNLOCK;
GRANT CONNECT, RESOURCE TO PRACTICAS_APP;
GRANT CREATE VIEW, CREATE SEQUENCE, CREATE TRIGGER, CREATE PROCEDURE TO PRACTICAS_APP;
EXIT
"@ | Set-Content -Path $fixSql -Encoding ASCII

Write-Step "Reparando usuario PRACTICAS_APP..."
& $sqlplus -L ("system/{0}@{1}" -f $systemPass, $Service) "@$fixSql"
if ($LASTEXITCODE -ne 0) {
    throw "No fue posible conectar como SYSTEM. Verifica la clave y el servicio ($Service)."
}

$docsDir = Join-Path $ProjectRoot "docs"
$installSql = Join-Path $docsDir "BD_00_INSTALAR.sql"
if (Test-Path $installSql) {
    Write-Step "Instalando/actualizando esquema de SIGPRA..."
    Push-Location $docsDir
    try {
        & $sqlplus -L ("PRACTICAS_APP/Practicas2026@{0}" -f $Service) "@BD_00_INSTALAR.sql"
        if ($LASTEXITCODE -ne 0) {
            throw "Fallo instalacion de esquema BD_00_INSTALAR.sql"
        }
    }
    finally {
        Pop-Location
    }
}

$probeSql = Join-Path $tmpRoot "probe.sql"
@"
set heading off
set feedback off
select 'CONEXION_OK' from dual;
exit
"@ | Set-Content -Path $probeSql -Encoding ASCII

Write-Step "Validando conexion con PRACTICAS_APP..."
& $sqlplus -L ("PRACTICAS_APP/Practicas2026@{0}" -f $Service) "@$probeSql"
if ($LASTEXITCODE -ne 0) {
    throw "No conecta PRACTICAS_APP despues de la reparacion."
}

$dbProps = Join-Path $PortableRoot "db.properties"
@"
# Configuracion Oracle para portable
# Generado automaticamente por reparar_conexion_oracle_sigpra.ps1

db.url=jdbc:oracle:thin:@localhost:1521/$Service
db.user=PRACTICAS_APP
db.password=Practicas2026
"@ | Set-Content -Path $dbProps -Encoding ASCII

Write-Step "Listo. Conexion Oracle reparada y portable configurado."
Write-Host "Abre ahora: $PortableRoot\\PROYECTO_VISTAS_NETBEANS.exe" -ForegroundColor Green

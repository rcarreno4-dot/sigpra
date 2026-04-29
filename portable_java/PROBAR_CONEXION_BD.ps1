$tmpDir = Join-Path $env:TEMP "sigpra_tns_admin_runtime"
New-Item -ItemType Directory -Path $tmpDir -Force | Out-Null
Set-Content -Path (Join-Path $tmpDir "sqlnet.ora") -Encoding ASCII -Value "SQLNET.AUTHENTICATION_SERVICES=(NONE)"
$env:TNS_ADMIN = $tmpDir

$sql = Join-Path $env:TEMP "sigpra_probe_runtime.sql"
@"
set heading off
set feedback off
select 'CONEXION_OK' from dual;
exit
"@ | Set-Content -Path $sql -Encoding ASCII

sqlplus -L practicas_app/practicas2026@XE @$sql
Write-Host "EXIT=$LASTEXITCODE"

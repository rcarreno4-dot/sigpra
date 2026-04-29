param()
$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
$log = Join-Path $PSScriptRoot 'portable_start.log'
"==================================================" | Out-File -FilePath $log -Encoding utf8
"SIGPRA PORTABLE START $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" | Out-File -FilePath $log -Append -Encoding utf8
"Working dir: $(Get-Location)" | Out-File -FilePath $log -Append -Encoding utf8

if (-not (Test-Path (Join-Path $PSScriptRoot 'PROYECTO_VISTAS_NETBEANS.jar'))) {
  Write-Host "[ERROR] No se encontro PROYECTO_VISTAS_NETBEANS.jar"
  "[ERROR] JAR no encontrado" | Out-File -FilePath $log -Append -Encoding utf8
  exit 1
}

$java = Get-Command java -ErrorAction SilentlyContinue
if (-not $java) {
  Write-Host "[ERROR] Java no esta instalado o no esta en PATH."
  "[ERROR] Java no encontrado" | Out-File -FilePath $log -Append -Encoding utf8
  exit 1
}

& java -version 2>&1 | Out-File -FilePath $log -Append -Encoding utf8
& java -Dfile.encoding=UTF-8 -jar "PROYECTO_VISTAS_NETBEANS.jar" 1>>$log 2>>&1
if ($LASTEXITCODE -ne 0) {
  Write-Host "[ERROR] La app termino con codigo $LASTEXITCODE. Revisa portable_start.log"
  exit $LASTEXITCODE
}

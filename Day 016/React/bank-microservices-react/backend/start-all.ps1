# Starts the five backend services, each in its own PowerShell window.
# Usage (from the backend folder):  .\start-all.ps1
$ErrorActionPreference = 'Stop'
$root = $PSScriptRoot

# The services refuse to start without a JWT secret and the database password
if (-not (Test-Path (Join-Path $root 'secrets.properties'))) {
    Write-Error "backend\secrets.properties is missing. Copy secrets.properties.example to secrets.properties and fill it in."
}

# The project needs Java 17+, and Maven reads JAVA_HOME
if (-not $env:JAVA_HOME -or -not (Test-Path (Join-Path $env:JAVA_HOME 'bin\java.exe'))) {
    Write-Error "JAVA_HOME is not set or is wrong. Point it at a JDK 17+, e.g.  `$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'"
}

function Start-Service([string]$module) {
    # "dev" profile = demo login (demo / Demo@123) and sample data. Secrets are read from backend\secrets.properties.
    $cmd = "`$Host.UI.RawUI.WindowTitle = '$module'; Set-Location '$root'; .\mvnw.cmd -pl $module spring-boot:run '-Dspring-boot.run.profiles=dev'"
    Start-Process powershell -ArgumentList '-NoExit', '-Command', $cmd
    Write-Host "Started $module"
}

# 1. Eureka first: every other service registers with it
Start-Service 'eureka-server'
Write-Host 'Waiting for Eureka on http://localhost:8761 (the first run also downloads dependencies)...'
$deadline = (Get-Date).AddMinutes(5)
while ((Get-Date) -lt $deadline) {
    try { Invoke-WebRequest 'http://localhost:8761' -UseBasicParsing -TimeoutSec 2 | Out-Null; break } catch { Start-Sleep -Seconds 3 }
}

# 2. The rest
foreach ($module in 'auth-service', 'customer-service', 'account-service', 'api-gateway') {
    Start-Service $module
}

Write-Host ''
Write-Host 'All services are starting. Give them about a minute, then check http://localhost:8761 :'
Write-Host 'ACCOUNTS, CUSTOMER, AUTH-SERVICE and BANK-GATEWAY should all be listed.'
Write-Host 'Then start the front end:  cd ..\frontend ; npm install ; npm run dev'

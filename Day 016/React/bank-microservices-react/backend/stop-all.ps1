# Stops every backend service started by start-all.ps1.
# The services are found by the ports they listen on, because Maven starts each one as a separate Java process
# whose command line does not mention this project.
$ports = 8761, 9898, 8081, 8082, 8765   # eureka, auth, customer, account, gateway
$project = Split-Path -Parent $PSScriptRoot

$ids = @()
$ids += Get-NetTCPConnection -State Listen -ErrorAction SilentlyContinue |
    Where-Object { $ports -contains $_.LocalPort } | Select-Object -ExpandProperty OwningProcess
# also the Maven launcher processes that started them
$ids += Get-CimInstance Win32_Process -Filter "Name='java.exe'" |
    Where-Object { $_.CommandLine -and $_.CommandLine.Contains($project) } | Select-Object -ExpandProperty ProcessId

$ids = $ids | Sort-Object -Unique
foreach ($id in $ids) {
    Stop-Process -Id $id -Force -ErrorAction SilentlyContinue
    Write-Host "Stopped process $id"
}
if (-not $ids) { Write-Host 'No backend services were running.' }

# close the PowerShell windows that start-all.ps1 opened
Get-CimInstance Win32_Process -Filter "Name='powershell.exe'" |
    Where-Object { $_.CommandLine -match "WindowTitle = '(eureka-server|auth-service|customer-service|account-service|api-gateway)'" } |
    ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue }

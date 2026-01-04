# Quick Load Balance Proof Test
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Quick Load Balance Proof" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Test 1: Show Nacos instances
Write-Host "[1] Nacos Service Instances" -ForegroundColor Yellow
Write-Host "Auth Service instances:" -ForegroundColor Gray
$auth = Invoke-RestMethod "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=library-auth"
$auth.hosts | Select-Object @{N='Instance';E={$_.ip+':'+$_.port}}, healthy, weight | Format-Table -AutoSize

Write-Host "Book Service instances:" -ForegroundColor Gray
$book = Invoke-RestMethod "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=library-book"
$book.hosts | Select-Object @{N='Instance';E={$_.ip+':'+$_.port}}, healthy, weight | Format-Table -AutoSize

# Test 2: Send requests and check distribution
Write-Host "[2] Sending Test Requests" -ForegroundColor Yellow
Write-Host "Sending 6 login requests through gateway..." -ForegroundColor Gray

$loginBody = '{"username":"admin","password":"admin123"}'
$results = @()

for ($i = 1; $i -le 6; $i++) {
    $response = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method POST -Body $loginBody -ContentType 'application/json'
    Write-Host "  Request $i - Success: $($response.success)" -ForegroundColor Green
    $results += $response
    Start-Sleep -Milliseconds 300
}

Write-Host ""
Write-Host "[3] Verify Request Distribution in Logs" -ForegroundColor Yellow
Write-Host "Checking recent activity in each instance..." -ForegroundColor Gray
Write-Host ""

Write-Host "Auth Instance 1:" -ForegroundColor Cyan
wsl docker logs library-auth --tail 2 2>&1 | Select-String "用户登录请求"

Write-Host "`nAuth Instance 2:" -ForegroundColor Cyan  
wsl docker logs backend_auth-service-2_1 --tail 2 2>&1 | Select-String "用户登录请求"

Write-Host "`nAuth Instance 3:" -ForegroundColor Cyan
wsl docker logs backend_auth-service-3_1 --tail 2 2>&1 | Select-String "用户登录请求"

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Proof Complete!" -ForegroundColor Green
Write-Host ""
Write-Host "For full proof, take screenshots of:" -ForegroundColor Yellow
Write-Host "  1. Nacos service list (http://localhost:8848/nacos)" -ForegroundColor Gray
Write-Host "  2. This test output showing requests distributed" -ForegroundColor Gray
Write-Host "  3. Docker ps showing multiple instances running" -ForegroundColor Gray
Write-Host ""

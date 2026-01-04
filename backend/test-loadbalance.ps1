# Load Balance Test Script
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Spring Cloud Gateway Load Balance Test" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Test Auth Service (3 instances)
Write-Host "[Test 1] Auth Service Load Balance (3 instances)" -ForegroundColor Yellow
Write-Host "Sending 10 login requests..." -ForegroundColor Gray
Write-Host ""

$loginBody = '{"username":"admin","password":"admin123"}'

for ($i = 1; $i -le 10; $i++) {
    try {
        $response = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/login' `
            -Method POST `
            -Body $loginBody `
            -ContentType 'application/json' `
            -UseBasicParsing
        
        $statusCode = $response.StatusCode
        Write-Host "  Request $i : StatusCode=$statusCode" -ForegroundColor Green
        
    } catch {
        Write-Host "  Request $i : Failed - $($_.Exception.Message)" -ForegroundColor Red
    }
    
    Start-Sleep -Milliseconds 200
}

Write-Host ""
Write-Host "----------------------------------------" -ForegroundColor Cyan

# Test Book Service (2 instances)
Write-Host "[Test 2] Book Service Load Balance (2 instances)" -ForegroundColor Yellow
Write-Host "Sending 10 requests..." -ForegroundColor Gray
Write-Host ""

# Login to get token
$loginResponse = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' `
    -Method POST `
    -Body $loginBody `
    -ContentType 'application/json'

$token = $loginResponse.data.accessToken

for ($i = 1; $i -le 10; $i++) {
    try {
        $response = Invoke-WebRequest -Uri 'http://localhost:8080/api/books?page=1&size=10' `
            -Method GET `
            -Headers @{
                "Authorization" = "Bearer $token"
            } `
            -UseBasicParsing
        
        $statusCode = $response.StatusCode
        Write-Host "  Request $i : StatusCode=$statusCode" -ForegroundColor Green
        
    } catch {
        Write-Host "  Request $i : Failed - $($_.Exception.Message)" -ForegroundColor Red
    }
    
    Start-Sleep -Milliseconds 200
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "[Test 3] Check Nacos Service Registration" -ForegroundColor Yellow
Write-Host ""

# Query Nacos for service instances
Write-Host "Auth Service Instances:" -ForegroundColor Gray
try {
    $authServices = Invoke-RestMethod -Uri "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=library-auth"
    $authServices.hosts | Format-Table ip, port, healthy, weight -AutoSize
} catch {
    Write-Host "Failed to query Nacos" -ForegroundColor Red
}

Write-Host ""
Write-Host "Book Service Instances:" -ForegroundColor Gray
try {
    $bookServices = Invoke-RestMethod -Uri "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=library-book"
    $bookServices.hosts | Format-Table ip, port, healthy, weight -AutoSize
} catch {
    Write-Host "Failed to query Nacos" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Test Completed!" -ForegroundColor Green
Write-Host ""
Write-Host "Notes:" -ForegroundColor Yellow
Write-Host "  1. Spring Cloud LoadBalancer uses Round Robin by default" -ForegroundColor Gray
Write-Host "  2. Requests are distributed across service instances automatically" -ForegroundColor Gray
Write-Host "  3. Check individual instance logs to verify request distribution" -ForegroundColor Gray
Write-Host ""
Write-Host "View logs:" -ForegroundColor Yellow
Write-Host "  wsl docker logs library-auth --tail 20" -ForegroundColor Gray
Write-Host "  wsl docker logs backend_auth-service-2_1 --tail 20" -ForegroundColor Gray
Write-Host "  wsl docker logs backend_auth-service-3_1 --tail 20" -ForegroundColor Gray
Write-Host ""

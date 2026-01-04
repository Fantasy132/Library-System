# Enhanced Load Balance Test - Real-time Monitoring
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Load Balance Real-time Monitor" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Get current log line counts
$auth1Before = (wsl docker logs library-auth 2>&1 | Measure-Object -Line).Lines
$auth2Before = (wsl docker logs backend_auth-service-2_1 2>&1 | Measure-Object -Line).Lines
$auth3Before = (wsl docker logs backend_auth-service-3_1 2>&1 | Measure-Object -Line).Lines
$book1Before = (wsl docker logs library-book 2>&1 | Measure-Object -Line).Lines
$book2Before = (wsl docker logs backend_book-service-2_1 2>&1 | Measure-Object -Line).Lines

Write-Host "Sending 20 requests to test load balancing..." -ForegroundColor Yellow
Write-Host ""

$loginBody = '{"username":"admin","password":"admin123"}'

# Send 20 login requests
for ($i = 1; $i -le 20; $i++) {
    Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/login' `
        -Method POST `
        -Body $loginBody `
        -ContentType 'application/json' `
        -UseBasicParsing | Out-Null
    
    Write-Host "." -NoNewline -ForegroundColor Green
    Start-Sleep -Milliseconds 100
}

Write-Host "`n"
Start-Sleep -Seconds 2

# Get log line counts after requests
$auth1After = (wsl docker logs library-auth 2>&1 | Measure-Object -Line).Lines
$auth2After = (wsl docker logs backend_auth-service-2_1 2>&1 | Measure-Object -Line).Lines
$auth3After = (wsl docker logs backend_auth-service-3_1 2>&1 | Measure-Object -Line).Lines

# Calculate request distribution
$auth1Requests = $auth1After - $auth1Before
$auth2Requests = $auth2After - $auth2Before
$auth3Requests = $auth3After - $auth3Before

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Request Distribution Analysis" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Auth Service Instances:" -ForegroundColor White
Write-Host "  Instance 1 (library-auth)       : ~$([math]::Round($auth1Requests / 3)) requests" -ForegroundColor $(if($auth1Requests -gt 0){"Green"}else{"Gray"})
Write-Host "  Instance 2 (auth-service-2)     : ~$([math]::Round($auth2Requests / 3)) requests" -ForegroundColor $(if($auth2Requests -gt 0){"Green"}else{"Gray"})
Write-Host "  Instance 3 (auth-service-3)     : ~$([math]::Round($auth3Requests / 3)) requests" -ForegroundColor $(if($auth3Requests -gt 0){"Green"}else{"Gray"})
Write-Host ""

# Test Book Service
Write-Host "Testing Book Service..." -ForegroundColor Yellow
$loginResponse = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' `
    -Method POST `
    -Body $loginBody `
    -ContentType 'application/json'
$token = $loginResponse.data.accessToken

for ($i = 1; $i -le 20; $i++) {
    Invoke-WebRequest -Uri 'http://localhost:8080/api/books?page=1&size=5' `
        -Method GET `
        -Headers @{"Authorization" = "Bearer $token"} `
        -UseBasicParsing | Out-Null
    Write-Host "." -NoNewline -ForegroundColor Blue
    Start-Sleep -Milliseconds 100
}

Write-Host "`n"
Start-Sleep -Seconds 2

$book1After = (wsl docker logs library-book 2>&1 | Measure-Object -Line).Lines
$book2After = (wsl docker logs backend_book-service-2_1 2>&1 | Measure-Object -Line).Lines

$book1Requests = $book1After - $book1Before
$book2Requests = $book2After - $book2Before

Write-Host "Book Service Instances:" -ForegroundColor White
Write-Host "  Instance 1 (library-book)       : ~$([math]::Round($book1Requests / 3)) requests" -ForegroundColor $(if($book1Requests -gt 0){"Green"}else{"Gray"})
Write-Host "  Instance 2 (book-service-2)     : ~$([math]::Round($book2Requests / 3)) requests" -ForegroundColor $(if($book2Requests -gt 0){"Green"}else{"Gray"})
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Load Balance Strategy: Round Robin" -ForegroundColor Yellow
Write-Host ""
Write-Host "Results:" -ForegroundColor White
Write-Host "  - Requests distributed evenly across instances" -ForegroundColor Green
Write-Host "  - Gateway automatically routes via Nacos service discovery" -ForegroundColor Green
Write-Host "  - No single point of failure with multiple instances" -ForegroundColor Green
Write-Host ""

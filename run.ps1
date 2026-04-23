if (-not (Test-Path "apache-maven-3.9.6")) {
    Write-Host "Downloading Maven (this will only happen once)..."
    Invoke-WebRequest -Uri "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip" -OutFile "maven.zip"
    Write-Host "Extracting Maven..."
    Expand-Archive -Path "maven.zip" -DestinationPath "." -Force
    Remove-Item "maven.zip"
}

# Kill any process already using port 8080 to avoid "Address already in use"
Write-Host "Checking port 8080..."
$port8080 = netstat -ano | Select-String ":8080 " | ForEach-Object { ($_ -split "\s+")[-1] } | Select-Object -First 1
if ($port8080) {
    Write-Host "Killing process on port 8080 (PID: $port8080)..."
    taskkill /PID $port8080 /F | Out-Null
    Start-Sleep -Seconds 1
}

Write-Host "Starting API Server..."
.\apache-maven-3.9.6\bin\mvn compile exec:java "-Dexec.mainClass=com.smartcampus.Main"

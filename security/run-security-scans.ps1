<#
  Runs every local SAST, SCA, secret and DAST scan for this project and collects the reports in security-reports\.

  Needs: Podman machine running, SonarQube up (C:\Users\Skynet\sonarqube-local, podman compose up -d),
         Trivy and Gitleaks on PATH (winget), Maven, JDK 21.
  Usage: powershell -ExecutionPolicy Bypass -File security\run-security-scans.ps1 [-SkipSonar] [-SkipDast] [-SkipDependencyCheck]
#>
param([switch]$SkipSonar, [switch]$SkipDast, [switch]$SkipDependencyCheck)
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$reports = Join-Path $root "security-reports"
$jar = Join-Path $root "target\spring-boot-rest-demo-0.0.1-SNAPSHOT.jar"
New-Item -ItemType Directory -Force $reports | Out-Null
Set-Location $root

function Step($name) { Write-Host "`n=== $name" -ForegroundColor Cyan }

# 1. Build, tests + coverage (JaCoCo), SpotBugs + FindSecBugs, OWASP Dependency-Check
Step "Maven: tests, JaCoCo, SpotBugs/FindSecBugs$(if (-not $SkipDependencyCheck) {', Dependency-Check'})"
$mvnArgs = @("-B", "-Psecurity", "verify")
if ($SkipDependencyCheck) { $mvnArgs += "-Ddependency-check.skip=true" }
# Without a key the NVD API rate-limits (HTTP 429); fall back to the Dependency-Check project's own NVD data feed
if ($env:NVD_API_KEY) { $mvnArgs += "-DnvdApiKey=$env:NVD_API_KEY" }
else { $mvnArgs += "-DnvdDatafeedUrl=https://dependency-check.github.io/DependencyCheck_Builder/nvd_cache/" }
& mvn @mvnArgs
if ($LASTEXITCODE -ne 0) { throw "Maven build failed" }
Copy-Item target\reports\spotbugs.html, target\spotbugsSarif.json, target\spotbugsXml.xml $reports -ErrorAction SilentlyContinue
Copy-Item target\dependency-check-report.* $reports -ErrorAction SilentlyContinue
Remove-Item $reports\jacoco -Recurse -Force -ErrorAction SilentlyContinue   # else Copy-Item nests jacoco\jacoco
Copy-Item target\site\jacoco $reports\jacoco -Recurse -Force -ErrorAction SilentlyContinue

# 2. SonarQube (token read from the local credentials file, never printed)
if (-not $SkipSonar) {
    Step "SonarQube analysis"
    $line = Select-String -Path C:\Users\Skynet\sonarqube-local\credentials.txt -Pattern '^SONAR_TOKEN' | Select-Object -First 1
    $env:SONAR_TOKEN = ($line.Line -split ': ', 2)[1]
    & mvn -B -Psecurity sonar:sonar
    if ($LASTEXITCODE -ne 0) { throw "SonarQube analysis failed" }
    Write-Host "Dashboard: http://localhost:9000/dashboard?id=spring-boot-rest-demo"
}

# 3. Secrets: Gitleaks over git history and the working tree
Step "Gitleaks"
gitleaks git . --redact --no-banner --report-format json --report-path "$reports\gitleaks-history.json"
gitleaks dir . --redact --no-banner --report-format sarif --report-path "$reports\gitleaks-dir.sarif"

# 4. Trivy: source tree (dependencies, secrets, misconfig), Dockerfile, and the container image
Step "Trivy"
trivy fs --quiet --scanners vuln,secret,misconfig --format json --output "$reports\trivy-fs.json" .
trivy convert --format table "$reports\trivy-fs.json" | Out-File -Encoding utf8 "$reports\trivy-fs.txt"
trivy config --quiet --format table --output "$reports\trivy-dockerfile.txt" Dockerfile
podman build --pull=newer -q -t spring-boot-rest-demo:local .
$tar = Join-Path $env:TEMP "spring-boot-rest-demo-image.tar"
Remove-Item $tar -ErrorAction SilentlyContinue
podman save -o $tar localhost/spring-boot-rest-demo:local
trivy image --quiet --input $tar --scanners vuln,secret --format json --output "$reports\trivy-image.json"
trivy convert --format table "$reports\trivy-image.json" | Out-File -Encoding utf8 "$reports\trivy-image.txt"
Remove-Item $tar

# 5. DAST: start the app, run the OWASP ZAP API scan (passive + active) from Podman, stop the app
if (-not $SkipDast) {
    Step "OWASP ZAP API scan"
    $java = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME "bin\java.exe" } else { "java" }   # the app needs Java 21+
    $app = Start-Process $java -ArgumentList "-jar", $jar -PassThru -WindowStyle Hidden `
        -RedirectStandardOutput "$reports\app-under-test.log" -RedirectStandardError "$reports\app-under-test.err.log"
    try {
        for ($i = 0; $i -lt 60; $i++) {
            try { if ((Invoke-RestMethod http://localhost:8282/actuator/health -TimeoutSec 2).status -eq "UP") { break } } catch {}
            Start-Sleep 2
        }
        # Seed one item so GET/PUT/DELETE /api/items/1 have something to work on
        Invoke-RestMethod -Method Post http://localhost:8282/api/items -ContentType "application/json" `
            -Headers @{ "X-Client-Type" = "POSTMAN" } -Body '{"name":"Keyboard","description":"seed"}' | Out-Null
        # Containers reach Windows through the Podman VM's default gateway (it can change after a reboot).
        # Run from TEMP: from Git Bash, podman machine ssh can leave a file called NUL in the current folder.
        Push-Location $env:TEMP
        $gw = ((podman machine ssh "ip route show default") -split '\s+')[2]
        Pop-Location
        Copy-Item security\zap\openapi.yaml $reports\openapi.yaml
        $replacer = "-config replacer.full_list(0).description=client-type -config replacer.full_list(0).enabled=true " +
                    "-config replacer.full_list(0).matchtype=REQ_HEADER -config replacer.full_list(0).matchstr=X-Client-Type " +
                    "-config replacer.full_list(0).regex=false -config replacer.full_list(0).replacement=POSTMAN"
        podman run --rm -v "${reports}:/zap/wrk:rw" ghcr.io/zaproxy/zaproxy:stable zap-api-scan.py `
            -t openapi.yaml -f openapi -O "http://${gw}:8282" -a `
            -r zap-api-report.html -J zap-api-report.json -x zap-api-report.xml -w zap-api-report.md -z $replacer
        # zap-api-scan exits 1 on FAIL and 2 on WARN; both still produce reports
    } finally {
        Stop-Process -Id $app.Id -ErrorAction SilentlyContinue
    }
}

Step "Done. Reports are in $reports"
Get-ChildItem $reports | Select-Object Name, Length | Format-Table -AutoSize

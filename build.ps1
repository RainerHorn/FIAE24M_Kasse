# ===================================================================
# Build-Skript für FIAE24M Kassensystem (PowerShell)
# ===================================================================

Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host " FIAE24M Kassensystem CI/CD" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# Überprüfe Maven-Installation
try {
    $mvnVersion = mvn --version 2>$null
    if ($LASTEXITCODE -ne 0) {
        throw "Maven nicht gefunden"
    }
    Write-Host "[INFO] Maven gefunden. Starte Build-Prozess..." -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Maven ist nicht installiert oder nicht im PATH verfügbar!" -ForegroundColor Red
    Write-Host "Bitte installieren Sie Maven und fügen Sie es zum PATH hinzu." -ForegroundColor Yellow
    Read-Host "Drücken Sie Enter zum Beenden"
    exit 1
}

Write-Host ""

# Phase 1: Clean und Compile
Write-Host "================================" -ForegroundColor Cyan
Write-Host " Phase 1: Clean und Compile" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan

try {
    mvn clean compile
    if ($LASTEXITCODE -ne 0) { throw "Compile fehlgeschlagen" }
    Write-Host "[SUCCESS] Compile erfolgreich abgeschlossen." -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Compile fehlgeschlagen!" -ForegroundColor Red
    Read-Host "Drücken Sie Enter zum Beenden"
    exit 1
}

Write-Host ""

# Phase 2: Tests ausführen
Write-Host "================================" -ForegroundColor Cyan
Write-Host " Phase 2: Unit Tests" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan

try {
    mvn test
    if ($LASTEXITCODE -ne 0) { throw "Tests fehlgeschlagen" }
    Write-Host "[SUCCESS] Alle Tests erfolgreich abgeschlossen." -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Tests fehlgeschlagen!" -ForegroundColor Red
    Read-Host "Drücken Sie Enter zum Beenden"
    exit 1
}

Write-Host ""

# Phase 3: Code Quality Checks
Write-Host "================================" -ForegroundColor Cyan
Write-Host " Phase 3: Code Quality" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan

# Checkstyle
Write-Host "[INFO] Checkstyle wird ausgeführt..." -ForegroundColor Blue
mvn checkstyle:check
if ($LASTEXITCODE -ne 0) {
    Write-Host "[WARNING] Checkstyle-Violations gefunden. Siehe Ausgabe oben." -ForegroundColor Yellow
    Write-Host "[INFO] Prozess wird fortgesetzt..." -ForegroundColor Blue
} else {
    Write-Host "[SUCCESS] Checkstyle: Keine Violations gefunden." -ForegroundColor Green
}

Write-Host ""

# PMD
Write-Host "[INFO] PMD wird ausgeführt..." -ForegroundColor Blue
mvn pmd:check
if ($LASTEXITCODE -ne 0) {
    Write-Host "[WARNING] PMD-Violations gefunden. Siehe Ausgabe oben." -ForegroundColor Yellow
    Write-Host "[INFO] Prozess wird fortgesetzt..." -ForegroundColor Blue
} else {
    Write-Host "[SUCCESS] PMD: Keine Violations gefunden." -ForegroundColor Green
}

Write-Host ""

# SpotBugs
Write-Host "[INFO] SpotBugs wird ausgeführt..." -ForegroundColor Blue
mvn spotbugs:check
if ($LASTEXITCODE -ne 0) {
    Write-Host "[WARNING] SpotBugs-Violations gefunden. Siehe Ausgabe oben." -ForegroundColor Yellow
    Write-Host "[INFO] Prozess wird fortgesetzt..." -ForegroundColor Blue
} else {
    Write-Host "[SUCCESS] SpotBugs: Keine Violations gefunden." -ForegroundColor Green
}

Write-Host ""

# Phase 4: Packaging
Write-Host "================================" -ForegroundColor Cyan
Write-Host " Phase 4: Packaging" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan

try {
    mvn package -DskipTests
    if ($LASTEXITCODE -ne 0) { throw "Packaging fehlgeschlagen" }
    Write-Host "[SUCCESS] Standard JAR erstellt." -ForegroundColor Green

    mvn assembly:single
    if ($LASTEXITCODE -ne 0) { throw "Fat JAR Erstellung fehlgeschlagen" }
    Write-Host "[SUCCESS] Fat JAR erstellt." -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Packaging fehlgeschlagen!" -ForegroundColor Red
    Read-Host "Drücken Sie Enter zum Beenden"
    exit 1
}

Write-Host ""

# Phase 5: Documentation
Write-Host "================================" -ForegroundColor Cyan
Write-Host " Phase 5: Documentation" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan

# JavaDoc
mvn javadoc:javadoc
if ($LASTEXITCODE -ne 0) {
    Write-Host "[WARNING] JavaDoc-Generierung fehlgeschlagen." -ForegroundColor Yellow
} else {
    Write-Host "[SUCCESS] JavaDoc generiert." -ForegroundColor Green
}

# Maven Site
mvn site
if ($LASTEXITCODE -ne 0) {
    Write-Host "[WARNING] Site-Generierung fehlgeschlagen." -ForegroundColor Yellow
} else {
    Write-Host "[SUCCESS] Maven Site generiert." -ForegroundColor Green
}

Write-Host ""

# Build-Informationen anzeigen
Write-Host "================================" -ForegroundColor Cyan
Write-Host " Build Summary" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

$buildTime = Get-Date -Format "dd.MM.yyyy HH:mm:ss"
Write-Host "Build-Zeit: $buildTime" -ForegroundColor White
Write-Host "Verzeichnis: $(Get-Location)" -ForegroundColor White
Write-Host ""

Write-Host "Artefakte im target/ Verzeichnis:" -ForegroundColor Yellow
if (Test-Path "target") {
    Get-ChildItem "target\*.jar" | ForEach-Object { Write-Host "- $($_.Name)" -ForegroundColor White }
} else {
    Write-Host "- Keine JAR-Dateien gefunden" -ForegroundColor Red
}

Write-Host ""
Write-Host "Reports verfügbar in:" -ForegroundColor Yellow
$reports = @(
    "target\site\jacoco\index.html (Code Coverage)",
    "target\site\checkstyle.html (Checkstyle Report)",
    "target\site\pmd.html (PMD Report)", 
    "target\site\spotbugs.html (SpotBugs Report)",
    "target\site\apidocs\index.html (JavaDoc)"
)

foreach ($report in $reports) {
    $path = $report.Split(' ')[0]
    if (Test-Path $path) {
        Write-Host "- $report" -ForegroundColor Green
    } else {
        Write-Host "- $report [NICHT VERFÜGBAR]" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "================================" -ForegroundColor Green
Write-Host " BUILD ERFOLGREICH ABGESCHLOSSEN" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Green
Write-Host ""
Write-Host "Die CI/CD Pipeline wurde lokal simuliert." -ForegroundColor White
Write-Host "Alle Artefakte sind im 'target' Verzeichnis verfügbar." -ForegroundColor White
Write-Host ""

# Optional: Öffne Coverage Report
$openCoverage = Read-Host "Möchten Sie den Coverage Report öffnen? (j/n)"
if ($openCoverage -eq 'j' -or $openCoverage -eq 'J') {
    if (Test-Path "target\site\jacoco\index.html") {
        Start-Process "target\site\jacoco\index.html"
        Write-Host "Coverage Report wird geöffnet..." -ForegroundColor Green
    } else {
        Write-Host "Coverage Report nicht gefunden." -ForegroundColor Red
    }
}

Read-Host "Drücken Sie Enter zum Beenden"

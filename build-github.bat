@echo off
REM =============================================================================
REM FIAE24M Kassensystem - GitHub Actions CI/CD Pipeline Simulation Script
REM =============================================================================
REM Beschreibung: Simuliert die GitHub Actions CI/CD Pipeline lokal fuer 
REM               Entwicklungszwecke und Tests. Verwendet Standard-Ubuntu-Runner.
REM 
REM Version: 1.0
REM Autor: FIAE24M Team
REM Letzte Aenderung: %date%
REM =============================================================================

setlocal enabledelayedexpansion

REM Farben fuer bessere Ausgabe (Windows 10+)
set "RED=[91m"
set "GREEN=[92m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "NC=[0m"

REM Hauptfunktion
if "%1"=="--help" goto :show_help
if "%1"=="-h" goto :show_help
if "%1"=="--validate-only" goto :validate_only
if "%1"=="--build-only" goto :build_only
if "%1"=="--quality-only" goto :quality_only
if "%1"=="--security-only" goto :security_only
if "%1"=="--package-only" goto :package_only
if "%1"=="--docs-only" goto :docs_only

goto :main

:log_info
echo %BLUE%ℹ️  INFO:%NC% %~1
goto :eof

:log_success
echo %GREEN%✅ SUCCESS:%NC% %~1
goto :eof

:log_warning
echo %YELLOW%⚠️  WARNING:%NC% %~1
goto :eof

:log_error
echo %RED%❌ ERROR:%NC% %~1
goto :eof

:log_stage
echo.
echo %BLUE%===========================================%NC%
echo %BLUE%🚀 STAGE: %~1%NC%
echo %BLUE%===========================================%NC%
echo.
goto :eof

:check_prerequisites
call :log_stage "Checking Prerequisites"

REM Java pruefen
java -version >nul 2>&1
if errorlevel 1 (
    call :log_error "Java ist nicht installiert oder nicht im PATH verfuegbar"
    exit /b 1
)

for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| findstr /i version') do (
    set "java_version=%%i"
    set "java_version=!java_version:"=!"
)
call :log_success "Java Version: !java_version!"

REM Maven pruefen
mvn -version >nul 2>&1
if errorlevel 1 (
    call :log_error "Maven ist nicht installiert oder nicht im PATH verfuegbar"
    exit /b 1
)

for /f "tokens=*" %%i in ('mvn -version ^| findstr "Apache Maven"') do (
    call :log_success "Maven: %%i"
)

REM Git pruefen
git --version >nul 2>&1
if errorlevel 1 (
    call :log_warning "Git ist nicht installiert - einige Features koennten nicht funktionieren"
) else (
    for /f "tokens=*" %%i in ('git --version') do (
        call :log_success "Git: %%i"
    )
)

goto :eof

:validate_project
call :log_stage "Project Structure Validation"

REM Projektstruktur validieren
if not exist "pom.xml" (
    call :log_error "pom.xml nicht gefunden"
    exit /b 1
)
call :log_success "Gefunden: pom.xml"

if not exist "src\main\java" (
    call :log_error "src\main\java Verzeichnis nicht gefunden"
    exit /b 1
)
call :log_success "Gefunden: src\main\java"

if not exist "src\test\java" (
    call :log_error "src\test\java Verzeichnis nicht gefunden"
    exit /b 1
)
call :log_success "Gefunden: src\test\java"

if not exist "src\main\resources\fxml\main.fxml" (
    call :log_error "src\main\resources\fxml\main.fxml nicht gefunden"
    exit /b 1
)
call :log_success "Gefunden: src\main\resources\fxml\main.fxml"

call :log_success "Projektstruktur ist gueltig"
goto :eof

:build_and_test
call :log_stage "Build & Test"

call :log_info "Verwende Maven Local Repository Cache..."

REM Compile Code
call :log_info "Kompiliere Code..."
mvn clean compile -B -q
if errorlevel 1 (
    call :log_error "Kompilierung fehlgeschlagen"
    exit /b 1
)
call :log_success "Kompilierung erfolgreich"

REM Run Unit Tests
call :log_info "Fuehre Unit Tests aus..."
mvn test -B -q
if errorlevel 1 (
    call :log_error "Unit Tests fehlgeschlagen"
    exit /b 1
)
call :log_success "Unit Tests erfolgreich"

REM Generate Test Report
call :log_info "Generiere Test Reports..."
mvn surefire-report:report -B -q
if errorlevel 1 (
    call :log_warning "Test Report Generation fehlgeschlagen"
) else (
    call :log_success "Test Reports generiert"
)

call :log_success "Build & Test Phase abgeschlossen"
goto :eof

:quality_analysis
call :log_stage "Code Quality Analysis"

REM SpotBugs Analysis
call :log_info "Fuehre SpotBugs Analyse aus..."
mvn spotbugs:check -B -q
if errorlevel 1 (
    call :log_warning "SpotBugs Analyse hat Probleme gefunden"
) else (
    call :log_success "SpotBugs Analyse erfolgreich"
)

REM PMD Analysis
call :log_info "Fuehre PMD Analyse aus..."
mvn pmd:check -B -q
if errorlevel 1 (
    call :log_warning "PMD Analyse hat Probleme gefunden"
) else (
    call :log_success "PMD Analyse erfolgreich"
)

REM Checkstyle
call :log_info "Fuehre Checkstyle aus..."
mvn checkstyle:check -B -q
if errorlevel 1 (
    call :log_warning "Checkstyle hat Probleme gefunden"
) else (
    call :log_success "Checkstyle erfolgreich"
)

REM Code Coverage
call :log_info "Generiere Code Coverage Report..."
mvn jacoco:report -B -q
if errorlevel 1 (
    call :log_warning "Code Coverage Report Generation fehlgeschlagen"
) else (
    call :log_success "Code Coverage Report generiert"
    if exist "target\site\jacoco\index.html" (
        call :log_info "Coverage Report verfuegbar unter: target\site\jacoco\index.html"
    )
)

call :log_success "Quality Analysis Phase abgeschlossen"
goto :eof

:security_scan
call :log_stage "Security Scan"

REM OWASP Dependency Check
call :log_info "Fuehre OWASP Dependency Check aus..."
mvn org.owasp:dependency-check-maven:check -B -q
if errorlevel 1 (
    call :log_warning "Security Scan hat potenzielle Sicherheitsprobleme gefunden"
) else (
    call :log_success "Security Scan erfolgreich"
)

if exist "target\dependency-check-report.html" (
    call :log_info "Security Report verfuegbar unter: target\dependency-check-report.html"
)

call :log_success "Security Scan Phase abgeschlossen"
goto :eof

:package_application
call :log_stage "Package Application"

REM Package Application
call :log_info "Erstelle Application Package..."
mvn clean package -B -DskipTests -q
if errorlevel 1 (
    call :log_error "Package Creation fehlgeschlagen"
    exit /b 1
)
call :log_success "Application Package erstellt"

REM Create Executable JAR with Dependencies
call :log_info "Erstelle Executable JAR mit Dependencies..."
mvn assembly:single -B -q
if errorlevel 1 (
    call :log_warning "Executable JAR Creation fehlgeschlagen"
) else (
    call :log_success "Executable JAR mit Dependencies erstellt"
)

REM Generate Application Info
call :log_info "Generiere Application Info..."
if not exist "target" mkdir target

echo Build Date: %date% %time% > target\build-info.txt

REM Git Commit (wenn verfuegbar)
git rev-parse HEAD >nul 2>&1
if errorlevel 1 (
    echo Git Commit: Unknown >> target\build-info.txt
) else (
    for /f %%i in ('git rev-parse HEAD') do echo Git Commit: %%i >> target\build-info.txt
)

REM Git Branch (wenn verfuegbar)
git rev-parse --abbrev-ref HEAD >nul 2>&1
if errorlevel 1 (
    echo Git Branch: Unknown >> target\build-info.txt
) else (
    for /f %%i in ('git rev-parse --abbrev-ref HEAD') do echo Git Branch: %%i >> target\build-info.txt
)

echo Build Environment: Local GitHub Actions Simulation >> target\build-info.txt

call :log_success "Package Phase abgeschlossen"
goto :eof

:generate_documentation
call :log_stage "Generate Documentation"

REM Generate JavaDoc
call :log_info "Generiere JavaDoc..."
mvn javadoc:javadoc -B -q
if errorlevel 1 (
    call :log_warning "JavaDoc Generation fehlgeschlagen"
) else (
    call :log_success "JavaDoc generiert"
    if exist "target\site\apidocs\index.html" (
        call :log_info "JavaDoc verfuegbar unter: target\site\apidocs\index.html"
    )
)

REM Generate Site Documentation
call :log_info "Generiere Site Documentation..."
mvn site -B -q
if errorlevel 1 (
    call :log_warning "Site Documentation Generation fehlgeschlagen"
) else (
    call :log_success "Site Documentation generiert"
    if exist "target\site\index.html" (
        call :log_info "Site Documentation verfuegbar unter: target\site\index.html"
    )
)

call :log_success "Documentation Phase abgeschlossen"
goto :eof

:show_summary
call :log_stage "Build Summary"

echo 📦 Erstellte Artifacts:
for %%f in (target\*.jar) do (
    echo   - %%f
)

echo.
echo 📊 Reports:
if exist "target\site\index.html" echo   - Site Documentation: target\site\index.html
if exist "target\site\apidocs\index.html" echo   - JavaDoc: target\site\apidocs\index.html
if exist "target\site\jacoco\index.html" echo   - Code Coverage: target\site\jacoco\index.html
if exist "target\dependency-check-report.html" echo   - Security Report: target\dependency-check-report.html
if exist "target\build-info.txt" echo   - Build Info: target\build-info.txt

echo.
call :log_success "GitHub Actions Pipeline Simulation erfolgreich abgeschlossen! 🎉"
echo.
echo 💡 Tipps:
echo   - Verwende 'mvn clean' um alle generierten Dateien zu loeschen
echo   - Oeffne die HTML Reports in einem Browser fuer detaillierte Analysen
echo   - Dieses Skript simuliert die Standard Ubuntu GitHub Actions Runner
echo.
goto :eof

:main
echo ==========================================
echo 🚀 FIAE24M Kassensystem - GitHub Actions Pipeline Simulation
echo 🔧 Environment: Standard Ubuntu Runner (ubuntu-latest)
echo 📅 Gestartet: %date% %time%
echo ==========================================
echo.

REM Pipeline ausfuehren
call :check_prerequisites
if errorlevel 1 exit /b 1

call :validate_project
if errorlevel 1 exit /b 1

call :build_and_test
if errorlevel 1 exit /b 1

call :quality_analysis
call :security_scan
call :package_application
if errorlevel 1 exit /b 1

call :generate_documentation
call :show_summary
goto :end

:validate_only
call :check_prerequisites
call :validate_project
goto :end

:build_only
call :check_prerequisites
call :validate_project
call :build_and_test
goto :end

:quality_only
call :check_prerequisites
call :quality_analysis
goto :end

:security_only
call :check_prerequisites
call :security_scan
goto :end

:package_only
call :check_prerequisites
call :package_application
goto :end

:docs_only
call :check_prerequisites
call :generate_documentation
goto :end

:show_help
echo FIAE24M Kassensystem - GitHub Actions Pipeline Simulation
echo.
echo Usage: %0 [OPTION]
echo.
echo Options:
echo   --help, -h           Diese Hilfe anzeigen
echo   --validate-only      Nur Projektvalidierung ausfuehren
echo   --build-only         Nur Build ^& Test ausfuehren
echo   --quality-only       Nur Quality Analysis ausfuehren
echo   --security-only      Nur Security Scan ausfuehren
echo   --package-only       Nur Package ausfuehren
echo   --docs-only          Nur Documentation generieren
echo.
echo Beispiele:
echo   %0                   Vollstaendige Pipeline ausfuehren
echo   %0 --build-only      Nur Build und Tests ausfuehren
echo   %0 --quality-only    Nur Code Quality Checks ausfuehren
echo.
goto :end

:end
endlocal

@echo off
REM ===================================================================
REM Build-Skript für FIAE24M Kassensystem
REM ===================================================================

echo.
echo ================================
echo  FIAE24M Kassensystem CI/CD
echo ================================
echo.

REM Überprüfe Maven-Installation
mvn --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Maven ist nicht installiert oder nicht im PATH verfügbar!
    echo Bitte installieren Sie Maven und fügen Sie es zum PATH hinzu.
    pause
    exit /b 1
)

echo [INFO] Maven gefunden. Starte Build-Prozess...
echo.

REM Phase 1: Clean und Compile
echo ================================
echo  Phase 1: Clean und Compile
echo ================================
call mvn clean compile
if %errorlevel% neq 0 (
    echo [ERROR] Compile fehlgeschlagen!
    pause
    exit /b 1
)
echo [SUCCESS] Compile erfolgreich abgeschlossen.
echo.

REM Phase 2: Tests ausführen
echo ================================
echo  Phase 2: Unit Tests
echo ================================
call mvn test
if %errorlevel% neq 0 (
    echo [ERROR] Tests fehlgeschlagen!
    pause
    exit /b 1
)
echo [SUCCESS] Alle Tests erfolgreich abgeschlossen.
echo.

REM Phase 3: Code Quality Checks
echo ================================
echo  Phase 3: Code Quality
echo ================================

echo [INFO] Checkstyle wird ausgeführt...
call mvn checkstyle:check
if %errorlevel% neq 0 (
    echo [WARNING] Checkstyle-Violations gefunden. Siehe Ausgabe oben.
    echo [INFO] Prozess wird fortgesetzt...
) else (
    echo [SUCCESS] Checkstyle: Keine Violations gefunden.
)
echo.

echo [INFO] PMD wird ausgeführt...
call mvn pmd:check
if %errorlevel% neq 0 (
    echo [WARNING] PMD-Violations gefunden. Siehe Ausgabe oben.
    echo [INFO] Prozess wird fortgesetzt...
) else (
    echo [SUCCESS] PMD: Keine Violations gefunden.
)
echo.

echo [INFO] SpotBugs wird ausgeführt...
call mvn spotbugs:check
if %errorlevel% neq 0 (
    echo [WARNING] SpotBugs-Violations gefunden. Siehe Ausgabe oben.
    echo [INFO] Prozess wird fortgesetzt...
) else (
    echo [SUCCESS] SpotBugs: Keine Violations gefunden.
)
echo.

REM Phase 4: Packaging
echo ================================
echo  Phase 4: Packaging
echo ================================
call mvn package -DskipTests
if %errorlevel% neq 0 (
    echo [ERROR] Packaging fehlgeschlagen!
    pause
    exit /b 1
)
echo [SUCCESS] Standard JAR erstellt.

call mvn assembly:single
if %errorlevel% neq 0 (
    echo [ERROR] Fat JAR Erstellung fehlgeschlagen!
    pause
    exit /b 1
)
echo [SUCCESS] Fat JAR erstellt.
echo.

REM Phase 5: Documentation
echo ================================
echo  Phase 5: Documentation
echo ================================
call mvn javadoc:javadoc
if %errorlevel% neq 0 (
    echo [WARNING] JavaDoc-Generierung fehlgeschlagen.
) else (
    echo [SUCCESS] JavaDoc generiert.
)

call mvn site
if %errorlevel% neq 0 (
    echo [WARNING] Site-Generierung fehlgeschlagen.
) else (
    echo [SUCCESS] Maven Site generiert.
)
echo.

REM Build-Informationen anzeigen
echo ================================
echo  Build Summary
echo ================================
echo.
echo Build-Datum: %date% %time%
echo Verzeichnis: %cd%
echo.
echo Artefakte im target/ Verzeichnis:
dir target\*.jar /b 2>nul
echo.
echo Reports verfügbar in:
echo - target\site\jacoco\index.html (Code Coverage)
echo - target\site\checkstyle.html (Checkstyle Report)
echo - target\site\pmd.html (PMD Report)
echo - target\site\spotbugs.html (SpotBugs Report)
echo - target\site\apidocs\index.html (JavaDoc)
echo.

echo ================================
echo  BUILD ERFOLGREICH ABGESCHLOSSEN
echo ================================
echo.
echo Die CI/CD Pipeline wurde lokal simuliert.
echo Alle Artefakte sind im 'target' Verzeichnis verfügbar.
echo.

pause

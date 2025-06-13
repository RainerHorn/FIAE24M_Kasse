#!/bin/bash

# =============================================================================
# FIAE24M Kassensystem - GitHub Actions CI/CD Pipeline Simulation Script
# =============================================================================
# Beschreibung: Simuliert die GitHub Actions CI/CD Pipeline lokal für 
#               Entwicklungszwecke und Tests. Verwendet Standard-Ubuntu-Runner.
# 
# Version: 1.0
# Autor: FIAE24M Team
# Letzte Änderung: $(date '+%Y-%m-%d')
# =============================================================================

set -e  # Exit bei erstem Fehler

# Farben für bessere Ausgabe
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Logging-Funktionen
log_info() {
    echo -e "${BLUE}ℹ️  INFO:${NC} $1"
}

log_success() {
    echo -e "${GREEN}✅ SUCCESS:${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}⚠️  WARNING:${NC} $1"
}

log_error() {
    echo -e "${RED}❌ ERROR:${NC} $1"
}

log_stage() {
    echo -e "\n${BLUE}===========================================${NC}"
    echo -e "${BLUE}🚀 STAGE: $1${NC}"
    echo -e "${BLUE}===========================================${NC}\n"
}

# Hilfsfunktionen
check_prerequisites() {
    log_stage "Checking Prerequisites"
    
    # Java prüfen
    if ! command -v java &> /dev/null; then
        log_error "Java ist nicht installiert oder nicht im PATH verfügbar"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt "17" ]; then
        log_error "Java 17 oder höher erforderlich. Aktuelle Version: $JAVA_VERSION"
        exit 1
    fi
    log_success "Java Version: $(java -version 2>&1 | head -n 1)"
    
    # Maven prüfen
    if ! command -v mvn &> /dev/null; then
        log_error "Maven ist nicht installiert oder nicht im PATH verfügbar"
        exit 1
    fi
    log_success "Maven Version: $(mvn -version | head -n 1)"
    
    # Git prüfen
    if ! command -v git &> /dev/null; then
        log_warning "Git ist nicht installiert - einige Features könnten nicht funktionieren"
    else
        log_success "Git Version: $(git --version)"
    fi
}

validate_project() {
    log_stage "Project Structure Validation"
    
    # Projektstruktur validieren
    local required_files=("pom.xml" "src/main/java" "src/test/java" "src/main/resources/fxml/main.fxml")
    
    for file in "${required_files[@]}"; do
        if [ ! -e "$file" ]; then
            log_error "Erforderliche Datei/Verzeichnis nicht gefunden: $file"
            exit 1
        fi
        log_success "Gefunden: $file"
    done
    
    log_success "Projektstruktur ist gültig"
}

build_and_test() {
    log_stage "Build & Test"
    
    # Cache Maven Dependencies (simuliert)
    log_info "Verwende Maven Local Repository Cache..."
    
    # Compile Code
    log_info "Kompiliere Code..."
    if mvn clean compile -B -q; then
        log_success "Kompilierung erfolgreich"
    else
        log_error "Kompilierung fehlgeschlagen"
        exit 1
    fi
    
    # Run Unit Tests
    log_info "Führe Unit Tests aus..."
    if mvn test -B -q; then
        log_success "Unit Tests erfolgreich"
    else
        log_error "Unit Tests fehlgeschlagen"
        exit 1
    fi
    
    # Generate Test Report
    log_info "Generiere Test Reports..."
    mvn surefire-report:report -B -q || log_warning "Test Report Generation fehlgeschlagen"
    
    log_success "Build & Test Phase abgeschlossen"
}

quality_analysis() {
    log_stage "Code Quality Analysis"
    
    # SpotBugs Analysis
    log_info "Führe SpotBugs Analyse aus..."
    if mvn spotbugs:check -B -q; then
        log_success "SpotBugs Analyse erfolgreich"
    else
        log_warning "SpotBugs Analyse hat Probleme gefunden"
    fi
    
    # PMD Analysis
    log_info "Führe PMD Analyse aus..."
    if mvn pmd:check -B -q; then
        log_success "PMD Analyse erfolgreich"
    else
        log_warning "PMD Analyse hat Probleme gefunden"
    fi
    
    # Checkstyle
    log_info "Führe Checkstyle aus..."
    if mvn checkstyle:check -B -q; then
        log_success "Checkstyle erfolgreich"
    else
        log_warning "Checkstyle hat Probleme gefunden"
    fi
    
    # Code Coverage
    log_info "Generiere Code Coverage Report..."
    if mvn jacoco:report -B -q; then
        log_success "Code Coverage Report generiert"
        if [ -f "target/site/jacoco/index.html" ]; then
            log_info "Coverage Report verfügbar unter: target/site/jacoco/index.html"
        fi
    else
        log_warning "Code Coverage Report Generation fehlgeschlagen"
    fi
    
    log_success "Quality Analysis Phase abgeschlossen"
}

security_scan() {
    log_stage "Security Scan"
    
    # OWASP Dependency Check
    log_info "Führe OWASP Dependency Check aus..."
    if mvn org.owasp:dependency-check-maven:check -B -q; then
        log_success "Security Scan erfolgreich"
    else
        log_warning "Security Scan hat potenzielle Sicherheitsprobleme gefunden"
    fi
    
    if [ -f "target/dependency-check-report.html" ]; then
        log_info "Security Report verfügbar unter: target/dependency-check-report.html"
    fi
    
    log_success "Security Scan Phase abgeschlossen"
}

package_application() {
    log_stage "Package Application"
    
    # Package Application
    log_info "Erstelle Application Package..."
    if mvn clean package -B -DskipTests -q; then
        log_success "Application Package erstellt"
    else
        log_error "Package Creation fehlgeschlagen"
        exit 1
    fi
    
    # Create Executable JAR with Dependencies
    log_info "Erstelle Executable JAR mit Dependencies..."
    if mvn assembly:single -B -q; then
        log_success "Executable JAR mit Dependencies erstellt"
    else
        log_warning "Executable JAR Creation fehlgeschlagen"
    fi
    
    # Generate Application Info
    log_info "Generiere Application Info..."
    mkdir -p target
    {
        echo "Build Date: $(date)"
        echo "Git Commit: $(git rev-parse HEAD 2>/dev/null || echo 'Unknown')"
        echo "Git Branch: $(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo 'Unknown')"
        echo "Build Environment: Local GitHub Actions Simulation"
    } > target/build-info.txt
    
    log_success "Package Phase abgeschlossen"
}

generate_documentation() {
    log_stage "Generate Documentation"
    
    # Generate JavaDoc
    log_info "Generiere JavaDoc..."
    if mvn javadoc:javadoc -B -q; then
        log_success "JavaDoc generiert"
        if [ -d "target/site/apidocs" ]; then
            log_info "JavaDoc verfügbar unter: target/site/apidocs/index.html"
        fi
    else
        log_warning "JavaDoc Generation fehlgeschlagen"
    fi
    
    # Generate Site Documentation
    log_info "Generiere Site Documentation..."
    if mvn site -B -q; then
        log_success "Site Documentation generiert"
        if [ -f "target/site/index.html" ]; then
            log_info "Site Documentation verfügbar unter: target/site/index.html"
        fi
    else
        log_warning "Site Documentation Generation fehlgeschlagen"
    fi
    
    log_success "Documentation Phase abgeschlossen"
}

show_summary() {
    log_stage "Build Summary"
    
    echo "📦 Erstellte Artifacts:"
    find target -name "*.jar" -type f | while read -r jar; do
        echo "  - $jar ($(du -h "$jar" | cut -f1))"
    done
    
    echo ""
    echo "📊 Reports:"
    [ -f "target/site/index.html" ] && echo "  - Site Documentation: target/site/index.html"
    [ -f "target/site/apidocs/index.html" ] && echo "  - JavaDoc: target/site/apidocs/index.html"
    [ -f "target/site/jacoco/index.html" ] && echo "  - Code Coverage: target/site/jacoco/index.html"
    [ -f "target/dependency-check-report.html" ] && echo "  - Security Report: target/dependency-check-report.html"
    [ -f "target/build-info.txt" ] && echo "  - Build Info: target/build-info.txt"
    
    echo ""
    log_success "GitHub Actions Pipeline Simulation erfolgreich abgeschlossen! 🎉"
    echo ""
    echo "💡 Tipps:"
    echo "  - Verwende 'mvn clean' um alle generierten Dateien zu löschen"
    echo "  - Öffne die HTML Reports in einem Browser für detaillierte Analysen"
    echo "  - Dieses Skript simuliert die Standard Ubuntu GitHub Actions Runner"
    echo ""
}

# Hauptfunktion
main() {
    echo "=========================================="
    echo "🚀 FIAE24M Kassensystem - GitHub Actions Pipeline Simulation"
    echo "🔧 Environment: Standard Ubuntu Runner (ubuntu-latest)"
    echo "📅 Gestartet: $(date)"
    echo "=========================================="
    echo ""
    
    # Pipeline ausführen
    check_prerequisites
    validate_project
    build_and_test
    quality_analysis
    security_scan
    package_application
    generate_documentation
    show_summary
}

# Script Optionen
case "${1:-}" in
    --help|-h)
        echo "FIAE24M Kassensystem - GitHub Actions Pipeline Simulation"
        echo ""
        echo "Usage: $0 [OPTION]"
        echo ""
        echo "Options:"
        echo "  --help, -h           Diese Hilfe anzeigen"
        echo "  --validate-only      Nur Projektvalidierung ausführen"
        echo "  --build-only         Nur Build & Test ausführen"
        echo "  --quality-only       Nur Quality Analysis ausführen"
        echo "  --security-only      Nur Security Scan ausführen"
        echo "  --package-only       Nur Package ausführen"
        echo "  --docs-only          Nur Documentation generieren"
        echo ""
        echo "Beispiele:"
        echo "  $0                   Vollständige Pipeline ausführen"
        echo "  $0 --build-only      Nur Build und Tests ausführen"
        echo "  $0 --quality-only    Nur Code Quality Checks ausführen"
        echo ""
        ;;
    --validate-only)
        check_prerequisites
        validate_project
        ;;
    --build-only)
        check_prerequisites
        validate_project
        build_and_test
        ;;
    --quality-only)
        check_prerequisites
        quality_analysis
        ;;
    --security-only)
        check_prerequisites
        security_scan
        ;;
    --package-only)
        check_prerequisites
        package_application
        ;;
    --docs-only)
        check_prerequisites
        generate_documentation
        ;;
    *)
        main
        ;;
esac

#!/bin/bash
# ===================================================================
# GitLab CI/CD Build-Skript für FIAE24M Kassensystem (Bash)
# ===================================================================
# Simuliert die GitLab CI/CD Pipeline lokal
# Äquivalent zu build.ps1 für PowerShell/GitHub Actions
# ===================================================================

set -e  # Exit bei Fehlern

# Farben für Output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo ""
echo -e "${CYAN}================================${NC}"
echo -e "${CYAN} FIAE24M Kassensystem GitLab CI${NC}"
echo -e "${CYAN}================================${NC}"
echo ""

# Überprüfe Maven-Installation
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}[ERROR] Maven ist nicht installiert oder nicht im PATH verfügbar!${NC}"
    echo -e "${YELLOW}Bitte installieren Sie Maven und fügen Sie es zum PATH hinzu.${NC}"
    exit 1
fi

echo -e "${GREEN}[INFO] Maven gefunden. Starte GitLab CI/CD Simulation...${NC}"
mvn --version
echo ""

# Maven CLI Optionen (wie in GitLab CI)
MAVEN_CLI_OPTS="--batch-mode --errors --fail-at-end --show-version"

# ==========================================
# VALIDATION STAGE
# ==========================================
echo -e "${CYAN}================================${NC}"
echo -e "${CYAN} Stage: Validation${NC}"
echo -e "${CYAN}================================${NC}"

echo -e "${BLUE}[INFO] 🔍 Validating project structure...${NC}"
if [[ ! -f pom.xml ]]; then
    echo -e "${RED}[ERROR] pom.xml nicht gefunden!${NC}"
    exit 1
fi

if [[ ! -d src/main/java ]]; then
    echo -e "${RED}[ERROR] src/main/java Verzeichnis nicht gefunden!${NC}"
    exit 1
fi

if [[ ! -d src/test/java ]]; then
    echo -e "${RED}[ERROR] src/test/java Verzeichnis nicht gefunden!${NC}"
    exit 1
fi

if [[ ! -f src/main/resources/fxml/main.fxml ]]; then
    echo -e "${RED}[ERROR] main.fxml nicht gefunden!${NC}"
    exit 1
fi

echo -e "${GREEN}[SUCCESS] ✅ Project structure is valid${NC}"
echo ""

# ==========================================
# BUILD & TEST STAGE
# ==========================================
echo -e "${CYAN}================================${NC}"
echo -e "${CYAN} Stage: Build & Test${NC}"
echo -e "${CYAN}================================${NC}"

echo -e "${BLUE}[INFO] 🔨 Compiling code...${NC}"
if ! mvn $MAVEN_CLI_OPTS clean compile; then
    echo -e "${RED}[ERROR] Compile fehlgeschlagen!${NC}"
    exit 1
fi
echo -e "${GREEN}[SUCCESS] Compile erfolgreich abgeschlossen.${NC}"
echo ""

echo -e "${BLUE}[INFO] 🧪 Running unit tests...${NC}"
if ! mvn $MAVEN_CLI_OPTS test; then
    echo -e "${RED}[ERROR] Tests fehlgeschlagen!${NC}"
    exit 1
fi
echo -e "${GREEN}[SUCCESS] Alle Tests erfolgreich abgeschlossen.${NC}"
echo ""

echo -e "${BLUE}[INFO] 📊 Generating test reports...${NC}"
mvn $MAVEN_CLI_OPTS surefire-report:report
echo ""

echo -e "${BLUE}[INFO] 📈 Generating code coverage...${NC}"
mvn $MAVEN_CLI_OPTS jacoco:report
echo ""

# ==========================================
# QUALITY ANALYSIS STAGE
# ==========================================
echo -e "${CYAN}================================${NC}"
echo -e "${CYAN} Stage: Quality Analysis${NC}"
echo -e "${CYAN}================================${NC}"

echo -e "${BLUE}[INFO] 🔍 Starting code quality analysis...${NC}"
echo ""

# SpotBugs
echo -e "${BLUE}[INFO] 🐛 Running SpotBugs analysis...${NC}"
if ! mvn $MAVEN_CLI_OPTS spotbugs:check; then
    echo -e "${YELLOW}[WARNING] SpotBugs violations found. Siehe Ausgabe oben.${NC}"
    echo -e "${BLUE}[INFO] Prozess wird fortgesetzt...${NC}"
else
    echo -e "${GREEN}[SUCCESS] SpotBugs: Keine Violations gefunden.${NC}"
fi
echo ""

# PMD
echo -e "${BLUE}[INFO] 📝 Running PMD analysis...${NC}"
if ! mvn $MAVEN_CLI_OPTS pmd:check; then
    echo -e "${YELLOW}[WARNING] PMD violations found. Siehe Ausgabe oben.${NC}"
    echo -e "${BLUE}[INFO] Prozess wird fortgesetzt...${NC}"
else
    echo -e "${GREEN}[SUCCESS] PMD: Keine Violations gefunden.${NC}"
fi
echo ""

# Checkstyle
echo -e "${BLUE}[INFO] 🎯 Running Checkstyle analysis...${NC}"
if ! mvn $MAVEN_CLI_OPTS checkstyle:check; then
    echo -e "${YELLOW}[WARNING] Checkstyle violations found. Siehe Ausgabe oben.${NC}"
    echo -e "${BLUE}[INFO] Prozess wird fortgesetzt...${NC}"
else
    echo -e "${GREEN}[SUCCESS] Checkstyle: Keine Violations gefunden.${NC}"
fi
echo ""

# ==========================================
# SECURITY SCAN STAGE
# ==========================================
echo -e "${CYAN}================================${NC}"
echo -e "${CYAN} Stage: Security Scan${NC}"
echo -e "${CYAN}================================${NC}"

echo -e "${BLUE}[INFO] 🔒 Starting security vulnerability scan...${NC}"
echo -e "${BLUE}[INFO] 🛡️ Running OWASP Dependency Check...${NC}"
if ! mvn $MAVEN_CLI_OPTS org.owasp:dependency-check-maven:check; then
    echo -e "${YELLOW}[WARNING] Security vulnerabilities found. Siehe Report.${NC}"
else
    echo -e "${GREEN}[SUCCESS] Security scan completed.${NC}"
fi
echo ""

# ==========================================
# PACKAGE STAGE
# ==========================================
echo -e "${CYAN}================================${NC}"
echo -e "${CYAN} Stage: Package${NC}"
echo -e "${CYAN}================================${NC}"

echo -e "${BLUE}[INFO] 📦 Starting application packaging...${NC}"

echo -e "${BLUE}[INFO] 🔨 Creating standard JAR...${NC}"
if ! mvn $MAVEN_CLI_OPTS package -DskipTests; then
    echo -e "${RED}[ERROR] Standard JAR packaging fehlgeschlagen!${NC}"
    exit 1
fi
echo -e "${GREEN}[SUCCESS] Standard JAR erstellt.${NC}"

echo -e "${BLUE}[INFO] 📦 Creating executable JAR with dependencies...${NC}"
if ! mvn $MAVEN_CLI_OPTS assembly:single; then
    echo -e "${RED}[ERROR] Fat JAR Erstellung fehlgeschlagen!${NC}"
    exit 1
fi
echo -e "${GREEN}[SUCCESS] Fat JAR erstellt.${NC}"

echo -e "${BLUE}[INFO] 📋 Generating build information...${NC}"
mkdir -p target/build-info
echo "Build Date: $(date)" > target/build-info/build-info.txt
echo "Git Commit: $(git rev-parse HEAD 2>/dev/null || echo 'N/A')" >> target/build-info/build-info.txt
echo "Git Branch: $(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo 'N/A')" >> target/build-info/build-info.txt
echo "User: $(whoami)" >> target/build-info/build-info.txt
echo ""

# ==========================================
# DOCUMENTATION STAGE
# ==========================================
echo -e "${CYAN}================================${NC}"
echo -e "${CYAN} Stage: Documentation${NC}"
echo -e "${CYAN}================================${NC}"

echo -e "${BLUE}[INFO] 📚 Starting documentation generation...${NC}"

# JavaDoc
echo -e "${BLUE}[INFO] 📖 Generating JavaDoc...${NC}"
if ! mvn $MAVEN_CLI_OPTS javadoc:javadoc; then
    echo -e "${YELLOW}[WARNING] JavaDoc-Generierung fehlgeschlagen.${NC}"
else
    echo -e "${GREEN}[SUCCESS] JavaDoc generiert.${NC}"
fi

# Maven Site
echo -e "${BLUE}[INFO] 🌐 Generating Maven Site...${NC}"
if ! mvn $MAVEN_CLI_OPTS site -DskipTests; then
    echo -e "${YELLOW}[WARNING] Site-Generierung fehlgeschlagen.${NC}"
else
    echo -e "${GREEN}[SUCCESS] Maven Site generiert.${NC}"
fi
echo ""

# ==========================================
# NOTIFICATION & SUMMARY
# ==========================================
echo -e "${CYAN}================================${NC}"
echo -e "${CYAN} Pipeline Summary${NC}"
echo -e "${CYAN}================================${NC}"
echo ""

BUILD_TIME=$(date '+%d.%m.%Y %H:%M:%S')
echo -e "${NC}Build-Zeit: ${BUILD_TIME}${NC}"
echo -e "${NC}Verzeichnis: $(pwd)${NC}"
echo ""

echo -e "${YELLOW}Artefakte im target/ Verzeichnis:${NC}"
if [[ -d target ]]; then
    find target -name "*.jar" -type f | while read jar; do
        echo -e "${NC}- $(basename "$jar")${NC}"
    done
else
    echo -e "${RED}- Keine JAR-Dateien gefunden${NC}"
fi
echo ""

echo -e "${YELLOW}Reports verfügbar in:${NC}"
REPORTS=(
    "target/site/jacoco/index.html (Code Coverage)"
    "target/site/checkstyle.html (Checkstyle Report)"
    "target/site/pmd.html (PMD Report)"
    "target/site/spotbugs.html (SpotBugs Report)"
    "target/site/apidocs/index.html (JavaDoc)"
    "target/dependency-check-report.html (Security Report)"
)

for report in "${REPORTS[@]}"; do
    path=$(echo "$report" | cut -d' ' -f1)
    if [[ -f "$path" ]]; then
        echo -e "${GREEN}- $report${NC}"
    else
        echo -e "${RED}- $report [NICHT VERFÜGBAR]${NC}"
    fi
done

echo ""
echo -e "${GREEN}================================${NC}"
echo -e "${GREEN} ✅ GITLAB CI/CD SIMULATION${NC}"
echo -e "${GREEN}    ERFOLGREICH ABGESCHLOSSEN${NC}"
echo -e "${GREEN}================================${NC}"
echo ""
echo -e "${NC}Die GitLab CI/CD Pipeline wurde lokal simuliert.${NC}"
echo -e "${NC}Alle Artefakte sind im 'target' Verzeichnis verfügbar.${NC}"
echo ""

# GitLab-spezifische Informationen
echo -e "${BLUE}🚀 GitLab CI/CD Deployment:${NC}"
echo -e "${NC}1. Committen Sie .gitlab-ci.yml ins Repository${NC}"
echo -e "${NC}2. Konfigurieren Sie GitLab Runner mit Tag 'mmbbs3'${NC}"
echo -e "${NC}3. Push zu GitLab löst Pipeline automatisch aus${NC}"
echo ""

# Optional: Öffne Coverage Report (Linux/Mac)
if command -v xdg-open &> /dev/null; then
    read -p "Möchten Sie den Coverage Report öffnen? (j/n): " OPEN_COVERAGE
    if [[ "$OPEN_COVERAGE" == "j" || "$OPEN_COVERAGE" == "J" ]]; then
        if [[ -f "target/site/jacoco/index.html" ]]; then
            xdg-open "target/site/jacoco/index.html"
            echo -e "${GREEN}Coverage Report wird geöffnet...${NC}"
        else
            echo -e "${RED}Coverage Report nicht gefunden.${NC}"
        fi
    fi
elif command -v open &> /dev/null; then
    read -p "Möchten Sie den Coverage Report öffnen? (j/n): " OPEN_COVERAGE
    if [[ "$OPEN_COVERAGE" == "j" || "$OPEN_COVERAGE" == "J" ]]; then
        if [[ -f "target/site/jacoco/index.html" ]]; then
            open "target/site/jacoco/index.html"
            echo -e "${GREEN}Coverage Report wird geöffnet...${NC}"
        else
            echo -e "${RED}Coverage Report nicht gefunden.${NC}"
        fi
    fi
fi

echo ""
read -p "Drücken Sie Enter zum Beenden..."

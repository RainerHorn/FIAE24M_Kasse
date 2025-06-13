# Deployment Guide - FIAE24M Kassensystem

## Übersicht

Dieser Guide beschreibt die Deployment-Optionen für das FIAE24M Kassensystem mit der CI/CD Pipeline.

## 🚀 Quick Start

### Lokales Development
```bash
# Repository klonen
git clone <repository-url>
cd FIAE24M_Kasse

# Build ausführen (Windows)
.\build.ps1

# Oder manuell
mvn clean package
java -jar target/fiae24m-kasse-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### CI/CD Pipeline Setup

#### 1. Self-Hosted Runner Einrichtung
```bash
# 1. GitHub Runner herunterladen
# 2. Runner konfigurieren mit Tag: mmbbs3
./config.cmd --url https://github.com/<your-org>/<repo> --token <token> --labels mmbbs3

# 3. Runner als Service installieren
./svc.cmd install
./svc.cmd start
```

#### 2. Required Software auf Runner
- **Java 17 & 21**: OpenJDK oder Oracle JDK
- **Maven 3.6+**: Build-Tool
- **Git**: Version Control
- **Windows/Linux**: OS-Unterstützung

## 📦 Deployment-Optionen

### Option 1: Standalone JAR (Empfohlen)
```bash
# Fat JAR mit allen Dependencies
java -jar target/fiae24m-kasse-1.0-SNAPSHOT-jar-with-dependencies.jar
```

**Vorteile:**
- Keine externe Dependencies
- Einfache Distribution
- Portabel zwischen Systemen

### Option 2: Standard JAR + Classpath
```bash
# Dependencies separat verwalten
java -cp "target/fiae24m-kasse-1.0-SNAPSHOT.jar:lib/*" de.berufsschule.kasse.App
```

### Option 3: JavaFX Runtime Distribution
```bash
# Mit jlink custom runtime erstellen
jlink --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml,java.desktop --output runtime
./runtime/bin/java -jar app.jar
```

## 🔧 Konfiguration

### Datenbank-Setup
```java
// Standard SQLite (kasse.db wird automatisch erstellt)
// Konfiguration in DatabaseManager.java

// Custom Database Path
System.setProperty("db.path", "/custom/path/kasse.db");
```

### JavaFX Headless Mode (Server)
```bash
# Für Server-Deployment ohne Display
java -Djava.awt.headless=true -Dtestfx.robot=glass -Dtestfx.headless=true -jar app.jar
```

## 🏗️ CI/CD Pipeline Triggers

### Automatische Builds
- **Push** auf `main`, `develop`, `feature/*`
- **Pull Request** auf `main`, `develop`
- **Release** Events

### Manuelle Builds
```bash
# GitHub Actions UI
# Repository → Actions → CI/CD Pipeline → Run workflow
```

## 📊 Monitoring & Quality Gates

### Build Status
```
✅ Build & Test (Java 17, 21)
✅ Quality Analysis (SpotBugs, PMD, Checkstyle)
✅ Security Scan (OWASP)
✅ Package (JAR Creation)
✅ Documentation (JavaDoc, Site)
```

### Quality Metrics
- **Code Coverage**: ≥80% (JaCoCo)
- **Security**: CVSS < 8.0 (OWASP)
- **Code Style**: Checkstyle konform
- **Test Success**: 50/50 Tests grün

## 🔐 Security Considerations

### Dependencies
```bash
# Security Scan ausführen
mvn org.owasp:dependency-check-maven:check

# Report prüfen
target/dependency-check-report.html
```

### Database Security
- SQLite Datei-Permissions setzen
- Backup-Strategie implementieren
- Sensitive Daten verschlüsseln

## 📈 Performance Tuning

### JVM Settings
```bash
# Für bessere Performance
java -Xmx2G -Xms512M -XX:+UseG1GC -jar app.jar

# Für Development
java -Xmx1G -Xms256M -jar app.jar
```

### Database Optimierung
```sql
-- SQLite Performance
PRAGMA journal_mode = WAL;
PRAGMA synchronous = NORMAL;
PRAGMA cache_size = 10000;
```

## 🐛 Troubleshooting

### Häufige Probleme

#### 1. JavaFX Module Issues
```bash
# Lösung: JavaFX explizit hinzufügen
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar app.jar
```

#### 2. Database Lock Issues
```bash
# Lösung: Stale Connections bereinigen
rm kasse.db-shm kasse.db-wal
```

#### 3. Build Failures
```bash
# Lösung: Clean Build
mvn clean
rm -rf target/
mvn package
```

#### 4. Test Failures (Headless)
```bash
# Lösung: Display Setup für Tests
export DISPLAY=:99
Xvfb :99 -ac -screen 0 1024x768x8 &
```

### Log-Analyse
```bash
# Application Logs
tail -f application.log

# Pipeline Logs
# GitHub Actions → Workflow → Job → Step
```

## 🔄 Rollback-Strategie

### Version Rollback
```bash
# 1. Previous Release identifizieren
git tag -l

# 2. Rollback durchführen
git checkout v1.0.0
mvn clean package
```

### Database Rollback
```bash
# 1. Backup restore
cp kasse_backup.db kasse.db

# 2. Migration script
java -cp app.jar DatabaseMigration --rollback
```

## 📋 Deployment Checklist

### Pre-Deployment
- [ ] Alle Tests grün ✅
- [ ] Quality Gates bestanden ✅
- [ ] Security Scan OK ✅
- [ ] Documentation aktuell ✅
- [ ] Database Backup erstellt
- [ ] Config Review abgeschlossen

### Deployment
- [ ] Runner verfügbar (mmbbs3)
- [ ] Pipeline getriggert
- [ ] Artefakte validiert
- [ ] Smoke Tests durchgeführt

### Post-Deployment
- [ ] Application Health Check
- [ ] Performance Monitoring
- [ ] Error Logs überprüft
- [ ] User Acceptance Testing

## 🔗 Useful Links

- **GitHub Repository**: `https://github.com/<org>/<repo>`
- **CI/CD Pipeline**: `https://github.com/<org>/<repo>/actions`
- **Documentation**: `target/site/index.html`
- **Coverage Report**: `target/site/jacoco/index.html`
- **Security Report**: `target/dependency-check-report.html`

---

**Version**: 1.0  
**Letzte Aktualisierung**: Juni 2025  
**Maintainer**: FIAE24M Team

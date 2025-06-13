# CI/CD Pipeline - FIAE24M Kassensystem

## Übersicht

Diese CI/CD Pipeline wurde für das FIAE24M Kassensystem entwickelt und verwendet **GitHub Standard Runner (ubuntu-latest)** für optimale Kompatibilität und Performance.

## Pipeline-Architektur

Die Pipeline besteht aus 7 Hauptjobs, die in folgender Reihenfolge ausgeführt werden:

### 1. Build & Test Job (`build-and-test`)

- **Runner**: ubuntu-latest
- **Matrix Build**: Java 17 und 21
- **Aufgaben**:
  - Projekt-Struktur validieren
  - Code kompilieren
  - Unit-Tests ausführen (50 Tests)
  - JaCoCo Coverage Report generieren
  - Test-Artefakte hochladen

### 2. Quality Analysis Job (`quality-analysis`)
- **Runner**: ubuntu-latest
- **Abhängigkeiten**: build-and-test
- **Aufgaben**:
  - SpotBugs statische Code-Analyse
  - PMD Code-Qualitätsprüfung
  - Checkstyle Code-Style-Prüfung
  - Code Coverage zu Codecov hochladen
  - Quality Reports als Artefakte speichern

### 3. Security Scan Job (`security-scan`)
- **Runner**: ubuntu-latest
- **Abhängigkeiten**: build-and-test
- **Aufgaben**:
  - OWASP Dependency Check
  - Sicherheitsbericht als Artefakt speichern

### 4. Package Job (`package`)
- **Runner**: ubuntu-latest
- **Abhängigkeiten**: build-and-test, quality-analysis
- **Auslöser**: Nur bei main-Branch oder Release
- **Aufgaben**:
  - Anwendung paketieren
  - Fat JAR mit allen Abhängigkeiten erstellen
  - Build-Informationen generieren
  - Deployment-Artefakte hochladen

### 5. Documentation Job (`documentation`)
- **Runner**: ubuntu-latest
- **Abhängigkeiten**: build-and-test
- **Aufgaben**:  - JavaDoc generieren
  - Maven Site Documentation erstellen
  - Dokumentation als Artefakte speichern

### 6. Release Job (`release`)

- **Runner**: ubuntu-latest
- **Abhängigkeiten**: package, documentation, security-scan
- **Auslöser**: Nur bei GitHub Release
- **Aufgaben**:
  - Release-Packages erstellen
  - Assets an GitHub Release anhängen

### 7. Notification Job (`notify`)

- **Runner**: ubuntu-latest
- **Abhängigkeiten**: build-and-test, quality-analysis, security-scan
- **Aufgaben**:
  - Erfolgs-/Fehlermeldungen ausgeben
  - Pipeline-Status kommunizieren

## Auslöser (Triggers)

Die Pipeline wird ausgelöst bei:
- **Push** auf Branches: `main`, `develop`, `feature/*`
- **Pull Request** auf Branches: `main`, `develop`
- **Release** Events (type: published)

## Quality Gates

### Code Coverage
- **Minimum**: 80% Instruction Coverage
- **Tool**: JaCoCo
- **Integration**: Codecov für Reporting

### Code Quality Tools
1. **SpotBugs**: Statische Analyse für Bug-Erkennung
2. **PMD**: Code-Qualität und Best Practices
3. **Checkstyle**: Code-Style und Konventionen

### Security
- **OWASP Dependency Check**: Überprüfung auf bekannte Vulnerabilities
- **Failure Threshold**: CVSS Score >= 8.0

## Artefakte

### Build Artefakte
- `fiae24m-kasse-1.0-SNAPSHOT.jar` - Standard JAR
- `fiae24m-kasse-1.0-SNAPSHOT-jar-with-dependencies.jar` - Fat JAR
- `build-info.txt` - Build-Metadaten

### Quality Reports
- SpotBugs Report
- PMD Report  
- Checkstyle Report
- JaCoCo Coverage Report

### Security Reports
- OWASP Dependency Check Report

### Documentation
- JavaDoc API-Dokumentation
- Maven Site Documentation

## Maven-Plugins Konfiguration

### Kern-Plugins
```xml
<!-- Compiler -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
</plugin>

<!-- Testing -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.0.0</version>
</plugin>
```

### Quality-Plugins
```xml
<!-- Code Coverage -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
</plugin>

<!-- Static Analysis -->
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.7.3.6</version>
</plugin>

<!-- Code Quality -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-pmd-plugin</artifactId>
    <version>3.19.0</version>
</plugin>

<!-- Code Style -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.2.0</version>
</plugin>
```

### Security & Packaging
```xml
<!-- Security -->
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>8.4.0</version>
</plugin>

<!-- Packaging -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-assembly-plugin</artifactId>
    <version>3.4.2</version>
</plugin>
```

## Lokale Pipeline-Simulation

### GitHub Actions Pipeline simulieren

**Linux/Mac:**
```bash
chmod +x build-github.sh
./build-github.sh
```

**Windows:**
```powershell
.\build-github.bat
```

### Einzelne Pipeline-Stages ausführen

**Nur Build & Test:**
```bash
./build-github.sh --build-only
```

**Nur Quality Analysis:**
```bash
./build-github.sh --quality-only
```

**Nur Security Scan:**
```bash
./build-github.sh --security-only
```

### Manuelle Kommandos

#### Alle Tests ausführen
```bash
mvn clean test
```

#### Quality Checks
```bash
mvn checkstyle:check
mvn pmd:check  
mvn spotbugs:check
```

#### Security Scan
```bash
mvn org.owasp:dependency-check-maven:check
```

### Packaging
```bash
mvn clean package
mvn assembly:single
```

### Documentation
```bash
mvn javadoc:javadoc
mvn site
```

## Konfigurationsdateien

### `.github/workflows/ci-cd.yml`
Haupt-Pipeline-Konfiguration mit allen Jobs und Steps.

### `checkstyle.xml`
Checkstyle-Regeln für Code-Style-Prüfung.

### `src/site/site.xml`
Maven Site-Konfiguration für Dokumentation.

### `pom.xml`
Maven-Konfiguration mit allen Plugins und Reporting.

## Monitoring und Wartung

### Artefakt-Aufbewahrung
- **Test Results**: Nach jedem Build
- **Quality Reports**: Nach jedem Build  
- **Security Reports**: Nach jedem Build
- **Application Packages**: Nur bei main/release
- **Documentation**: Nach jedem Build

### Benachrichtigungen
- ✅ **Erfolg**: Pipeline-Details werden ausgegeben
- ❌ **Fehler**: Fehlerhafte Jobs werden identifiziert

## Standard Runner Anforderungen

### Software-Environment
- **Runner**: ubuntu-latest (GitHub Actions Standard Runner)
- **Java**: 17 und 21 erforderlich (automatisch bereitgestellt)
- **Maven**: 3.6+ erforderlich (automatisch bereitgestellt)
- **Git**: Für Checkout (automatisch bereitgestellt)

### Vorteile
- Keine Hardware-Wartung erforderlich
- Automatisch aktualisierte Software-Stack
- Optimale Performance und Skalierbarkeit
- Netzwerkzugriff zu Maven Central Repository

## Troubleshooting

### Häufige Probleme

1. **Test Failures**: 
   - Überprüfen Sie Datenbankverbindung
   - JavaFX Headless-Konfiguration (`DISPLAY=:99`)

2. **Quality Gate Failures**:
   - Checkstyle: Code-Style-Violations beheben
   - PMD: Code-Qualität verbessern
   - SpotBugs: Potentielle Bugs beheben

3. **Security Issues**:
   - Dependencies aktualisieren
   - OWASP Reports überprüfen

4. **Build Issues**:
   - Maven Dependencies Cache leeren
   - Java Version überprüfen (17/21)

## Best Practices

1. **Feature Branches**: Immer von `develop` branchen
2. **Pull Requests**: Vor Merge in `main`/`develop`
3. **Code Quality**: Lokale Checks vor Push
4. **Security**: Regelmäßige Dependency Updates
5. **Documentation**: JavaDoc für Public APIs
6. **Testing**: 80%+ Code Coverage beibehalten

---

**Erstellt für**: FIAE24M Kassensystem  
**Runner**: ubuntu-latest (GitHub Actions Standard Runner)  
**Version**: 2.0  
**Letzte Aktualisierung**: Juni 2025

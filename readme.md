# Pflichtenheft: Kassenprogramm mit JavaFX-GUI und Datenbankanbindung

## 1. Einleitung

Dieses Pflichtenheft beschreibt die Umsetzung des im Lastenheft definierten Kassenprogramms als Anwendung mit einer JavaFX-Benutzeroberfläche und einer persistenten Datenbankanbindung.

## 2. Zielsetzung

Ziel ist die Entwicklung eines einfach bedienbaren Kassensystems für kleine Ladengeschäfte, das folgende Funktionen bereitstellt:

- Produktverwaltung (Name, Preis, Lagerbestand)
- Durchführung von Kassenvorgängen mit Bon-Ausgabe
- Warenzugänge erfassen
- Lagerbestand anzeigen
- Datenbankbasierte Speicherung aller relevanten Daten
- JavaFX-Benutzeroberfläche für Interaktion

## 3. Produktfunktionen

### 3.1 Hauptmenü (JavaFX-Oberfläche)

Nach dem Programmstart wird ein JavaFX-Menü angezeigt mit folgenden Auswahlmöglichkeiten:

- Kassenvorgang starten
- Neues Produkt hinzufügen
- Warenzugang erfassen
- Lagerbestand anzeigen
- Programm beenden

### 3.2 Kassenvorgang

- Anzeige aller verfügbaren Produkte (Name, Preis, Produktnummer)
- Produktauswahl per Produktnummer
- Eingabe der Menge
- Berechnung von Teil- und Gesamtpreis
- Prüfung der Lagerverfügbarkeit
- Wiederholung der Produktauswahl bis Abschluss
- Anzeige des Bons (gekaufte Produkte, Mengen, Preise, Gesamtbetrag)
- Aktualisierung des Lagerbestands in der Datenbank

### 3.3 Neues Produkt hinzufügen

- Eingabe: Name, Preis, Anfangsbestand
- Speicherung in der Produktdatenbank

### 3.4 Warenzugang erfassen

- Auswahl des Produkts
- Eingabe der zu ergänzenden Menge
- Aktualisierung des Lagerbestands in der Datenbank

### 3.5 Lagerbestand anzeigen

- Anzeige aller Produkte mit Name und aktuellem Lagerbestand

### 3.6 Programm beenden

- Beenden der JavaFX-Anwendung
- Sicherstellung, dass alle Verbindungen zur Datenbank ordnungsgemäß geschlossen werden

## 4. Nicht-funktionale Anforderungen

- **Benutzerfreundlichkeit:** Intuitive JavaFX-Oberfläche, klare Navigation
- **Fehlerbehandlung:** Eingabevalidierung, Benutzerfeedback bei Fehlern
- **Stabilität:** Keine Abstürze bei Fehleingaben
- **Wartbarkeit:** Strukturierter, kommentierter Quellcode (Java / JavaFX)
- **Nachhaltigkeit:** Trennung von GUI, Logik und Datenzugriff (MVC-Prinzip)

## 5. Technische Umsetzung

### 5.1 Programmiersprache

- Java (Version 17 oder höher)

### 5.2 GUI

- JavaFX (z. B. SceneBuilder-kompatibel)

### 5.3 Datenbank

- SQLite oder H2 (eingebettet)
- Verwendung von JDBC zur Datenbankanbindung
- Tabellen:
  - `produkte` (id, name, preis, bestand)
  - `verkaeufe` (timestamp, produkt_id, menge, einzelpreis, gesamtpreis)

#### Datenbankinitialisierung

Die Datenbank wird automatisch beim ersten Start erstellt. Für Testzwecke kann die Datenbank mit 100 Produkten und 100 Verkäufen befüllt werden:

```bash
mvn exec:java -Dexec.mainClass="de.berufsschule.kasse.util.DatabaseInitializer"
```

Dies erstellt die Datei `kasse.db` im Projekthauptverzeichnis mit realistischen Testdaten:

- 100 verschiedene Produkte aus verschiedenen Kategorien
- Realistische Preise (0,30€ - 50,00€)
- Verschiedene Lagerbestände (0 - 200 Stück)
- 100 Verkäufe über die letzten 30 Tage verteilt

### 5.4 Persistenz

- Alle Änderungen am Lagerbestand werden sofort in der Datenbank gespeichert.
- Optional: Verkaufshistorie in separater Tabelle erfassen

### 5.5 Datenstrukturen

- Verwendung von `ObservableList`, `Map`, `List`, ggf. JavaFX-TableView für Lagerbestand

### 5.6 Methoden und Modularisierung

- Methoden zur Trennung von GUI-Logik, Geschäftslogik und Datenbankzugriff:
  - `addProdukt(...)`
  - `verkaufeProdukt(...)`
  - `ladeLagerbestand()`
  - `verbindeMitDatenbank()`, etc.

## 6. Testbarkeit

- Alle Ein- und Ausgaben über JavaFX-Schnittstelle
- Testdatenbank mit vordefinierten Produkten möglich
- Nachvollziehbare Tests durch UI-Abläufe

## 7. Abgabe

- Quellcode in einem GitHub-Repository oder ZIP-Ordner
- Dokumentation mit Aufbau, Klassendiagramm und Nutzungsanleitung (optional)
- Der Code muss durchgängig kommentiert und strukturiert sein

## 8. Bewertungskriterien

| Kriterium              | Gewichtung |
|------------------------|------------|
| Funktionale Umsetzung  | 30 %       |
| Codequalität           | 25 %       |
| Benutzerfreundlichkeit | 15 %       |
| Fehlerbehandlung       | 10 %       |
| Einhaltung der Struktur| 10 %       |
| Datenbankanbindung     | 10 %       |

## 9. CI/CD Pipeline

Das Projekt verfügt über eine vollständige CI/CD Pipeline mit folgenden Features:

### 🚀 Pipeline-Jobs
- **Build & Test**: Kompilierung und Ausführung aller 50 Unit-Tests
- **Quality Analysis**: SpotBugs, PMD, Checkstyle Code-Qualitätsprüfung
- **Security Scan**: OWASP Dependency Check für Sicherheit
- **Package**: Erstellung ausführbarer JAR-Dateien
- **Documentation**: JavaDoc und Maven Site Generation
- **Release**: Automatisierte Release-Erstellung
- **Notification**: Pipeline-Status-Benachrichtigungen

### 📊 Quality Gates
- **Code Coverage**: Minimum 80% (JaCoCo)
- **Security**: CVSS Score < 8.0 (OWASP)
- **Code Style**: Checkstyle-Konformität
- **Static Analysis**: SpotBugs und PMD Checks

### 🏗️ Self-Hosted Runner
- **Tag**: `mmbbs3`
- **Java**: 17 und 21 Matrix Build
- **Trigger**: Push/PR auf main, develop, feature/*

### 📖 Dokumentation
Detaillierte Pipeline-Dokumentation: [CI-CD-PIPELINE.md](CI-CD-PIPELINE.md)

## 10. Erweiterungsmöglichkeiten (optional)

- Export des Bons als PDF
- Rabattfunktion
- Produktkategorien
- Benutzerverwaltung
- Dashboard mit Statistiken ✅ **Implementiert**

---

**Hinweis:** Dieses Pflichtenheft erweitert die Mindestanforderungen des Lastenhefts um JavaFX, Datenbankintegration und CI/CD Pipeline zur professionellen Anwendung.

# Aufgabenplan zur Umsetzung des Kassenprogramms (`tasks.md`)

## ✅ Vorbereitungsphase

- [ ] Entwicklungsumgebung einrichten (z. B. IntelliJ, VSCode)
- [ ] JavaFX-Bibliothek einbinden
- [ ] SQLite- oder H2-Datenbank einrichten
- [ ] JDBC-Treiber hinzufügen
- [ ] Projektstruktur anlegen: `/model`, `/view`, `/controller`, `/database`

---

## 🔧 Datenbankanbindung

- [x] Datenbankstruktur entwerfen
  - [x] Tabelle `produkte` (id, name, preis, bestand)
  - [x] Tabelle `verkaeufe` (timestamp, produkt_id, menge, einzelpreis, gesamtpreis)
- [x] Klasse `DatabaseManager` erstellen
  - [x] Verbindung zur Datenbank herstellen
  - [x] Methoden zur Initialisierung der Tabellen schreiben
- [x] CRUD-Methoden für Produkte implementieren
  - [x] Produkte einfügen (`INSERT`)
  - [x] Produkte aktualisieren (`UPDATE`)
  - [x] Lagerbestand lesen (`SELECT`)
  - [x] Lagerbestand erhöhen/verringern
- [x] Datenbank mit 100 Testeinträgen pro Tabelle befüllen (11.06.2025)

---

## 🎨 GUI: JavaFX-Oberfläche

- [ ] Hauptfenster mit Menü anlegen (MainView)
  - [ ] Button: „Kassenvorgang starten“
  - [ ] Button: „Produkt hinzufügen“
  - [ ] Button: „Warenzugang erfassen“
  - [ ] Button: „Lagerbestand anzeigen“
  - [ ] Button: „Beenden“

---

## 🛒 Kassenvorgang umsetzen

- [ ] Ansicht „Kassenvorgang“ erstellen
  - [ ] Produktauswahl per Dropdown oder Eingabe Produktnummer
  - [ ] Menge eingeben
  - [ ] Preis berechnen
  - [ ] Lagerprüfung implementieren
  - [ ] Produktliste (Warenkorb) anzeigen
  - [ ] Button „Bon anzeigen und abschließen“
- [ ] Bon im UI anzeigen (TextArea / Dialog)

---

## ➕ Produktverwaltung

- [ ] Formular zur Produkterstellung mit:
  - [ ] Name
  - [ ] Preis
  - [ ] Anfangsbestand
- [ ] Validierung der Eingaben
- [ ] Speichern in Datenbank

---

## 📦 Warenzugang erfassen

- [ ] Ansicht zur Auswahl eines bestehenden Produkts
- [ ] Eingabe der zusätzlichen Menge
- [ ] Bestand in der Datenbank aktualisieren

---

## 📋 Lagerbestand anzeigen

- [ ] Tabelle aller Produkte anzeigen (`TableView`)
- [ ] Spalten: Produktname, Preis, aktueller Bestand
- [ ] Datenbindung an ObservableList

---

## 🧪 Fehlerbehandlung & Robustheit

- [ ] Ungültige Eingaben prüfen (Preis, Menge, Produktnummer)
- [ ] Benutzerfeedback bei Fehlern
- [ ] Datenbankausfälle behandeln

---

## 💾 Persistenz & Sicherheit

- [ ] Sicherstellen, dass alle Änderungen direkt in der Datenbank landen
- [ ] Sauberer Umgang mit Datenbankverbindungen (try-with-resources)

---

## 🔄 Version Control & Deployment

- [x] Git-Repository initialisieren (11.06.2025)
- [x] .gitignore für Java-Projekte erstellen (11.06.2025)
- [x] Ersten Commit mit vollständigem Projektstand erstellen (11.06.2025)
- [ ] Repository auf Git-Server (GitHub/GitLab) pushen
- [ ] CI/CD Pipeline einrichten (optional)

---

## 📦 Abschluss

- [ ] Finales Testing aller Funktionen
- [ ] Quellcode strukturieren und kommentieren
- [ ] Screenshots der Anwendung anfertigen
- [ ] Dokumentation schreiben (z. B. `README.md`)
- [ ] Abgabe vorbereiten (ZIP oder Repository)

---

## Discovered During Work

- **11.06.2025**: Vollständige MVC-Architektur implementiert:
  - Model-Klassen: Produkt, Verkauf, Warenkorb
  - Database-Layer: DatabaseManager mit SQLite
  - Service-Layer: KassenService für Geschäftslogik  
  - Controller: MainController für JavaFX-GUI
  - View: main.fxml mit TabPane-basierter Benutzeroberfläche
- **11.06.2025**: Umfassende Unit-Tests implementiert (41 Tests total):
  - ProduktTest: 11 Tests
  - VerkaufTest: 10 Tests
  - WarenkorbTest: 10 Tests
  - DatabaseInitializerTest: 9 Tests
  - AppTest: 1 Test
- **11.06.2025**: Realistische Testdateninitialisierung mit DatabaseInitializer
- **11.06.2025**: JavaFX-Anwendung erfolgreich getestet und startbereit
- **11.06.2025**: Vollständige Dokumentation in README.md und ANLEITUNG.md erstellt
- **11.06.2025**: Git-Repository initialisiert und lokaler Commit erstellt
  - .gitignore für Java/Maven-Projekte konfiguriert
  - Erstes lokales Repository mit vollständigem Projektstand
  - Bereit für Push auf externen Git-Server (GitHub/GitLab)

# Anleitung zum Starten des FIAE24M Kassensystems

## Voraussetzungen

- Java 17 oder höher
- Maven 3.6 oder höher

## Anwendung starten

### 1. Projekt kompilieren

```bash
mvn clean compile
```

### 2. Datenbank initialisieren (optional)

Für Testzwecke mit 100 Produkten und 100 Verkäufen:

```bash
mvn exec:java -Dexec.mainClass="de.berufsschule.kasse.util.DatabaseInitializer"
```

### 3. JavaFX-Anwendung starten

```bash
mvn javafx:run
```

Alternativ über exec-Plugin:

```bash
mvn exec:java -Dexec.mainClass="de.berufsschule.kasse.App"
```

## Tests ausführen

```bash
mvn test
```

## Projektstruktur

- `src/main/java/de/berufsschule/kasse/` - Hauptquellcode
  - `model/` - Datenmodelle (Produkt, Verkauf, Warenkorb)
  - `database/` - Datenbankzugriff (DatabaseManager)
  - `service/` - Geschäftslogik (KassenService)
  - `controller/` - JavaFX Controller
  - `util/` - Hilfsprogramme (DatabaseInitializer)
- `src/main/resources/fxml/` - JavaFX FXML-Dateien
- `src/test/java/` - Unit-Tests
- `kasse.db` - SQLite-Datenbankdatei (wird automatisch erstellt)

## Funktionen der Anwendung

1. **Lagerbestand anzeigen** - Übersicht aller Produkte
2. **Produkt hinzufügen** - Neue Produkte mit Name, Preis und Bestand anlegen
3. **Warenzugang** - Lagerbestand bestehender Produkte erhöhen
4. **Kassenvorgang** - Verkäufe durchführen und Bon erstellen
5. **Bon-Anzeige** - Detaillierte Rechnung des letzten Kassenvorgangs

## Datenbankstruktur

### Tabelle `produkte`
- `id` - Eindeutige Produkt-ID (Auto-Increment)
- `name` - Produktname (eindeutig)
- `preis` - Preis in Euro
- `bestand` - Aktueller Lagerbestand

### Tabelle `verkaeufe`
- `id` - Eindeutige Verkaufs-ID (Auto-Increment)
- `timestamp` - Zeitpunkt des Verkaufs
- `produkt_id` - Referenz auf Produkt
- `menge` - Verkaufte Menge
- `einzelpreis` - Preis pro Stück zum Verkaufszeitpunkt
- `gesamtpreis` - Gesamtpreis (Menge × Einzelpreis)

## Troubleshooting

### JavaFX-Module nicht gefunden
Falls JavaFX-Module nicht gefunden werden, stellen Sie sicher, dass Sie Java 17+ verwenden und Maven ordnungsgemäß konfiguriert ist.

### Datenbankfehler
Die SQLite-Datenbank wird automatisch im Hauptverzeichnis erstellt. Bei Problemen können Sie die Datei `kasse.db` löschen - sie wird beim nächsten Start neu erstellt.

### Tests schlagen fehl
Stellen Sie sicher, dass alle Dependencies korrekt installiert sind:
```bash
mvn clean install
```

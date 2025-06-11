package de.berufsschule.kasse.util;

import de.berufsschule.kasse.database.DatabaseManager;
import de.berufsschule.kasse.model.Produkt;
import de.berufsschule.kasse.model.Verkauf;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility-Klasse zur Initialisierung der Datenbank mit Testdaten.
 * 
 * Diese Klasse erstellt realistische Testdaten für das Kassensystem
 * mit 100 Produkten und 100 Verkäufen.
 * 
 * @author FIAE24M
 * @version 1.0
 */
public class DatabaseInitializer {
    
    private static final String[] PRODUKT_NAMEN = {
        "Apfel", "Banane", "Orange", "Birne", "Trauben", "Ananas", "Mango", "Kiwi",
        "Erdbeeren", "Himbeeren", "Blaubeeren", "Kirschen", "Pfirsich", "Pflaume",
        "Brot", "Weißbrot", "Vollkornbrot", "Brötchen", "Croissant", "Bagel",
        "Milch", "Joghurt", "Quark", "Käse", "Butter", "Sahne", "Frischkäse",
        "Hackfleisch", "Hähnchenbrust", "Rinderfilet", "Schweinefilet", "Lachs",
        "Thunfisch", "Garnelen", "Forelle", "Kabeljau", "Sardinen",
        "Kartoffeln", "Zwiebeln", "Karotten", "Paprika", "Tomaten", "Gurken",
        "Salat", "Spinat", "Brokkoli", "Blumenkohl", "Zucchini", "Aubergine",
        "Reis", "Nudeln", "Spaghetti", "Penne", "Fusilli", "Linsen", "Bohnen",
        "Kichererbsen", "Quinoa", "Haferflocken", "Müsli", "Cornflakes",
        "Coca Cola", "Pepsi", "Fanta", "Sprite", "Wasser", "Sprudelwasser",
        "Orangensaft", "Apfelsaft", "Multivitaminsaft", "Tee", "Kaffee",
        "Bier", "Wein", "Sekt", "Whisky", "Vodka", "Rum",
        "Schokolade", "Kekse", "Chips", "Nüsse", "Gummibärchen", "Bonbons",
        "Eis", "Tiefkühlpizza", "Pommes", "Fischstäbchen", "Gemüsemix",
        "Shampoo", "Duschgel", "Zahnpasta", "Seife", "Deo", "Creme",
        "Windeln", "Babybrei", "Babynahrung", "Spielzeug", "Schnuller",
        "Batterien", "Glühbirne", "Kerzen", "Feuerzeug", "Streichhölzer"
    };

    private static final String[] KATEGORIEN = {
        "Obst", "Gemüse", "Backwaren", "Milchprodukte", "Fleisch", "Fisch",
        "Getränke", "Süßwaren", "Tiefkühl", "Drogerie", "Baby", "Haushalt"
    };

    private final DatabaseManager dbManager;
    private final Random random;

    /**
     * Konstruktor für den DatabaseInitializer.
     */
    public DatabaseInitializer() {
        this.dbManager = DatabaseManager.getInstance();
        this.random = new Random();
    }

    /**
     * Initialisiert die Datenbank mit 100 Produkten und 100 Verkäufen.
     * 
     * @throws SQLException bei Datenbankfehlern
     */
    public void initialisiereMitTestdaten() throws SQLException {
        System.out.println("Starte Datenbankinitialisierung mit Testdaten...");
        
        // Erst alle bestehenden Daten löschen
        loescheBestehendeDaten();
        
        // 100 Produkte erstellen
        List<Produkt> produkte = erstelleTestprodukte();
        System.out.println("100 Testprodukte erstellt");
        
        // 100 Verkäufe erstellen
        erstelleTestverkaeufe(produkte);
        System.out.println("100 Testverkäufe erstellt");
        
        System.out.println("Datenbankinitialisierung abgeschlossen!");
    }

    /**
     * Löscht alle bestehenden Daten aus den Tabellen.
     * 
     * @throws SQLException bei Datenbankfehlern
     */
    private void loescheBestehendeDaten() throws SQLException {
        System.out.println("Lösche bestehende Daten...");
        
        try (var conn = dbManager.getConnection();
             var stmt = conn.createStatement()) {
            
            // Reihenfolge beachten wegen Foreign Key Constraints
            stmt.execute("DELETE FROM verkaeufe");
            stmt.execute("DELETE FROM produkte");
            
            // Auto-Increment zurücksetzen
            stmt.execute("DELETE FROM sqlite_sequence WHERE name='produkte'");
            stmt.execute("DELETE FROM sqlite_sequence WHERE name='verkaeufe'");
        }
    }

    /**
     * Erstellt 100 realistische Testprodukte.
     * 
     * @return Liste der erstellten Produkte
     * @throws SQLException bei Datenbankfehlern
     */
    private List<Produkt> erstelleTestprodukte() throws SQLException {
        List<Produkt> produkte = new ArrayList<>();
        
        for (int i = 0; i < 100; i++) {
            String name = generiereProduktname(i);
            double preis = generiereRealistischenPreis(name);
            int bestand = generiereRealistischenBestand();
            
            Produkt produkt = new Produkt(name, preis, bestand);
            dbManager.fuegeProduktHinzu(produkt);
            produkte.add(produkt);
        }
        
        return produkte;
    }

    /**
     * Generiert einen eindeutigen Produktnamen.
     * 
     * @param index der Index des Produkts
     * @return eindeutiger Produktname
     */
    private String generiereProduktname(int index) {
        if (index < PRODUKT_NAMEN.length) {
            return PRODUKT_NAMEN[index];
        } else {
            // Für zusätzliche Produkte Variationen erstellen
            String basisName = PRODUKT_NAMEN[index % PRODUKT_NAMEN.length];
            String kategorie = KATEGORIEN[random.nextInt(KATEGORIEN.length)];
            return basisName + " " + kategorie + " " + (index - PRODUKT_NAMEN.length + 1);
        }
    }

    /**
     * Generiert einen realistischen Preis basierend auf dem Produktnamen.
     * 
     * @param produktName der Name des Produkts
     * @return realistischer Preis
     */
    private double generiereRealistischenPreis(String produktName) {
        String lowerName = produktName.toLowerCase();
        
        // Kategoriebasierte Preisgestaltung
        if (lowerName.contains("obst") || lowerName.contains("apfel") || 
            lowerName.contains("banane") || lowerName.contains("orange")) {
            return rundePReisAuf(0.30 + random.nextDouble() * 2.70); // 0.30€ - 3.00€
        } else if (lowerName.contains("fleisch") || lowerName.contains("filet") ||
                  lowerName.contains("steak")) {
            return rundePReisAuf(8.00 + random.nextDouble() * 17.00); // 8.00€ - 25.00€
        } else if (lowerName.contains("milch") || lowerName.contains("joghurt") ||
                  lowerName.contains("käse")) {
            return rundePReisAuf(1.00 + random.nextDouble() * 6.00); // 1.00€ - 7.00€
        } else if (lowerName.contains("brot") || lowerName.contains("brötchen")) {
            return rundePReisAuf(0.50 + random.nextDouble() * 3.50); // 0.50€ - 4.00€
        } else if (lowerName.contains("getränk") || lowerName.contains("wasser") ||
                  lowerName.contains("saft") || lowerName.contains("cola")) {
            return rundePReisAuf(0.80 + random.nextDouble() * 3.20); // 0.80€ - 4.00€
        } else if (lowerName.contains("alkohol") || lowerName.contains("bier") ||
                  lowerName.contains("wein") || lowerName.contains("whisky")) {
            return rundePReisAuf(2.00 + random.nextDouble() * 48.00); // 2.00€ - 50.00€
        } else {
            // Standard-Preisbereich
            return rundePReisAuf(0.99 + random.nextDouble() * 9.01); // 0.99€ - 10.00€
        }
    }

    /**
     * Rundet den Preis auf 2 Dezimalstellen und endet meist auf 9.
     * 
     * @param preis der ursprüngliche Preis
     * @return gerundeter Preis
     */
    private double rundePReisAuf(double preis) {
        // 70% der Preise enden auf .99, .49, .95
        if (random.nextDouble() < 0.7) {
            double[] endungen = {0.99, 0.49, 0.95, 0.89, 0.79};
            double basis = Math.floor(preis);
            return basis + endungen[random.nextInt(endungen.length)];
        } else {
            return Math.round(preis * 100.0) / 100.0;
        }
    }

    /**
     * Generiert einen realistischen Bestand zwischen 0 und 200.
     * 
     * @return realistischer Bestand
     */
    private int generiereRealistischenBestand() {
        // Normalverteilung um 50 mit gelegentlichen Ausreißern
        if (random.nextDouble() < 0.1) {
            return 0; // 10% der Produkte sind ausverkauft
        } else if (random.nextDouble() < 0.85) {
            return 5 + random.nextInt(96); // 85% haben 5-100 Stück
        } else {
            return 100 + random.nextInt(101); // 15% haben 100-200 Stück
        }
    }

    /**
     * Erstellt 100 realistische Testverkäufe.
     * 
     * @param produkte die verfügbaren Produkte
     * @throws SQLException bei Datenbankfehlern
     */
    private void erstelleTestverkaeufe(List<Produkt> produkte) throws SQLException {
        LocalDateTime startZeit = LocalDateTime.now().minusDays(30);
        
        for (int i = 0; i < 100; i++) {
            // Zufälliges Produkt auswählen
            Produkt produkt = produkte.get(random.nextInt(produkte.size()));
            
            // Realistische Verkaufsmenge (meist 1-5 Stück)
            int maxMenge = Math.min(produkt.getBestand(), 10);
            if (maxMenge <= 0) continue; // Überspringe ausverkaufte Produkte
            
            int menge = 1 + random.nextInt(maxMenge);
            
            // Verkauf in den letzten 30 Tagen
            LocalDateTime verkaufszeit = startZeit.plusMinutes(random.nextInt(30 * 24 * 60));
            
            Verkauf verkauf = new Verkauf(produkt.getId(), produkt.getName(), 
                                        menge, produkt.getPreis());
            verkauf.setTimestamp(verkaufszeit);
            
            dbManager.speichereVerkauf(verkauf);
            
            // Bestand des Produkts reduzieren
            int neuerBestand = produkt.getBestand() - menge;
            dbManager.aktualisiereBestand(produkt.getId(), neuerBestand);
            produkt.setBestand(neuerBestand);
        }
    }

    /**
     * Hauptmethode zum Ausführen der Datenbankinitialisierung.
     * 
     * @param args Kommandozeilenargumente
     */
    public static void main(String[] args) {
        try {
            DatabaseInitializer initializer = new DatabaseInitializer();
            initializer.initialisiereMitTestdaten();
            
            // Statistiken anzeigen
            zeigeStatistiken();
            
        } catch (SQLException e) {
            System.err.println("Fehler bei der Datenbankinitialisierung: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Zeigt Statistiken über die erstellten Daten an.
     * 
     * @throws SQLException bei Datenbankfehlern
     */
    private static void zeigeStatistiken() throws SQLException {
        DatabaseManager dbManager = DatabaseManager.getInstance();
        
        try (var conn = dbManager.getConnection();
             var stmt = conn.createStatement()) {
            
            // Produktstatistiken
            var rs = stmt.executeQuery("SELECT COUNT(*) as anzahl FROM produkte");
            if (rs.next()) {
                System.out.println("\nProdukte in Datenbank: " + rs.getInt("anzahl"));
            }
            
            rs = stmt.executeQuery("SELECT AVG(preis) as durchschnittspreis FROM produkte");
            if (rs.next()) {
                System.out.printf("Durchschnittspreis: %.2f€%n", rs.getDouble("durchschnittspreis"));
            }
            
            rs = stmt.executeQuery("SELECT SUM(bestand) as gesamtbestand FROM produkte");
            if (rs.next()) {
                System.out.println("Gesamtbestand: " + rs.getInt("gesamtbestand") + " Stück");
            }
            
            // Verkaufsstatistiken
            rs = stmt.executeQuery("SELECT COUNT(*) as anzahl FROM verkaeufe");
            if (rs.next()) {
                System.out.println("Verkäufe in Datenbank: " + rs.getInt("anzahl"));
            }
            
            rs = stmt.executeQuery("SELECT SUM(gesamtpreis) as gesamtumsatz FROM verkaeufe");
            if (rs.next()) {
                System.out.printf("Gesamtumsatz: %.2f€%n", rs.getDouble("gesamtumsatz"));
            }
        }
    }
}

package de.berufsschule.kasse.service;

import de.berufsschule.kasse.database.DatabaseManager;
import de.berufsschule.kasse.util.DatabaseInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests für den StatistikService.
 * 
 * Diese Testklasse überprüft die korrekte Funktionalität
 * der Statistik-Berechnungen und Dashboard-Daten.
 * 
 * @author FIAE24M
 * @version 1.0
 */
class StatistikServiceTest {

    
    private static DatabaseManager dbManager;
    private StatistikService statistikService;

    @BeforeAll
    static void setUpClass() throws SQLException {
        dbManager = DatabaseManager.getInstance();
        DatabaseInitializer initializer = new DatabaseInitializer();
        initializer.initialisiereMitTestdaten();
    }

    @BeforeEach
    void setUp() {
        statistikService = new StatistikService();
    }

    @Test
    void testGetTagesumsatz() throws StatistikService.StatistikServiceException {
        // Happy Path: Tagesumsatz abrufen (sollte >= 0 sein)
        double tagesumsatz = statistikService.getTagesumsatz();
        
        assertTrue(tagesumsatz >= 0.0, 
                  "Tagesumsatz sollte nicht negativ sein");
    }

    @Test
    void testGetAnzahlVerkaufeHeute() throws StatistikService.StatistikServiceException {
        // Happy Path: Anzahl Verkäufe abrufen (sollte >= 0 sein)
        int anzahlVerkaufe = statistikService.getAnzahlVerkaufeHeute();
        
        assertTrue(anzahlVerkaufe >= 0, 
                  "Anzahl Verkäufe sollte nicht negativ sein");
    }

    @Test
    void testGetTop5ProdukteMenge() throws StatistikService.StatistikServiceException {
        // Happy Path: Top 5 Produkte abrufen
        var topProdukte = statistikService.getTop5ProdukteMenge();
        
        assertNotNull(topProdukte, "Top-Produkte Liste sollte nicht null sein");
        assertTrue(topProdukte.size() <= 5, "Maximal 5 Top-Produkte erwartet");
        
        // Edge Case: Prüfe Sortierung (absteigende Reihenfolge nach verkaufter Menge)
        for (int i = 1; i < topProdukte.size(); i++) {
            assertTrue(topProdukte.get(i-1).getVerkaufteMenge() >= topProdukte.get(i).getVerkaufteMenge(),
                      "Top-Produkte sollten nach verkaufter Menge sortiert sein");
        }
    }

    @Test
    void testGetNiedrigbestandProdukte() throws StatistikService.StatistikServiceException {
        // Happy Path: Niedrigbestand-Produkte abrufen
        var niedrigbestandProdukte = statistikService.getNiedrigbestandProdukte();
        
        assertNotNull(niedrigbestandProdukte, "Niedrigbestand-Liste sollte nicht null sein");
        
        // Edge Case: Alle zurückgegebenen Produkte sollten niedrigen Bestand haben
        for (var produkt : niedrigbestandProdukte) {
            assertTrue(produkt.getBestand() < 10, 
                      "Produkt sollte niedrigen Bestand haben (< 10)");
        }
    }

    @Test
    void testTopProduktToString() {
        // Happy Path: TopProdukt toString Methode
        StatistikService.TopProdukt topProdukt = new StatistikService.TopProdukt(
            1, "Test Produkt", 2.50, 15
        );
        
        String erwartet = "Test Produkt (15 verkauft)";
        assertEquals(erwartet, topProdukt.toString(), 
                    "TopProdukt toString sollte korrekt formatiert sein");
    }

    @Test
    void testTopProduktGetters() {
        // Happy Path: TopProdukt Getter-Methoden
        StatistikService.TopProdukt topProdukt = new StatistikService.TopProdukt(
            42, "Test Produkt", 1.99, 25
        );
        
        assertEquals(42, topProdukt.getId(), "ID sollte korrekt sein");
        assertEquals("Test Produkt", topProdukt.getName(), "Name sollte korrekt sein");
        assertEquals(1.99, topProdukt.getPreis(), 0.001, "Preis sollte korrekt sein");
        assertEquals(25, topProdukt.getVerkaufteMenge(), "Verkaufte Menge sollte korrekt sein");
    }

    @Test
    void testStatistikServiceExceptionMitCause() {
        // Failure Case: Exception mit Ursache
        SQLException cause = new SQLException("Test SQL Fehler");
        StatistikService.StatistikServiceException exception = 
            new StatistikService.StatistikServiceException("Test Nachricht", cause);
        
        assertEquals("Test Nachricht", exception.getMessage(), 
                    "Exception-Nachricht sollte korrekt sein");
        assertEquals(cause, exception.getCause(), 
                    "Exception-Ursache sollte korrekt sein");
    }

    @Test
    void testNiedrigbestandSchwellwert() throws StatistikService.StatistikServiceException {
        // Edge Case: Prüfe dass Schwellwert korrekt angewendet wird
        var niedrigbestandProdukte = statistikService.getNiedrigbestandProdukte();
        
        // Alle Produkte sollten Bestand < 10 haben (der definierte Schwellwert)
        for (var produkt : niedrigbestandProdukte) {
            assertTrue(produkt.getBestand() < 10, 
                      String.format("Produkt '%s' hat Bestand %d, erwartet < 10", 
                                  produkt.getName(), produkt.getBestand()));
        }
    }

    @Test
    void testEmptyResults() throws StatistikService.StatistikServiceException {
        // Edge Case: Wenn keine Daten vorhanden sind, sollten leere Listen zurückgegeben werden
        var topProdukte = statistikService.getTop5ProdukteMenge();
        var niedrigbestand = statistikService.getNiedrigbestandProdukte();
        
        // Listen sollten nicht null sein (können aber leer sein)
        assertNotNull(topProdukte, "Top-Produkte sollten nicht null sein");
        assertNotNull(niedrigbestand, "Niedrigbestand-Produkte sollten nicht null sein");
    }
}

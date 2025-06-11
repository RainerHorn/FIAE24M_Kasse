package de.berufsschule.kasse.util;

import de.berufsschule.kasse.database.DatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests für den DatabaseInitializer.
 * 
 * Diese Testklasse überprüft die korrekte Funktionalität
 * der Datenbankinitialisierung mit Testdaten.
 * 
 * @author FIAE24M
 * @version 1.0
 */
class DatabaseInitializerTest {

    private DatabaseInitializer initializer;
    private DatabaseManager dbManager;

    @BeforeEach
    void setUp() {
        initializer = new DatabaseInitializer();
        dbManager = DatabaseManager.getInstance();
    }

    @Test
    void testDatenbankverbindung() {
        assertTrue(dbManager.testVerbindung(), 
                  "Datenbankverbindung sollte funktionieren");
    }

    @Test
    void testProduktAnzahlNachInitialisierung() throws SQLException {
        // Happy Path: Prüfe dass 100 Produkte erstellt wurden
        try (var conn = dbManager.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery("SELECT COUNT(*) as anzahl FROM produkte")) {
            
            if (rs.next()) {
                int anzahl = rs.getInt("anzahl");
                assertEquals(100, anzahl, 
                           "Es sollten 100 Produkte in der Datenbank sein");
            }
        }
    }

    @Test
    void testVerkaufAnzahlNachInitialisierung() throws SQLException {
        // Edge Case: Prüfe dass Verkäufe erstellt wurden (kann weniger als 100 sein)
        try (var conn = dbManager.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery("SELECT COUNT(*) as anzahl FROM verkaeufe")) {
            
            if (rs.next()) {
                int anzahl = rs.getInt("anzahl");
                assertTrue(anzahl > 0, 
                          "Es sollten Verkäufe in der Datenbank sein");
                assertTrue(anzahl <= 100, 
                          "Es sollten maximal 100 Verkäufe sein");
            }
        }
    }

    @Test
    void testProdukteHabenGueltigePreise() throws SQLException {
        // Failure Case: Prüfe dass alle Preise positiv sind
        try (var conn = dbManager.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery("SELECT COUNT(*) as anzahl FROM produkte WHERE preis <= 0")) {
            
            if (rs.next()) {
                int anzahlUngueltig = rs.getInt("anzahl");
                assertEquals(0, anzahlUngueltig, 
                           "Alle Produkte sollten einen positiven Preis haben");
            }
        }
    }

    @Test
    void testProdukteHabenGueltigeBestaende() throws SQLException {
        // Edge Case: Prüfe dass alle Bestände nicht-negativ sind
        try (var conn = dbManager.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery("SELECT COUNT(*) as anzahl FROM produkte WHERE bestand < 0")) {
            
            if (rs.next()) {
                int anzahlUngueltig = rs.getInt("anzahl");
                assertEquals(0, anzahlUngueltig, 
                           "Alle Produkte sollten einen nicht-negativen Bestand haben");
            }
        }
    }

    @Test
    void testVerkaufeHabenGueltigePreise() throws SQLException {
        // Failure Case: Prüfe dass alle Verkaufspreise positiv sind
        try (var conn = dbManager.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery("SELECT COUNT(*) as anzahl FROM verkaeufe WHERE einzelpreis <= 0 OR gesamtpreis <= 0")) {
            
            if (rs.next()) {
                int anzahlUngueltig = rs.getInt("anzahl");
                assertEquals(0, anzahlUngueltig, 
                           "Alle Verkäufe sollten positive Preise haben");
            }
        }
    }

    @Test
    void testVerkaufeHabenGueltigeMengen() throws SQLException {
        // Edge Case: Prüfe dass alle Verkaufsmengen positiv sind
        try (var conn = dbManager.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery("SELECT COUNT(*) as anzahl FROM verkaeufe WHERE menge <= 0")) {
            
            if (rs.next()) {
                int anzahlUngueltig = rs.getInt("anzahl");
                assertEquals(0, anzahlUngueltig, 
                           "Alle Verkäufe sollten eine positive Menge haben");
            }
        }
    }

    @Test
    void testGesamtpreisBerechnung() throws SQLException {
        // Happy Path: Prüfe dass Gesamtpreis = Menge * Einzelpreis
        try (var conn = dbManager.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(
                 "SELECT COUNT(*) as anzahl FROM verkaeufe " +
                 "WHERE ABS(gesamtpreis - (menge * einzelpreis)) > 0.01")) {
            
            if (rs.next()) {
                int anzahlFehlerhaft = rs.getInt("anzahl");
                assertEquals(0, anzahlFehlerhaft, 
                           "Gesamtpreis sollte korrekt berechnet sein");
            }
        }
    }

    @Test
    void testProduktnamenSindEindeutig() throws SQLException {
        // Edge Case: Prüfe dass alle Produktnamen eindeutig sind
        try (var conn = dbManager.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(
                 "SELECT COUNT(*) as total, COUNT(DISTINCT name) as distinct_names FROM produkte")) {
            
            if (rs.next()) {
                int total = rs.getInt("total");
                int distinctNames = rs.getInt("distinct_names");
                assertEquals(total, distinctNames, 
                           "Alle Produktnamen sollten eindeutig sein");
            }
        }
    }
}

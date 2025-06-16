package de.berufsschule.kasse.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests für die Verkauf-Klasse.
 * 
 * Diese Testklasse überprüft alle Funktionalitäten der Verkauf-Klasse
 * inklusive Konstruktoren, Getter/Setter und Geschäftslogik.
 * 
 * @author FIAE24M
 * @version 1.0
 */
class VerkaufTest {

    private Verkauf verkauf;
    private LocalDateTime testZeitpunkt;

    @BeforeEach
    void setUp() {
        testZeitpunkt = LocalDateTime.now();
        verkauf = new Verkauf(1, "Testprodukt", 3, 2.50);
    }

    @Test
    void testKonstruktorMitParametern() {
        // Happy Path: Prüfe Konstruktor mit allen Parametern
        assertEquals(1, verkauf.getProduktId());
        assertEquals("Testprodukt", verkauf.getProduktName());
        assertEquals(3, verkauf.getMenge());
        assertEquals(2.50, verkauf.getEinzelpreis(), 0.01);
        assertEquals(7.50, verkauf.getGesamtpreis(), 0.01);
        assertNotNull(verkauf.getTimestamp());
    }

    @Test
    void testLeererKonstruktor() {
        // Edge Case: Prüfe leeren Konstruktor
        Verkauf leererVerkauf = new Verkauf();
        assertNotNull(leererVerkauf.getTimestamp());
        assertEquals(0, leererVerkauf.getId());
        assertEquals(0, leererVerkauf.getProduktId());
        assertNull(leererVerkauf.getProduktName());
        assertEquals(0, leererVerkauf.getMenge());
        assertEquals(0.0, leererVerkauf.getEinzelpreis(), 0.01);
        assertEquals(0.0, leererVerkauf.getGesamtpreis(), 0.01);
    }

    @Test
    void testGesamtpreisBerechnung() {
        // Happy Path: Prüfe automatische Berechnung des Gesamtpreises
        assertEquals(7.50, verkauf.getGesamtpreis(), 0.01);
        
        // Prüfe Neuberechnung bei Änderung der Menge
        verkauf.setMenge(5);
        assertEquals(12.50, verkauf.getGesamtpreis(), 0.01);
        
        // Prüfe Neuberechnung bei Änderung des Einzelpreises
        verkauf.setEinzelpreis(3.00);
        assertEquals(15.00, verkauf.getGesamtpreis(), 0.01);
    }

    @Test
    void testManuellBerechneGesamtpreis() {
        // Edge Case: Prüfe manuelle Neuberechnung
        verkauf.setMenge(4);
        verkauf.setEinzelpreis(1.75);
        verkauf.berechneGesamtpreis();
        assertEquals(7.00, verkauf.getGesamtpreis(), 0.01);
    }

    @Test
    void testSetterUndGetter() {
        // Happy Path: Prüfe alle Setter und Getter
        verkauf.setId(99);
        verkauf.setTimestamp(testZeitpunkt);
        verkauf.setProduktId(42);
        verkauf.setProduktName("Neuer Name");
        
        assertEquals(99, verkauf.getId());
        assertEquals(testZeitpunkt, verkauf.getTimestamp());
        assertEquals(42, verkauf.getProduktId());
        assertEquals("Neuer Name", verkauf.getProduktName());
    }    @Test
    void testToString() {
        // Happy Path: Prüfe String-Darstellung
        String result = verkauf.toString();
        assertTrue(result.contains("3x"));
        assertTrue(result.contains("Testprodukt"));
        assertTrue(result.contains("2,50"));
        assertTrue(result.contains("7,50"));
        assertTrue(result.contains("€"));
    }

    @Test
    void testGesamtpreisMitNullWerten() {
        // Edge Case: Prüfe Verhalten mit Null-Werten
        Verkauf nullVerkauf = new Verkauf();
        nullVerkauf.berechneGesamtpreis();
        assertEquals(0.0, nullVerkauf.getGesamtpreis(), 0.01);
    }

    @Test
    void testGesamtpreisMitNegativenWerten() {
        // Failure Case: Prüfe Verhalten mit negativen Werten (sollte theoretisch nicht auftreten)
        verkauf.setMenge(-1);
        verkauf.setEinzelpreis(-2.00);
        verkauf.berechneGesamtpreis();
        assertEquals(2.00, verkauf.getGesamtpreis(), 0.01); // -1 * -2.00 = 2.00
    }

    @Test
    void testPraezisionBeiPreisberechnung() {
        // Edge Case: Prüfe Präzision bei Dezimalzahlen
        verkauf.setMenge(3);
        verkauf.setEinzelpreis(1.99);
        verkauf.berechneGesamtpreis();
        assertEquals(5.97, verkauf.getGesamtpreis(), 0.01);
    }

    @Test
    void testTimestampAutomatischGesetzt() {
        // Happy Path: Prüfe dass Timestamp automatisch gesetzt wird
        LocalDateTime vor = LocalDateTime.now().minusSeconds(1);
        Verkauf neuerVerkauf = new Verkauf(1, "Test", 1, 1.00);
        LocalDateTime nach = LocalDateTime.now().plusSeconds(1);
        
        assertTrue(neuerVerkauf.getTimestamp().isAfter(vor));
        assertTrue(neuerVerkauf.getTimestamp().isBefore(nach));
    }
}

package de.berufsschule.kasse.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests für die Warenkorb-Klasse.
 * 
 * Diese Testklasse überprüft alle Funktionalitäten des Warenkorbs
 * inklusive Hinzufügen, Entfernen und Berechnungen.
 * 
 * @author FIAE24M
 * @version 1.0
 */
class WarenkorbTest {

    private Warenkorb warenkorb;
    private Verkauf verkauf1;
    private Verkauf verkauf2;

    @BeforeEach
    void setUp() {
        warenkorb = new Warenkorb();
        verkauf1 = new Verkauf(1, "Apfel", 3, 0.50);
        verkauf2 = new Verkauf(2, "Banane", 2, 0.30);
    }

    @Test
    void testNeuerWarenkorbIstLeer() {
        assertTrue(warenkorb.istLeer());
        assertEquals(0, warenkorb.getAnzahlArtikel());
        assertEquals(0.0, warenkorb.berechneGesamtbetrag(), 0.01);
    }

    @Test
    void testVerkaufHinzufuegen() {
        warenkorb.fuegeVerkaufHinzu(verkauf1);
        
        assertFalse(warenkorb.istLeer());
        assertEquals(1, warenkorb.getAnzahlArtikel());
        assertEquals(1.50, warenkorb.berechneGesamtbetrag(), 0.01);
    }

    @Test
    void testMehrereVerkaeufeHinzufuegen() {
        warenkorb.fuegeVerkaufHinzu(verkauf1);
        warenkorb.fuegeVerkaufHinzu(verkauf2);
        
        assertEquals(2, warenkorb.getAnzahlArtikel());
        assertEquals(2.10, warenkorb.berechneGesamtbetrag(), 0.01); // 1.50 + 0.60
    }

    @Test
    void testVerkaufEntfernen() {
        warenkorb.fuegeVerkaufHinzu(verkauf1);
        warenkorb.fuegeVerkaufHinzu(verkauf2);
        
        boolean entfernt = warenkorb.entferneVerkauf(verkauf1);
        
        assertTrue(entfernt);
        assertEquals(1, warenkorb.getAnzahlArtikel());
        assertEquals(0.60, warenkorb.berechneGesamtbetrag(), 0.01);
    }

    @Test
    void testVerkaufEntfernenNichtVorhanden() {
        warenkorb.fuegeVerkaufHinzu(verkauf1);
        
        Verkauf nichtVorhandenerVerkauf = new Verkauf(99, "Nicht da", 1, 1.0);
        boolean entfernt = warenkorb.entferneVerkauf(nichtVorhandenerVerkauf);
        
        assertFalse(entfernt);
        assertEquals(1, warenkorb.getAnzahlArtikel());
    }

    @Test
    void testWarenkorbLeeren() {
        warenkorb.fuegeVerkaufHinzu(verkauf1);
        warenkorb.fuegeVerkaufHinzu(verkauf2);
        
        warenkorb.leeren();
        
        assertTrue(warenkorb.istLeer());
        assertEquals(0, warenkorb.getAnzahlArtikel());
        assertEquals(0.0, warenkorb.berechneGesamtbetrag(), 0.01);
    }

    @Test
    void testGetVerkaeufeGibtKopieZurueck() {
        warenkorb.fuegeVerkaufHinzu(verkauf1);
        
        var verkaeufe = warenkorb.getVerkaeufe();
        verkaeufe.clear(); // Kopie ändern
        
        // Original sollte unverändert sein
        assertEquals(1, warenkorb.getAnzahlArtikel());
    }

    @Test
    void testErstelleBonFormat() {
        warenkorb.fuegeVerkaufHinzu(verkauf1);
        warenkorb.fuegeVerkaufHinzu(verkauf2);
        
        String bon = warenkorb.erstelleBon();
        
        assertTrue(bon.contains("KASSENSYSTEM FIAE24M"));
        assertTrue(bon.contains("3x Apfel"));
        assertTrue(bon.contains("2x Banane"));
        assertTrue(bon.contains("GESAMT: 2,10€"));
        assertTrue(bon.contains("Vielen Dank"));
    }

    @Test
    void testToString() {
        warenkorb.fuegeVerkaufHinzu(verkauf1);
        warenkorb.fuegeVerkaufHinzu(verkauf2);
        
        String warenkorbString = warenkorb.toString();
        
        assertTrue(warenkorbString.contains("2 Artikel"));
        assertTrue(warenkorbString.contains("2,10€"));
    }

    @Test
    void testErstellungszeitpunktGesetzt() {
        assertNotNull(warenkorb.getErstellungszeitpunkt());
    }
}

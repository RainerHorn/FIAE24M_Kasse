package de.berufsschule.kasse.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests für die Produkt-Klasse.
 * 
 * Diese Testklasse überprüft alle Funktionalitäten der Produkt-Klasse
 * inklusive Konstruktoren, Getter/Setter und Geschäftslogik.
 * 
 * @author FIAE24M
 * @version 1.0
 */
class ProduktTest {

    private Produkt produkt;

    @BeforeEach
    void setUp() {
        produkt = new Produkt(1, "Testprodukt", 2.50, 10);
    }

    @Test
    void testKonstruktorMitAllenParametern() {
        assertEquals(1, produkt.getId());
        assertEquals("Testprodukt", produkt.getName());
        assertEquals(2.50, produkt.getPreis(), 0.01);
        assertEquals(10, produkt.getBestand());
    }

    @Test
    void testKonstruktorOhneId() {
        Produkt neuesProdukt = new Produkt("Neues Produkt", 5.99, 20);
        assertEquals(0, neuesProdukt.getId()); // Default-Wert
        assertEquals("Neues Produkt", neuesProdukt.getName());
        assertEquals(5.99, neuesProdukt.getPreis(), 0.01);
        assertEquals(20, neuesProdukt.getBestand());
    }

    @Test
    void testErhoeheBestand() {
        int ursprungsbestand = produkt.getBestand();
        produkt.erhoeheBestand(5);
        assertEquals(ursprungsbestand + 5, produkt.getBestand());
    }

    @Test
    void testVerringereBestandErfolgreich() {
        boolean erfolg = produkt.verringereBestand(5);
        assertTrue(erfolg);
        assertEquals(5, produkt.getBestand());
    }

    @Test
    void testVerringereBestandNichtMoeglich() {
        boolean erfolg = produkt.verringereBestand(15);
        assertFalse(erfolg);
        assertEquals(10, produkt.getBestand()); // Bestand unverändert
    }

    @Test
    void testVerringereBestandGenauNull() {
        boolean erfolg = produkt.verringereBestand(10);
        assertTrue(erfolg);
        assertEquals(0, produkt.getBestand());
    }

    @Test
    void testIstVerfuegbarPositiv() {
        assertTrue(produkt.istVerfuegbar(5));
        assertTrue(produkt.istVerfuegbar(10));
    }

    @Test
    void testIstVerfuegbarNegativ() {
        assertFalse(produkt.istVerfuegbar(15));
    }

    @Test
    void testIstVerfuegbarGrenzeNull() {
        assertTrue(produkt.istVerfuegbar(0));
    }

    @Test
    void testSetterUndGetter() {
        produkt.setId(99);
        produkt.setName("Geänderter Name");
        produkt.setPreis(9.99);
        produkt.setBestand(50);

        assertEquals(99, produkt.getId());
        assertEquals("Geänderter Name", produkt.getName());
        assertEquals(9.99, produkt.getPreis(), 0.01);
        assertEquals(50, produkt.getBestand());
    }

    @Test
    void testToString() {
        String expected = "Produkt{id=1, name='Testprodukt', preis=2,50€, bestand=10}";
        String actual = produkt.toString();
        assertTrue(actual.contains("id=1"));
        assertTrue(actual.contains("Testprodukt"));
        assertTrue(actual.contains("preis=2"));
        assertTrue(actual.contains("bestand=10"));
    }
}

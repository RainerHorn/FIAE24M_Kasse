package de.berufsschule.kasse.model;

/**
 * Repräsentiert ein Produkt im Kassensystem.
 * 
 * Diese Klasse enthält alle Informationen zu einem Produkt inklusive
 * ID, Name, Preis und Lagerbestand.
 * 
 * @author FIAE24M
 * @version 1.0
 */
public class Produkt {
    private int id;
    private String name;
    private double preis;
    private int bestand;

    /**
     * Standard-Konstruktor für ein leeres Produkt.
     */
    public Produkt() {
    }

    /**
     * Konstruktor für ein Produkt mit allen Parametern.
     * 
     * @param id der eindeutige Identifier des Produkts
     * @param name der Name des Produkts
     * @param preis der Preis des Produkts in Euro
     * @param bestand die Anzahl der verfügbaren Stück im Lager
     */
    public Produkt(int id, String name, double preis, int bestand) {
        this.id = id;
        this.name = name;
        this.preis = preis;
        this.bestand = bestand;
    }

    /**
     * Konstruktor für ein neues Produkt ohne ID (wird bei der Erstellung gesetzt).
     * 
     * @param name der Name des Produkts
     * @param preis der Preis des Produkts in Euro
     * @param bestand die Anzahl der verfügbaren Stück im Lager
     */
    public Produkt(String name, double preis, int bestand) {
        this.name = name;
        this.preis = preis;
        this.bestand = bestand;
    }

    // Getter und Setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPreis() {
        return preis;
    }

    public void setPreis(double preis) {
        this.preis = preis;
    }

    public int getBestand() {
        return bestand;
    }

    public void setBestand(int bestand) {
        this.bestand = bestand;
    }

    /**
     * Erhöht den Bestand um die angegebene Menge (Warenzugang).
     * 
     * @param menge die hinzuzufügende Menge
     */
    public void erhoeheBestand(int menge) {
        this.bestand += menge;
    }

    /**
     * Verringert den Bestand um die angegebene Menge (Verkauf).
     * 
     * @param menge die zu verringernde Menge
     * @return true wenn der Bestand ausreichend war, false sonst
     */
    public boolean verringereBestand(int menge) {
        if (this.bestand >= menge) {
            this.bestand -= menge;
            return true;
        }
        return false;
    }

    /**
     * Prüft ob genügend Bestand für die gewünschte Menge vorhanden ist.
     * 
     * @param menge die gewünschte Menge
     * @return true wenn genügend Bestand vorhanden ist, false sonst
     */
    public boolean istVerfuegbar(int menge) {
        return this.bestand >= menge;
    }

    @Override
    public String toString() {
        return String.format("Produkt{id=%d, name='%s', preis=%.2f€, bestand=%d}", 
                           id, name, preis, bestand);
    }
}

package de.berufsschule.kasse.model;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Repräsentiert einen einzelnen Verkauf in einem Kassenvorgang.
 * 
 * Diese Klasse speichert die Details eines verkauften Produkts inklusive
 * Zeitstempel, Produktinformationen, Menge und Preise.
 * 
 * @author FIAE24M
 * @version 1.0
 */
public class Verkauf {
    private int id;
    private LocalDateTime timestamp;
    private int produktId;
    private String produktName;
    private int menge;
    private double einzelpreis;
    private double gesamtpreis;

    /**
     * Standard-Konstruktor für einen leeren Verkauf.
     */
    public Verkauf() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Konstruktor für einen Verkauf mit allen Parametern.
     * 
     * @param produktId die ID des verkauften Produkts
     * @param produktName der Name des verkauften Produkts
     * @param menge die verkaufte Menge
     * @param einzelpreis der Preis pro Stück
     */
    public Verkauf(int produktId, String produktName, int menge, double einzelpreis) {
        this();
        this.produktId = produktId;
        this.produktName = produktName;
        this.menge = menge;
        this.einzelpreis = einzelpreis;
        this.gesamtpreis = menge * einzelpreis;
    }

    // Getter und Setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getProduktId() {
        return produktId;
    }

    public void setProduktId(int produktId) {
        this.produktId = produktId;
    }

    public String getProduktName() {
        return produktName;
    }

    public void setProduktName(String produktName) {
        this.produktName = produktName;
    }

    public int getMenge() {
        return menge;
    }

    public void setMenge(int menge) {
        this.menge = menge;
        this.gesamtpreis = menge * einzelpreis;
    }

    public double getEinzelpreis() {
        return einzelpreis;
    }

    public void setEinzelpreis(double einzelpreis) {
        this.einzelpreis = einzelpreis;
        this.gesamtpreis = menge * einzelpreis;
    }

    public double getGesamtpreis() {
        return gesamtpreis;
    }

    /**
     * Berechnet den Gesamtpreis neu basierend auf Menge und Einzelpreis.
     */
    public void berechneGesamtpreis() {
        this.gesamtpreis = this.menge * this.einzelpreis;
    }    @Override
    public String toString() {
        return String.format(Locale.GERMAN, "%dx %s à %.2f€ = %.2f€", 
                           menge, produktName, einzelpreis, gesamtpreis);
    }
}

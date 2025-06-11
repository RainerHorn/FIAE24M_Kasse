package de.berufsschule.kasse.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert einen Warenkorb für einen Kassenvorgang.
 * 
 * Diese Klasse sammelt alle Verkäufe eines Kassenvorgangs und
 * berechnet den Gesamtbetrag.
 * 
 * @author FIAE24M
 * @version 1.0
 */
public class Warenkorb {
    private List<Verkauf> verkaeufe;
    private LocalDateTime erstellungszeitpunkt;

    /**
     * Erstellt einen neuen leeren Warenkorb.
     */
    public Warenkorb() {
        this.verkaeufe = new ArrayList<>();
        this.erstellungszeitpunkt = LocalDateTime.now();
    }

    /**
     * Fügt einen Verkauf zum Warenkorb hinzu.
     * 
     * @param verkauf der hinzuzufügende Verkauf
     */
    public void fuegeVerkaufHinzu(Verkauf verkauf) {
        this.verkaeufe.add(verkauf);
    }

    /**
     * Entfernt einen Verkauf aus dem Warenkorb.
     * 
     * @param verkauf der zu entfernende Verkauf
     * @return true wenn der Verkauf entfernt wurde, false sonst
     */
    public boolean entferneVerkauf(Verkauf verkauf) {
        return this.verkaeufe.remove(verkauf);
    }

    /**
     * Leert den kompletten Warenkorb.
     */
    public void leeren() {
        this.verkaeufe.clear();
    }

    /**
     * Berechnet den Gesamtbetrag aller Verkäufe im Warenkorb.
     * 
     * @return der Gesamtbetrag in Euro
     */
    public double berechneGesamtbetrag() {
        return verkaeufe.stream()
                       .mapToDouble(Verkauf::getGesamtpreis)
                       .sum();
    }

    /**
     * Gibt die Anzahl der verschiedenen Artikel im Warenkorb zurück.
     * 
     * @return Anzahl der Artikel
     */
    public int getAnzahlArtikel() {
        return verkaeufe.size();
    }

    /**
     * Prüft ob der Warenkorb leer ist.
     * 
     * @return true wenn leer, false sonst
     */
    public boolean istLeer() {
        return verkaeufe.isEmpty();
    }

    /**
     * Gibt eine Kopie der Verkaufsliste zurück.
     * 
     * @return unveränderliche Liste der Verkäufe
     */
    public List<Verkauf> getVerkaeufe() {
        return new ArrayList<>(verkaeufe);
    }

    /**
     * Gibt den Erstellungszeitpunkt des Warenkorbs zurück.
     * 
     * @return Zeitpunkt der Erstellung
     */
    public LocalDateTime getErstellungszeitpunkt() {
        return erstellungszeitpunkt;
    }

    /**
     * Erstellt eine formatierte Bon-Darstellung des Warenkorbs.
     * 
     * @return formatierter Bon als String
     */
    public String erstelleBon() {
        StringBuilder bon = new StringBuilder();
        bon.append("=".repeat(40)).append("\n");
        bon.append("           KASSENSYSTEM FIAE24M\n");
        bon.append("=".repeat(40)).append("\n");
        bon.append("Datum: ").append(erstellungszeitpunkt.toLocalDate()).append("\n");
        bon.append("Zeit:  ").append(erstellungszeitpunkt.toLocalTime().toString().substring(0, 8)).append("\n");
        bon.append("-".repeat(40)).append("\n");

        for (Verkauf verkauf : verkaeufe) {
            bon.append(verkauf.toString()).append("\n");
        }

        bon.append("-".repeat(40)).append("\n");
        bon.append(String.format("GESAMT: %.2f€", berechneGesamtbetrag())).append("\n");
        bon.append("=".repeat(40)).append("\n");
        bon.append("Vielen Dank für Ihren Einkauf!\n");

        return bon.toString();
    }

    @Override
    public String toString() {
        return String.format("Warenkorb{%d Artikel, Gesamtbetrag: %.2f€}", 
                           getAnzahlArtikel(), berechneGesamtbetrag());
    }
}

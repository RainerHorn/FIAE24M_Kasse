package de.berufsschule.kasse.service;

import de.berufsschule.kasse.database.DatabaseManager;
import de.berufsschule.kasse.model.Produkt;
import de.berufsschule.kasse.model.Verkauf;
import de.berufsschule.kasse.model.Warenkorb;

import java.sql.SQLException;
import java.util.List;

/**
 * Service-Klasse für die Geschäftslogik des Kassensystems.
 * 
 * Diese Klasse stellt die Hauptfunktionalitäten des Kassensystems bereit
 * und koordiniert zwischen der GUI und der Datenbankschicht.
 * 
 * @author FIAE24M
 * @version 1.0
 */
public class KassenService {
    private final DatabaseManager dbManager;
    private Warenkorb aktuellerWarenkorb;

    /**
     * Erstellt eine neue Instanz des KassenService.
     */
    public KassenService() {
        this.dbManager = DatabaseManager.getInstance();
        this.aktuellerWarenkorb = new Warenkorb();
    }

    /**
     * Lädt alle verfügbaren Produkte aus der Datenbank.
     * 
     * @return Liste aller Produkte
     * @throws KassenServiceException bei Datenbankfehlern
     */
    public List<Produkt> getAlleProdukte() throws KassenServiceException {
        try {
            return dbManager.getAlleProdukte();
        } catch (SQLException e) {
            throw new KassenServiceException("Fehler beim Laden der Produkte", e);
        }
    }

    /**
     * Sucht ein Produkt anhand seiner ID.
     * 
     * @param id die ID des gesuchten Produkts
     * @return das Produkt oder null wenn nicht gefunden
     * @throws KassenServiceException bei Datenbankfehlern
     */
    public Produkt getProduktById(int id) throws KassenServiceException {
        try {
            return dbManager.getProduktById(id);
        } catch (SQLException e) {
            throw new KassenServiceException("Fehler beim Suchen des Produkts mit ID " + id, e);
        }
    }

    /**
     * Fügt ein neues Produkt hinzu.
     * 
     * @param name der Name des Produkts
     * @param preis der Preis des Produkts
     * @param bestand der Anfangsbestand
     * @return das erstellte Produkt mit ID
     * @throws KassenServiceException bei Validierungs- oder Datenbankfehlern
     */
    public Produkt fuegeProduktHinzu(String name, double preis, int bestand) throws KassenServiceException {
        // Validierung
        if (name == null || name.trim().isEmpty()) {
            throw new KassenServiceException("Produktname darf nicht leer sein");
        }
        if (preis <= 0) {
            throw new KassenServiceException("Preis muss größer als 0 sein");
        }
        if (bestand < 0) {
            throw new KassenServiceException("Bestand darf nicht negativ sein");
        }

        try {
            Produkt produkt = new Produkt(name.trim(), preis, bestand);
            dbManager.fuegeProduktHinzu(produkt);
            return produkt;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                throw new KassenServiceException("Ein Produkt mit diesem Namen existiert bereits");
            }
            throw new KassenServiceException("Fehler beim Hinzufügen des Produkts", e);
        }
    }

    /**
     * Erfasst einen Warenzugang für ein bestehendes Produkt.
     * 
     * @param produktId die ID des Produkts
     * @param zusaetzlicheMenge die hinzuzufügende Menge
     * @throws KassenServiceException bei Validierungs- oder Datenbankfehlern
     */
    public void erfasseWarenzugang(int produktId, int zusaetzlicheMenge) throws KassenServiceException {
        if (zusaetzlicheMenge <= 0) {
            throw new KassenServiceException("Zusätzliche Menge muss größer als 0 sein");
        }

        try {
            Produkt produkt = dbManager.getProduktById(produktId);
            if (produkt == null) {
                throw new KassenServiceException("Produkt mit ID " + produktId + " nicht gefunden");
            }

            int neuerBestand = produkt.getBestand() + zusaetzlicheMenge;
            dbManager.aktualisiereBestand(produktId, neuerBestand);
        } catch (SQLException e) {
            throw new KassenServiceException("Fehler beim Erfassen des Warenzugangs", e);
        }
    }

    /**
     * Startet einen neuen Kassenvorgang (leert den Warenkorb).
     */
    public void starteNeuenKassenvorgang() {
        this.aktuellerWarenkorb = new Warenkorb();
    }

    /**
     * Fügt ein Produkt zum aktuellen Warenkorb hinzu.
     * 
     * @param produktId die ID des Produkts
     * @param menge die gewünschte Menge
     * @throws KassenServiceException bei Validierungs- oder Verfügbarkeitsfehlern
     */
    public void fuegeProduktZumWarenkorbHinzu(int produktId, int menge) throws KassenServiceException {
        if (menge <= 0) {
            throw new KassenServiceException("Menge muss größer als 0 sein");
        }

        try {
            Produkt produkt = dbManager.getProduktById(produktId);
            if (produkt == null) {
                throw new KassenServiceException("Produkt mit ID " + produktId + " nicht gefunden");
            }

            if (!produkt.istVerfuegbar(menge)) {
                throw new KassenServiceException(
                    String.format("Nicht genügend Bestand verfügbar. Verfügbar: %d, gewünscht: %d", 
                                produkt.getBestand(), menge));
            }

            Verkauf verkauf = new Verkauf(produktId, produkt.getName(), menge, produkt.getPreis());
            aktuellerWarenkorb.fuegeVerkaufHinzu(verkauf);

        } catch (SQLException e) {
            throw new KassenServiceException("Fehler beim Hinzufügen zum Warenkorb", e);
        }
    }

    /**
     * Schließt den aktuellen Kassenvorgang ab und aktualisiert die Bestände.
     * 
     * @return der Bon als formatierter String
     * @throws KassenServiceException bei Datenbankfehlern
     */
    public String schliesseKassenvorgangAb() throws KassenServiceException {
        if (aktuellerWarenkorb.istLeer()) {
            throw new KassenServiceException("Der Warenkorb ist leer");
        }

        try {
            // Bestände aktualisieren und Verkäufe speichern
            for (Verkauf verkauf : aktuellerWarenkorb.getVerkaeufe()) {
                Produkt produkt = dbManager.getProduktById(verkauf.getProduktId());
                if (produkt != null) {
                    int neuerBestand = produkt.getBestand() - verkauf.getMenge();
                    dbManager.aktualisiereBestand(verkauf.getProduktId(), neuerBestand);
                    dbManager.speichereVerkauf(verkauf);
                }
            }

            String bon = aktuellerWarenkorb.erstelleBon();
            
            // Warenkorb für nächsten Vorgang leeren
            starteNeuenKassenvorgang();
            
            return bon;

        } catch (SQLException e) {
            throw new KassenServiceException("Fehler beim Abschließen des Kassenvorgangs", e);
        }
    }

    /**
     * Gibt den aktuellen Warenkorb zurück.
     * 
     * @return der aktuelle Warenkorb
     */
    public Warenkorb getAktuellerWarenkorb() {
        return aktuellerWarenkorb;
    }

    /**
     * Testet die Datenbankverbindung.
     * 
     * @return true wenn Verbindung erfolgreich, false sonst
     */
    public boolean testDatenbankverbindung() {
        return dbManager.testVerbindung();
    }

    /**
     * Custom Exception für Service-Fehler.
     */
    public static class KassenServiceException extends Exception {
        public KassenServiceException(String message) {
            super(message);
        }

        public KassenServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

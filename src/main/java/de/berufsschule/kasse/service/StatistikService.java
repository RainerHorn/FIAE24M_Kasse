package de.berufsschule.kasse.service;

import de.berufsschule.kasse.database.DatabaseManager;
import de.berufsschule.kasse.model.Produkt;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service-Klasse für Statistiken und Dashboard-Daten.
 * 
 * Diese Klasse stellt Methoden zur Verfügung um verschiedene
 * Statistiken und KPIs für das Dashboard zu berechnen.
 * 
 * @author FIAE24M
 * @version 1.0
 */
public class StatistikService {
    
    private static final int NIEDRIGBESTAND_SCHWELLWERT = 10;
    
    private final DatabaseManager dbManager;

    /**
     * Konstruktor für den StatistikService.
     */
    public StatistikService() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Berechnet den Gesamtumsatz des aktuellen Tages.
     * 
     * @return Gesamtumsatz des heutigen Tages
     * @throws StatistikServiceException bei Datenbankfehlern
     */
    public double getTagesumsatz() throws StatistikServiceException {
        String heute = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String sql = "SELECT COALESCE(SUM(gesamtpreis), 0.0) as umsatz FROM verkaeufe " +
                    "WHERE DATE(timestamp) = ?";
        
        try (var conn = dbManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, heute);
            
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("umsatz");
                }
                return 0.0;
            }
            
        } catch (SQLException e) {
            throw new StatistikServiceException("Fehler beim Berechnen des Tagesumsatzes", e);
        }
    }

    /**
     * Ermittelt die Anzahl der Verkäufe des aktuellen Tages.
     * 
     * @return Anzahl der heutigen Verkäufe
     * @throws StatistikServiceException bei Datenbankfehlern
     */
    public int getAnzahlVerkaufeHeute() throws StatistikServiceException {
        String heute = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String sql = "SELECT COUNT(*) as anzahl FROM verkaeufe " +
                    "WHERE DATE(timestamp) = ?";
        
        try (var conn = dbManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, heute);
            
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("anzahl");
                }
                return 0;
            }
            
        } catch (SQLException e) {
            throw new StatistikServiceException("Fehler beim Zählen der Verkäufe", e);
        }
    }

    /**
     * Ermittelt die Top 5 meistverkauften Produkte des aktuellen Tages.
     * 
     * @return Liste der Top 5 Produkte mit Verkaufsmenge
     * @throws StatistikServiceException bei Datenbankfehlern
     */
    public List<TopProdukt> getTop5ProdukteMenge() throws StatistikServiceException {
        String heute = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String sql = """
            SELECT p.id, p.name, p.preis, SUM(v.menge) as verkaufte_menge
            FROM produkte p 
            JOIN verkaeufe v ON p.id = v.produkt_id 
            WHERE DATE(v.timestamp) = ?
            GROUP BY p.id, p.name, p.preis 
            ORDER BY verkaufte_menge DESC 
            LIMIT 5
            """;
        
        List<TopProdukt> topProdukte = new ArrayList<>();
        
        try (var conn = dbManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, heute);
            
            try (var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TopProdukt topProdukt = new TopProdukt(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("preis"),
                        rs.getInt("verkaufte_menge")
                    );
                    topProdukte.add(topProdukt);
                }
            }
            
        } catch (SQLException e) {
            throw new StatistikServiceException("Fehler beim Ermitteln der Top-Produkte", e);
        }
        
        return topProdukte;
    }

    /**
     * Ermittelt alle Produkte mit niedrigem Bestand.
     * 
     * @return Liste der Produkte mit Bestand unter dem Schwellwert
     * @throws StatistikServiceException bei Datenbankfehlern
     */
    public List<Produkt> getNiedrigbestandProdukte() throws StatistikServiceException {
        String sql = "SELECT * FROM produkte WHERE bestand < ? ORDER BY bestand ASC";
        List<Produkt> niedrigbestandProdukte = new ArrayList<>();
        
        try (var conn = dbManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, NIEDRIGBESTAND_SCHWELLWERT);
            
            try (var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produkt produkt = new Produkt(
                        rs.getString("name"),
                        rs.getDouble("preis"),
                        rs.getInt("bestand")
                    );
                    produkt.setId(rs.getInt("id"));
                    niedrigbestandProdukte.add(produkt);
                }
            }
            
        } catch (SQLException e) {
            throw new StatistikServiceException("Fehler beim Ermitteln der Niedrigbestand-Produkte", e);
        }
        
        return niedrigbestandProdukte;
    }

    /**
     * Datenklasse für Top-verkaufte Produkte.
     */
    public static class TopProdukt {
        private final int id;
        private final String name;
        private final double preis;
        private final int verkaufteMenge;

        public TopProdukt(int id, String name, double preis, int verkaufteMenge) {
            this.id = id;
            this.name = name;
            this.preis = preis;
            this.verkaufteMenge = verkaufteMenge;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public double getPreis() { return preis; }
        public int getVerkaufteMenge() { return verkaufteMenge; }

        @Override
        public String toString() {
            return String.format("%s (%d verkauft)", name, verkaufteMenge);
        }
    }

    /**
     * Exception-Klasse für StatistikService-Fehler.
     */
    public static class StatistikServiceException extends Exception {
        public StatistikServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
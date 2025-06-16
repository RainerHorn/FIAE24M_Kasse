package de.berufsschule.kasse.database;

import de.berufsschule.kasse.model.Produkt;
import de.berufsschule.kasse.model.Verkauf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Verwaltet die Datenbankverbindung und -operationen für das Kassensystem.
 * 
 * Diese Klasse stellt alle CRUD-Operationen für Produkte und Verkäufe bereit
 * und verwendet SQLite als eingebettete Datenbank.
 *  * @author FIAE24M
 * @version 1.0
 */
public final class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:kasse.db";
    private static DatabaseManager instance;

    /**
     * Private Konstruktor für Singleton-Pattern.
     */
    private DatabaseManager() {
        initialisiereDatenbank();
    }

    /**
     * Gibt die Singleton-Instanz des DatabaseManagers zurück.
     * 
     * @return die DatabaseManager-Instanz
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }    /**
     * Erstellt eine neue Datenbankverbindung.
     * 
     * @return Connection zur Datenbank
     * @throws SQLException bei Verbindungsfehlern
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    /**
     * Initialisiert die Datenbank und erstellt die benötigten Tabellen.
     */
    private void initialisiereDatenbank() {
        String createProduktTable = """
            CREATE TABLE IF NOT EXISTS produkte (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE,
                preis REAL NOT NULL CHECK(preis > 0),
                bestand INTEGER NOT NULL CHECK(bestand >= 0)
            )
        """;

        String createVerkaufTable = """
            CREATE TABLE IF NOT EXISTS verkaeufe (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                timestamp TEXT NOT NULL,
                produkt_id INTEGER NOT NULL,
                menge INTEGER NOT NULL CHECK(menge > 0),
                einzelpreis REAL NOT NULL CHECK(einzelpreis > 0),
                gesamtpreis REAL NOT NULL CHECK(gesamtpreis > 0),
                FOREIGN KEY (produkt_id) REFERENCES produkte (id)
            )
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createProduktTable);
            stmt.execute(createVerkaufTable);
            
            // Testdaten einfügen falls Tabelle leer ist
            if (getAlleProdukte().isEmpty()) {
                erstelleTestdaten();
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Initialisieren der Datenbank", e);
        }
    }

    /**
     * Erstellt Testdaten für die Demonstration.
     */
    private void erstelleTestdaten() {
        try {
            fuegeProduktHinzu(new Produkt("Apfel", 0.50, 100));
            fuegeProduktHinzu(new Produkt("Banane", 0.30, 80));
            fuegeProduktHinzu(new Produkt("Brot", 2.50, 20));
            fuegeProduktHinzu(new Produkt("Milch", 1.20, 15));
            fuegeProduktHinzu(new Produkt("Käse", 4.99, 10));
        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen der Testdaten: " + e.getMessage());
        }
    }

    /**
     * Fügt ein neues Produkt zur Datenbank hinzu.
     * 
     * @param produkt das hinzuzufügende Produkt
     * @return die generierte ID des Produkts
     * @throws SQLException bei Datenbankfehlern
     */
    public int fuegeProduktHinzu(Produkt produkt) throws SQLException {
        String sql = "INSERT INTO produkte (name, preis, bestand) VALUES (?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, produkt.getName());
            pstmt.setDouble(2, produkt.getPreis());
            pstmt.setInt(3, produkt.getBestand());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Produkt konnte nicht hinzugefügt werden");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    produkt.setId(id);
                    return id;
                } else {
                    throw new SQLException("Keine ID für neues Produkt erhalten");
                }
            }
        }
    }

    /**
     * Lädt alle Produkte aus der Datenbank.
     * 
     * @return Liste aller Produkte
     * @throws SQLException bei Datenbankfehlern
     */
    public List<Produkt> getAlleProdukte() throws SQLException {
        List<Produkt> produkte = new ArrayList<>();
        String sql = "SELECT id, name, preis, bestand FROM produkte ORDER BY name";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Produkt produkt = new Produkt(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("preis"),
                    rs.getInt("bestand")
                );
                produkte.add(produkt);
            }
        }
        
        return produkte;
    }

    /**
     * Sucht ein Produkt anhand seiner ID.
     * 
     * @param id die ID des gesuchten Produkts
     * @return das Produkt oder null wenn nicht gefunden
     * @throws SQLException bei Datenbankfehlern
     */
    public Produkt getProduktById(int id) throws SQLException {
        String sql = "SELECT id, name, preis, bestand FROM produkte WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Produkt(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("preis"),
                        rs.getInt("bestand")
                    );
                }
            }
        }
        
        return null;
    }

    /**
     * Aktualisiert den Bestand eines Produkts.
     * 
     * @param produktId die ID des Produkts
     * @param neuerBestand der neue Bestand
     * @throws SQLException bei Datenbankfehlern
     */
    public void aktualisiereBestand(int produktId, int neuerBestand) throws SQLException {
        String sql = "UPDATE produkte SET bestand = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, neuerBestand);
            pstmt.setInt(2, produktId);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Produkt mit ID " + produktId + " nicht gefunden");
            }
        }
    }

    /**
     * Speichert einen Verkauf in der Datenbank.
     * 
     * @param verkauf der zu speichernde Verkauf
     * @throws SQLException bei Datenbankfehlern
     */    public void speichereVerkauf(Verkauf verkauf) throws SQLException {
        String sql = "INSERT INTO verkaeufe (timestamp, produkt_id, menge, einzelpreis, gesamtpreis) "
                + "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, verkauf.getTimestamp().toString());
            pstmt.setInt(2, verkauf.getProduktId());
            pstmt.setInt(3, verkauf.getMenge());
            pstmt.setDouble(4, verkauf.getEinzelpreis());
            pstmt.setDouble(5, verkauf.getGesamtpreis());
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Testet die Datenbankverbindung.
     * 
     * @return true wenn Verbindung erfolgreich, false sonst
     */
    public boolean testVerbindung() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}

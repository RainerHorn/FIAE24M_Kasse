package de.berufsschule.kasse.controller;

import de.berufsschule.kasse.model.Produkt;
import de.berufsschule.kasse.service.KassenService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller für die Hauptansicht des Kassensystems.
 * 
 * Diese Klasse verwaltet die Interaktionen der JavaFX-Hauptoberfläche
 * und koordiniert zwischen GUI und Service-Layer.
 * 
 * @author FIAE24M
 * @version 1.0
 */
public class MainController implements Initializable {
    
    @FXML private Button btnKassenvorgang;
    @FXML private Button btnProduktHinzufuegen;
    @FXML private Button btnWarenzugang;
    @FXML private Button btnLagerbestand;
    @FXML private Button btnBeenden;
    
    @FXML private TableView<Produkt> tableLagerbestand;
    @FXML private TableColumn<Produkt, Integer> colId;
    @FXML private TableColumn<Produkt, String> colName;
    @FXML private TableColumn<Produkt, Double> colPreis;
    @FXML private TableColumn<Produkt, Integer> colBestand;
    
    @FXML private TextField txtProduktName;
    @FXML private TextField txtProduktPreis;
    @FXML private TextField txtProduktBestand;
    @FXML private Button btnProduktSpeichern;
    
    @FXML private ComboBox<Produkt> cmbWarenzugangProdukt;
    @FXML private TextField txtWarenzugangMenge;
    @FXML private Button btnWarenzugangSpeichern;
    
    @FXML private ComboBox<Produkt> cmbKassenProdukt;
    @FXML private TextField txtKassenMenge;
    @FXML private Button btnZumWarenkorbHinzufuegen;
    @FXML private ListView<String> listWarenkorb;
    @FXML private Label lblGesamtbetrag;
    @FXML private Button btnBonAnzeigen;
    @FXML private Button btnNeuerKassenvorgang;
    
    @FXML private TextArea txtBonAnzeige;
    
    @FXML private TabPane tabPane;
    
    private final KassenService kassenService;
    private final ObservableList<Produkt> produktListe;

    /**
     * Konstruktor für den MainController.
     */
    public MainController() {
        this.kassenService = new KassenService();
        this.produktListe = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTableView();
        initializeComboBoxes();
        initializeEventHandlers();
        ladeDaten();
        aktualisiereWarenkorbAnzeige();
    }

    /**
     * Initialisiert die TableView für den Lagerbestand.
     */
    private void initializeTableView() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPreis.setCellValueFactory(new PropertyValueFactory<>("preis"));
        colBestand.setCellValueFactory(new PropertyValueFactory<>("bestand"));
        
        // Formatierung für Preis-Spalte
        colPreis.setCellFactory(col -> new TableCell<Produkt, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f€", item));
                }
            }
        });
        
        tableLagerbestand.setItems(produktListe);
    }

    /**
     * Initialisiert die ComboBoxes für Produktauswahl.
     */
    private void initializeComboBoxes() {
        // Custom StringConverter für bessere Anzeige in ComboBoxes
        cmbWarenzugangProdukt.setConverter(new javafx.util.StringConverter<Produkt>() {
            @Override
            public String toString(Produkt produkt) {
                return produkt != null ? String.format("[%d] %s", produkt.getId(), produkt.getName()) : "";
            }

            @Override
            public Produkt fromString(String string) {
                return null; // Nicht benötigt für ComboBox
            }
        });
        
        cmbKassenProdukt.setConverter(new javafx.util.StringConverter<Produkt>() {
            @Override
            public String toString(Produkt produkt) {
                return produkt != null ? 
                    String.format("[%d] %s (%.2f€, Bestand: %d)", 
                                produkt.getId(), produkt.getName(), produkt.getPreis(), produkt.getBestand()) : "";
            }

            @Override
            public Produkt fromString(String string) {
                return null; // Nicht benötigt für ComboBox
            }
        });
    }

    /**
     * Initialisiert die Event-Handler für alle Buttons.
     */
    private void initializeEventHandlers() {
        btnProduktSpeichern.setOnAction(e -> produktHinzufuegen());
        btnWarenzugangSpeichern.setOnAction(e -> warenzugangErfassen());
        btnZumWarenkorbHinzufuegen.setOnAction(e -> produktZumWarenkorbHinzufuegen());
        btnBonAnzeigen.setOnAction(e -> bonAnzeigenUndAbschliessen());
        btnNeuerKassenvorgang.setOnAction(e -> neuerKassenvorgang());
        btnLagerbestand.setOnAction(e -> {
            ladeDaten();
            tabPane.getSelectionModel().select(0); // Lagerbestand-Tab
        });
        btnBeenden.setOnAction(e -> beenden());
    }

    /**
     * Lädt alle Daten neu aus der Datenbank.
     */
    private void ladeDaten() {
        try {
            produktListe.clear();
            produktListe.addAll(kassenService.getAlleProdukte());
            
            // ComboBoxes aktualisieren
            cmbWarenzugangProdukt.setItems(FXCollections.observableArrayList(produktListe));
            cmbKassenProdukt.setItems(FXCollections.observableArrayList(produktListe));
            
        } catch (KassenService.KassenServiceException e) {
            zeigeFehlermeldung("Fehler beim Laden der Daten", e.getMessage());
        }
    }

    /**
     * Fügt ein neues Produkt hinzu.
     */
    @FXML
    private void produktHinzufuegen() {
        try {
            String name = txtProduktName.getText();
            double preis = Double.parseDouble(txtProduktPreis.getText());
            int bestand = Integer.parseInt(txtProduktBestand.getText());
            
            kassenService.fuegeProduktHinzu(name, preis, bestand);
            
            // Felder leeren
            txtProduktName.clear();
            txtProduktPreis.clear();
            txtProduktBestand.clear();
            
            ladeDaten();
            zeigeSuccessMeldung("Produkt wurde erfolgreich hinzugefügt");
            
        } catch (NumberFormatException e) {
            zeigeFehlermeldung("Eingabefehler", "Bitte geben Sie gültige Zahlen für Preis und Bestand ein");
        } catch (KassenService.KassenServiceException e) {
            zeigeFehlermeldung("Fehler beim Hinzufügen", e.getMessage());
        }
    }

    /**
     * Erfasst einen Warenzugang.
     */
    @FXML
    private void warenzugangErfassen() {
        try {
            Produkt produkt = cmbWarenzugangProdukt.getValue();
            if (produkt == null) {
                zeigeFehlermeldung("Produktauswahl", "Bitte wählen Sie ein Produkt aus");
                return;
            }
            
            int menge = Integer.parseInt(txtWarenzugangMenge.getText());
            
            kassenService.erfasseWarenzugang(produkt.getId(), menge);
            
            txtWarenzugangMenge.clear();
            cmbWarenzugangProdukt.setValue(null);
            
            ladeDaten();
            zeigeSuccessMeldung("Warenzugang wurde erfolgreich erfasst");
            
        } catch (NumberFormatException e) {
            zeigeFehlermeldung("Eingabefehler", "Bitte geben Sie eine gültige Menge ein");
        } catch (KassenService.KassenServiceException e) {
            zeigeFehlermeldung("Fehler beim Warenzugang", e.getMessage());
        }
    }

    /**
     * Fügt ein Produkt zum Warenkorb hinzu.
     */
    @FXML
    private void produktZumWarenkorbHinzufuegen() {
        try {
            Produkt produkt = cmbKassenProdukt.getValue();
            if (produkt == null) {
                zeigeFehlermeldung("Produktauswahl", "Bitte wählen Sie ein Produkt aus");
                return;
            }
            
            int menge = Integer.parseInt(txtKassenMenge.getText());
            
            kassenService.fuegeProduktZumWarenkorbHinzu(produkt.getId(), menge);
            
            txtKassenMenge.clear();
            cmbKassenProdukt.setValue(null);
            
            aktualisiereWarenkorbAnzeige();
            ladeDaten(); // Aktualisierte Bestände anzeigen
            
        } catch (NumberFormatException e) {
            zeigeFehlermeldung("Eingabefehler", "Bitte geben Sie eine gültige Menge ein");
        } catch (KassenService.KassenServiceException e) {
            zeigeFehlermeldung("Fehler beim Hinzufügen", e.getMessage());
        }
    }

    /**
     * Zeigt den Bon an und schließt den Kassenvorgang ab.
     */
    @FXML
    private void bonAnzeigenUndAbschliessen() {
        try {
            String bon = kassenService.schliesseKassenvorgangAb();
            txtBonAnzeige.setText(bon);
            
            aktualisiereWarenkorbAnzeige();
            ladeDaten();
            
            // Zum Bon-Tab wechseln
            tabPane.getSelectionModel().select(4);
            
        } catch (KassenService.KassenServiceException e) {
            zeigeFehlermeldung("Fehler beim Abschließen", e.getMessage());
        }
    }

    /**
     * Startet einen neuen Kassenvorgang.
     */
    @FXML
    private void neuerKassenvorgang() {
        kassenService.starteNeuenKassenvorgang();
        aktualisiereWarenkorbAnzeige();
        txtBonAnzeige.clear();
    }

    /**
     * Aktualisiert die Warenkorb-Anzeige.
     */
    private void aktualisiereWarenkorbAnzeige() {
        ObservableList<String> warenkorbItems = FXCollections.observableArrayList();
        
        kassenService.getAktuellerWarenkorb().getVerkaeufe().forEach(verkauf -> 
            warenkorbItems.add(verkauf.toString())
        );
        
        listWarenkorb.setItems(warenkorbItems);
        lblGesamtbetrag.setText(String.format("Gesamtbetrag: %.2f€", 
                                            kassenService.getAktuellerWarenkorb().berechneGesamtbetrag()));
    }

    /**
     * Beendet die Anwendung.
     */
    @FXML
    private void beenden() {
        System.exit(0);
    }

    /**
     * Zeigt eine Fehlermeldung an.
     */
    private void zeigeFehlermeldung(String titel, String nachricht) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titel);
        alert.setHeaderText(null);
        alert.setContentText(nachricht);
        alert.showAndWait();
    }

    /**
     * Zeigt eine Erfolgsmeldung an.
     */
    private void zeigeSuccessMeldung(String nachricht) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Erfolg");
        alert.setHeaderText(null);
        alert.setContentText(nachricht);
        alert.showAndWait();
    }
}

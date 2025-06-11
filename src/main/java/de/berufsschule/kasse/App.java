package de.berufsschule.kasse;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Hauptklasse der FIAE24M Kassensystem-Anwendung.
 * 
 * Diese Klasse startet die JavaFX-Anwendung und lädt die 
 * Hauptoberfläche des Kassensystems.
 * 
 * @author FIAE24M
 * @version 1.0
 */
public class App extends Application {

    /**
     * Startet die JavaFX-Anwendung.
     * 
     * @param stage die Hauptbühne der Anwendung
     * @throws IOException falls die FXML-Datei nicht geladen werden kann
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        
        stage.setTitle("FIAE24M Kassensystem");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
        
        System.out.println("FIAE24M Kassensystem erfolgreich gestartet!");
    }

    /**
     * Hauptmethode zum Starten der Anwendung.
     * 
     * @param args Kommandozeilenargumente
     */
    public static void main(String[] args) {
        launch(args);
    }
}

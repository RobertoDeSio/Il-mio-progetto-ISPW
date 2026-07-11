package org.ispw.eventi.controller.viewcontroller;

import org.ispw.eventi.model.SessioneUtente;
import org.ispw.eventi.navigation.NavigationService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML private Button btnNavHome;

    private final SessioneUtente    sessione;
    private final NavigationService nav;

    public HomeController(SessioneUtente sessione, NavigationService nav) {
        this.sessione = sessione;
        this.nav      = nav;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnNavHome.getStyleClass().add("nav-link-attivo");
    }

    @FXML private void handleHome()   { nav.goToHome(); }
    @FXML private void handleAccedi() { nav.goToLogin(); }

    @FXML
    private void handlePrenotaEvento() {
        if (sessione.isLoggato() && "CLIENTE".equals(sessione.getRuolo())) {
            // Già loggato come cliente → vai direttamente
            nav.goToEsploraAttivita();
        } else {
            // Non loggato o ruolo sbagliato → login con destinazione ESPLORA
            sessione.setDestinazionePostLogin("ESPLORA");
            nav.goToLogin();
        }
    }

    @FXML
    private void handleCreaEvento() {
        if (sessione.isLoggato() && "ORGANIZZATORE".equals(sessione.getRuolo())) {
            // Già loggato come organizzatore → vai direttamente
            nav.goToGestioneRichieste();
        } else {
            // Non loggato o ruolo sbagliato → login con destinazione GESTIONE
            sessione.setDestinazionePostLogin("GESTIONE");
            nav.goToLogin();
        }
    }
}
package org.ispw.eventi.navigation;

import org.ispw.eventi.controller.viewcontroller.*;
import org.ispw.eventi.exception.NavigationException;
import org.ispw.eventi.model.SessioneUtente;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestisce la navigazione tra le schermate.
 * Riceve sessione e nav separatamente — non li accoppia.
 */
public class NavigationService {

    private static final Logger LOGGER             = Logger.getLogger(NavigationService.class.getName());
    private static final String ERRORE_NAVIGAZIONE = "Errore di navigazione";

    private static final String FXML_HOME                = "/org/ispw/eventi/fxml/home.fxml";
    private static final String FXML_LOGIN               = "/org/ispw/eventi/fxml/login.fxml";
    private static final String FXML_ESPLORA_ATTIVITA    = "/org/ispw/eventi/fxml/esplora.fxml";
    private static final String FXML_PRENOTAZIONI        = "/org/ispw/eventi/fxml/prenotazioni.fxml";
    private static final String FXML_GESTIONE_RICHIESTE  = "/org/ispw/eventi/fxml/gestionerichieste.fxml";
    private static final String FXML_DETTAGLIO_EVENTO    = "/org/ispw/eventi/fxml/dettaglioevento.fxml";
    private static final String FXML_MODULO_PRENOTAZIONE = "/org/ispw/eventi/fxml/moduloprenotazione.fxml";

    private final Stage stage;
    private final SessioneUtente sessione;

    public NavigationService(Stage stage, SessioneUtente sessione) {
        this.stage    = stage;
        this.sessione = sessione;
    }

    // -------------------------------------------------------------------------
    // Metodi di navigazione
    // -------------------------------------------------------------------------

    public void goToHome()               { navigate(FXML_HOME,                new HomeController(sessione, this)); }
    public void goToLogin()              { navigate(FXML_LOGIN,               new LoginController(sessione, this)); }
    public void goToEsploraAttivita()    { navigate(FXML_ESPLORA_ATTIVITA,    new EsploraController(sessione, this)); }
    public void goToDettaglioEvento()    { navigate(FXML_DETTAGLIO_EVENTO,    new DettaglioEventoController(sessione, this)); }
    public void goToPrenotazioni()       { navigate(FXML_PRENOTAZIONI,        new PrenotazioniController(sessione, this)); }
    public void goToGestioneRichieste()  { navigate(FXML_GESTIONE_RICHIESTE,  new GestioneRichiesteController(sessione, this)); }
    public void goToModuloPrenotazione() { navigate(FXML_MODULO_PRENOTAZIONE, new ModuloPrenotazioneController(sessione, this)); }
    public void goToCreaEvento()         { LOGGER.info("Crea Evento non ancora implementato"); }

    // -------------------------------------------------------------------------
    // Navigazione interna
    // -------------------------------------------------------------------------

    private void navigate(String fxmlPath, Object controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setController(controller);
            Parent root = loader.load();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            try {
                throw new NavigationException("Impossibile caricare la schermata: " + fxmlPath, e);
            } catch (NavigationException ex) {
                LOGGER.log(Level.SEVERE, ex, ex::getMessage);
                showError(ERRORE_NAVIGAZIONE, ex.getMessage());
            }
        }
    }

    private void showError(String header, String details) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(ERRORE_NAVIGAZIONE);
        alert.setHeaderText(header);
        alert.setContentText(details);
        alert.showAndWait();
    }
}
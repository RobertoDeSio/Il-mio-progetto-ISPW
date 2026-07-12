package org.ispw.eventi.controller.viewcontroller;

import org.ispw.eventi.controller.appcontroller.PrenotaEventoAppController;
import org.ispw.eventi.exception.EventoNonDisponibileException;
import org.ispw.eventi.exception.ValidationException;
import org.ispw.eventi.model.SessioneUtente;
import org.ispw.eventi.model.bean.EventoBean;
import org.ispw.eventi.model.bean.PrenotazioneBean;
import org.ispw.eventi.model.bean.PrenotazioneRequestBean;
import org.ispw.eventi.model.dao.DAOFactory;
import org.ispw.eventi.navigation.NavigationService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModuloPrenotazioneController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
            ModuloPrenotazioneController.class.getName());

    @FXML private Button    btnNavHome;
    @FXML private Button    btnNavEsplora;
    @FXML private Button    btnNavPrenotazioni;
    @FXML private Label     labelNomeEvento;
    @FXML private TextField fieldPartecipanti;
    @FXML private TextField fieldNumeroDiTelefono;   // sostituisce fieldLuogoDiIncontro
    @FXML private TextArea  fieldNote;
    @FXML private Label     labelMaxPosti;
    @FXML private Label     labelPrezzoPersona;
    @FXML private Label     labelTotale;
    @FXML private Label     labelErrore;
    @FXML private Label     labelSuccesso;

    private final SessioneUtente             sessione;
    private final NavigationService          nav;
    private final PrenotaEventoAppController appController;

    public ModuloPrenotazioneController(SessioneUtente sessione, NavigationService nav) {
        this.sessione      = sessione;
        this.nav           = nav;
        this.appController = new PrenotaEventoAppController(
                DAOFactory.getInstance().getEventoDAO(),
                DAOFactory.getInstance().getPrenotazioneDAO()
        );
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setNavAttivo(btnNavEsplora);
        labelErrore.setVisible(false);
        labelSuccesso.setVisible(false);

        EventoBean bean = sessione.getEventoSelezionato();
        labelNomeEvento.setText(bean.getNome());
        labelMaxPosti.setText("Massimo " + bean.getPostiDisponibili() + " posti disponibili.");
        labelPrezzoPersona.setText(bean.getPrezzoLabel() + " / persona");
        labelTotale.setText(bean.getPrezzoLabel());

        fieldPartecipanti.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                int n = Integer.parseInt(newVal.trim());
                labelTotale.setText("€" + (int)(n * bean.getPrezzo()));
            } catch (NumberFormatException e) {
                labelTotale.setText(bean.getPrezzoLabel());
            }
        });
    }

    @FXML private void handleHome()            { nav.goToHome(); }
    @FXML private void handleEsploraAttivita() { nav.goToEsploraAttivita(); }
    @FXML private void handlePrenotazioni()    { nav.goToPrenotazioni(); }
    @FXML private void handleIndietro()        { nav.goToEsploraAttivita(); }
    @FXML private void handleLogout()          { sessione.logout(); nav.goToHome(); }

    @FXML
    private void handleConferma() {
        labelErrore.setVisible(false);
        labelSuccesso.setVisible(false);

        int partecipanti;
        try {
            partecipanti = Integer.parseInt(fieldPartecipanti.getText().trim());
        } catch (NumberFormatException e) {
            mostraErrore("Inserisci un numero valido di partecipanti.");
            return;
        }

        PrenotazioneRequestBean requestBean = new PrenotazioneRequestBean(
                sessione.getEventoSelezionato().getId(),
                sessione.getUtenteLoggato().getEmail(),
                partecipanti,
                fieldNumeroDiTelefono.getText(),
                fieldNote.getText()
        );

        try {
            PrenotazioneBean esito = appController.richiediPrenotazione(requestBean);
            LOGGER.log(Level.INFO, "Richiesta inviata: {0}", esito.getId());
            nav.goToPrenotazioni();

        } catch (ValidationException e) {
            LOGGER.log(Level.WARNING, "Validazione: {0}", e.getMessage());
            mostraErrore(e.getMessage());
        } catch (EventoNonDisponibileException e) {
            LOGGER.log(Level.WARNING, "Evento non disponibile: {0}", e.getMessage());
            mostraErrore(e.getMessage());
        }
    }

    private void mostraErrore(String msg)   { labelErrore.setText(msg);   labelErrore.setVisible(true); }

    private void setNavAttivo(Button b) {
        for (Button btn : new Button[]{btnNavHome, btnNavEsplora, btnNavPrenotazioni})
            btn.getStyleClass().remove("nav-link-attivo");
        b.getStyleClass().add("nav-link-attivo");
    }
}
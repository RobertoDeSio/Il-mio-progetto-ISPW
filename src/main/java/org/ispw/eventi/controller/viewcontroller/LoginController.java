package org.ispw.eventi.controller.viewcontroller;

import org.ispw.eventi.controller.appcontroller.LoginAppController;
import org.ispw.eventi.exception.AuthenticationException;
import org.ispw.eventi.exception.ValidationException;
import org.ispw.eventi.model.SessioneUtente;
import org.ispw.eventi.model.bean.LoginBean;
import org.ispw.eventi.model.bean.UserBean;
import org.ispw.eventi.model.dao.DAOFactory;
import org.ispw.eventi.navigation.NavigationService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @FXML private TextField     fieldEmail;
    @FXML private PasswordField fieldPassword;
    @FXML private Label         labelErrore;

    private final SessioneUtente     sessione;
    private final NavigationService  nav;
    private final LoginAppController appController;

    public LoginController(SessioneUtente sessione, NavigationService nav) {
        this.sessione      = sessione;
        this.nav           = nav;
        this.appController = new LoginAppController(
                DAOFactory.getInstance().getUtenteDAO()
        );
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelErrore.setVisible(false);
    }

    @FXML private void handleHome() { nav.goToHome(); }

    @FXML private void handleRegistrati() {
        LOGGER.info("Registrazione non attiva in modalità Demo.");
    }

    @FXML
    private void handleLogin() {
        labelErrore.setVisible(false);

        LoginBean bean = new LoginBean(
                fieldEmail.getText(),
                fieldPassword.getText()
        );

        try {
            UserBean userBean = appController.login(bean);
            sessione.setUtenteLoggato(userBean);

            // Naviga in base alla destinazione richiesta e al ruolo dell'utente
            navigaDopoLogin(userBean.getRuolo());

        } catch (ValidationException e) {
            LOGGER.log(Level.WARNING, "Validazione fallita: {0}", e.getMessage());
            mostraErrore(e.getMessage());
        } catch (AuthenticationException e) {
            LOGGER.log(Level.WARNING, "Autenticazione fallita: {0}", e.getMessage());
            mostraErrore(e.getMessage());
        }
    }

    /**
     * Decide dove navigare dopo il login in base a:
     * - la destinazione richiesta (impostata in SessioneUtente da chi ha chiamato goToLogin)
     * - il ruolo dell'utente appena loggato
     *
     * Se il ruolo non corrisponde alla destinazione richiesta mostra un errore.
     */
    private void navigaDopoLogin(String ruolo) {
        String destinazione = sessione.getDestinazionePostLogin();
        sessione.setDestinazionePostLogin(null); // consuma la destinazione

        if ("ESPLORA".equals(destinazione)) {
            // Richiesto da "Prenota Evento" → serve ruolo CLIENTE
            if ("CLIENTE".equals(ruolo)) {
                nav.goToEsploraAttivita();
            } else {
                sessione.logout();
                mostraErrore("Accesso negato: questa sezione è riservata ai clienti.\n" +
                        "Accedi con un account cliente.");
            }

        } else if ("GESTIONE".equals(destinazione)) {
            // Richiesto da "Crea Evento" → serve ruolo ORGANIZZATORE
            if ("ORGANIZZATORE".equals(ruolo)) {
                nav.goToGestioneRichieste();
            } else {
                sessione.logout();
                mostraErrore("Accesso negato: questa sezione è riservata agli organizzatori.\n" +
                        "Accedi con un account organizzatore.");
            }

        } else {
            // Login diretto da "Accedi" → naviga in base al ruolo
            if ("ORGANIZZATORE".equals(ruolo)) {
                nav.goToGestioneRichieste();
            } else {
                nav.goToEsploraAttivita();
            }
        }
    }

    private void mostraErrore(String messaggio) {
        labelErrore.setText(messaggio);
        labelErrore.setVisible(true);
    }
}
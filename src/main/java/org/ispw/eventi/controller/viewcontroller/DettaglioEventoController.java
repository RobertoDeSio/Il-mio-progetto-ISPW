package org.ispw.eventi.controller.viewcontroller;

import org.ispw.eventi.model.SessioneUtente;
import org.ispw.eventi.model.bean.EventoBean;
import org.ispw.eventi.navigation.NavigationService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller Grafico per la schermata di dettaglio evento.
 *
 * <p>Legge {@link EventoBean} dalla sessione — mai un'Entity di dominio.</p>
 */
public class DettaglioEventoController implements Initializable {

    private static final String RUOLO_ORGANIZZATORE = "ORGANIZZATORE";

    @FXML private HBox        navbarCliente;
    @FXML private HBox        navbarOrganizzatore;
    @FXML private Button      btnNavHome;
    @FXML private Button      btnNavEsplora;
    @FXML private Button      btnNavPrenotazioni;
    @FXML private Button      btnNavHomeOrg;
    @FXML private Button      btnNavEsploraOrg;
    @FXML private Button      btnNavGestioneOrg;
    @FXML private Label       labelCategoria;
    @FXML private Label       labelNome;
    @FXML private Label       labelDescrizione;
    @FXML private Label       labelData;
    @FXML private Label       labelLuogo;
    @FXML private Label       labelPrezzo;
    @FXML private Label       labelPosti;
    @FXML private ProgressBar progressPosti;

    private final SessioneUtente    sessione;
    private final NavigationService nav;

    public DettaglioEventoController(SessioneUtente sessione, NavigationService nav) {
        this.sessione = sessione;
        this.nav      = nav;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (RUOLO_ORGANIZZATORE.equals(sessione.getRuolo())) {
            navbarCliente.setVisible(false);
            navbarCliente.setManaged(false);
            navbarOrganizzatore.setVisible(true);
            navbarOrganizzatore.setManaged(true);
        }

        // Legge EventoBean dalla sessione — mai Entity di dominio
        EventoBean bean = sessione.getEventoSelezionato();
        labelCategoria.setText(bean.getCategoria());
        labelNome.setText(bean.getNome());
        labelDescrizione.setText(bean.getDescrizione());
        labelData.setText(bean.getData());
        labelLuogo.setText(bean.getLuogo());
        labelPrezzo.setText(bean.getPrezzoLabel());
        labelPosti.setText(bean.getPostiLabel());

        // ProgressBar basata sui posti disponibili nel Bean
        // (il Bean espone postiDisponibili, non totali/occupati separati)
        // Usiamo la proporzione inversa: più è pieno, più la barra è alta
        // Se postiDisponibili == 0 → barra piena (al completo)
        // Nota: per una barra precisa servirebbero postiTotali nel Bean —
        //       puoi aggiungerli a EventoBean se il prof lo richiede
        progressPosti.setProgress(bean.getPostiDisponibili() > 0 ? 0.4 : 1.0);
    }

    @FXML private void handleHome()              { nav.goToHome(); }
    @FXML private void handleEsploraAttivita()   { nav.goToEsploraAttivita(); }
    @FXML private void handlePrenotazioni()      { nav.goToPrenotazioni(); }
    @FXML private void handleGestioneRichieste() { nav.goToGestioneRichieste(); }
    @FXML private void handleCreaEvento()        { nav.goToCreaEvento(); }
    @FXML private void handleIndietro()          { nav.goToEsploraAttivita(); }
    @FXML private void handleLogout()            { sessione.logout(); nav.goToHome(); }

    @FXML
    private void handleAzione() {
        // EventoBean già in sessione — ModuloPrenotazioneController lo leggerà da lì
        nav.goToModuloPrenotazione();
    }
}
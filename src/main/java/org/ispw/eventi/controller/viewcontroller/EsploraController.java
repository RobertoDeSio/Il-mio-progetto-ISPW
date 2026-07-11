package org.ispw.eventi.controller.viewcontroller;

import org.ispw.eventi.controller.appcontroller.PrenotaEventoAppController;
import org.ispw.eventi.model.SessioneUtente;
import org.ispw.eventi.model.bean.EventoBean;
import org.ispw.eventi.model.dao.DAOFactory;
import org.ispw.eventi.navigation.NavigationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller Grafico per la schermata Esplora.
 *
 * <p>Ottiene gli eventi tramite {@link PrenotaEventoAppController#getEventiDisponibili()}
 * e riceve {@link EventoBean}, mai Entity di dominio.</p>
 */
public class EsploraController implements Initializable {

    private static final Logger LOGGER              = Logger.getLogger(EsploraController.class.getName());
    private static final String FXML_CARD           = "/org/ispw/eventi/fxml/cardevento.fxml";
    private static final String RUOLO_ORGANIZZATORE = "ORGANIZZATORE";

    @FXML private HBox     navbarCliente;
    @FXML private HBox     navbarOrganizzatore;
    @FXML private Button   btnNavHome;
    @FXML private Button   btnNavEsplora;
    @FXML private Button   btnNavPrenotazioni;
    @FXML private Button   btnNavHomeOrg;
    @FXML private Button   btnNavEsploraOrg;
    @FXML private Button   btnNavGestioneOrg;
    @FXML private FlowPane flowPaneEventi;
    @FXML private Button   btnFiltroAll;
    @FXML private Button   btnFiltroMoto;
    @FXML private Button   btnFiltroBoat;
    @FXML private Button   btnFiltroTrekking;

    private final SessioneUtente             sessione;
    private final NavigationService          nav;
    private final PrenotaEventoAppController appController;

    public EsploraController(SessioneUtente sessione, NavigationService nav) {
        this.sessione      = sessione;
        this.nav           = nav;
        this.appController = new PrenotaEventoAppController(
                DAOFactory.getInstance().getEventoDAO(),
                DAOFactory.getInstance().getPrenotazioneDAO()
        );
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (RUOLO_ORGANIZZATORE.equals(sessione.getRuolo())) {
            navbarCliente.setVisible(false);
            navbarCliente.setManaged(false);
            navbarOrganizzatore.setVisible(true);
            navbarOrganizzatore.setManaged(true);
            setNavAttivo(btnNavEsploraOrg, btnNavHomeOrg, btnNavEsploraOrg, btnNavGestioneOrg);
        } else {
            setNavAttivo(btnNavEsplora, btnNavHome, btnNavEsplora, btnNavPrenotazioni);
        }

        // Riceve EventoBean — mai Entity di dominio
        List<EventoBean> eventi = appController.getEventiDisponibili();
        for (EventoBean bean : eventi) {
            aggiungiCard(bean);
        }
    }

    @FXML private void tornaIndietro()           { nav.goToHome(); }
    @FXML private void handleEsplora()           { nav.goToEsploraAttivita(); }
    @FXML private void vaiAPrenotazioni()        { nav.goToPrenotazioni(); }
    @FXML private void handleGestioneRichieste() { nav.goToGestioneRichieste(); }
    @FXML private void handleCreaEvento()        { nav.goToCreaEvento(); }
    @FXML private void handleLogout()            { sessione.logout(); nav.goToHome(); }

    @FXML private void handleFiltroAll()      { setFiltroAttivo(btnFiltroAll); }
    @FXML private void handleFiltroMoto()     { setFiltroAttivo(btnFiltroMoto); }
    @FXML private void handleFiltroBoat()     { setFiltroAttivo(btnFiltroBoat); }
    @FXML private void handleFiltroTrekking() { setFiltroAttivo(btnFiltroTrekking); }

    private void aggiungiCard(EventoBean bean) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_CARD));
            loader.setController(new CardEventoController(sessione, nav, bean));
            VBox card = loader.load();
            flowPaneEventi.getChildren().add(card);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore caricamento card: " + bean.getNome());
        }
    }

    private void setNavAttivo(Button attivo, Button... tutti) {
        for (Button b : tutti) b.getStyleClass().remove("nav-link-attivo");
        attivo.getStyleClass().add("nav-link-attivo");
    }

    private void setFiltroAttivo(Button bottoneAttivo) {
        for (Button b : List.of(btnFiltroAll, btnFiltroMoto, btnFiltroBoat, btnFiltroTrekking))
            b.getStyleClass().remove("filter-button-active");
        bottoneAttivo.getStyleClass().add("filter-button-active");
    }
}
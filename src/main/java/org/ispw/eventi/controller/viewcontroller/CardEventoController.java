package org.ispw.eventi.controller.viewcontroller;

import org.ispw.eventi.model.SessioneUtente;
import org.ispw.eventi.model.bean.EventoBean;
import org.ispw.eventi.navigation.NavigationService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller Grafico per la card di un singolo evento.
 *
 * <p>Riceve un {@link EventoBean} — mai un'Entity di dominio.
 * Al click su "Vedi Dettagli" deposita il Bean in sessione
 * e naviga al dettaglio.</p>
 */
public class CardEventoController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(CardEventoController.class.getName());

    @FXML private Label labelPrezzo;
    @FXML private Label labelCategoria;
    @FXML private Label labelNome;
    @FXML private Label labelData;
    @FXML private Label labelLuogo;
    @FXML private Label labelPosti;

    private final SessioneUtente    sessione;
    private final NavigationService nav;
    private final EventoBean        eventoBean;

    public CardEventoController(SessioneUtente sessione, NavigationService nav,
                                EventoBean eventoBean) {
        this.sessione   = sessione;
        this.nav        = nav;
        this.eventoBean = eventoBean;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelPrezzo.setText(eventoBean.getPrezzoLabel());
        labelCategoria.setText(eventoBean.getCategoria());
        labelNome.setText(eventoBean.getNome());
        labelData.setText("📅 " + eventoBean.getData());
        labelLuogo.setText("📍 " + eventoBean.getLuogo());
        labelPosti.setText("👥 " + eventoBean.getPostiLabel());
    }

    @FXML
    private void handleVediDettagli() {
        LOGGER.log(Level.INFO, "click: vedi dettagli — {0}", eventoBean.getNome());
        sessione.setEventoSelezionato(eventoBean);   // deposita il Bean in sessione
        nav.goToDettaglioEvento();
    }
}
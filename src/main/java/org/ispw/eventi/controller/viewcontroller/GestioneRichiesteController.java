package org.ispw.eventi.controller.viewcontroller;

import org.ispw.eventi.controller.appcontroller.PrenotaEventoAppController;
import org.ispw.eventi.exception.PrenotazioneNotFoundException;
import org.ispw.eventi.model.SessioneUtente;
import org.ispw.eventi.model.bean.PrenotazioneBean;
import org.ispw.eventi.model.dao.DAOFactory;
import org.ispw.eventi.navigation.NavigationService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GestioneRichiesteController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(GestioneRichiesteController.class.getName());

    @FXML private Button btnNavHome;
    @FXML private Button btnNavEsplora;
    @FXML private Button btnNavGestioneRichieste;
    @FXML private Label  labelNomeUtente;
    @FXML private VBox   listaRichieste;

    private final SessioneUtente             sessione;
    private final NavigationService          nav;
    private final PrenotaEventoAppController appController;

    public GestioneRichiesteController(SessioneUtente sessione, NavigationService nav) {
        this.sessione      = sessione;
        this.nav           = nav;
        this.appController = new PrenotaEventoAppController(
                DAOFactory.getInstance().getEventoDAO(),
                DAOFactory.getInstance().getPrenotazioneDAO()
        );
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setNavAttivo(btnNavGestioneRichieste);
        if (sessione.isLoggato()) {
            labelNomeUtente.setText(sessione.getUtenteLoggato().getNome());
        }
        caricaRichieste();
    }

    private void caricaRichieste() {
        listaRichieste.getChildren().clear();

        List<PrenotazioneBean> richieste = appController.getRichiesteOrganizzatore();

        if (richieste.isEmpty()) {
            Label vuoto = new Label("Nessuna richiesta ricevuta.");
            vuoto.setStyle("-fx-text-fill: #64748B; -fx-font-size: 14px;");
            listaRichieste.getChildren().add(vuoto);
            return;
        }

        for (PrenotazioneBean p : richieste) {
            listaRichieste.getChildren().add(creaCard(p));
        }
    }

    private VBox creaCard(PrenotazioneBean p) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.08),8,0,0,2); " +
                "-fx-padding: 16;");
        VBox.setMargin(card, new Insets(0, 0, 12, 0));

        // Header — etichetta e stile badge vengono dallo State, non da switch
        HBox header = new HBox(12);
        Label nome = new Label(p.getNomeEvento());
        nome.setStyle("-fx-font-size: 15px; -fx-font-weight: BOLD; -fx-text-fill: #1E293B;");
        Label badge = new Label(p.getEtichettaStato());
        badge.setStyle(p.getBadgeStile());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(nome, spacer, badge);

        Label cliente = new Label("Cliente: " + p.getClienteEmail() +
                "  •  Partecipanti: " + p.getNumeroPartecipanti());
        cliente.setStyle("-fx-text-fill: #64748B; -fx-font-size: 13px;");

        card.getChildren().addAll(header, cliente);

        // Bottoni Approva/Rifiuta — visibili solo se lo State lo permette
        if (p.isPuoiApprovare() || p.isPuoiRifiutare()) {
            card.getChildren().add(creaAzioni(p.getId(), p.isPuoiApprovare(), p.isPuoiRifiutare()));
        }

        return card;
    }

    private HBox creaAzioni(String prenotazioneId, boolean mostraApprova, boolean mostraRifiuta) {
        HBox row = new HBox(8);
        row.setStyle("-fx-padding: 4 0 0 0;");

        if (mostraApprova) {
            Button btnApprova = new Button("✓ Approva");
            btnApprova.setStyle("-fx-background-color: #16A34A; -fx-text-fill: white; " +
                    "-fx-background-radius: 6; -fx-font-weight: BOLD; -fx-cursor: hand;");
            btnApprova.setOnAction(e -> {
                try {
                    appController.approvaRichiesta(prenotazioneId);
                    caricaRichieste();
                } catch (PrenotazioneNotFoundException ex) {
                    LOGGER.log(Level.SEVERE, "Non trovata: {0}", ex.getMessage());
                }
            });
            row.getChildren().add(btnApprova);
        }

        if (mostraRifiuta) {
            Button btnRifiuta = new Button("✗ Rifiuta");
            btnRifiuta.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; " +
                    "-fx-background-radius: 6; -fx-font-weight: BOLD; -fx-cursor: hand;");
            btnRifiuta.setOnAction(e -> {
                try {
                    appController.rifiutaRichiesta(prenotazioneId);
                    caricaRichieste();
                } catch (PrenotazioneNotFoundException ex) {
                    LOGGER.log(Level.SEVERE, "Non trovata: {0}", ex.getMessage());
                }
            });
            row.getChildren().add(btnRifiuta);
        }

        return row;
    }

    @FXML private void handleHome()              { nav.goToHome(); }
    @FXML private void handleEsploraAttivita()   { nav.goToEsploraAttivita(); }
    @FXML private void handleCreaEvento()        { nav.goToCreaEvento(); }
    @FXML private void handleGestioneRichieste() { nav.goToGestioneRichieste(); }
    @FXML private void handleLogout()            { sessione.logout(); nav.goToHome(); }

    private void setNavAttivo(Button b) {
        for (Button btn : List.of(btnNavHome, btnNavEsplora, btnNavGestioneRichieste))
            btn.getStyleClass().remove("nav-link-attivo");
        b.getStyleClass().add("nav-link-attivo");
    }
}
package org.ispw.eventi.controller.viewcontroller;

import org.ispw.eventi.controller.appcontroller.PrenotaEventoAppController;
import org.ispw.eventi.exception.PagamentoFallitoException;
import org.ispw.eventi.exception.PrenotazioneNotFoundException;
import org.ispw.eventi.model.SessioneUtente;
import org.ispw.eventi.model.bean.PrenotazioneBean;
import org.ispw.eventi.model.bean.PrenotazioneRequestBean;
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

public class PrenotazioniController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(PrenotazioniController.class.getName());

    // Stile comune ai bottoni azione della card prenotazione (SonarQube S1192)
    private static final String STILE_BOTTONE_AZIONE =
            "-fx-background-radius: 6; -fx-font-weight: BOLD; -fx-cursor: hand;";

    @FXML private Button btnNavHome;
    @FXML private Button btnNavEsplora;
    @FXML private Button btnNavPrenotazioni;
    @FXML private Label  labelNomeUtente;
    @FXML private VBox   listaPrenotazioni;

    private final SessioneUtente             sessione;
    private final NavigationService          nav;
    private final PrenotaEventoAppController appController;

    public PrenotazioniController(SessioneUtente sessione, NavigationService nav) {
        this.sessione      = sessione;
        this.nav           = nav;
        this.appController = new PrenotaEventoAppController(
                DAOFactory.getInstance().getEventoDAO(),
                DAOFactory.getInstance().getPrenotazioneDAO()
        );
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setNavAttivo(btnNavPrenotazioni);

        if (sessione.isLoggato()) {
            labelNomeUtente.setText(sessione.getUtenteLoggato().getNome());
            caricaPrenotazioni();
        }
    }

    private void caricaPrenotazioni() {
        listaPrenotazioni.getChildren().clear();

        List<PrenotazioneBean> prenotazioni =
                appController.getPrenotazioniCliente(sessione.getUtenteLoggato().getEmail());

        if (prenotazioni.isEmpty()) {
            Label vuoto = new Label("Non hai ancora nessuna prenotazione.");
            vuoto.setStyle("-fx-text-fill: #64748B; -fx-font-size: 14px;");
            listaPrenotazioni.getChildren().add(vuoto);
            return;
        }

        for (PrenotazioneBean p : prenotazioni) {
            listaPrenotazioni.getChildren().add(creaCard(p));
        }
    }

    private VBox creaCard(PrenotazioneBean p) {
        VBox card = new VBox(8);
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

        Label dettagli = new Label(
                "Partecipanti: " + p.getNumeroPartecipanti() +
                        (p.getTotale() > 0 ? "  •  Totale: " + p.getTotaleLabel() : ""));
        dettagli.setStyle("-fx-text-fill: #64748B; -fx-font-size: 13px;");

        card.getChildren().addAll(header, dettagli);

        // Bottone "Paga ora" — visibile solo se lo State lo permette
        if (p.isPuoiPagare()) {
            card.getChildren().add(creaPulsantePagamento(p.getId()));
        }

        return card;
    }

    private HBox creaPulsantePagamento(String prenotazioneId) {
        Button btnPaga = new Button("💳 Paga ora");
        btnPaga.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; " + STILE_BOTTONE_AZIONE);

        Button btnFallisci = new Button("⚠ Simula pagamento fallito");
        btnFallisci.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; " + STILE_BOTTONE_AZIONE);

        Button btnScadenza = new Button("⌛ Simula scadenza 24h");
        btnScadenza.setStyle("-fx-background-color: #92400E; -fx-text-fill: white; " + STILE_BOTTONE_AZIONE);

        Label feedback = new Label();
        feedback.setWrapText(true);
        feedback.setStyle("-fx-font-size: 12px;");

        btnPaga.setOnAction(e     -> gestisciPagamento(prenotazioneId, "OK", feedback));
        btnFallisci.setOnAction(e -> gestisciPagamento(
                prenotazioneId, PrenotazioneRequestBean.CARTA_TEST_FALLIMENTO, feedback));
        btnScadenza.setOnAction(e -> gestisciScadenza(prenotazioneId, feedback));

        HBox row = new HBox(8, btnPaga, btnFallisci, btnScadenza, feedback);
        row.setStyle("-fx-padding: 4 0 0 0;");
        return row;
    }

    private void gestisciPagamento(String prenotazioneId, String carta, Label feedback) {
        try {
            appController.effettuaPagamento(prenotazioneId, carta);
            caricaPrenotazioni();
        } catch (PagamentoFallitoException ex) {
            LOGGER.log(Level.WARNING, "Pagamento fallito: {0}", ex.getMessage());
            feedback.setText(ex.getMessage());
            feedback.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 12px;");
        } catch (PrenotazioneNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Prenotazione non trovata: {0}", ex.getMessage());
        }
    }

    private void gestisciScadenza(String prenotazioneId, Label feedback) {
        try {
            appController.simulaScadenza(prenotazioneId);
            // Ricarica la lista — la card mostrerà lo stato SCADUTA senza bottoni
            caricaPrenotazioni();
        } catch (PrenotazioneNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Prenotazione non trovata: {0}", ex.getMessage());
            feedback.setText("Errore: prenotazione non trovata.");
            feedback.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 12px;");
        }
    }

    @FXML private void handleHome()            { nav.goToHome(); }
    @FXML private void handleEsploraAttivita() { nav.goToEsploraAttivita(); }
    @FXML private void handlePrenotazioni()    { nav.goToPrenotazioni(); }
    @FXML private void handleLogout()          { sessione.logout(); nav.goToHome(); }

    private void setNavAttivo(Button b) {
        for (Button btn : List.of(btnNavHome, btnNavEsplora, btnNavPrenotazioni))
            btn.getStyleClass().remove("nav-link-attivo");
        b.getStyleClass().add("nav-link-attivo");
    }
}
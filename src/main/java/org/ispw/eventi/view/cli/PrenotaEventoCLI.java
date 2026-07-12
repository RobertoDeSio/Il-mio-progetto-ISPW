package org.ispw.eventi.view.cli;

import org.ispw.eventi.controller.appcontroller.PrenotaEventoAppController;
import org.ispw.eventi.exception.EventoNonDisponibileException;
import org.ispw.eventi.exception.PagamentoFallitoException;
import org.ispw.eventi.exception.PrenotazioneNotFoundException;
import org.ispw.eventi.exception.ValidationException;
import org.ispw.eventi.model.bean.EventoBean;
import org.ispw.eventi.model.bean.PrenotazioneBean;
import org.ispw.eventi.model.bean.PrenotazioneRequestBean;
import org.ispw.eventi.model.dao.DAOFactory;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrenotaEventoCLI {

    private static final Logger LOGGER = Logger.getLogger(PrenotaEventoCLI.class.getName());

    private static final String INVALID_OPTION_MSG = "Opzione non valida. Riprova.";

    private final PrenotaEventoAppController appController;
    private final Scanner scanner;
    private String emailUtenteLoggato;

    public PrenotaEventoCLI() {
        this.appController = new PrenotaEventoAppController(
                DAOFactory.getInstance().getEventoDAO(),
                DAOFactory.getInstance().getPrenotazioneDAO()
        );
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        LOGGER.info("\n--- BENVENUTO IN WEEKENDER (Versione CLI) ---");

        boolean activeSession = true;
        while (activeSession) {
            String menuRuoli = """
                          ==================================
                                SELEZIONE RUOLO / SESSIONE  
                          ==================================
                          1. Accedi come Cliente
                          2. Accedi come Organizzatore
                          3. Chiudi Applicazione (Esci definitivamente)
                          Scegli un opzione (1-3): """.stripTrailing();
            LOGGER.info(menuRuoli);
            String ruolo = scanner.nextLine().trim();

            // Gestione pulita e lineare approvata da SonarQube per eliminare sia java:S6916 che java:S1905
            if (("1".equals(ruolo) || "2".equals(ruolo)) && !richiediEmail()) {
                continue;
            }

            switch (ruolo) {
                case "1" -> menuCliente();
                case "2" -> menuOrganizzatore();
                case "3" -> {
                    LOGGER.info("Uscita definitiva dall applicazione. Arrivederci!");
                    activeSession = false;
                }
                default -> LOGGER.warning(INVALID_OPTION_MSG);
            }
        }
    }

    private boolean richiediEmail() {
        LOGGER.info("Inserisci la tua email per accedere: ");
        this.emailUtenteLoggato = scanner.nextLine().trim();

        if (this.emailUtenteLoggato.isEmpty()) {
            LOGGER.warning("Email non valida. Ritorno al menu principale.");
            return false;
        }
        return true;
    }

    private void menuCliente() {
        boolean running = true;
        while (running) {
            String menu = """
                          ==================================
                                MENU PRINCIPALE CLIENTE     
                          ==================================
                          1. Visualizza Eventi Disponibili
                          2. Richiedi Nuova Prenotazione
                          3. Le Mie Prenotazioni & Pagamenti
                          4. Logout (Torna al cambio ruolo)
                          Scegli un opzione (1-4): """.stripTrailing();
            LOGGER.info(menu);

            String scelta = scanner.nextLine().trim();
            switch (scelta) {
                case "1" -> mostraEventi();
                case "2" -> prenotaEvento();
                case "3" -> mostraEManeggiaPrenotazioniCliente();
                case "4" -> {
                    LOGGER.info("Disconnessione da Cliente effettuata.");
                    running = false;
                }
                default -> LOGGER.warning(INVALID_OPTION_MSG);
            }
        }
    }

    private void mostraEventi() {
        LOGGER.info("\n--- CATALOGO EVENTI DISPONIBILI ---");
        List<EventoBean> eventi = appController.getEventiDisponibili();
        if (eventi.isEmpty()) {
            LOGGER.info("Nessun evento disponibile al momento.");
            return;
        }
        for (EventoBean e : eventi) {
            String logMsg = String.format("ID: %s | %s | Luogo: %s | Posti rimasti: %d | Prezzo: %.2f EUR",
                    e.getId(), e.getNome(), e.getLuogo(), e.getPostiDisponibili(), e.getPrezzo());
            LOGGER.info(logMsg);
        }
    }

    private void prenotaEvento() {
        LOGGER.info("\n--- NUOVA RICHIESTA DI PRENOTAZIONE ---");
        LOGGER.info("Inserisci ID dell evento scelto: ");
        String idEvento = scanner.nextLine().trim();

        LOGGER.info("Numero partecipanti: ");
        int partecipanti;
        try {
            partecipanti = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            LOGGER.warning("Errore: Inserisci un numero intero valido.");
            return;
        }

        LOGGER.info("Inserisci il tuo numero di telefono: ");
        String telefono = scanner.nextLine().trim();

        LOGGER.info("Note facoltative per organizzatore: ");
        String note = scanner.nextLine().trim();

        PrenotazioneRequestBean requestBean = new PrenotazioneRequestBean(
                idEvento, this.emailUtenteLoggato, partecipanti, telefono, note
        );

        try {
            requestBean.validate();
            PrenotazioneBean ris = appController.richiediPrenotazione(requestBean);
            LOGGER.info(() -> "\nRichiesta inviata con successo. Stato attuale: " + ris.getEtichettaStato());
        } catch (ValidationException e) {
            LOGGER.log(Level.WARNING, "Errore di validazione: {0}", e.getMessage());
        } catch (EventoNonDisponibileException e) {
            LOGGER.log(Level.SEVERE, "Errore di disponibilita: {0}", e.getMessage());
        }
    }

    private void mostraEManeggiaPrenotazioniCliente() {
        LOGGER.info("\n--- LE MIE PRENOTAZIONI ---");
        List<PrenotazioneBean> lista = appController.getPrenotazioniCliente(this.emailUtenteLoggato);

        if (lista.isEmpty()) {
            LOGGER.info("Non hai ancora effettuato nessuna prenotazione.");
            return;
        }

        for (int i = 0; i < lista.size(); i++) {
            PrenotazioneBean p = lista.get(i);
            String itemMsg = String.format("[%d] Evento: %s | Stato: %s | Totale: %s",
                    i + 1, p.getNomeEvento(), p.getEtichettaStato(), p.getTotaleLabel());
            LOGGER.info(itemMsg);
        }

        LOGGER.info("\nVuoi gestire una prenotazione? Inserisci il numero (0 per annullare): ");
        int indice = richiediIndiceValido(lista.size());
        if (indice == -1) return;

        PrenotazioneBean selezionata = lista.get(indice);

        if (!selezionata.isPuoiPagare()) {
            LOGGER.info(() -> "Questa prenotazione non e in stato utile al pagamento o attende l approvazione dell organizzatore. Stato attuale: " + selezionata.getEtichettaStato());
            return;
        }

        String opzioni = """
                         Opzioni disponibili:
                         1. Paga Ora (Simula transazione OK)
                         2. Simula Pagamento Fallito (Carta FAIL)
                         3. Simula Scadenza 24h (Annulla e rimuove la prenotazione)
                         Scegli cosa fare: """.stripTrailing();
        LOGGER.info(opzioni);
        String azione = scanner.nextLine().trim();

        try {
            switch (azione) {
                case "1" -> {
                    appController.effettuaPagamento(selezionata.getId(), "OK");
                    LOGGER.info("Pagamento completato! Prenotazione CONFERMATA.");
                }
                case "2" -> simulaFallimentoFlusso(selezionata.getId());
                case "3" -> {
                    appController.simulaScadenza(selezionata.getId());
                    LOGGER.info("Scadenza 24h forzata. La prenotazione e scaduta e non compare piu tra quelle attive.");
                }
                default -> LOGGER.warning(INVALID_OPTION_MSG);
            }
        } catch (PrenotazioneNotFoundException | PagamentoFallitoException e) {
            LOGGER.log(Level.SEVERE, "Errore: {0}", e.getMessage());
        }
    }

    private void menuOrganizzatore() {
        boolean running = true;
        while (running) {
            String menu = """
                          ==================================
                                MENU PRINCIPALE ORGANIZZATORE  
                          ==================================
                          1. Gestisci Richieste di Prenotazione (Approva/Rifiuta)
                          2. Logout (Torna al cambio ruolo)
                          Scegli un opzione (1-2): """.stripTrailing();
            LOGGER.info(menu);

            String choix = scanner.nextLine().trim();
            switch (choix) {
                case "1" -> gestisciRichiesteOrganizzatore();
                case "2" -> {
                    LOGGER.info("Disconnessione da Organizzatore effettuata.");
                    running = false;
                }
                default -> LOGGER.warning(INVALID_OPTION_MSG);
            }
        }
    }

    private void gestisciRichiesteOrganizzatore() {
        LOGGER.info("\n--- RICHIESTE DI PRENOTAZIONE DA GESTIRE ---");
        List<PrenotazioneBean> lista = appController.getRichiesteOrganizzatore();

        if (lista.isEmpty()) {
            LOGGER.info("Nessuna richiesta di prenotazione in attesa.");
            return;
        }

        for (int i = 0; i < lista.size(); i++) {
            PrenotazioneBean p = lista.get(i);
            String itemMsg = String.format("[%d] Da: %s | Evento: %s | Partecipanti: %d | Stato: %s",
                    i + 1, p.getClienteEmail(), p.getNomeEvento(), p.getNumeroPartecipanti(), p.getEtichettaStato());
            LOGGER.info(itemMsg);
        }

        LOGGER.info("\nSeleziona il numero della richiesta da gestire (0 per annullare): ");
        int indice = richiediIndiceValido(lista.size());
        if (indice == -1) return;

        PrenotazioneBean selezionata = lista.get(indice);

        if (!"IN_ATTESA".equalsIgnoreCase(selezionata.getStato())) {
            LOGGER.info(() -> "Questa richiesta e gia stata elaborata. Stato attuale: " + selezionata.getEtichettaStato());
            return;
        }

        String opzioniValutazione = """
                                    Come vuoi valutare questa richiesta?
                                    1. Approva (Il cliente potra pagare)
                                    2. Rifiuta (Annulla la richiesta)
                                    Scegli (1-2): """.stripTrailing();
        LOGGER.info(opzioniValutazione);
        String scelta = scanner.nextLine().trim();

        try {
            if ("1".equals(scelta)) {
                appController.approvaRichiesta(selezionata.getId());
                LOGGER.info("Richiesta approvata con successo! Ora il cliente puo completare il pagamento eseguendo il logout e rientrando come Cliente.");
            } else if ("2".equals(scelta)) {
                appController.rifiutaRichiesta(selezionata.getId());
                LOGGER.info("Richiesta rifiutata correttamente.");
            } else {
                LOGGER.warning("Azione non valida. Operazione annullata.");
            }
        } catch (PrenotazioneNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Errore: Prenotazione non trovata: {0}", e.getMessage());
        }
    }

    private int richiediIndiceValido(int size) {
        try {
            int indice = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (indice >= 0 && indice < size) {
                return indice;
            }
        } catch (NumberFormatException e) {
            // Catturato in conformita a SonarQube
        }
        LOGGER.info("Operazione annullata o input non valido.");
        return -1;
    }

    private void simulaFallimentoFlusso(String prenotazioneId) {
        try {
            appController.effettuaPagamento(prenotazioneId, PrenotazioneRequestBean.CARTA_TEST_FALLIMENTO);
        } catch (PagamentoFallitoException ex) {
            LOGGER.log(Level.WARNING, "Errore simulato ricevuto da AppController: {0}", ex.getMessage());
        } catch (PrenotazioneNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Prenotazione non trovata durante simulazione: {0}", ex.getMessage());
        }
    }
}
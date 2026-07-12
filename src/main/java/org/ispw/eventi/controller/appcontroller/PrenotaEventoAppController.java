package org.ispw.eventi.controller.appcontroller;

import org.ispw.eventi.model.state.StatoPrenotazione;
import org.ispw.eventi.exception.EventoNonDisponibileException;
import org.ispw.eventi.exception.PagamentoFallitoException;
import org.ispw.eventi.exception.PrenotazioneNotFoundException;
import org.ispw.eventi.exception.ValidationException;
import org.ispw.eventi.model.bean.EventoBean;
import org.ispw.eventi.model.bean.PrenotazioneBean;
import org.ispw.eventi.model.bean.PrenotazioneRequestBean;
import org.ispw.eventi.model.dao.EventoDAO;
import org.ispw.eventi.model.dao.PrenotazioneDAO;
import org.ispw.eventi.model.entity.Evento;
import org.ispw.eventi.model.entity.Prenotazione;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.time.ZoneId;

/**
 * Controller Applicativo per il caso d'uso "Prenota Evento".
 *
 * Gestisce il flusso completo multi-step:
 *   Step 1-6  → richiediPrenotazione()  [cliente]
 *   Step 7    → approvaRichiesta()      [organizzatore]
 *   Step 7a   → rifiutaRichiesta()      [organizzatore]
 *   Step 9    → effettuaPagamento()     [cliente]
 *
 * Stateless: i due DAO sono gli unici attributi immutabili.
 * Thread-safe: più finestre possono condividere la stessa istanza.
 */
public class PrenotaEventoAppController {

    private static final Logger LOGGER = Logger.getLogger(
            PrenotaEventoAppController.class.getName());

    private final EventoDAO eventoDAO;
    private final PrenotazioneDAO prenotazioneDAO;

    public PrenotaEventoAppController(EventoDAO eventoDAO,
                                      PrenotazioneDAO prenotazioneDAO) {
        this.eventoDAO = eventoDAO;
        this.prenotazioneDAO = prenotazioneDAO;
    }

    // =========================================================================
    // Step 2 — Catalogo eventi per la UI
    // =========================================================================

    public List<EventoBean> getEventiDisponibili() {
        List<Evento> eventi = eventoDAO.findAll();
        List<EventoBean> beans = new ArrayList<>();
        for (Evento e : eventi) {
            beans.add(toEventoBean(e));
        }
        return beans;
    }

    // =========================================================================
    // Step 3-6 — Cliente invia la richiesta di prenotazione
    // =========================================================================

    /**
     * Valida i dati, verifica i posti e salva la prenotazione in IN_ATTESA.
     * Non simula nulla: il flusso si ferma qui e aspetta l'organizzatore.
     *
     * @throws ValidationException           campi invalidi
     * @throws EventoNonDisponibileException evento al completo (extension 4a)
     */
    public PrenotazioneBean richiediPrenotazione(PrenotazioneRequestBean bean)
            throws ValidationException, EventoNonDisponibileException {

        // 1. Validazione sintattica — fail-fast
        bean.validate();

        // 2. Recupero evento e verifica posti
        Evento evento = eventoDAO.findById(bean.getEventoId());
        if (evento == null) {
            throw new ValidationException("L'evento selezionato non esiste più nel catalogo.");
        }

        int postiDisponibili = evento.getPostiTotali() - evento.getPostiOccupati();
        if (postiDisponibili <= 0) {
            throw new EventoNonDisponibileException(
                    "L'evento \"" + evento.getNome() + "\" è al completo.");
        }
        if (bean.getNumeroPartecipanti() > postiDisponibili) {
            throw new EventoNonDisponibileException(
                    "Posti richiesti (" + bean.getNumeroPartecipanti() + ") " +
                            "superiori ai disponibili (" + postiDisponibili + ").");
        }

        // 3. Salva in IN_ATTESA — il flusso si ferma qui
        Prenotazione prenotazione = new Prenotazione(
                UUID.randomUUID().toString(),
                evento.getId(),
                bean.getClienteEmail(),
                bean.getNumeroPartecipanti(),
                bean.getNote()
        );
        // stato default = IN_ATTESA (impostato dal costruttore)
        prenotazioneDAO.save(prenotazione);

        LOGGER.info(() -> "[STEP 6] Richiesta inviata all'organizzatore: "
                + evento.getNome() + " — in attesa di risposta.");

        return toPrenotazioneBean(prenotazione, evento.getNome(), 0);
    }

    // =========================================================================
    // Step 7 — Organizzatore approva
    // =========================================================================

    /**
     * Cambia lo stato da IN_ATTESA ad APPROVATA.
     * Il cliente potrà ora procedere con il pagamento.
     *
     * @throws PrenotazioneNotFoundException prenotazione non trovata
     */
    public PrenotazioneBean approvaRichiesta(String prenotazioneId)
            throws PrenotazioneNotFoundException {

        Prenotazione prenotazione = trovaOLancia(prenotazioneId);

        // Delega la transizione all'Entity — che la delega allo State
        // Lancia IllegalStateException se la prenotazione non è IN_ATTESA
        prenotazione.approva();
        prenotazioneDAO.update(prenotazione);

        Evento evento = eventoDAO.findById(prenotazione.getEventoId());
        String nomeEvento = evento != null ? evento.getNome() : prenotazione.getEventoId();

        LOGGER.info(() -> "[STEP 7] Organizzatore: APPROVATA — " + prenotazioneId);

        return toPrenotazioneBean(prenotazione, nomeEvento, 0);
    }

    // =========================================================================
    // Step 7a — Organizzatore rifiuta (extension)
    // =========================================================================

    /**
     * Cambia lo stato da IN_ATTESA a RIFIUTATA.
     * Il caso d'uso termina qui per questo cliente.
     *
     * @throws PrenotazioneNotFoundException prenotazione non trovata
     */
    public PrenotazioneBean rifiutaRichiesta(String prenotazioneId)
            throws PrenotazioneNotFoundException {

        Prenotazione prenotazione = trovaOLancia(prenotazioneId);

        prenotazione.rifiuta();
        prenotazioneDAO.update(prenotazione);

        Evento evento = eventoDAO.findById(prenotazione.getEventoId());
        String nomeEvento = evento != null ? evento.getNome() : prenotazione.getEventoId();

        LOGGER.info(() -> "[STEP 7a] Organizzatore: RIFIUTATA — " + prenotazioneId);

        return toPrenotazioneBean(prenotazione, nomeEvento, 0);
    }

    // =========================================================================
    // Step 9 — Cliente effettua il pagamento
    // =========================================================================

    /**
     * Elabora il pagamento per una prenotazione APPROVATA.
     * Se va a buon fine: stato → CONFERMATA, posti evento aggiornati.
     * Se fallisce (carta = "FAIL"): stato rimane APPROVATA (extension 9a).
     *
     * @throws PrenotazioneNotFoundException prenotazione non trovata
     * @throws PagamentoFallitoException     pagamento non andato a buon fine
     */
    public PrenotazioneBean effettuaPagamento(String prenotazioneId, String numeroCarta)
            throws PrenotazioneNotFoundException, PagamentoFallitoException {

        Prenotazione prenotazione = trovaOLancia(prenotazioneId);

        Evento evento = eventoDAO.findById(prenotazione.getEventoId());
        double totale = prenotazione.getNumeroPartecipanti()
                * (evento != null ? evento.getPrezzo() : 0);

        // Simulazione pagamento (extension 9a)
        if (PrenotazioneRequestBean.CARTA_TEST_FALLIMENTO
                .equalsIgnoreCase(numeroCarta)) {
            LOGGER.warning(() -> "[STEP 9] Pagamento fallito — carta: " + numeroCarta);
            throw new PagamentoFallitoException(
                    "Pagamento non andato a buon fine. Riprova con un'altra carta.",
                    prenotazioneId
            );
        }

        // Transizione via State — lancia IllegalStateException se non APPROVATA
        prenotazione.paga();
        prenotazioneDAO.update(prenotazione);

        if (evento != null) {
            evento.setPostiOccupati(
                    evento.getPostiOccupati() + prenotazione.getNumeroPartecipanti());
            eventoDAO.update(evento);
        }

        LOGGER.info(() -> "[STEP 9-10] Pagamento OK — prenotazione CONFERMATA: "
                + prenotazioneId);

        String nomeEvento = evento != null ? evento.getNome() : prenotazione.getEventoId();
        return toPrenotazioneBean(prenotazione, nomeEvento, totale);
    }

    // =========================================================================
    // Metodi di lettura per i controller grafici
    // =========================================================================

    /**
     * Tutte le prenotazioni di un cliente (per PrenotazioniController).
     */
    public List<PrenotazioneBean> getPrenotazioniCliente(String clienteEmail) {
        List<PrenotazioneBean> beans = new ArrayList<>();
        for (Prenotazione p : prenotazioneDAO.findByClienteEmail(clienteEmail)) {
            Evento e = eventoDAO.findById(p.getEventoId());
            String nome = e != null ? e.getNome() : p.getEventoId();
            double totale = e != null ? p.getNumeroPartecipanti() * e.getPrezzo() : 0;
            beans.add(toPrenotazioneBean(p, nome, totale));
        }
        return beans;
    }

    /**
     * Tutte le prenotazioni IN_ATTESA o APPROVATA (per GestioneRichiesteController).
     */
    public List<PrenotazioneBean> getRichiesteOrganizzatore() {
        List<PrenotazioneBean> beans = new ArrayList<>();
        for (Prenotazione p : prenotazioneDAO.findAll()) {
            if (p.getStato() == Prenotazione.Stato.IN_ATTESA
                    || p.getStato() == Prenotazione.Stato.APPROVATA
                    || p.getStato() == Prenotazione.Stato.CONFERMATA) {
                Evento e = eventoDAO.findById(p.getEventoId());
                String nome = e != null ? e.getNome() : p.getEventoId();
                double totale = e != null ? p.getNumeroPartecipanti() * e.getPrezzo() : 0;
                beans.add(toPrenotazioneBean(p, nome, totale));
            }
        }
        return beans;
    }

    // =========================================================================
    // Helpers privati
    // =========================================================================

    private Prenotazione trovaOLancia(String id) throws PrenotazioneNotFoundException {
        Prenotazione p = prenotazioneDAO.findById(id);
        if (p == null) throw new PrenotazioneNotFoundException(
                "Prenotazione non trovata: " + id);
        return p;
    }

    private EventoBean toEventoBean(Evento e) {
        int disponibili = e.getPostiTotali() - e.getPostiOccupati();
        return EventoBean.builder()
                .id(e.getId())
                .nome(e.getNome())
                .descrizione(e.getDescrizione())
                .data(e.getData())
                .luogo(e.getLuogo())
                .categoria(e.getCategoria().name())
                .postiDisponibili(disponibili)
                .prezzo(e.getPrezzo())
                .build();
    }

    private PrenotazioneBean toPrenotazioneBean(Prenotazione p,
                                                String nomeEvento,
                                                double totale) {
        StatoPrenotazione stato = p.getStatoCorrente();
        String dataAppr = p.getDataApprovazione() != null
                ? p.getDataApprovazione().toString()
                : null;
        return PrenotazioneBean.builder()
                .id(p.getId())
                .nomeEvento(nomeEvento)
                .clienteEmail(p.getClienteEmail())
                .numeroPartecipanti(p.getNumeroPartecipanti())
                .note(p.getNote())
                .stato(p.getStato().name())
                .totale(totale)
                .puoiPagare(stato.puoiPagare())
                .puoiApprovare(stato.puoiApprovare())
                .puoiRifiutare(stato.puoiRifiutare())
                .etichettaStato(stato.getEtichetta())
                .badgeStile(stato.getBadgeStile())
                .dataApprovazione(dataAppr)
                .build();
    }


// =========================================================================
// Simulazione scadenza 24h
// =========================================================================

    /**
     * Simula la scadenza delle 24h spostando la dataApprovazione indietro
     * di 25 ore e verificando immediatamente se la prenotazione è scaduta.
     * <p>
     * Flusso:
     * 1. Sposta dataApprovazione = now - 25h (simula il passare del tempo)
     * 2. Chiama isScaduta(now) → true
     * 3. Chiama prenotazione.scadi() → transizione State APPROVATA → SCADUTA
     * 4. Persiste e restituisce il Bean aggiornato
     *
     * @throws PrenotazioneNotFoundException prenotazione non trovata
     * @throws IllegalStateException         se la prenotazione non è APPROVATA
     */
    public PrenotazioneBean simulaScadenza(String prenotazioneId)
            throws PrenotazioneNotFoundException {

        Prenotazione prenotazione = trovaOLancia(prenotazioneId);

        // 1. Sposta dataApprovazione 25h nel passato
        prenotazione.setDataApprovazione(
                java.time.LocalDateTime.now(ZoneId.of("Europe/Rome")).minusHours(25)
        );

        // 2. Verifica scadenza con il tempo reale — ora sarà true
        if (prenotazione.isScaduta(java.time.LocalDateTime.now(ZoneId.of("Europe/Rome")))) {
            // 3. Transizione via State
            prenotazione.scadi();
            prenotazioneDAO.update(prenotazione);
            LOGGER.info(() -> "[SCADENZA] Prenotazione " + prenotazioneId +
                    " scaduta per mancato pagamento entro 24h.");
        }

        Evento evento = eventoDAO.findById(prenotazione.getEventoId());
        String nomeEvento = evento != null ? evento.getNome() : prenotazione.getEventoId();
        return toPrenotazioneBean(prenotazione, nomeEvento, 0);
    }
}
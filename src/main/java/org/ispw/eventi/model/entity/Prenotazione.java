package org.ispw.eventi.model.entity;

import org.ispw.eventi.model.state.*;

/**
 * Entity Prenotazione con pattern State integrato.
 *
 * L'enum Stato viene mantenuto per la persistenza (Gson, SQLite).
 * Il metodo getStatoCorrente() costruisce l'oggetto State corrispondente
 * che incapsula tutta la logica di transizione e presentazione.
 *
 * In questo modo:
 * - la persistenza rimane semplice (salva/legge una stringa enum)
 * - la logica di dominio è incapsulata negli oggetti State
 * - i controller non hanno più switch/if sullo stato
 */
public class Prenotazione {

    public enum Stato { IN_ATTESA, APPROVATA, RIFIUTATA, CONFERMATA }

    private String id;
    private String eventoId;
    private String clienteEmail;
    private int    numeroPartecipanti;
    private String note;
    private Stato  stato;

    public Prenotazione(String id, String eventoId, String clienteEmail,
                        int numeroPartecipanti, String note) {
        this.id                 = id;
        this.eventoId           = eventoId;
        this.clienteEmail       = clienteEmail;
        this.numeroPartecipanti = numeroPartecipanti;
        this.note               = note;
        this.stato              = Stato.IN_ATTESA;
    }

    // -------------------------------------------------------------------------
    // Pattern State — punto di accesso alla macchina a stati
    // -------------------------------------------------------------------------

    /**
     * Restituisce l'oggetto State corrispondente allo stato corrente.
     * Usato dall'AppController per le transizioni e dal Bean per la UI.
     */
    public StatoPrenotazione getStatoCorrente() {
        return switch (stato) {
            case IN_ATTESA  -> new StatoInAttesa();
            case APPROVATA  -> new StatoApprovata();
            case RIFIUTATA  -> new StatoRifiutata();
            case CONFERMATA -> new StatoConfermata();
        };
    }

    /**
     * Esegue la transizione di approvazione delegando allo State.
     * Aggiorna l'enum interno per la persistenza.
     *
     * @throws IllegalStateException se la transizione non è permessa
     */
    public void approva() {
        StatoPrenotazione nuovoStato = getStatoCorrente().approva();
        this.stato = fromState(nuovoStato);
    }

    /**
     * Esegue la transizione di rifiuto delegando allo State.
     *
     * @throws IllegalStateException se la transizione non è permessa
     */
    public void rifiuta() {
        StatoPrenotazione nuovoStato = getStatoCorrente().rifiuta();
        this.stato = fromState(nuovoStato);
    }

    /**
     * Esegue la transizione di pagamento delegando allo State.
     *
     * @throws IllegalStateException se la transizione non è permessa
     */
    public void paga() {
        StatoPrenotazione nuovoStato = getStatoCorrente().paga();
        this.stato = fromState(nuovoStato);
    }

    // -------------------------------------------------------------------------
    // Getter / Setter
    // -------------------------------------------------------------------------

    public String getId()                  { return id; }
    public String getEventoId()            { return eventoId; }
    public String getClienteEmail()        { return clienteEmail; }
    public int    getNumeroPartecipanti()  { return numeroPartecipanti; }
    public String getNote()                { return note; }
    public Stato  getStato()               { return stato; }

    public void setStato(Stato stato)      { this.stato = stato; }

    // -------------------------------------------------------------------------
    // Helper privato — converte oggetto State → enum per la persistenza
    // -------------------------------------------------------------------------

    private Stato fromState(StatoPrenotazione state) {
        if (state instanceof StatoInAttesa)  return Stato.IN_ATTESA;
        if (state instanceof StatoApprovata) return Stato.APPROVATA;
        if (state instanceof StatoRifiutata) return Stato.RIFIUTATA;
        if (state instanceof StatoConfermata)return Stato.CONFERMATA;
        throw new IllegalStateException("Stato sconosciuto: " + state.getClass().getSimpleName());
    }
}
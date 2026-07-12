package org.ispw.eventi.model.entity;

import org.ispw.eventi.model.state.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Entity Prenotazione con pattern State integrato.
 *
 * Aggiunta: dataApprovazione — memorizza il momento in cui l'organizzatore
 * approva la prenotazione. Serve per verificare la scadenza delle 24h.
 */
public class Prenotazione {

    public enum Stato { IN_ATTESA, APPROVATA, RIFIUTATA, CONFERMATA, SCADUTA }

    private static final long ORE_SCADENZA = 24L;

    private String        id;
    private String        eventoId;
    private String        clienteEmail;
    private int           numeroPartecipanti;
    private String        note;
    private Stato         stato;
    private LocalDateTime dataApprovazione;   // null finché non viene approvata

    public Prenotazione(String id, String eventoId, String clienteEmail,
                        int numeroPartecipanti, String note) {
        this.id                 = id;
        this.eventoId           = eventoId;
        this.clienteEmail       = clienteEmail;
        this.numeroPartecipanti = numeroPartecipanti;
        this.note               = note;
        this.stato              = Stato.IN_ATTESA;
        this.dataApprovazione   = null;
    }

    // -------------------------------------------------------------------------
    // Pattern State
    // -------------------------------------------------------------------------

    public StatoPrenotazione getStatoCorrente() {
        return switch (stato) {
            case IN_ATTESA  -> new StatoInAttesa();
            case APPROVATA  -> new StatoApprovata();
            case RIFIUTATA  -> new StatoRifiutata();
            case CONFERMATA -> new StatoConfermata();
            case SCADUTA    -> new StatoScaduta();
        };
    }

    /** Approva la prenotazione e registra il timestamp di approvazione. */
    public void approva() {
        StatoPrenotazione nuovoStato = getStatoCorrente().approva();
        this.stato            = fromState(nuovoStato);
        this.dataApprovazione = LocalDateTime.now(ZoneId.of("Europe/Rome"));   // ← timestamp registrato qui
    }

    public void rifiuta() {
        this.stato = fromState(getStatoCorrente().rifiuta());
    }

    public void paga() {
        this.stato = fromState(getStatoCorrente().paga());
    }

    /**
     * Transizione di scadenza — delegata allo State.
     *
     * Contiene una guardia interna: verifica che le 24h siano effettivamente
     * trascorse prima di permettere la transizione. Questo protegge l'Entity
     * da chiamate dirette a scadi() senza il controllo del tempo.
     *
     * @throws IllegalStateException se le 24h non sono ancora trascorse
     *                               o se la prenotazione non è APPROVATA
     */
    public void scadi() {
        if (!isScaduta(LocalDateTime.now(ZoneId.of("Europe/Rome")))) {
            throw new IllegalStateException(
                    "Non è possibile far scadere una prenotazione prima delle 24h " +
                            "dall'approvazione.");
        }
        this.stato = fromState(getStatoCorrente().scadi());
    }

    /**
     * Verifica se le 24h dall'approvazione sono trascorse.
     * Usato dall'AppController per decidere se chiamare scadi().
     *
     * @param riferimento il momento da confrontare con dataApprovazione
     *                    (normalmente LocalDateTime.now(), ma può essere
     *                     manipolato dalla simulazione)
     */
    public boolean isScaduta(LocalDateTime riferimento) {
        if (stato != Stato.APPROVATA || dataApprovazione == null) return false;
        return riferimento.isAfter(dataApprovazione.plusHours(ORE_SCADENZA));
    }

    // -------------------------------------------------------------------------
    // Getter / Setter
    // -------------------------------------------------------------------------

    public String        getId()                  { return id; }
    public String        getEventoId()            { return eventoId; }
    public String        getClienteEmail()        { return clienteEmail; }
    public int           getNumeroPartecipanti()  { return numeroPartecipanti; }
    public String        getNote()                { return note; }
    public Stato         getStato()               { return stato; }
    public LocalDateTime getDataApprovazione()    { return dataApprovazione; }

    public void setStato(Stato stato)                          { this.stato = stato; }
    public void setDataApprovazione(LocalDateTime dataApprovazione) {
        this.dataApprovazione = dataApprovazione;
    }

    // -------------------------------------------------------------------------
    // Helper privato
    // -------------------------------------------------------------------------

    private Stato fromState(StatoPrenotazione state) {
        if (state instanceof StatoInAttesa)  return Stato.IN_ATTESA;
        if (state instanceof StatoApprovata) return Stato.APPROVATA;
        if (state instanceof StatoRifiutata) return Stato.RIFIUTATA;
        if (state instanceof StatoConfermata)return Stato.CONFERMATA;
        if (state instanceof StatoScaduta)   return Stato.SCADUTA;
        throw new IllegalStateException("Stato sconosciuto: " + state.getClass().getSimpleName());
    }
}
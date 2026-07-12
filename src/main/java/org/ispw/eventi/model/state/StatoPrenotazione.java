package org.ispw.eventi.model.state;

/**
 * Interfaccia del pattern State per la Prenotazione.
 *
 * Ogni stato concreto sa:
 * - quali azioni sono permesse (guard)
 * - come transitare allo stato successivo
 * - come presentarsi alla UI (etichetta e stile badge)
 *
 * L'Entity Prenotazione delega a questi oggetti tutta la logica
 * legata allo stato, eliminando switch/if sparsi nei controller.
 */
public interface StatoPrenotazione {

    // -------------------------------------------------------------------------
    // Guard — cosa è permesso fare in questo stato
    // -------------------------------------------------------------------------

    boolean puoiApprovare();
    boolean puoiRifiutare();
    boolean puoiPagare();

    // -------------------------------------------------------------------------
    // Transizioni — restituiscono il nuovo stato
    // Lanciano IllegalStateException se la transizione non è permessa
    // -------------------------------------------------------------------------

    StatoPrenotazione approva();
    StatoPrenotazione rifiuta();
    StatoPrenotazione paga();

    /**
     * Transizione di scadenza — permessa solo dallo stato APPROVATA.
     * Chiamata dall'AppController quando le 24h sono trascorse senza pagamento.
     */
    StatoPrenotazione scadi();

    // -------------------------------------------------------------------------
    // Presentazione per la UI (passate al Bean, mai viste direttamente dalla UI)
    // -------------------------------------------------------------------------

    String getEtichetta();
    String getBadgeStile();
}
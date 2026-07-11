package org.ispw.eventi.model.state;

/**
 * Stato iniziale della prenotazione.
 * Il cliente ha inviato la richiesta, l'organizzatore non ha ancora risposto.
 *
 * Transizioni permesse: approva() → StatoApprovata
 *                       rifiuta() → StatoRifiutata
 */
public class StatoInAttesa implements StatoPrenotazione {

    private static final String BADGE_STILE =
            "-fx-background-radius:4;-fx-padding:2 8 2 8;-fx-font-size:11px;" +
                    "-fx-font-weight:BOLD;-fx-background-color:#FEF3C7;-fx-text-fill:#92400E;";

    @Override public boolean puoiApprovare() { return true; }
    @Override public boolean puoiRifiutare() { return true; }
    @Override public boolean puoiPagare()    { return false; }

    @Override
    public StatoPrenotazione approva() {
        return new StatoApprovata();
    }

    @Override
    public StatoPrenotazione rifiuta() {
        return new StatoRifiutata();
    }

    @Override
    public StatoPrenotazione paga() {
        throw new IllegalStateException(
                "Non è possibile pagare una prenotazione ancora in attesa di conferma.");
    }

    @Override public String getEtichetta()  { return "⏳ In attesa di conferma"; }
    @Override public String getBadgeStile() { return BADGE_STILE; }
}
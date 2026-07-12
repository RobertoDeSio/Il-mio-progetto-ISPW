package org.ispw.eventi.model.state;

/**
 * La prenotazione era APPROVATA ma il cliente non ha pagato entro 24 ore.
 * Stato terminale — nessuna transizione permessa.
 */
public class StatoScaduta implements StatoPrenotazione {

    private static final String BADGE_STILE =
            "-fx-background-radius:4;-fx-padding:2 8 2 8;-fx-font-size:11px;" +
                    "-fx-font-weight:BOLD;-fx-background-color:#FEF3C7;-fx-text-fill:#92400E;";

    @Override public boolean puoiApprovare() { return false; }
    @Override public boolean puoiRifiutare() { return false; }
    @Override public boolean puoiPagare()    { return false; }

    @Override
    public StatoPrenotazione approva() {
        throw new IllegalStateException("La prenotazione è scaduta per mancato pagamento.");
    }

    @Override
    public StatoPrenotazione rifiuta() {
        throw new IllegalStateException("La prenotazione è scaduta per mancato pagamento.");
    }

    @Override
    public StatoPrenotazione paga() {
        throw new IllegalStateException("La prenotazione è scaduta — non è più possibile pagare.");
    }

    @Override
    public StatoPrenotazione scadi() {
        throw new IllegalStateException("La prenotazione è già scaduta.");
    }

    @Override public String getEtichetta()  { return "⌛ Scaduta — pagamento non effettuato entro 24h"; }
    @Override public String getBadgeStile() { return BADGE_STILE; }
}
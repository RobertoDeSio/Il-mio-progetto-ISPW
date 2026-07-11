package org.ispw.eventi.model.state;

/**
 * L'organizzatore ha accettato la richiesta.
 * Il cliente deve ora effettuare il pagamento.
 *
 * Transizioni permesse: paga() → StatoConfermata
 */
public class StatoApprovata implements StatoPrenotazione {

    private static final String BADGE_STILE =
            "-fx-background-radius:4;-fx-padding:2 8 2 8;-fx-font-size:11px;" +
                    "-fx-font-weight:BOLD;-fx-background-color:#DBEAFE;-fx-text-fill:#1D4ED8;";

    @Override public boolean puoiApprovare() { return false; }
    @Override public boolean puoiRifiutare() { return false; }
    @Override public boolean puoiPagare()    { return true; }

    @Override
    public StatoPrenotazione approva() {
        throw new IllegalStateException(
                "La prenotazione è già stata approvata.");
    }

    @Override
    public StatoPrenotazione rifiuta() {
        throw new IllegalStateException(
                "Non è possibile rifiutare una prenotazione già approvata.");
    }

    @Override
    public StatoPrenotazione paga() {
        return new StatoConfermata();
    }

    @Override public String getEtichetta()  { return "✅ Approvata — in attesa di pagamento"; }
    @Override public String getBadgeStile() { return BADGE_STILE; }
}
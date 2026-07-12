package org.ispw.eventi.model.state;

public class StatoConfermata implements StatoPrenotazione {

    private static final String BADGE_STILE =
            "-fx-background-radius:4;-fx-padding:2 8 2 8;-fx-font-size:11px;" +
                    "-fx-font-weight:BOLD;-fx-background-color:#DCFCE7;-fx-text-fill:#166534;";

    @Override public boolean puoiApprovare() { return false; }
    @Override public boolean puoiRifiutare() { return false; }
    @Override public boolean puoiPagare()    { return false; }

    @Override public StatoPrenotazione approva() {
        throw new IllegalStateException("La prenotazione è già confermata.");
    }
    @Override public StatoPrenotazione rifiuta() {
        throw new IllegalStateException("Non è possibile rifiutare una prenotazione già confermata.");
    }
    @Override public StatoPrenotazione paga() {
        throw new IllegalStateException("Il pagamento è già stato effettuato.");
    }
    @Override public StatoPrenotazione scadi() {
        throw new IllegalStateException("Non è possibile scadere una prenotazione già confermata.");
    }

    @Override public String getEtichetta()  { return "🎉 Confermata e pagata"; }
    @Override public String getBadgeStile() { return BADGE_STILE; }
}
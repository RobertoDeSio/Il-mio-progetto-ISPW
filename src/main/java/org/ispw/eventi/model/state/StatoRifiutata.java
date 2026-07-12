package org.ispw.eventi.model.state;

public class StatoRifiutata implements StatoPrenotazione {

    private static final String BADGE_STILE =
            "-fx-background-radius:4;-fx-padding:2 8 2 8;-fx-font-size:11px;" +
                    "-fx-font-weight:BOLD;-fx-background-color:#FEE2E2;-fx-text-fill:#991B1B;";

    @Override public boolean puoiApprovare() { return false; }
    @Override public boolean puoiRifiutare() { return false; }
    @Override public boolean puoiPagare()    { return false; }

    @Override public StatoPrenotazione approva() {
        throw new IllegalStateException("La prenotazione è stata rifiutata e non può essere modificata.");
    }
    @Override public StatoPrenotazione rifiuta() {
        throw new IllegalStateException("La prenotazione è già stata rifiutata.");
    }
    @Override public StatoPrenotazione paga() {
        throw new IllegalStateException("Non è possibile pagare una prenotazione rifiutata.");
    }
    @Override public StatoPrenotazione scadi() {
        throw new IllegalStateException("Non è possibile scadere una prenotazione rifiutata.");
    }

    @Override public String getEtichetta()  { return "❌ Rifiutata dall'organizzatore"; }
    @Override public String getBadgeStile() { return BADGE_STILE; }
}
package org.ispw.eventi.model.state;

public class StatoApprovata implements StatoPrenotazione {

    private static final String BADGE_STILE =
            "-fx-background-radius:4;-fx-padding:2 8 2 8;-fx-font-size:11px;" +
                    "-fx-font-weight:BOLD;-fx-background-color:#DBEAFE;-fx-text-fill:#1D4ED8;";

    @Override public boolean puoiApprovare() { return false; }
    @Override public boolean puoiRifiutare() { return false; }
    @Override public boolean puoiPagare()    { return true; }

    @Override public StatoPrenotazione approva() {
        throw new IllegalStateException("La prenotazione è già stata approvata.");
    }

    @Override public StatoPrenotazione rifiuta() {
        throw new IllegalStateException("Non è possibile rifiutare una prenotazione già approvata.");
    }

    @Override public StatoPrenotazione paga()   { return new StatoConfermata(); }

    /**
     * Unico stato da cui scadi() è permessa.
     * Chiamata quando le 24h di pagamento sono scadute.
     */
    @Override public StatoPrenotazione scadi()  { return new StatoScaduta(); }

    @Override public String getEtichetta()  { return "✅ Approvata — in attesa di pagamento"; }
    @Override public String getBadgeStile() { return BADGE_STILE; }
}